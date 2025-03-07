import React, { useState, useEffect } from "react";
import {checkPostOwnership, getPosts} from "../api/PostService.jsx";
import "../styles/common-style.css";
import { useNavigate } from "react-router-dom";
import NavigationButton from "../components/NavigationButton.jsx";

const HomePage = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const username = localStorage.getItem("username");
    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const fetchedPosts = await getPosts();
                console.log(fetchedPosts);
                setPosts(fetchedPosts);
            } catch (err) {
                console.error("Failed to fetch posts:", err);
                setError("Failed to fetch posts. Please try again later.");
            }
        };
        fetchPosts();
    }, []);

    // Handle click on a post card
    const handlePostClick = async (postId) => {
        try {
            // Check ownership via the backend
            const ownershipStatus = await checkPostOwnership(postId);

            if (ownershipStatus === "owner") {
                // Navigate to mypost page if the logged-in user is the owner
                navigate(`/myposts/${postId}`);
            } else {
                // Navigate to public post page otherwise
                navigate(`/posts/${postId}`);
            }
        } catch (error) {
            console.error("Failed to check ownership:", error);
            alert("An error occurred. Please try again later.");
        }
    };


    const handlePostCreation = () => {
        navigate("/posts/create");
    }

    return (
        <div className="dashboard">
            {error && <p className="error-message">{error}</p>}

            <div className="posts-container">
                {posts.length > 0 ? (
                    posts.map((post) => (
                        <div
                            key={post.id}
                            className="post-card"
                            onClick={() => handlePostClick(post.id)}
                        >
                            <p>{post.user.username}</p>
                            <h2>{post.title}</h2>
                            <p>{post.description}</p>
                            <small>
                                Created at: {new Date(post.created_at).toLocaleString()}
                            </small>
                        </div>
                    ))
                ) : (
                    <p>No posts available</p>
                )}
            </div>
                <button className="plus-button" onClick={handlePostCreation}>
                    <div className="plus-button-sign">+</div>
                    <div className="plus-button-text">Create</div>
                </button>
            {/* Profile Navigation Button */}
            {username && (
                <NavigationButton to={`/users/${username}/profile`}
                                  label="My Profile"
                                  className="profile-button"/>
            )}
        </div>
    );
};

export default HomePage;
