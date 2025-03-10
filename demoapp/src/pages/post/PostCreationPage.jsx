import React, { useState, useEffect } from "react";
import { createPost } from "../../api/PostService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";

const PostCreationPage = () => {
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        username: "",
        date: "",
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    // Fetch username from localStorage on component mount
    useEffect(() => {
        const storedUsername = localStorage.getItem("username");
        if (storedUsername) {
            setFormData((prevFormData) => ({
                ...prevFormData,
                username: storedUsername,
            }));
        }
    }, []);

    // Handle Input Changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    // Validate Input
    const validateForm = () => {
        if (!formData.title.trim()) {
            setError("Title cannot be empty.");
            return false;
        }
        if (!formData.description.trim()) {
            setError("Description cannot be empty.");
            return false;
        }
        return true;
    };

    // Handle Post Creation
    const handlePostCreation = async () => {
        if (!validateForm()) return; // Prevent submission if validation fails

        setLoading(true);
        setError(null);

        try {
            const newPost = {
                title: formData.title,
                description: formData.description,
                user: { username: formData.username },
                date: new Date().toISOString(),
            };

            await createPost(newPost);
            setSuccess(true);

            // Clear form after successful submission but keep username
            setFormData({
                title: "",
                description: "",
                username: formData.username,
                date: "",
            });
        } catch (err) {
            console.error("Error while creating post:", err);
            setError("Failed to create post. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <p>Creating your post...</p>;

    return (
        <div className="post-creation">
            <NavigationButton to="/home" label="Go Back to Home" className="form-button"/>
            <h1>Create a New Post</h1>

            {error && <p className="error">{error}</p>}
            {success && <p className="success">Post created successfully!</p>}

            <form onSubmit={(e) => e.preventDefault()} className="post-form">
                <label>
                    Title:
                    <input
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleInputChange}
                        required
                    />
                </label>

                <label>
                    Description:
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleInputChange}
                        required
                    ></textarea>
                </label>

                <button type="button" onClick={handlePostCreation} className="form-button">
                    Create Post
                </button>
            </form>
        </div>
    );
};

export default PostCreationPage;
