# Devices API Contract

## Overview

This document describes the HTTP API for managing devices.

**Base path**

```http
/api/devices
```

**Content type**

For endpoints that send JSON in the request body:

```http
Content-Type: application/json
```

**Response format**

The API returns device data as JSON.

```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T10:15:30Z"
}
```

`state` must be a valid value from the `DeviceState` enum, for example:

```json
"AVAILABLE"
```

```json
"IN_USE"
```

---

## Error Response Format

When an error occurs, the API returns a JSON object with an `error` field.

```json
{
  "error": "Device not found: 1"
}
```

Common error statuses:

| Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid request body, invalid patch field, invalid state value, or validation failure |
| `404 Not Found` | Device with the given ID does not exist |
| `409 Conflict` | Device exists, but the requested operation is not allowed because of its current state |

---

# Endpoints

---

## Create Device

Creates a new device.

```http
POST /api/devices
```

### Request Body

```json
{
  "name": "iPhone 15",
  "brand": "Apple"
}
```

### Successful Response

```http
201 Created
```

```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T10:15:30Z"
}
```

### Notes

A newly created device is created with state:

```json
"AVAILABLE"
```

---

## Update Device

Updates all editable information of an existing device.

```http
PUT /api/devices/{id}
```

### Path Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `id` | `Long` | Yes | ID of the device to update |

### Request Body

```json
{
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "AVAILABLE"
}
```

### Successful Response

```http
200 OK
```

```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T10:15:30Z"
}
```

### Possible Errors

```http
404 Not Found
```

```json
{
  "error": "Device not found: 1"
}
```

```http
409 Conflict
```

```json
{
  "error": "Device details cannot be updated when the device is in use."
}
```

### Notes

Device details cannot be updated when the current device state is:

```json
"IN_USE"
```

---

## Partially Update Device

Partially updates an existing device.

```http
PATCH /api/devices/{id}
```

### Path Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `id` | `Long` | Yes | ID of the device to partially update |

### Request Body

Send only the fields that should be changed.

```json
{
  "name": "iPhone 15 Pro"
}
```

Another valid example:

```json
{
  "state": "IN_USE"
}
```

Another valid example:

```json
{
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "AVAILABLE"
}
```

### Allowed Fields

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | `String` | No | New device name |
| `brand` | `String` | No | New device brand |
| `state` | `DeviceState` | No | New device state |

### Successful Response

```http
200 OK
```

```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T10:15:30Z"
}
```

### Possible Errors

```http
400 Bad Request
```

```json
{
  "error": "Invalid state value."
}
```

```http
404 Not Found
```

```json
{
  "error": "Device not found: 1"
}
```

```http
409 Conflict
```

```json
{
  "error": "Device details cannot be updated when the device is in use."
}
```

### Notes

Use `PATCH` when you only want to update some fields.

Use `PUT` when you want to send the full updated device data.

---

## Get Device by ID

Gets one device by its ID.

```http
GET /api/devices/{id}
```

### Path Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `id` | `Long` | Yes | ID of the device to retrieve |

### Successful Response

```http
200 OK
```

```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T10:15:30Z"
}
```

### Possible Errors

```http
404 Not Found
```

```json
{
  "error": "Device not found: 1"
}
```

---

## Get All Devices

Gets all devices.

```http
GET /api/devices
```

### Successful Response

```http
200 OK
```

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "brand": "Apple",
    "state": "AVAILABLE",
    "creationTime": "2026-05-12T10:15:30Z"
  },
  {
    "id": 2,
    "name": "Galaxy S24",
    "brand": "Samsung",
    "state": "IN_USE",
    "creationTime": "2026-05-12T11:20:00Z"
  }
]
```

### Empty Response Example

If no devices exist, the API returns an empty array.

```json
[]
```

---

## Get Devices by Brand

Gets all devices matching a specific brand.

```http
GET /api/devices/brand/{brand}
```

### Path Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `brand` | `String` | Yes | Brand name to filter by |

### Example Path

```http
GET /api/devices/brand/Apple
```

### Successful Response

```http
200 OK
```

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "brand": "Apple",
    "state": "AVAILABLE",
    "creationTime": "2026-05-12T10:15:30Z"
  },
  {
    "id": 3,
    "name": "iPad Pro",
    "brand": "Apple",
    "state": "IN_USE",
    "creationTime": "2026-05-12T12:45:00Z"
  }
]
```

### Empty Response Example

If no devices match the brand, the API returns an empty array.

```json
[]
```

---

## Get Devices by State

Gets all devices matching a specific state.

```http
GET /api/devices/state/{state}
```

### Path Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `state` | `DeviceState` | Yes | Device state to filter by |

### Example Path

```http
GET /api/devices/state/AVAILABLE
```

### Successful Response

```http
200 OK
```

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "brand": "Apple",
    "state": "AVAILABLE",
    "creationTime": "2026-05-12T10:15:30Z"
  },
  {
    "id": 4,
    "name": "Pixel 8",
    "brand": "Google",
    "state": "AVAILABLE",
    "creationTime": "2026-05-12T13:00:00Z"
  }
]
```

### Empty Response Example

If no devices match the state, the API returns an empty array.

```json
[]
```

### Possible Errors

```http
400 Bad Request
```

This can happen if `{state}` is not a valid `DeviceState` enum value.

---

## Delete Device

Deletes one device by its ID.

```http
DELETE /api/devices/{id}
```

### Path Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| `id` | `Long` | Yes | ID of the device to delete |

### Successful Response

```http
204 No Content
```

The response body is empty.

### Possible Errors

```http
404 Not Found
```

```json
{
  "error": "Device not found: 1"
}
```

```http
409 Conflict
```

```json
{
  "error": "In-use devices cannot be deleted."
}
```

### Notes

Devices cannot be deleted when their state is:

```json
"IN_USE"
```

---

# Device JSON Schema

## Device Response

```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T10:15:30Z"
}
```

| Field | Type | Description |
|---|---|---|
| `id` | `Long` | Unique device ID |
| `name` | `String` | Device name |
| `brand` | `String` | Device brand |
| `state` | `DeviceState` | Current device state |
| `creationTime` | `Instant` | Creation timestamp in ISO-8601 format |

---

## Create Device Request

```json
{
  "name": "iPhone 15",
  "brand": "Apple"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | `String` | Yes | Device name |
| `brand` | `String` | Yes | Device brand |

---

## Update Device Request

```json
{
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "AVAILABLE"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | `String` | Yes | Device name |
| `brand` | `String` | Yes | Device brand |
| `state` | `DeviceState` | Yes | Device state |

---

## Patch Device Request

```json
{
  "name": "iPhone 15 Pro"
}
```

All fields are optional, but at least one field should be provided.

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | `String` | No | Device name |
| `brand` | `String` | No | Device brand |
| `state` | `DeviceState` | No | Device state |
