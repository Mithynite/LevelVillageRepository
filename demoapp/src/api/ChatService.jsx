import axios from "axios";
const API_BASE_URL = 'http://localhost:8080/api/';  // Path to the API

export const sendChatRequest = async (receiverUsername, postId) => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }

    try {
        const response = await axios.post(
            `${API_BASE_URL}chat-requests/${receiverUsername}/posts/${postId}`,
            null, // no request body
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error sending functions request: ", error);
        throw error;
    }
};

export const getMyIncomingChatRequests = async () => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }

    try {
        const response = await axios.get(
            `${API_BASE_URL}chat-requests`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                }
            }
        );
        return response.data;
    }catch (error) {
        console.error("Error sending functions request: ", error);
        throw error;
    }
};

export const chatRequestWasAlreadySent = async (receiverUsername) => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }

    try {
        const response = await axios.get(
            `${API_BASE_URL}chat-requests/${receiverUsername}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                }
            }
        );
        return response.data;
    }catch (error) {
        console.error("Error sending functions request: ", error);
        throw error;
    }
};

export const respondToChatRequest = async (requestId, accepted) => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) throw new Error('No JWT token found');

    try {
        const response = await axios.post(
            `${API_BASE_URL}chat-requests/${requestId}/respond?response=${accepted}`,
            {},
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error responding to chat request: ", error);
        throw error;
    }
};

