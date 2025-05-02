# Testing Your Authentication API

Once your server is running, you can test your API using the following tools:

- **Swagger UI**: http://127.0.0.1:8000/swagger/
- **ReDoc UI**: http://127.0.0.1:8000/redoc/

## Endpoints

### Register a Patient

- **Endpoint**: `POST /api/auth/register/patient/`
- **Required fields**: `username`, `password`, `password2`, `email`
- `role` is automatically set to `patient`

### Register a Doctor

- **Endpoint**: `POST /api/auth/register/doctor/`
- **Required fields**: `username`, `password`, `password2`, `email`, `specialty`
- **Optional field**: `clinic_id`
- `role` is automatically set to `doctor`

### Login

- **Endpoint**: `POST /api/auth/login/`
- **Required fields**: `username`, `password`
- **Returns**: `access` token, `refresh` token, user details

### Refresh Token

- **Endpoint**: `POST /api/auth/token/refresh/`
- **Required field**: `refresh`
- **Returns**: new `access` token

## Using JWT Authentication

After logging in or registering, include the token in the request headers:


This header must be included in all requests to protected endpoints.

## Example: JavaScript Fetch

```javascript
// Login
const login = async (username, password) => {
  const response = await fetch('http://127.0.0.1:8000/api/auth/login/', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      username,
      password
    })
  });

  const data = await response.json();

  // Store tokens
  localStorage.setItem('access_token', data.access);
  localStorage.setItem('refresh_token', data.refresh);

  return data;
};

// Accessing a protected endpoint
const getProtectedData = async () => {
  const token = localStorage.getItem('access_token');

  const response = await fetch('http://127.0.0.1:8000/api/some-protected-endpoint/', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  return await response.json();
};
