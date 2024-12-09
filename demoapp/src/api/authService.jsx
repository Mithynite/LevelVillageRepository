import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api'; // Adjust URL for production

/**
 * Register a new user
 */
export const registerUser = async (userData) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/signup`, userData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * Log in user and store JWT token
 */
export const loginUser = async (userData) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/login`, userData);

        const { token, expiration } = response.data; // Extract the JWT token from the response
        if (token) {
            localStorage.setItem('JWTAuthToken', token); // Store the token in localStorage for later use (it will probably expire later due to Spring Boot, I guess)
            localStorage.setItem("JWTAuthTokenExpiration", expiration);
        }
        return response.data;
    } catch (error) {
        throw error;
    }
};

// Attach JWT token to all authenticated requests
export const getAuthHeaders = () => {
    const token = localStorage.getItem('JWTAuthToken');
    return token ? { Authorization: `Bearer ${token}` } : {};
};

// Example: Fetch protected data
export const fetchProtectedData = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/protected`, {
            headers: getAuthHeaders(),
        });
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

// Log out the user (clear token)
export const logoutUser = () => {
    localStorage.removeItem('JWTAuthToken'); // Clear token from local storage
};
