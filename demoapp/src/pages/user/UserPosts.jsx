import React, { useState, useEffect } from "react";
import {useNavigate} from "react-router-dom";
import {getUserLikedPosts, getUserPosts} from "../../api/UserService.jsx";
import {fetchSkills} from "../../api/SkillService.jsx";
import PostSearchFilter from "../../components/PostSearchFilter.jsx";
import LVIcon from "../../assets/icon-components/LVIcon.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import PostCard from "../../components/PostCard.jsx";

const UserPosts = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [filteredPosts, setFilteredPosts] = useState([]);
    const [likedPostIds, setLikedPostIds] = useState(new Set());

    const [allSkills, setAllSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [dateOrder, setDateOrder] = useState("recent");

    const username = localStorage.getItem("username");

    // Fetch posts, skills, likes, chat requests
    useEffect(() => {
        const fetchPostsAndSkills = async () => {
            try {
                const [fetchedPosts, userLikedPosts, allSkills] = await Promise.all([
                    getUserPosts(),
                    getUserLikedPosts(),
                    fetchSkills(),
                ]);

                setAllSkills(allSkills); // Save all skills for the dropdown
                const enrichedPosts = fetchedPosts.map(post => {
                    const fullSkills = post.skills.map(skillId => {
                        const matched = allSkills.find(skill => Number(skill.id) === Number(skillId));
                        return matched || { id: skillId, skillName: "Unknown Skill" };
                    });
                    return { ...post, skills: fullSkills };
                });

                setPosts(enrichedPosts);
                setFilteredPosts(enrichedPosts);
                setLikedPostIds(new Set(userLikedPosts.map(post => post.id)));
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to fetch data. Please try again later.");
            }
        };

        fetchPostsAndSkills();
    }, []);

    // Handle post search
    const handleSearchChange = (e) => {
        const query = e.target.value.toLowerCase();
        setSearchQuery(query);
    };

    useEffect(() => {
        let result = posts;

        // Search
        if (searchQuery) {
            result = result.filter(
                (post) =>
                    post.title.toLowerCase().includes(searchQuery) ||
                    post.description.toLowerCase().includes(searchQuery)
            );
        }

        // Skills filter
        if (selectedSkills.length > 0) {
            result = result.filter((post) =>
                post.skills.some(skill => selectedSkills.includes(skill.id))
            );
        }

        // Date sort
        result = result.sort((a, b) => {
            const dateA = new Date(a.date);
            const dateB = new Date(b.date);
            return dateOrder === "recent" ? dateB - dateA : dateA - dateB;
        });

        setFilteredPosts(result);
    }, [searchQuery, selectedSkills, dateOrder, posts]);

    const handlePostClick = async (postId) => {
        try {
            navigate(`/myposts/${postId}`);
        } catch (error) {
            console.error("Failed to check ownership:", error);
            alert("An error occurred. Please try again later.");
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
            {error && <p className="error-message">{error}</p>}
            <div className="search-filter-container">
                <LVIcon/>
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
                {filteredPosts.length > 0 ? (
                    filteredPosts.map((post) => (
                        <PostCard
                            key={post.id}
                            post={post}
                            likedPostIds={likedPostIds}
                            handlePostClick={handlePostClick}
                            showLikes={false}
                        />
                    ))
                ) : (
                    <p>No posts available</p>
                )}
            </div>
        </div>
    );
};

export default UserPosts;
