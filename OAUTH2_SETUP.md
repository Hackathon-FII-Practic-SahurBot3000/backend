# Google OAuth2 Authentication with JWT

This implementation provides Google OAuth2 authentication that creates users and returns JWT tokens for your Spring Boot application.

## Features

- ✅ Google OAuth2 authentication
- ✅ Automatic user creation/update from Google profile
- ✅ JWT token generation and validation
- ✅ Stateless authentication
- ✅ User profile information extraction

## API Endpoints

### 1. Get Google OAuth2 URL
```
GET /auth/google
```
Returns the Google OAuth2 authorization URL to redirect users to.

**Response:**
```json
{
  "authUrl": "/oauth2/authorization/google",
  "message": "Redirect to this URL to start Google OAuth2 authentication"
}
```

### 2. Google OAuth2 Callback
```
GET /oauth2/authorization/google
```
This is handled automatically by Spring Security. After successful authentication, it returns a JWT token.

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Get Current User (Protected)
```
GET /auth/me
Authorization: Bearer <jwt_token>
```
Returns current authenticated user information.

**Response:**
```json
{
  "email": "user@gmail.com",
  "firstName": "John",
  "lastName": "Doe",
  "profilePictureUrl": "https://lh3.googleusercontent.com/...",
  "userType": "basic",
  "googleAccount": true
}
```

## Authentication Flow

1. **Frontend**: Call `GET /auth/google` to get the OAuth2 URL
2. **Frontend**: Redirect user to the OAuth2 URL
3. **User**: Authenticates with Google
4. **Google**: Redirects back to your app with authorization code
5. **Backend**: Processes the OAuth2 callback, creates/updates user, returns JWT
6. **Frontend**: Store the JWT token for subsequent API calls
7. **Frontend**: Include JWT in Authorization header for protected endpoints

## Configuration

The Google OAuth2 configuration is already set up in `application.properties`:

```properties
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/oauth2/callback/google
spring.security.oauth2.client.registration.google.scope=email,profile
```

## User Model

Users created via OAuth2 have the following properties:
- `email`: From Google profile
- `firstName`: From Google profile
- `lastName`: From Google profile  
- `profilePictureUrl`: From Google profile
- `googleAccount`: Set to `true`
- `userType`: Set to `basic` by default
- `password`: Set to `null` (OAuth2 users don't have passwords)

## JWT Token

- **Expiration**: 24 hours
- **Subject**: User email
- **Algorithm**: HS256
- **Usage**: Include in Authorization header as `Bearer <token>`

## Testing

You can test the implementation using:

1. **Swagger UI**: Available at `/swagger-ui.html`
2. **Manual Testing**: Use the endpoints described above
3. **Database Check**: Use `GET /auth/test-db` to see total users

## Security Features

- CSRF protection disabled (stateless JWT)
- Session management set to stateless
- OAuth2 endpoints are public
- All other endpoints require JWT authentication
- Automatic token validation on each request

## Error Handling

The implementation includes basic error handling:
- Invalid/expired JWT tokens return 401
- Missing users return appropriate error messages
- OAuth2 failures are handled by Spring Security

## Next Steps

To enhance this implementation, consider adding:
- Refresh token support
- Role-based authorization
- User profile update endpoints
- Account linking (OAuth2 + traditional login)
- Rate limiting
- Comprehensive error handling 