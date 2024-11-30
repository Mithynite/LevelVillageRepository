import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/posts';

export const getPosts = async () => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }

    try {
        const response = await axios.get(`${API_BASE_URL}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data; // Return only the data part of the response
    } catch (error) {
        console.error('Error fetching posts:', error);
        throw error;
    }
};

export const checkPostOwnership = async (postId) => {
    const token = localStorage.getItem('JWTAuthToken');
    const response = await axios.get(`${API_BASE_URL}/${postId}/check-ownership`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    return response.data; // Returns "owner" or "not_owner"
};

export const fetchPostById = async (postId) => {
    const token = localStorage.getItem('JWTAuthToken');
    try {
        const response = await fetch(`${API_BASE_URL}/${postId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }); // Replace with your actual backend API URL
        if (!response.ok) {
            throw new Error("Failed to fetch post");
        }
        return await response.json();
    } catch (error) {
        console.error("Error fetching post by ID:", error);
        throw error;
    }
};
