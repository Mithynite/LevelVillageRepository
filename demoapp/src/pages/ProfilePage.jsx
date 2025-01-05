import React, { useEffect, useState } from "react";
import NavigationButton from "../components/NavigationButton.jsx";
import {fetchUserProfile, updateUserProfile} from "../api/UserService.js";

const ProfilePage = () => {
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        bio: "",
        skills: "",
    });
    const [error, setError] = useState(null);

    // Fetch User Profile
    const fetchUser = async () => {
        try {
            const userProfile = await fetchUserProfile();
            setUser(userProfile);
            setFormData({
                username: userProfile.username,
                email: userProfile.email,
                bio: userProfile.bio || "",
                skills: userProfile.skills || "",
            });
        } catch (err) {
            console.error("Error fetching user profile:", err);
            setError("Failed to fetch user profile.");
        } finally {
            setLoading(false);
        }
    };

    // Handle Input Changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    // Update User Profile
    const handleProfileUpdate = async () => {
        try {
            const updatedUser = await updateUserProfile(formData);
            if (updatedUser) {
                setUser(updatedUser);
            }
            setIsEditing(false);
        } catch (err) {
            console.error("Error updating profile:", err);
            setError("Failed to update profile.");
        }
    };

    useEffect(() => {
        fetchUser();
    }, []);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="profile-page">
            <NavigationButton to="/home" label="Go Back to Home" />
            {isEditing ? (
                <div>
                    <form>
                        <textarea
                            name="bio"
                            value={formData.bio}
                            onChange={handleInputChange}
                        />
                        <input
                            type="text"
                            name="skills"
                            value={formData.skills}
                            onChange={handleInputChange}
                        />
                    </form>
                    <button onClick={handleProfileUpdate}>Save Changes</button>
                    <button onClick={() => setIsEditing(false)}>Cancel</button>
                </div>
            ) : (
                <div>
                    <h1>{user.username}</h1>
                    <p>Email: {user.email}</p>
                    <p>Bio: {user.bio}</p>
                    <p>Skills: {user.skills}</p>
                    <button onClick={() => setIsEditing(true)}>Edit Profile</button>
                </div>
            )}
        </div>
    );
};

export default ProfilePage;
