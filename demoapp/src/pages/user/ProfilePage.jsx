import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import NavigationButton from "../../components/NavigationButton.jsx";
import { fetchUserProfile, updateUserProfile, getUserLikedPosts } from "../../api/UserService.jsx";

const ProfilePage = () => {
    const { username } = useParams();
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({ bio: "", skills: [] });
    const [likedPosts, setLikedPosts] = useState([]);
    const [error, setError] = useState(null);

    const loggedInUsername = localStorage.getItem("username");

    useEffect(() => {
        const fetchData = async () => {
            try {

                const userProfile = await fetchUserProfile(username);

                setUser({
                    ...userProfile,
                });

                setFormData({
                    bio: userProfile.bio || "",
                });

                if (username === loggedInUsername) {
                    const likedPostsData = await getUserLikedPosts();
                    setLikedPosts(likedPostsData);
                }

                setLoading(false);
            } catch (err) {
                console.error("Error fetching data:", err);
                setLoading(false);
            }
        };

        if (username) {
            fetchData();
        }
    }, [username]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleProfileUpdate = async () => {
        try {
            await updateUserProfile(username, { bio: formData.bio, skills: selectedSkills });

            setUser(prevUser => ({
                ...prevUser,
                bio: formData.bio,
            }));

            setIsEditing(false);
        } catch (err) {
            console.error("Error updating profile:", err);
            setError("Failed to update profile.");
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="profile-page">
            <NavigationButton to="/home" label="Go Back to Home" className="form-button"/>

            {isEditing ? (
                <div className="profile-edit">
                    <form>
                        <label>Bio:
                            <textarea
                                name="bio"
                                value={formData.bio}
                                onChange={handleInputChange}
                                className="bio-input"
                            />
                        </label>
                    </form>
                    <button onClick={handleProfileUpdate} className="form-button">Save Changes</button>
                    <button onClick={() => setIsEditing(false)} className="form-button">Cancel</button>
                </div>
            ) : (
                <div className="profile-display">
                    <h1>{user?.username}</h1>
                    <p><strong>Email:</strong> {user?.email}</p>
                    <p><strong>Bio:</strong> {user?.bio}</p>
                    {loggedInUsername === username && (
                        <button onClick={() => setIsEditing(true)} className="form-button">Edit Profile</button>
                    )}

                    {/* Liked Posts Section */}
                    {loggedInUsername === username && (
                        <NavigationButton
                            to={`/users/${username}/liked-posts`}
                            label="View Liked Posts"
                            className="form-button"
                        />
                    )}
                </div>
            )}
        </div>
    );
};

export default ProfilePage;
