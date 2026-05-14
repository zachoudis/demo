package com.devices.api;

import com.devices.exception.ApiExceptionHandler;
import com.devices.exception.DeviceConflictException;
import com.devices.exception.DeviceNotFoundException;
import com.devices.model.Device;
import com.devices.model.DeviceState;
import com.devices.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
@Import(ApiExceptionHandler.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService service;

    private static Device sampleDevice(Long id, String name, String brand, DeviceState state) {
        return new Device(
                id,
                name,
                brand,
                state,
                Instant.parse("2026-05-12T09:00:00Z")
        );
    }

    @Test
    void createReturnsCreatedDevice() throws Exception {
        when(service.create("iPhone 15", "Apple"))
                .thenReturn(sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE));

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "iPhone 15",
                                  "brand": "Apple"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        verify(service).create("iPhone 15", "Apple");
    }

    @Test
    void createReturnsBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "brand": "Apple"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void createReturnsBadRequestWhenBrandIsBlank() throws Exception {
        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "iPhone 15",
                                  "brand": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void updateReturnsUpdatedDevice() throws Exception {
        when(service.update(1L, "iPhone 15 Pro", "Apple", DeviceState.INACTIVE))
                .thenReturn(sampleDevice(1L, "iPhone 15 Pro", "Apple", DeviceState.INACTIVE));

        mockMvc.perform(put("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "iPhone 15 Pro",
                                  "brand": "Apple",
                                  "state": "INACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("INACTIVE"));

        verify(service).update(1L, "iPhone 15 Pro", "Apple", DeviceState.INACTIVE);
    }

    @Test
    void updateReturnsBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(put("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "brand": "Apple",
                                  "state": "AVAILABLE"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void updateReturnsBadRequestWhenStateIsMissing() throws Exception {
        mockMvc.perform(put("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "iPhone 15 Pro",
                                  "brand": "Apple"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void updateReturnsNotFoundForMissingDevice() throws Exception {
        when(service.update(99L, "iPhone 15 Pro", "Apple", DeviceState.AVAILABLE))
                .thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(put("/api/devices/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "iPhone 15 Pro",
                                  "brand": "Apple",
                                  "state": "AVAILABLE"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Device not found: 99"));

        verify(service).update(99L, "iPhone 15 Pro", "Apple", DeviceState.AVAILABLE);
    }

    @Test
    void updateReturnsConflictWhenServiceRejectsIt() throws Exception {
        when(service.update(1L, "New Name", "Apple", DeviceState.INACTIVE))
                .thenThrow(new DeviceConflictException(
                        "Device details cannot be updated when the device is in use."
                ));

        mockMvc.perform(put("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New Name",
                                  "brand": "Apple",
                                  "state": "INACTIVE"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(
                        "Device details cannot be updated when the device is in use."
                ));

        verify(service).update(1L, "New Name", "Apple", DeviceState.INACTIVE);
    }

    @Test
    void patchReturnsUpdatedDevice() throws Exception {
        when(service.partialUpdate(eq(1L), anyMap()))
                .thenReturn(sampleDevice(1L, "iPhone 15 Pro", "Apple", DeviceState.AVAILABLE));

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "iPhone 15 Pro"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        verify(service).partialUpdate(
                eq(1L),
                argThat(patch -> "iPhone 15 Pro".equals(patch.get("name")))
        );
    }

    @Test
    void patchReturnsBadRequestForInvalidPayload() throws Exception {
        when(service.partialUpdate(eq(1L), anyMap()))
                .thenThrow(new IllegalArgumentException("Name cannot be empty."));

        mockMvc.perform(patch("/api/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Name cannot be empty."));

        verify(service).partialUpdate(eq(1L), anyMap());
    }

    @Test
    void patchReturnsNotFoundForMissingDevice() throws Exception {
        when(service.partialUpdate(eq(99L), anyMap()))
                .thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(patch("/api/devices/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New Name"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Device not found: 99"));

        verify(service).partialUpdate(eq(99L), anyMap());
    }

    @Test
    void getOneReturnsDevice() throws Exception {
        when(service.get(1L))
                .thenReturn(sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE));

        mockMvc.perform(get("/api/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        verify(service).get(1L);
    }

    @Test
    void getOneReturnsNotFoundForMissingDevice() throws Exception {
        when(service.get(99L))
                .thenThrow(new DeviceNotFoundException(99L));

        mockMvc.perform(get("/api/devices/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Device not found: 99"));

        verify(service).get(99L);
    }

    @Test
    void getAllReturnsPaginatedResponse() throws Exception {
        when(service.listAll(0, 2)).thenReturn(new PageImpl<>(
                List.of(
                        sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE),
                        sampleDevice(2L, "Galaxy S24", "Samsung", DeviceState.INACTIVE)
                ),
                PageRequest.of(0, 2),
                5
        ));

        mockMvc.perform(get("/api/devices?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.content[1].name").value("Galaxy S24"))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0));

        verify(service).listAll(0, 2);
    }

    @Test
    void getAllUsesDefaultPagination() throws Exception {
        when(service.listAll(0, 10)).thenReturn(new PageImpl<>(
                List.of(
                        sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE)
                ),
                PageRequest.of(0, 10),
                1
        ));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(service).listAll(0, 10);
    }

    @Test
    void getByBrandReturnsFilteredList() throws Exception {
        when(service.listByBrand("Apple")).thenReturn(List.of(
                sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE)
        ));

        mockMvc.perform(get("/api/devices/brand/Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$[0].brand").value("Apple"))
                .andExpect(jsonPath("$[0].state").value("AVAILABLE"));

        verify(service).listByBrand("Apple");
    }

    @Test
    void getByStateReturnsFilteredList() throws Exception {
        when(service.listByState(DeviceState.AVAILABLE)).thenReturn(List.of(
                sampleDevice(1L, "iPhone 15", "Apple", DeviceState.AVAILABLE)
        ));

        mockMvc.perform(get("/api/devices/state/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$[0].brand").value("Apple"))
                .andExpect(jsonPath("$[0].state").value("AVAILABLE"));

        verify(service).listByState(DeviceState.AVAILABLE);
    }

    @Test
    void getByStateReturnsBadRequestForInvalidState() throws Exception {
        mockMvc.perform(get("/api/devices/state/NOT_A_REAL_STATE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Invalid state value. Allowed values are: [AVAILABLE, IN_USE, INACTIVE]"
                ));

        verifyNoInteractions(service);
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void deleteReturnsNotFoundForMissingDevice() throws Exception {
        doThrow(new DeviceNotFoundException(99L))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/devices/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Device not found: 99"));

        verify(service).delete(99L);
    }

    @Test
    void deleteReturnsConflictWhenDeviceIsInUse() throws Exception {
        doThrow(new DeviceConflictException("In-use devices cannot be deleted."))
                .when(service).delete(1L);

        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("In-use devices cannot be deleted."));

        verify(service).delete(1L);
    }
}