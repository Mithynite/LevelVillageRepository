import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { deletePost, fetchPostById, updatePost } from "../../api/PostService.jsx";
import { fetchUserProfile } from "../../api/UserService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import {fetchSkills} from "../../api/SkillService.jsx";
import {sendChatRequest, chatRequestWasAlreadySent} from "../../api/ChatService.jsx";
import Select from "react-select";

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
    const [chatSent, setChatSent] = useState(false);

    const MAX_TITLE_CHAR_LENGTH = 50;
    const MAX_DESCRIPTION_CHAR_LENGTH = 400;


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
                const chatAlreadySent = await chatRequestWasAlreadySent(fetchedPost.username)
                setChatSent(chatAlreadySent);
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

    const handleSayHello = async () => {
        try {
            const receiverUsername = post.username;

            await sendChatRequest(receiverUsername, post.id);
            setChatSent(true);
        } catch (err) {
            console.error("Failed to send functions request:", err);
            alert("Could not send functions request.");
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="post-details">
            {!isMyPost ? (
                <div className="post-information">
                    <NavigationButton to="/home" label="Back to Home" className="navigation-button"/>

                    <h1>{post.title}</h1>
                    <p>{post.description}</p>
                    <p>
                        <strong>Skills:</strong>{" "}
                        {post?.skills.length
                            ? post.skills.map(skill => skill.skillName).join(", ")
                            : "No skills added yet"}
                    </p>

                    <div className="other-user-box">
                        <button
                            className="say-hello-button"
                            onClick={handleSayHello}
                            disabled={chatSent}
                        >
                            {chatSent ? "Chat request Sent!" : "Say Hello 👋"}
                        </button>

                        <NavigationButton
                            to={`/users/${post.username}/profile`}
                            label={`View ${post.username}'s Profile`}
                            className="navigation-button"
                        />
                    </div>

                    <p>
                        <strong>Published on:</strong>{"  "}
                        {(() => {
                            const date = new Date(post.createdAt);
                            return `${date.getDate()}. ${date.getMonth() + 1}. ${date.getFullYear()}`;
                        })()}
                    </p>
                </div>
            ) : (
                <div className="post-information" style={{justifyContent: "center"}}>
                    <NavigationButton to="/home" label="Back to Home" className="navigation-button"/>

                    {isEditing ? (
                        <div style={{display: "flex", flexDirection: "column", gap: "20px", width: "100%", marginTop: "20px"}}>
                                <div className="form-group">
                                    <strong>Title:</strong>
                                    <input
                                        type="text"
                                        name="title"
                                        value={formData.title}
                                        onChange={handleInputChange}
                                    />
                                    <small style={{color: formData.title.length > MAX_TITLE_CHAR_LENGTH ? 'red' : 'gray'}}>
                                        {formData.title.length}/{MAX_TITLE_CHAR_LENGTH}
                                    </small>
                                </div>
                                <div className="form-group">
                                    <strong>Description:</strong>
                                    <textarea
                                        name="description"
                                        value={formData.description}
                                        onChange={handleInputChange}
                                    />
                                    <small
                                        style={{color: formData.description.length > MAX_DESCRIPTION_CHAR_LENGTH ? 'red' : 'gray'}}>
                                        {formData.description.length}/{MAX_DESCRIPTION_CHAR_LENGTH}
                                    </small>
                                </div>
                                <div className="form-group" style={{
                                    display: "flex",
                                    flexDirection: "column",
                                    alignItems: "flex-start",
                                    gap: "0.5rem"
                                }}>
                                    <label htmlFor="edit-skills">Related Skills (select up to 5):</label>
                                    <div style={{width: "100%"}}>
                                        <Select
                                            inputId="edit-skills"
                                            isMulti
                                            options={skills.map(skill => ({
                                                value: skill.id,
                                                label: skill.skillName,
                                            }))}
                                            value={skills
                                                .filter(skill => selectedSkills.includes(skill.id))
                                                .map(skill => ({value: skill.id, label: skill.skillName}))}
                                            onChange={(selectedOptions) => {
                                                if (selectedOptions.length <= 5) {
                                                    setSelectedSkills(selectedOptions.map(option => option.value));
                                                } else {
                                                    alert("You can select up to 5 skills only.");
                                                }
                                            }}
                                            classNamePrefix="react-select"
                                            styles={{
                                                control: (base) => ({
                                                    ...base,
                                                    backgroundColor: "rgba(30, 27, 22, 1)",
                                                    border: "2px solid goldenrod",
                                                    color: "white",
                                                    borderRadius: "5px",
                                                    padding: "5px",
                                                    boxShadow: "none",
                                                    "&:hover": {
                                                        borderColor: "white",
                                                    },
                                                }),
                                                menu: (base) => ({
                                                    ...base,
                                                    backgroundColor: "rgba(30, 27, 22, 1)",
                                                    border: "1px solid goldenrod",
                                                    zIndex: 20,
                                                }),
                                                option: (base, state) => ({
                                                    ...base,
                                                    backgroundColor: state.isFocused
                                                        ? "rgba(255, 215, 0, 0.2)"
                                                        : "rgba(30, 27, 22, 1)",
                                                    color: state.isSelected ? "goldenrod" : "white",
                                                    "&:hover": {
                                                        backgroundColor: "rgba(255, 215, 0, 0.5)",
                                                    },
                                                }),
                                                multiValue: (base) => ({
                                                    ...base,
                                                    backgroundColor: "goldenrod",
                                                    color: "black",
                                                    borderRadius: "4px",
                                                }),
                                                multiValueLabel: (base) => ({
                                                    ...base,
                                                    color: "black",
                                                }),
                                                multiValueRemove: (base) => ({
                                                    ...base,
                                                    color: "black",
                                                    "&:hover": {
                                                        backgroundColor: "rgba(255, 215, 0, 0.5)",
                                                        color: "white",
                                                    },
                                                }),
                                            }}
                                        />
                                    </div>
                                </div>
                                <div style={{display: "flex", gap: "10px"}}>
                                    <button
                                        onClick={handlePostUpdate}
                                        disabled={
                                            formData.title.length > MAX_TITLE_CHAR_LENGTH ||
                                            formData.description.length > MAX_DESCRIPTION_CHAR_LENGTH
                                        }
                                        style={{
                                            backgroundColor:
                                                formData.title.length > MAX_TITLE_CHAR_LENGTH ||
                                                formData.description.length > MAX_DESCRIPTION_CHAR_LENGTH
                                                    ? 'gray'
                                                    : '',
                                            cursor:
                                                formData.title.length > MAX_TITLE_CHAR_LENGTH ||
                                                formData.description.length > MAX_DESCRIPTION_CHAR_LENGTH
                                                    ? 'not-allowed'
                                                    : ''
                                        }}
                                        className="form-button">
                                        Save Changes
                                    </button>

                                    <button onClick={() => setIsEditing(false)} className="form-button">Cancel</button>
                                </div>
                            </div>
                        ) : (
                            <>
                                <h1>{post.title}</h1>
                                <p>{post.description}</p>
                                <p>
                                    <strong>Skills:</strong>{" "}
                                    {post?.skills.length
                                        ? post.skills.map(skill => skill.skillName).join(", ")
                                        : "No skills added yet"}
                                </p>
                                <p>
                                    <strong>Published on:</strong>{"  "}
                                    {(() => {
                                        const date = new Date(post.createdAt);
                                        return `${date.getDate()}. ${date.getMonth() + 1}. ${date.getFullYear()}`;
                                    })()}
                                </p>

                                <div style={{display: "flex", gap: "10px", marginTop: "10px"}}>
                                    <button onClick={() => setIsEditing(true)} className="form-button">Edit</button>
                                    <button onClick={handlePostDelete} className="form-button">Delete</button>
                                </div>
                            </>
                        )}
                </div>
            )}
        </div>
    );
};

export default PostDetails;
