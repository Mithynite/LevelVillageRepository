import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import NavigationButton from "../../components/NavigationButton.jsx";
import { fetchUserProfile, updateUserProfile, getUserLikedPosts } from "../../api/UserService.jsx";
import { fetchSkills } from "../../api/SkillService.jsx";

const ProfilePage = () => {
    const { username } = useParams();
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [skills, setSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({ bio: "", skills: [] });
    const [likedPosts, setLikedPosts] = useState([]);
    const [error, setError] = useState(null);

    const loggedInUsername = localStorage.getItem("username");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const allSkills = await fetchSkills();
                setSkills(allSkills);

                const userProfile = await fetchUserProfile(username);

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

                if (username === loggedInUsername) {
                    const likedPostsData = await getUserLikedPosts();
                    setLikedPosts(likedPostsData);
                }

                setSelectedSkills(userProfile.skills);
                setLoading(false);
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to fetch profile or skills.");
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

    const handleSkillChange = (e) => {
        const selectedOptions = [...e.target.selectedOptions].map(option => Number(option.value));
        setSelectedSkills(selectedOptions);
    };

    const handleProfileUpdate = async () => {
        try {
            await updateUserProfile(username, { bio: formData.bio, skills: selectedSkills });

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
                        <label>Skills:
                            <select
                                multiple
                                value={selectedSkills}
                                onChange={handleSkillChange}
                                className="skill-dropdown"
                            >
                                {skills.map(skill => (
                                    <option key={skill.id} value={skill.id}>{skill.skillName}</option>
                                ))}
                            </select>
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
                    <p><strong>Skills:</strong> {user?.skills.map(skill => skill.skillName).join(", ") || "No skills added yet"}</p>

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
