import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api'; // Adjust URL for production

// Register new user
export const registerUser = async (userData) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/signup`, userData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

// Log in user
export const loginUser = async (userData) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/login`, userData);
        return response.data;
    } catch (error) {
        throw error;
    }
};
