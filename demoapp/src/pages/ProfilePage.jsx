import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import NavigationButton from "../components/NavigationButton.jsx";
import { fetchUserProfile, updateUserProfile } from "../api/UserService.jsx";
import { fetchSkills } from "../api/SkillService.jsx";

const ProfilePage = () => {
    const { username } = useParams(); // Get username from URL
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [skills, setSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]); // Holds selected skills (skill IDs)
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({ bio: "", skills: [] });
    const [error, setError] = useState(null);

    // Get logged-in user's username from localStorage
    const loggedInUsername = localStorage.getItem("username");

    // Fetch All Available Skills and User Profile (in order)
    const fetchAllSkillsAndUser = async () => {
        try {
            const allSkills = await fetchSkills();
            setSkills(allSkills);

            const userProfile = await fetchUserProfile(username);

            // Map user's skill IDs to skill names AFTER skills are available
            const userSkills = userProfile.skills.map(skillId => {
                const skill = allSkills.find(s => Number(s.id) === Number(skillId));
                return skill || { id: skillId, skillName: "Unknown Skill" };
            });

            setUser({
                ...userProfile,
                skills: userSkills,
            });

            setFormData({
                bio: userProfile.bio || "",
                skills: userProfile.skills || [],
            });

            setSelectedSkills(userProfile.skills);
            setLoading(false);
        } catch (err) {
            console.error("Error fetching data:", err);
            setError("Failed to fetch profile or skills.");
            setLoading(false);
        }
    };

// Fetch data on mount
    useEffect(() => {
        if (username) {
            fetchAllSkillsAndUser();
        }
    }, [username]);

    // Handle Input Changes for Bio
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevFormData => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    // Handle Skill Selection Changes
    const handleSkillChange = (e) => {
        const selectedOptions = [...e.target.selectedOptions].map(option => Number(option.value));
        setSelectedSkills(selectedOptions); // Update selected skills (IDs)
    };

    // Update User Profile
    const handleProfileUpdate = async () => {
        try {
            await updateUserProfile(username, {
                bio: formData.bio,
                skills: selectedSkills, // Array of skill IDs
            });

            // Update the user state with the selected skill names
            setUser(prevUser => ({
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
                                value={selectedSkills} // Ensure selected skills are reflected
                                onChange={handleSkillChange}
                                className="skill-dropdown"
                            >
                                {skills.length > 0 ? (
                                    skills.map(skill => (
                                        <option key={skill.id} value={skill.id}>
                                            {skill.skillName} {/* Display skill name */}
                                        </option>
                                    ))
                                ) : (
                                    <option>No skills available</option> // Handle case where no skills exist
                                )}
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
                        <strong>Skills:</strong>{" "}
                        {user?.skills.length > 0
                            ? user.skills
                                .map(skill => {
                                    return skill ? skill.skillName : "Unknown Skill";
                                })
                                .join(", ")
                            : "No skills added yet"}
                    </p>

                    {/* Show "Edit Profile" button only if logged-in user is viewing their own profile */}
                    {loggedInUsername === username && (
                        <button onClick={() => setIsEditing(true)}>Edit Profile</button>
                    )}
                </div>
            )}
        </div>
    );
};

export default ProfilePage;
