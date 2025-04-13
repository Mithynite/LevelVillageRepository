import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { deletePost, fetchPostById, updatePost } from "../../api/PostService.jsx";
import { fetchUserProfile } from "../../api/UserService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import { FaDiscord, FaInstagram, FaLinkedin } from "react-icons/fa";
import {fetchSkills} from "../../api/SkillService.jsx";

const PostDetails = ({ isMyPost }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [post, setPost] = useState(null);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({ title: "", description: "" });
    const [error, setError] = useState(null);
    const [skills, setSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const fetchedPost = await fetchPostById(id);
                const allSkills = await fetchSkills();

                // Normalize skills: match IDs to full skill objects
                const fullSkillObjects = fetchedPost.skills.map(skillId => {
                    const skill = allSkills.find(s => Number(s.id) === Number(skillId));
                    return skill || { id: skillId, skillName: "Unknown Skill" };
                });

                setSelectedSkills(fullSkillObjects.map(skill => skill.id));
                setPost({ ...fetchedPost, skills: fullSkillObjects });
                setSkills(allSkills);
                setFormData({ title: fetchedPost.title, description: fetchedPost.description });

                if (fetchedPost.username) {
                    fetchUserProfileData(fetchedPost.username);
                }
            } catch (err) {
                console.error("Error fetching post:", err);
                setError("Failed to fetch post data.");
            } finally {
                setLoading(false);
            }
        };

        fetchPost();
    }, [id]);


    const fetchUserProfileData = async (username) => {
        try {
            const userProfile = await fetchUserProfile(username);
            setUser(userProfile);
        } catch (err) {
            console.error("Error fetching user profile:", err);
            setUser(null);
        }
    };

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handlePostUpdate = async () => {
        try {
            const updatedData = {
                title: formData.title,
                description: formData.description,
                skills: selectedSkills,
            };

            await updatePost(id, updatedData);
            setPost({ ...post, ...updatedData, skills: selectedSkills.map(id => skills.find(s => s.id === id)) }); // Update local display too
            setIsEditing(false);
        } catch (err) {
            console.error("Error updating post:", err);
            setError("Failed to update post.");
        }
    };


    const handlePostDelete = async () => {
        try {
            await deletePost(id);
            navigate("/home");
        } catch (err) {
            console.error("Error deleting post:", err);
            setError("Failed to delete post.");
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="post-details">
            <div className="post-container" style={{ justifyContent: isMyPost ? "center" : "flex-start" }}>
                {!isMyPost && user && (
                    <div className="user-information">
                        <NavigationButton to="/home" label="Back to Home" className="navigation-button" />
                        <h1>{user.username}</h1>
                        <p>{user?.bio || "No bio available"}</p>
                        <div className="contact-info">
                            <h3>Contact:</h3>
                            <ul className="contact-list">
                                {user.discord && (
                                    <li className="contact-item discord">
                                        <FaDiscord className="contact-icon" /> <span>{user.discord}</span>
                                    </li>
                                )}
                                {user.instagram && (
                                    <li className="contact-item instagram">
                                        <FaInstagram className="contact-icon" />
                                        <a href={user.instagram} target="_blank" rel="noopener noreferrer">{user.instagram}</a>
                                    </li>
                                )}
                                {user.linkedIn && (
                                    <li className="contact-item linkedin">
                                        <FaLinkedin className="contact-icon" />
                                        <a href={user.linkedIn} target="_blank" rel="noopener noreferrer">LinkedIn</a>
                                    </li>
                                )}
                            </ul>
                        </div>
                    </div>
                )}

                <div className="post-information">
                    {isMyPost && (
                        <NavigationButton to="/home" label="Back to Home" className="navigation-button" />
                    )}
                    {isMyPost && isEditing ? (
                        <div style={{display: "flex", flexDirection: "column", gap: "15px"}}>
                            <input type="text" name="title" value={formData.title} onChange={handleInputChange}/>
                            <textarea name="description" value={formData.description}
                                      onChange={handleInputChange}></textarea>

                            <label>
                                Related Skills (select up to 5):
                                <select
                                    multiple
                                    value={selectedSkills}
                                    onChange={(e) => {
                                        const selected = Array.from(e.target.selectedOptions, opt => Number(opt.value));
                                        setSelectedSkills(selected);
                                    }}
                                    className="skill-dropdown"
                                >
                                    {skills.map(skill => (
                                        <option key={skill.id} value={skill.id}>
                                            {skill.skillName}
                                        </option>
                                    ))}
                                </select>
                            </label>


                            <div style={{display: "flex", gap: "10px"}}>
                                <button onClick={handlePostUpdate}>Save Changes</button>
                                <button onClick={() => setIsEditing(false)}>Cancel</button>
                            </div>
                        </div>
                    ) : (
                        <div>
                            <h1>{post.title}</h1>
                            <p>{post.description}</p>
                            <p>
                                <strong>Skills:</strong>{" "}
                                {post?.skills.length
                                    ? post.skills.map(skill => skill.skillName).join(", ")
                                    : "No skills added yet"}
                            </p>
                            {isMyPost && (
                                <div style={{display: "flex", gap: "10px", marginTop: "10px"}}>
                                <button onClick={() => setIsEditing(true)}>Edit</button>
                                    <button onClick={handlePostDelete}>Delete</button>
                                </div>
                            )}
                        </div>
                    )}
                    <p><strong>Published on:</strong> {new Date(post.createdAt).toLocaleDateString()}</p>
                </div>
            </div>
        </div>
    );
};

export default PostDetails;
