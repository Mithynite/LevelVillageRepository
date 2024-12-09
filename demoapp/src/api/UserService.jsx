import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/users';

export const fetchUserById = async (userId) => {
    const token = localStorage.getItem('JWTAuthToken');

    try {
        const response = await axios.get(
            `${API_BASE_URL}/${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
        if (!response.ok) {
            throw new Error("Failed to fetch post");
        }
        return await response.json();
    } catch (error) {
        console.error("Error fetching post by ID:", error);
        throw error;
    }
};