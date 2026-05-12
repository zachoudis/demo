package com.devices.entity;

import com.devices.model.Device;
import com.devices.model.DeviceState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String brand;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DeviceState state;

	@Column(name = "creation_time", nullable = false, updatable = false)
	private Instant creationTime;

	//Mapper method to convert Device object to DeviceEntity object
	public static DeviceEntity fromDomain(Device device) {
		return new DeviceEntity(
				device.getId(),
				device.getName(),
				device.getBrand(),
				device.getState(),
				device.getCreationTime()
		);
	}

	//Mapper method to convert DeviceEntity object to Device object
	public Device toDomain() {
		return new Device(id, name, brand, state, creationTime);
	}
}

