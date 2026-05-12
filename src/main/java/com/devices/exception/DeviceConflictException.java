package com.devices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DeviceConflictException extends RuntimeException {
    public DeviceConflictException(String message) {
        super(message);
    }
}
