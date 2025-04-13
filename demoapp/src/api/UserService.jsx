import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/users';  // Path to the API

// Fetch the user's profile by username
export const fetchUserProfile = async (username) => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }
    try {
        const response = await axios.get(
            `${API_BASE_URL}/${username}/profile`,
            {
                headers: { Authorization: `Bearer ${token}` },
            }
        );
        return response.data;
    } catch (error) {
        console.error('Error fetching user profile:', error);
        throw error;
    }
};

export const updateUserProfile = async (username, userData) => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token || !username) {
        console.error("No JWT token or username found in localStorage!");
        throw new Error("No JWT token or username found");
    }

    try {
        const response = await axios.put(
            `${API_BASE_URL}/${username}/profile`,
            userData,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error updating profile: ", error);
        throw error;
    }
};

export const updateUserLikedPosts = async (postIds) => {
    const token = localStorage.getItem('JWTAuthToken');
    const username = localStorage.getItem('username');
    if (!token || !username) {
        console.error("No JWT token or username found in localStorage!");
        throw new Error("No JWT token or username found");
    }

    try {
        const response = await axios.put(
            `${API_BASE_URL}/${username}/liked-posts`,
            postIds,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error updating profile: ", error);
        throw error;
    }
};

export const getUserLikedPosts = async () => {
    const token = localStorage.getItem('JWTAuthToken');
    const username = localStorage.getItem('username');
    if (!token || !username) {
        console.error("No JWT token or username found in localStorage!");
        throw new Error("No JWT token or username found");
    }

    try {
        const response = await axios.get(
            `${API_BASE_URL}/${username}/liked-posts`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );
        console.log(response.data)
        return response.data;
    } catch (error) {
        console.error("Error updating profile: ", error);
        throw error;
    }
};
