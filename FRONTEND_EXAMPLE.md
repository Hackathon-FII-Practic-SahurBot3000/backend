# Frontend Integration Example

This document shows how to integrate with the Google OAuth2 authentication from a frontend application.

## JavaScript/React Example

### 1. Initiate Google OAuth2 Login

```javascript
// Get the Google OAuth2 URL from your backend
const initiateGoogleLogin = async () => {
  try {
    const response = await fetch('http://localhost:8080/auth/google');
    const data = await response.json();
    
    // Redirect user to Google OAuth2 authorization
    window.location.href = `http://localhost:8080${data.authUrl}`;
  } catch (error) {
    console.error('Error initiating Google login:', error);
  }
};
```

### 2. Handle OAuth2 Callback (React Router)

```javascript
import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const OAuth2Callback = () => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    // The backend will return a JSON response with the JWT token
    // You might need to handle this differently based on your setup
    
    // If the backend redirects to a callback URL with token as query param:
    const urlParams = new URLSearchParams(location.search);
    const token = urlParams.get('token');
    
    if (token) {
      // Store the JWT token
      localStorage.setItem('authToken', token);
      
      // Redirect to dashboard or home page
      navigate('/dashboard');
    } else {
      // Handle error case
      navigate('/login?error=oauth_failed');
    }
  }, [location, navigate]);

  return <div>Processing authentication...</div>;
};
```

### 3. Make Authenticated API Calls

```javascript
// Utility function to make authenticated requests
const makeAuthenticatedRequest = async (url, options = {}) => {
  const token = localStorage.getItem('authToken');
  
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  
  const response = await fetch(url, {
    ...options,
    headers,
  });
  
  if (response.status === 401) {
    // Token expired or invalid, redirect to login
    localStorage.removeItem('authToken');
    window.location.href = '/login';
    return null;
  }
  
  return response;
};

// Get current user information
const getCurrentUser = async () => {
  try {
    const response = await makeAuthenticatedRequest('http://localhost:8080/auth/me');
    if (response && response.ok) {
      return await response.json();
    }
  } catch (error) {
    console.error('Error fetching current user:', error);
  }
  return null;
};
```

### 4. React Hook for Authentication

```javascript
import { useState, useEffect, createContext, useContext } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('authToken'));

  useEffect(() => {
    if (token) {
      fetchCurrentUser();
    } else {
      setLoading(false);
    }
  }, [token]);

  const fetchCurrentUser = async () => {
    try {
      const response = await makeAuthenticatedRequest('http://localhost:8080/auth/me');
      if (response && response.ok) {
        const userData = await response.json();
        setUser(userData);
      } else {
        // Invalid token
        logout();
      }
    } catch (error) {
      console.error('Error fetching user:', error);
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = (newToken) => {
    localStorage.setItem('authToken', newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setToken(null);
    setUser(null);
  };

  const initiateGoogleLogin = async () => {
    try {
      const response = await fetch('http://localhost:8080/auth/google');
      const data = await response.json();
      window.location.href = `http://localhost:8080${data.authUrl}`;
    } catch (error) {
      console.error('Error initiating Google login:', error);
    }
  };

  const value = {
    user,
    token,
    loading,
    login,
    logout,
    initiateGoogleLogin,
    isAuthenticated: !!user,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
```

### 5. Protected Route Component

```javascript
import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthProvider';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
```

### 6. Login Component

```javascript
import { useAuth } from './AuthProvider';

const LoginPage = () => {
  const { initiateGoogleLogin } = useAuth();

  return (
    <div className="login-page">
      <h1>Login</h1>
      <button 
        onClick={initiateGoogleLogin}
        className="google-login-btn"
      >
        Sign in with Google
      </button>
    </div>
  );
};

export default LoginPage;
```

## Alternative: Popup-based OAuth2 Flow

If you prefer a popup-based flow instead of full page redirect:

```javascript
const initiateGoogleLoginPopup = () => {
  const popup = window.open(
    'http://localhost:8080/oauth2/authorization/google',
    'google-oauth',
    'width=500,height=600,scrollbars=yes,resizable=yes'
  );

  // Listen for the popup to close or send a message
  const checkClosed = setInterval(() => {
    if (popup.closed) {
      clearInterval(checkClosed);
      // Check if token was stored (you'd need to modify backend to support this)
      const token = localStorage.getItem('authToken');
      if (token) {
        window.location.reload(); // Or update state
      }
    }
  }, 1000);
};
```

## Important Notes

1. **CORS Configuration**: Make sure your backend allows requests from your frontend domain.

2. **Redirect URI**: The Google OAuth2 redirect URI in your backend configuration should match your setup.

3. **Token Storage**: Consider using secure storage methods for production (httpOnly cookies, secure storage).

4. **Error Handling**: Implement proper error handling for network failures, invalid tokens, etc.

5. **Token Refresh**: The current implementation doesn't include refresh tokens. Consider implementing this for better UX.

## Environment Variables

For different environments, use environment variables:

```javascript
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// Use API_BASE_URL in your fetch calls
const response = await fetch(`${API_BASE_URL}/auth/google`);
``` 