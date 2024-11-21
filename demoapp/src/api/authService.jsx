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

        const { token } = response.data; // Extract the JWT token from the response
        if (token) {
            localStorage.setItem('JWTAuthToken', token); // Store the token in localStorage for later use (it will probably expire later due to Spring Boot, I guess)
        }

        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * Get the JWT token from localStorage
 */
export const getAuthToken = () => {
    return localStorage.getItem('authToken');
};

/**
 * Set the Authorization header for authenticated requests
 */
export const setAuthHeader = () => {
    const token = getAuthToken();

    if (token) {
        return {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        };
    }

    return {}; // No token, no Authorization header
};
