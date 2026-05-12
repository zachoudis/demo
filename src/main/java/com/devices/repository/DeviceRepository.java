package com.devices.repository;

import com.devices.entity.DeviceEntity;
import com.devices.model.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
	List<DeviceEntity> findByBrandIgnoreCase(String brand);

	List<DeviceEntity> findByState(DeviceState state);

	List<DeviceEntity> findByBrandIgnoreCaseAndState(String brand, DeviceState state);
}

