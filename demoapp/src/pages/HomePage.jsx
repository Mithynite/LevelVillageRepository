import React, { useState, useEffect } from "react";
import "../styles/common-style.css";
import { checkPostOwnership, getPosts } from "../api/PostService.jsx";
import { useNavigate } from "react-router-dom";
import { updateUserLikedPosts, getUserLikedPosts } from "../api/UserService.jsx";
import NavigationButton from "../components/NavigationButton.jsx";
import PostCard from "../components/PostCard.jsx";

const HomePage = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [filteredPosts, setFilteredPosts] = useState([]);
    const [likedPostIds, setLikedPostIds] = useState(new Set());

    const username = localStorage.getItem("username");

    // Fetch posts and liked posts from the backend
    useEffect(() => {
        const fetchPostsAndLikes = async () => {
            try {
                const [fetchedPosts, userLikedPosts] = await Promise.all([
                    getPosts(),
                    getUserLikedPosts(),
                ]);
                setPosts(fetchedPosts);
                setFilteredPosts(fetchedPosts);
                setLikedPostIds(new Set(userLikedPosts.map(post => post.id)));
            } catch (err) {
                console.error("Error fetching posts or likes:", err);
                setError("Failed to fetch data. Please try again later.");
            }
        };
        fetchPostsAndLikes();
    }, []);

    // Handle post search
    const handleSearchChange = (e) => {
        const query = e.target.value.toLowerCase();
        setSearchQuery(query);

        const filtered = posts.filter(
            (post) =>
                post.title.toLowerCase().includes(query) ||
                post.description.toLowerCase().includes(query)
        );
        setFilteredPosts(filtered);
    };

    // Navigate to correct post page based on ownership
    const handlePostClick = async (postId) => {
        try {
            const ownershipStatus = await checkPostOwnership(postId);
            navigate(ownershipStatus === "owner" ? `/myposts/${postId}` : `/posts/${postId}`);
        } catch (error) {
            console.error("Failed to check ownership:", error);
            alert("An error occurred. Please try again later.");
        }
    };

    // Handle post creation
    const handlePostCreation = () => navigate("/posts/create");

    // Handle like/unlike a post
    const handlePostLike = async (postId) => {
        try {
            let updatedLikedPosts = new Set(likedPostIds);
            if (updatedLikedPosts.has(postId)) {
                updatedLikedPosts.delete(postId); // Unlike
            } else {
                updatedLikedPosts.add(postId); // Like
            }
            setLikedPostIds(new Set(updatedLikedPosts)); // Update UI optimistically

            // Sync the change with the backend
            await updateUserLikedPosts(Array.from(updatedLikedPosts));
        } catch (error) {
            console.error("Failed to update liked posts:", error);
            alert("An error occurred while updating likes.");
        }
    };

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

            {/* Posts Container */}
            <div className="posts-container">
                {filteredPosts.length > 0 ? (
                    filteredPosts.map((post) => (
                        <PostCard
                            key={post.id}
                            post={post}
                            likedPostIds={likedPostIds}
                            handlePostClick={handlePostClick}
                            handlePostLike={handlePostLike}
                        />
                    ))
                ) : (
                    <p>No posts available</p>
                )}
            </div>

            {/* Create Post Button */}
            <button className="plus-button" onClick={handlePostCreation}>
                <div className="plus-button-sign">+</div>
                <div className="plus-button-text">Create</div>
            </button>

            {/* Profile Navigation Button */}
            {username && (
                <NavigationButton
                    to={`/users/${username}/profile`}
                    label="My Profile"
                    className="profile-button"
                />
            )}
        </div>
    );
};

export default HomePage;
