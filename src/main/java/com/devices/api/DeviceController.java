package com.devices.api;

import com.devices.exception.DeviceNotFoundException;
import com.devices.model.Device;
import com.devices.model.DeviceState;
import com.devices.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
	private final DeviceService service;

	public DeviceController(DeviceService service) {
		this.service = service;
	}

	//Post Endpoint for creating a new device
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Device create(@Valid @RequestBody DeviceCreateRequest req) {
		return service.create(req.name(), req.brand(), req.state());
	}

	//Put Endpoint for updating the information of a device
	@PutMapping("/{id}")
	public Device update(@PathVariable Long id, @Valid @RequestBody DeviceUpdateRequest req) {
		try {
			return service.update(id, req.name(), req.brand(), req.state());
		} catch (DeviceNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	//Patch Endpoint for partially updating the information of a device
	@PatchMapping("/{id}")
	public Device partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> patch) {
		try {
			return service.partialUpdate(id, patch);
		} catch (DeviceNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid state value.");
		}
	}

	//Get Endpoint for getting one record from the db
	@GetMapping("/{id}")
	public Device getOne(@PathVariable Long id) {
		try {
			return service.get(id);
		} catch (DeviceNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	//Get Endpoint for getting all records from the db
	@GetMapping
	public List<Device> getAll(
			@RequestParam(required = false) String brand,
			@RequestParam(required = false) DeviceState state
	) {
		return service.listAll();
	}

	//Get Endpoint for getting all records by brand from the db
	@GetMapping("/by-brand/{brand}")
	public List<Device> getByBrand(@PathVariable String brand) {
		return service.listByBrand(brand);
	}

	//Get Endpoint for getting all records by state from the db
	@GetMapping("/by-state/{state}")
	public List<Device> getByState(@PathVariable DeviceState state) {
		return service.listByState(state);
	}

	//Delete Endpoint for deleting a record from the db
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		try {
			service.delete(id);
		} catch (DeviceNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}
}

