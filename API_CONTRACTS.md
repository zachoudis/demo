# API Contracts

## Device object

```json
{
  "id": 1,
  "name": "iPhone 15",
  "brand": "Apple",
  "state": "AVAILABLE",
  "creationTime": "2026-05-12T09:00:00Z"
}
```

## Valid `state` values

```json
"AVAILABLE"
"IN_USE"
"INACTIVE"
```

## Error object

```json
{
  "error": "Error message here"
}
```

## `POST /api/devices`

- Purpose: Create a device
- Success: `201 Created`
- Request body:

```json
{
  "name": "iPhone 15",
  "brand": "Apple"
}
```

- Response body: `Device object`
- Error cases:
  - `400 Bad Request`: validation error (missing/blank `name` or `brand`)
    - Response body: `Error object`

## `PUT /api/devices/{id}`

- Purpose: Fully update a device
- Success: `200 OK`
- Path params:
  - `id`: device id
- Request body:

```json
{
  "name": "iPhone 15 Pro",
  "brand": "Apple",
  "state": "INACTIVE"
}
```

- Response body: `Device object`
- Error cases:
  - `400 Bad Request`: validation error (missing/blank fields, or missing `state`)
    - Response body: `Error object`
  - `404 Not Found`: device id does not exist
    - Response body: `Error object`
  - `409 Conflict`: domain rule violation (`name`/`brand` cannot change while device is `IN_USE`; sending the **same** stored `name` and `brand` and only changing `state` is allowed)
    - Response body: `Error object`

## `PATCH /api/devices/{id}`

- Purpose: Partially update a device
- Success: `200 OK`
- Path params:
  - `id`: device id
- Request body can include any subset of fields below. Omitted fields are left unchanged (you may send only `name`, only `brand`, only `state`, or any combination).

```json
{
  "name": "Updated name"
}
```

```json
{
  "brand": "Updated brand"
}
```

```json
{
  "state": "IN_USE"
}
```

```json
{
  "name": "Galaxy S24",
  "brand": "Samsung",
  "state": "AVAILABLE"
}
```

- Response body: `Device object`
- Error cases:
  - `400 Bad Request`: invalid payload (e.g. blank `name` / blank `brand`, explicit `state: null`, or invalid `state`)
    - Response body: `Error object`
  - `404 Not Found`: device id does not exist
    - Response body: `Error object`
  - `409 Conflict`: domain rule violation (`name`/`brand` cannot change while device is `IN_USE`; a patch that only changes `state`, or keeps the same `name`/`brand`, is allowed)
    - Response body: `Error object`

## `GET /api/devices/{id}`

- Purpose: Get one device by id
- Success: `200 OK`
- Path params:
  - `id`: device id
- Response body: `Device object`
- Error cases:
  - `404 Not Found`: device id does not exist
    - Response body: `Error object`

## `GET /api/devices`

- Purpose: Get all devices
- Success: `200 OK`
- Query params:
  - `page`: zero-based page number, default `0`
  - `size`: page size, default `10`
- Response body:

```json
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15",
      "brand": "Apple",
      "state": "AVAILABLE",
      "creationTime": "2026-05-12T09:00:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```
- Error cases:
  - `400 Bad Request`: invalid pagination params (non-numeric `page`/`size`)
    - Response body: `Error object`

## `GET /api/devices/brand/{brand}`

- Purpose: Get all devices by brand
- Success: `200 OK`
- Path params:
  - `brand`: device brand
- Response body:

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "brand": "Apple",
    "state": "AVAILABLE",
    "creationTime": "2026-05-12T09:00:00Z"
  }
]
```
- Error cases:
  - `400 Bad Request`: invalid request (e.g. missing/invalid `brand` path value)
    - Response body: `Error object`

## `GET /api/devices/state/{state}`

- Purpose: Get all devices by state
- Success: `200 OK`
- Path params:
  - `state`: `AVAILABLE`, `IN_USE`, or `INACTIVE`
- Response body:

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "brand": "Apple",
    "state": "AVAILABLE",
    "creationTime": "2026-05-12T09:00:00Z"
  }
]
```
- Error cases:
  - `400 Bad Request`: invalid `state` value (must be one of `AVAILABLE`, `IN_USE`, `INACTIVE`)
    - Response body: `Error object`

## `DELETE /api/devices/{id}`

- Purpose: Delete a device by id
- Success: `204 No Content`
- Path params:
  - `id`: device id
- Error cases:
  - `404 Not Found`: device id does not exist
    - Response body: `Error object`
  - `409 Conflict`: domain rule violation (in-use devices cannot be deleted)
    - Response body: `Error object`
