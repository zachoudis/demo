package com.devices.api;

import com.devices.model.DeviceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceUpdateRequest(
		@NotBlank String name,
		@NotBlank String brand,
		@NotNull DeviceState state
) {
}

