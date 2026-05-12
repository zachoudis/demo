package com.devices.model;

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
	 * Domain rule: name/brand cannot be changed when the device is in use.
	 * Creation time is intentionally immutable (no setter / no update method).
	 */
	public Device updateDetails(@NonNull String name, @NonNull String brand) {
		boolean nameChanged = !this.name.equals(name);
		boolean brandChanged = !this.brand.equals(brand);
		if ((nameChanged || brandChanged) && this.state == DeviceState.IN_USE) {
			throw new IllegalStateException("Name and brand cannot be updated when the device is in use.");
		}

		if (!nameChanged && !brandChanged) {
			return this;
		}
		return new Device(this.id, name, brand, this.state, this.creationTime);
	}

	public Device withState(@NonNull DeviceState state) {
		if (this.state == state) {
			return this;
		}
		return new Device(this.id, this.name, this.brand, state, this.creationTime);
	}

	/**
	 * Domain rule: in-use devices cannot be deleted.
	 */
	public void ensureDeletable() {
		if (this.state == DeviceState.IN_USE) {
			throw new IllegalStateException("In-use devices cannot be deleted.");
		}
	}
}

