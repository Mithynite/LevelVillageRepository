import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const getPosts = async () => {
    const token = localStorage.getItem('authToken'); // Retrieve token from storage
    return axios.get(`${API_BASE_URL}/posts`, {
        headers: {
            Authorization: `Bearer ${token}`, // Include token in headers
        },
    });
};
