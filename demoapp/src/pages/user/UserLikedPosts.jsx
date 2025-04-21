import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/common-style.css";
import { updateUserLikedPosts, getUserLikedPosts } from "../../api/UserService.jsx";
import PostCard from "../../components/PostCard.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";

const UserLikedPosts = () => {
    const [likedPosts, setLikedPosts] = useState([]);
    const [likedPostIds, setLikedPostIds] = useState(new Set());
    const [error, setError] = useState(null);
    const [searchQuery, setSearchQuery] = useState("");
    const [filteredPosts, setFilteredPosts] = useState([]);
    const navigate = useNavigate();
    const username = localStorage.getItem("username");

    useEffect(() => {
        const fetchLikedPosts = async () => {
            try {
                const likedPostsData = await getUserLikedPosts();
                setLikedPosts(likedPostsData);
                setFilteredPosts(likedPostsData);
                setLikedPostIds(new Set(likedPostsData.map(post => post.id)));
            } catch (err) {
                console.error("Error fetching liked posts:", err);
                setError("Failed to fetch liked posts.");
            }
        };
        fetchLikedPosts();
    }, []);

    // Handle search functionality
    const handleSearchChange = (e) => {
        const query = e.target.value.toLowerCase();
        setSearchQuery(query);

        const filtered = likedPosts.filter(
            (post) =>
                post.title.toLowerCase().includes(query) ||
                post.description.toLowerCase().includes(query)
        );
        setFilteredPosts(filtered);
    };

    const handlePostClick = (postId) => {
        navigate(`/posts/${postId}`);
    };

    const handlePostLike = async (postId) => {
        try {
            const updatedLikedPostIds = new Set(likedPostIds);
            if (updatedLikedPostIds.has(postId)) {
                updatedLikedPostIds.delete(postId); // Unlike
            } else {
                updatedLikedPostIds.add(postId); // Like
            }
            setLikedPostIds(updatedLikedPostIds); // Update UI optimistically
            await updateUserLikedPosts(Array.from(updatedLikedPostIds)); // Sync with backend

            // Remove post from UI if unliked
            setLikedPosts(likedPosts.filter(post => updatedLikedPostIds.has(post.id)));
            setFilteredPosts(filteredPosts.filter(post => updatedLikedPostIds.has(post.id)));
        } catch (error) {
            console.error("Failed to update liked posts:", error);
            alert("An error occurred while updating likes.");
        }
    };

    return (
        <div className="dashboard">
            {/* Navigation Button */}
            {username && (
                <NavigationButton
                    to={`/users/${username}/profile`}
                    label="Go back"
                    className="profile-button"
                />
            )}

            <h1>Liked Posts</h1>

            {error && <p className="error-message">{error}</p>}

            {/* Search Bar for Liked Posts */}
            <div className="search-bar">
                <input
                    type="text"
                    placeholder="Search liked posts..."
                    value={searchQuery}
                    onChange={handleSearchChange}
                />
            </div>

            {/* Posts Container (Ensures consistent styling) */}
            <div className="posts-container">
                {filteredPosts.length === 0 ? (
                    <p>You haven't liked any posts yet.</p>
                ) : (
                    filteredPosts.map(post => (
                        <PostCard
                            key={post.id}
                            post={post}
                            likedPostIds={likedPostIds}
                            handlePostClick={handlePostClick}
                            handlePostLike={handlePostLike}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default UserLikedPosts;
