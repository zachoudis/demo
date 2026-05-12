package com.devices.api;

import com.devices.model.DeviceState;
import jakarta.validation.constraints.NotBlank;

public record DeviceCreateRequest(
		@NotBlank String name,
		@NotBlank String brand,
		DeviceState state
) {
}

