package com.devices.service;

import com.devices.entity.DeviceEntity;
import com.devices.exception.DeviceConflictException;
import com.devices.exception.DeviceNotFoundException;
import com.devices.model.Device;
import com.devices.model.DeviceState;
import com.devices.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

	@Mock
	private DeviceRepository repo;

	@InjectMocks
	private DeviceService service;

	@Test
	void createSetsAvailableStateByDefault() {
		ArgumentCaptor<DeviceEntity> captor = ArgumentCaptor.forClass(DeviceEntity.class);

		when(repo.save(any(DeviceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Device result = service.create("Phone", "Apple");

		verify(repo).save(captor.capture());
		Device savedDevice = captor.getValue().toDomain();

		assertEquals(DeviceState.AVAILABLE, savedDevice.getState());
		assertEquals("Phone", result.getName());
		assertEquals("Apple", result.getBrand());
		assertEquals(DeviceState.AVAILABLE, result.getState());
	}

	@Test
	void getThrowsWhenDeviceDoesNotExist() {
		when(repo.findById(99L)).thenReturn(Optional.empty());

		assertThrows(DeviceNotFoundException.class, () -> service.get(99L));
	}

	@Test
	void updateThrowsConflictWhenDeviceIsInUse() {
		Device existing = new Device(1L, "Phone", "Apple", DeviceState.IN_USE, Instant.now());
		when(repo.findById(1L)).thenReturn(Optional.of(DeviceEntity.fromDomain(existing)));

		assertThrows(DeviceConflictException.class,
				() -> service.update(1L, "New Name", "New Brand", DeviceState.INACTIVE));
	}

	@Test
	void partialUpdateThrowsWhenNameIsBlank() {
		Device existing = new Device(1L, "Phone", "Apple", DeviceState.AVAILABLE, Instant.now());
		when(repo.findById(1L)).thenReturn(Optional.of(DeviceEntity.fromDomain(existing)));

		assertThrows(IllegalArgumentException.class,
				() -> service.partialUpdate(1L, Map.of("name", "   ")));
	}

	@Test
	void deleteThrowsConflictWhenDeviceIsInUse() {
		Device existing = new Device(1L, "Phone", "Apple", DeviceState.IN_USE, Instant.now());
		when(repo.findById(1L)).thenReturn(Optional.of(DeviceEntity.fromDomain(existing)));

		assertThrows(DeviceConflictException.class, () -> service.delete(1L));
	}

	@Test
	void listByBrandReturnsMappedDevices() {
		DeviceEntity first = DeviceEntity.fromDomain(
				new Device(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE, Instant.now()));
		DeviceEntity second = DeviceEntity.fromDomain(
				new Device(2L, "iPhone 16", "Apple", DeviceState.INACTIVE, Instant.now()));

		when(repo.findByBrandIgnoreCase("Apple")).thenReturn(List.of(first, second));

		List<Device> result = service.listByBrand("Apple");

		assertEquals(2, result.size());
		assertTrue(result.stream().allMatch(device -> device.getBrand().equals("Apple")));
	}
}
