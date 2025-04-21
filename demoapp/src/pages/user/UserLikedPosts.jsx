import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/common-style.css";
import { updateUserLikedPosts, getUserLikedPosts } from "../../api/UserService.jsx";
import PostCard from "../../components/PostCard.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import LVIcon from "../../assets/icon-components/LVIcon.jsx";
import PostSearchFilter from "../../components/PostSearchFilter.jsx";
import { fetchSkills } from "../../api/SkillService.jsx";

const UserLikedPosts = () => {
    const [likedPosts, setLikedPosts] = useState([]);
    const [filteredPosts, setFilteredPosts] = useState([]);
    const [likedPostIds, setLikedPostIds] = useState(new Set());
    const [searchQuery, setSearchQuery] = useState("");
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [dateOrder, setDateOrder] = useState("recent");
    const [allSkills, setAllSkills] = useState([]);
    const [error, setError] = useState(null);

    const navigate = useNavigate();
    const username = localStorage.getItem("username");

    useEffect(() => {
        const fetchLikedPosts = async () => {
            try {
                const [likedPostsData, globalSkills] = await Promise.all([
                    getUserLikedPosts(),
                    fetchSkills()
                ]);

                // Enrich each post with full skill objects from global skill list
                const enrichedPosts = likedPostsData.map(post => {
                    const fullSkills = post.skills.map(skillId => {
                        const matched = globalSkills.find(skill => Number(skill.id) === Number(skillId));
                        return matched || { id: skillId, skillName: "Unknown Skill" };
                    });
                    return { ...post, skills: fullSkills };
                });

                setAllSkills(globalSkills);
                setLikedPosts(enrichedPosts);
                setFilteredPosts(enrichedPosts);
                setLikedPostIds(new Set(enrichedPosts.map(post => post.id)));
            } catch (err) {
                console.error("Error fetching liked posts or skills:", err);
                setError("Failed to fetch liked posts.");
            }
        };

        fetchLikedPosts();
    }, []);



    // Filtering logic applied on any change
    useEffect(() => {
        let result = [...likedPosts];

        // Search
        if (searchQuery) {
            result = result.filter(
                post =>
                    post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    post.description.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }

        // Skill filter
        if (selectedSkills.length > 0) {
            result = result.filter(post =>
                selectedSkills.every(skillId =>
                    post.skills.some(skill => skill.id === skillId)
                )
            );
        }

        // Date sort
        result.sort((a, b) => {
            const dateA = new Date(a.createdAt);
            const dateB = new Date(b.createdAt);
            return dateOrder === "recent" ? dateB - dateA : dateA - dateB;
        });

        setFilteredPosts(result);
    }, [searchQuery, selectedSkills, dateOrder, likedPosts]);

    const handleSearchChange = (e) => {
        setSearchQuery(e.target.value);
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

            setLikedPostIds(updatedLikedPostIds); // Optimistic update
            await updateUserLikedPosts(Array.from(updatedLikedPostIds));

            const newLiked = likedPosts.filter(post => updatedLikedPostIds.has(post.id));
            setLikedPosts(newLiked);
        } catch (error) {
            console.error("Failed to update liked posts:", error);
            alert("An error occurred while updating likes.");
        }
    };

    return (
        <div className="dashboard">
            {username && (
                <NavigationButton
                    to={`/users/${username}/profile`}
                    label="Go back"
                    className="profile-button"
                />
            )}

            <h1>Liked Posts</h1>
            {error && <p className="error-message">{error}</p>}

            <div className="search-filter-container">
                <LVIcon />
                <PostSearchFilter
                    searchQuery={searchQuery}
                    onSearchChange={handleSearchChange}
                    selectedSkills={selectedSkills}
                    onSkillsChange={setSelectedSkills}
                    dateOrder={dateOrder}
                    onDateOrderChange={setDateOrder}
                    allSkills={allSkills}
                />
            </div>

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
