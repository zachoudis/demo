package com.devices.exception;

import com.devices.model.DeviceState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDeviceNotFound(DeviceNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
    
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleIllegalState(IllegalStateException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(DeviceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDeviceConflict(DeviceConflictException ex) {
        return Map.of("error", ex.getMessage());
    }

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		if (ex.getRequiredType() == DeviceState.class) {
			String allowed = Arrays.toString(DeviceState.values());
			return Map.of("error", "Invalid state value. Allowed values are: " + allowed);
		}
		return Map.of("error", "Invalid parameter value.");
	}
}