import axios from "axios";

const API_BASE_URL = "/api/skills"; // Base path to Skill API

// Fetch all skills
export const fetchSkills = async () => {
    const token = localStorage.getItem('JWTAuthToken');
    if (!token) {
        console.error('No JWT token found in localStorage!');
        throw new Error('No JWT token found');
    }
    try {
        const response = await axios.get(
            `${API_BASE_URL}`,{
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
        return response.data; // List of skills
    } catch (error) {
        console.error("Error fetching skills: ", error);
        throw new Error("Failed to fetch skills");
    }
};
