import React, { useState, useEffect } from "react";
import {checkPostOwnership, getPosts} from "../api/PostService.jsx";
import "../styles/common-style.css";
import { useNavigate } from "react-router-dom";
import NavigationButton from "../components/NavigationButton.jsx";

const HomePage = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [filteredPosts, setFilteredPosts] = useState([]);

    const username = localStorage.getItem("username");
    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const fetchedPosts = await getPosts();
                setPosts(fetchedPosts);
                setFilteredPosts(fetchedPosts);
            } catch (err) {
                console.error("Failed to fetch posts:", err);
                setError("Failed to fetch posts. Please try again later.");
            }
        };
        fetchPosts();
    }, []);

    const handleSearchChange = (e) => {
        const query = e.target.value.toLowerCase();
        setSearchQuery(query);

        const filtered = posts.filter((post) =>
            post.title.toLowerCase().includes(query) ||
            post.description.toLowerCase().includes(query)
        );
        setFilteredPosts(filtered);
    };

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

            {/* Search Bar */}
            <div className="search-bar">
                <input
                    type="text"
                    placeholder="Search posts..."
                    value={searchQuery}
                    onChange={handleSearchChange}
                />
            </div>

            <div className="posts-container">
                {filteredPosts.length > 0 ? (
                    filteredPosts.map((post) => (
                        <div
                            key={post.id}
                            className="post-card"
                            onClick={() => handlePostClick(post.id)}
                        >

                            <label className="container">
                                <input type="checkbox"/>
                                <div className="checkmark">
                                    <svg viewBox="0 0 256 256">
                                        <rect fill="none" height="256" width="256"></rect>
                                        <path
                                            d="M224.6,51.9a59.5,59.5,0,0,0-43-19.9,60.5,60.5,0,0,0-44,17.6L128,59.1l-7.5-7.4C97.2,
                                            28.3,59.2,26.3,35.9,47.4a59.9,59.9,0,0,0-2.3,87l83.1,83.1a15.9,15.9,0,0,0,22.6,
                                            0l81-81C243.7,113.2,245.6,75.2,224.6,51.9Z"
                                            strokeWidth="20px" stroke="#FFF" fill="none"></path>
                                    </svg>
                                </div>
                            </label>

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
