package com.devices.model;

import com.devices.exception.DeviceConflictException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;

@Getter
@AllArgsConstructor
public final class Device {
	private final Long id;
	@NonNull private final String name;
	@NonNull private final String brand;
	@NonNull private final DeviceState state;
	@NonNull private final Instant creationTime;

	public Device(String name, String brand, DeviceState state, Instant creationTime) {
		this(null, name, brand, state, creationTime);
	}

	/**
	 * Domain Validation: name/brand cannot be changed when the device is in use.
	 * I return a new device object with the updated details and the same creation time
	 */
	public Device updateDetails(@NonNull String name, @NonNull String brand) {
		if (this.state == DeviceState.IN_USE) {
			throw new DeviceConflictException("Device details cannot be updated when the device is in use.");
		}

		return new Device(this.id, name, brand, state, this.creationTime);
	}

	public Device withState(@NonNull DeviceState state) {
		if (this.state == state) {
			return this;
		}
		return new Device(this.id, this.name, this.brand, state, this.creationTime);
	}

	/**
	 * Domain validation: in-use devices cannot be deleted.
	 */
	public void ensureDeletable() {
		if (this.state == DeviceState.IN_USE) {
			throw new DeviceConflictException("In-use devices cannot be deleted.");
		}
	}
}

