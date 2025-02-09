import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";  // Import useParams
import NavigationButton from "../components/NavigationButton.jsx";
import { fetchUserProfile, updateUserProfile } from "../api/UserService.jsx";
import { fetchSkills } from "../api/SkillService.jsx";

const ProfilePage = () => {
    const { username } = useParams(); // Get username from URL
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [skills, setSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        bio: "",
        skills: [],
    });
    const [error, setError] = useState(null);

    // Get logged-in user's username from localStorage
    const loggedInUsername = localStorage.getItem("username");

    // Fetch User Profile
    const fetchUser = async () => {
        try {
            const userProfile = await fetchUserProfile(username); // Fetch by username
            setUser(userProfile);
            setFormData({
                bio: userProfile.bio || "",
                skills: userProfile.skills || [],
                username: userProfile.username || "",
                email: userProfile.email || "",
            });

            setSelectedSkills(userProfile.skills.map((skill) => skill.id));
            setLoading(false);
        } catch (err) {
            console.error("Error fetching user profile:", err);
            setError("Failed to fetch user profile.");
            setLoading(false);
        }
    };

    // Fetch Skills
    const fetchAllSkills = async () => {
        try {
            const allSkills = await fetchSkills();
            setSkills(allSkills);
        } catch (err) {
            console.error("Error fetching skills:", err);
            setError("Failed to fetch skills.");
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

    // Handle Skill Change
    const handleSkillChange = (e) => {
        const selectedOptions = [...e.target.selectedOptions].map(option => option.value);
        setSelectedSkills(selectedOptions);
    };

    // Update User Profile
    const handleProfileUpdate = async () => {
        try {
            console.log("Updating with:", formData);

            await updateUserProfile(username, {
                username: user.username,
                email: user.email,
                bio: formData.bio,
                skills: selectedSkills,
            });

            setUser((prevUser) => ({
                ...prevUser,
                bio: formData.bio,
                skills: skills.filter(skill => selectedSkills.includes(skill.id)),
            }));

            setIsEditing(false);
        } catch (err) {
            console.error("Error updating profile:", err);
            setError("Failed to update profile.");
        }
    };

    // Fetch user profile and skills on component mount
    useEffect(() => {
        if (username) {
            fetchUser();
            fetchAllSkills();
        }
    }, [username]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="profile-page">
            <NavigationButton to="/home" label="Go Back to Home" />
            {isEditing ? (
                <div>
                    <form>
                        <label>
                            Bio:
                            <textarea
                                name="bio"
                                value={formData.bio}
                                onChange={handleInputChange}
                            />
                        </label>
                        <label>
                            Skills:
                            <select
                                multiple
                                value={selectedSkills}
                                onChange={handleSkillChange}
                                size={5}
                            >
                                {skills.map((skill) => (
                                    <option key={skill.id} value={skill.id}>
                                        {skill.name}
                                    </option>
                                ))}
                            </select>
                        </label>
                    </form>
                    <button onClick={handleProfileUpdate}>Save Changes</button>
                    <button onClick={() => setIsEditing(false)}>Cancel</button>
                </div>
            ) : (
                <div>
                    <h1>{user?.username}</h1>
                    <p>Email: {user?.email}</p>
                    <p>Bio: {user?.bio}</p>
                    <p>
                        Skills: {user?.skills.length > 0 ? user.skills.map(skill => skill.name).join(", ") : "No skills added yet"}
                    </p>

                    {/* Only show "Edit Profile" button if logged-in user is viewing their own profile */}
                    {loggedInUsername === username && (
                        <button onClick={() => setIsEditing(true)}>Edit Profile</button>
                    )}
                </div>
            )}
        </div>
    );
};

export default ProfilePage;
