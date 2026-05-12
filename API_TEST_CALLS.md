# API Test Calls

- Create a device:

```bash
curl -X POST http://localhost:8080/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "brand": "Apple"
  }'
```

- Create another device:

```bash
curl -X POST http://localhost:8080/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Galaxy S24",
    "brand": "Samsung"
  }'
```

- Get all devices:

```bash
curl http://localhost:8080/api/devices
```

- Get one device by id:

```bash
curl http://localhost:8080/api/devices/1
```

- Try to get a device that does not exist:

```bash
curl -i http://localhost:8080/api/devices/999
```

- Get devices by brand:

```bash
curl http://localhost:8080/api/devices/brand/Apple
```

```bash
curl http://localhost:8080/api/devices/brand/Samsung
```

- Get devices by state:

```bash
curl http://localhost:8080/api/devices/state/AVAILABLE
```

```bash
curl http://localhost:8080/api/devices/state/IN_USE
```

- Fully update a device:

```bash
curl -X PUT http://localhost:8080/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "brand": "Apple",
    "state": "INACTIVE"
  }'
```

- Set a device to `IN_USE`:

```bash
curl -X PUT http://localhost:8080/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "brand": "Apple",
    "state": "IN_USE"
  }'
```

- Partially update only the name:

```bash
curl -X PATCH http://localhost:8080/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Updated"
  }'
```

- Partially update only the brand:

```bash
curl -X PATCH http://localhost:8080/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Apple Inc"
  }'
```

- Partially update only the state:

```bash
curl -X PATCH http://localhost:8080/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "state": "AVAILABLE"
  }'
```

- Try to update the name of a device already in use:

```bash
curl -i -X PATCH http://localhost:8080/api/devices/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Should Fail"
  }'
```

- Delete a device:

```bash
curl -X DELETE http://localhost:8080/api/devices/2
```

- Try to delete a device that is in use:

```bash
curl -i -X DELETE http://localhost:8080/api/devices/1
```
