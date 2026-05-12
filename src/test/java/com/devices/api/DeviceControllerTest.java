package com.devices.api;

import com.devices.exception.ApiExceptionHandler;
import com.devices.exception.DeviceConflictException;
import com.devices.exception.DeviceNotFoundException;
import com.devices.model.Device;
import com.devices.model.DeviceState;
import com.devices.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
@Import(ApiExceptionHandler.class)
class DeviceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DeviceService service;

	private static Device sampleDevice(Long id, String name, String brand, DeviceState state) {
		return new Device(id, name, brand, state, Instant.parse("2026-05-12T09:00:00Z"));
	}

	@Test
	void createReturnsCreatedDevice() throws Exception {
		when(service.create("iPhone 15", "Apple"))
				.thenReturn(sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE));

		mockMvc.perform(post("/api/devices")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "iPhone 15",
								  "brand": "Apple"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.state").value("AVAILABLE"));
	}

	@Test
	void getOneReturnsNotFoundForMissingDevice() throws Exception {
		when(service.get(99L)).thenThrow(new DeviceNotFoundException(99L));

		mockMvc.perform(get("/api/devices/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Device not found: 99"));
	}

	@Test
	void getAllReturnsList() throws Exception {
		when(service.listAll()).thenReturn(List.of(
				sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE),
				sampleDevice(2L, "Galaxy S24", "Samsung", DeviceState.INACTIVE)
		));

		mockMvc.perform(get("/api/devices"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void getByBrandReturnsFilteredList() throws Exception {
		when(service.listByBrand("Apple")).thenReturn(List.of(
				sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE)
		));

		mockMvc.perform(get("/api/devices/brand/Apple"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].brand").value("Apple"));
	}

	@Test
	void getByStateReturnsFilteredList() throws Exception {
		when(service.listByState(DeviceState.AVAILABLE)).thenReturn(List.of(
				sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE)
		));

		mockMvc.perform(get("/api/devices/state/AVAILABLE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].state").value("AVAILABLE"));
	}

	@Test
	void updateReturnsConflictWhenServiceRejectsIt() throws Exception {
		when(service.update(eq(1L), eq("New Name"), eq("Apple"), eq(DeviceState.INACTIVE)))
				.thenThrow(new DeviceConflictException("Device details cannot be updated when the device is in use."));

		mockMvc.perform(put("/api/devices/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "New Name",
								  "brand": "Apple",
								  "state": "INACTIVE"
								}
								"""))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Device details cannot be updated when the device is in use."));
	}

	@Test
	void patchReturnsBadRequestForInvalidPayload() throws Exception {
		when(service.partialUpdate(eq(1L), anyMap()))
				.thenThrow(new IllegalArgumentException("Name cannot be empty."));

		mockMvc.perform(patch("/api/devices/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "name": "   "
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Name cannot be empty."));
	}

	@Test
	void deleteReturnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/devices/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteReturnsConflictWhenDeviceIsInUse() throws Exception {
		doThrow(new DeviceConflictException("In-use devices cannot be deleted."))
				.when(service).delete(1L);

		mockMvc.perform(delete("/api/devices/1"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("In-use devices cannot be deleted."));
	}
}
