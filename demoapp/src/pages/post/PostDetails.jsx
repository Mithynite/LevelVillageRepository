import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { deletePost, fetchPostById, updatePost } from "../../api/PostService.jsx";
import { fetchUserProfile } from "../../api/UserService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import { FaDiscord, FaInstagram, FaLinkedin } from "react-icons/fa";

const PostDetails = ({ isMyPost }) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [post, setPost] = useState(null);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        title: "",
        description: "",
    });
    const [error, setError] = useState(null);

    // Fetch Post
    const fetchPost = async () => {
        try {
            const fetchedPost = await fetchPostById(id);
            setPost(fetchedPost);
            setFormData({
                title: fetchedPost.title,
                description: fetchedPost.description,
            });

            // Fetch User Profile if post contains username
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

    // Fetch User Profile
    const fetchUserProfileData = async (username) => {
        try {
            const userProfile = await fetchUserProfile(username);
            setUser({
                profilePicture: userProfile.profilePicture,
                username: userProfile.username,
                bio: userProfile.bio,
                skills: userProfile.skills,
                discord: userProfile.discord,
                instagram: userProfile.instagram,
                linkedIn: userProfile.linkedIn,
            });
        } catch (err) {
            console.error("Error fetching user profile:", err);
            setUser(null);
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

    // Update Post
    const handlePostUpdate = async () => {
        try {
            await updatePost(id, formData);
            setPost((prevPost) => ({ ...prevPost, ...formData }));
            setIsEditing(false);
        } catch (err) {
            console.error("Error updating post:", err);
            setError("Failed to update post.");
        }
    };

    // Delete Post
    const handlePostDelete = async () => {
        try {
            await deletePost(id);
            navigate("/home");
        } catch (err) {
            console.error("Error deleting post:", err);
            setError("Failed to delete post.");
        }
    };

    // Fetch Post Data when the component mounts or the post ID changes
    useEffect(() => {
        fetchPost();
    }, [id]);

    // Ensure user is fetched after post data is available
    useEffect(() => {
        if (post && post.username) {
            fetchUserProfileData(post.username);
        }
    }, [post]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="post-details">
            {isMyPost ? (
                <div>
                    {isEditing ? (
                        <div>
                            <input
                                type="text"
                                name="title"
                                value={formData.title}
                                onChange={handleInputChange}
                            />
                            <textarea
                                name="description"
                                value={formData.description}
                                onChange={handleInputChange}
                            ></textarea>
                            <button onClick={handlePostUpdate}>Save Changes</button>
                            <button onClick={() => setIsEditing(false)}>Cancel</button>
                        </div>
                    ) : (
                        <div className="post-information">
                            <h1>{post.title}</h1>
                            <p>{post.description}</p>
                            <button onClick={() => setIsEditing(true)}>Edit</button>
                            <button onClick={handlePostDelete}>Delete</button>
                        </div>
                    )}
                    <div>
                        <p><strong>Published on:</strong> {new Date(post.createdAt).toLocaleDateString()}</p>
                    </div>
                </div>

            ) : (
                <div className="foreign-post">
                    <div className="user-information">
                        <NavigationButton to="/home" label="Go Back to Home" className="navigation-button"/>
                    <h1>{user ? user.username : "Loading user..."}</h1>
                        {/* Ensure 'user' exists before accessing its properties */}
                        <p>{user && user.bio ? user.bio : "No bio available"}</p>
                        {/* Contact Information */}
                        <div className="contact-info">
                            <h3>Contact:</h3>
                            {user ? (
                                <ul className="contact-list">
                                    {user.discord && (
                                        <li className="contact-item discord">
                                            <FaDiscord className="contact-icon"/> <span>{user.discord}</span>
                                        </li>
                                    )}
                                    {user.instagram && (
                                        <li className="contact-item instagram">
                                            <FaInstagram className="contact-icon"/>
                                            <a href={user.instagram} target="_blank" rel="noopener noreferrer">
                                                {user.instagram}
                                            </a>
                                        </li>
                                    )}
                                    {user.linkedIn && (
                                        <li className="contact-item linkedin">
                                            <FaLinkedin className="contact-icon"/>
                                            <a href={user.linkedIn} target="_blank" rel="noopener noreferrer">
                                                LinkedIn Profile
                                            </a>
                                        </li>
                                    )}
                                </ul>
                            ) : (
                                <p>No contact details available.</p>
                            )}
                        </div>
                    </div>

                    <div className="post-information">
                        <h1>{post.title}</h1>
                        <p>{post.description}</p>
                        <div>
                            <p><strong>Published on:</strong> {new Date(post.createdAt).toLocaleDateString()}</p>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PostDetails;
