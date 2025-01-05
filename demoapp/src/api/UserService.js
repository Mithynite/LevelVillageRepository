import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/users';  // Path to the API

// Fetch the user's profile
export const fetchUserProfile = async () => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }
    try {
        const response = await axios.get(
            `${API_BASE_URL}/profile`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching user profile:', error);
        throw error;
    }
}

export const updateUserProfile = async (userData) => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }
    try {
        const response = await axios.put(`${API_BASE_URL}/profile`, userData, {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        console.error("Error updating profile: ", error);
        throw error;
    }
};
