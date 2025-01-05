import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { deletePost, fetchPostById, updatePost } from "../../api/PostService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";

const PostDetails = ({ isMyPost }) => {
    const { id } = useParams(); // Extract the dynamic parameter from the URL
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [post, setPost] = useState(null);
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
        } catch (err) {
            console.error("Error fetching post:", err);
            setError("Failed to fetch post data.");
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

    useEffect(() => {
        fetchPost();
    }, [id]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="post-details">
            <NavigationButton to="/home" label="Go Back to Home" />
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
                        <div>
                            <h1>{post.title}</h1>
                            <p>{post.description}</p>
                            <button onClick={() => setIsEditing(true)}>Edit</button>
                            <button onClick={handlePostDelete}>Delete</button>
                        </div>
                    )}
                </div>
            ) : (
                <div>
                    <h1>{post.title}</h1>
                    <p>{post.description}</p>
                </div>
            )}
            <div>
                <p><strong>Published on:</strong> {new Date(post.created_at).toLocaleDateString()}</p>
            </div>
        </div>
    );
};

export default PostDetails;
