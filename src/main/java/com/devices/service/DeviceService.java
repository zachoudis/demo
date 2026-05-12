package com.devices.service;

import com.devices.entity.DeviceEntity;
import com.devices.exception.DeviceNotFoundException;
import com.devices.model.Device;
import com.devices.model.DeviceState;
import com.devices.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeviceService {
	private final DeviceRepository repo;

	public DeviceService(DeviceRepository repo) {
		this.repo = repo;
	}


	public Device create(String name, String brand, DeviceState state) {
		Device device = new Device(name, brand, DeviceState.AVAILABLE, Instant.now());
		return repo.save(DeviceEntity.fromDomain(device)).toDomain();
	}

	//Here we update the details of the device only if the device is not in use
	public Device update(Long id, String name, String brand, DeviceState state) {
		Device existing = this.get(id);

		if (existing.getState() == DeviceState.IN_USE)
			throw new IllegalStateException("Device details cannot be updated when the device is in use.");
		
		Device updated = new Device(id, name, brand, state, existing.getCreationTime());

		return repo.save(DeviceEntity.fromDomain(updated)).toDomain();
	}

	//We get the existing device and then we update the details of the device 
	// only if the device is not in use and if the state is not IN_USE
	public Device partialUpdate(Long id, Map<String, Object> patch) {
		Device existing = get(id);

		String name = existing.getName();
		String brand = existing.getBrand();
		DeviceState state = existing.getState();

		if (patch.containsKey("name")) {
			Object v = patch.get("name");
			name = v == null ? null : v.toString();
		}
		if (patch.containsKey("brand")) {
			Object v = patch.get("brand");
			brand = v == null ? null : v.toString();
		}
		if (patch.containsKey("state")) {
			Object v = patch.get("state");
			if (v == null) {
				state = null;
			} else {
				state = DeviceState.valueOf(v.toString().trim().toUpperCase());
			}
		}

		//We update the details of the device only if the name and brand are not null
		Device updated = existing;
		if (name != null && brand != null) {
			updated = updated.updateDetails(name, brand);
		}

		updated = updated.withState(state);
		

		return repo.save(DeviceEntity.fromDomain(updated)).toDomain();
	}
	
	//Since the class is transactions here we use transactional read only to ensure
	//that the data is read from the database and not from the cache
	@Transactional(readOnly = true)
	public Device get(Long id) {
		return repo.findById(id)
				.map(DeviceEntity::toDomain)
				.orElseThrow(() -> new DeviceNotFoundException(id));
	}

	//Since the class is transactions here we use transactional read only to ensure
	//that the data is read from the database and not from the cache
	@Transactional(readOnly = true)
	public List<Device> listAll() {
		return repo.findAll().stream()
				.map(DeviceEntity::toDomain)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<Device> listByBrand(String brand) {
		return repo.findByBrandIgnoreCase(brand).stream()
				.map(DeviceEntity::toDomain)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<Device> listByState(DeviceState state) {
		return repo.findByState(state).stream()
				.map(DeviceEntity::toDomain)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<Device> listByBrandAndState(String brand, DeviceState state) {
		return repo.findByBrandIgnoreCaseAndState(brand, state).stream()
				.map(DeviceEntity::toDomain)
				.toList();
	}

	
	public void delete(Long id) {
		Device existing = get(id);
		//We check if the device we want to delete is in use
		//and if it is we throw an exception
		existing.ensureDeletable();
		repo.deleteById(id);
	}
}

