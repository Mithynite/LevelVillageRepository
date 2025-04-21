import React, { useState, useEffect } from "react";
import "../styles/common-style.css";
import { checkPostOwnership, getPosts } from "../api/PostService.jsx";
import { useNavigate } from "react-router-dom";
import { updateUserLikedPosts, getUserLikedPosts } from "../api/UserService.jsx";
import NavigationButton from "../components/NavigationButton.jsx";
import PostCard from "../components/PostCard.jsx";
import LVIcon from "../assets/icon-components/LVIcon.jsx";
import { getMyIncomingChatRequests } from "../api/ChatService.jsx";
import { fetchSkills } from "../api/SkillService.jsx";
import { FaBell } from "react-icons/fa";
import Select from "react-select"; // Add this at the top

const Home = () => {
    const [posts, setPosts] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [filteredPosts, setFilteredPosts] = useState([]);
    const [likedPostIds, setLikedPostIds] = useState(new Set());
    const [chatRequests, setChatRequests] = useState([]);

    const [allSkills, setAllSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);
    const [dateOrder, setDateOrder] = useState("recent");

    const username = localStorage.getItem("username");

    // Fetch posts, skills, likes, chat requests
    useEffect(() => {
        const fetchPostsAndSkills = async () => {
            try {
                const [fetchedPosts, userLikedPosts, pendingRequests, allSkills] = await Promise.all([
                    getPosts(),
                    getUserLikedPosts(),
                    getMyIncomingChatRequests(),
                    fetchSkills(),
                ]);

                setAllSkills(allSkills); // Save all skills for the dropdown

                // Attach full skill objects to each post
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
                setChatRequests(pendingRequests);
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
            const ownershipStatus = await checkPostOwnership(postId);
            navigate(ownershipStatus === "owner" ? `/myposts/${postId}` : `/posts/${postId}`);
        } catch (error) {
            console.error("Failed to check ownership:", error);
            alert("An error occurred. Please try again later.");
        }
    };

    const handlePostCreation = () => navigate("/posts/create");

    const handlePostLike = async (postId) => {
        try {
            let updatedLikedPosts = new Set(likedPostIds);
            if (updatedLikedPosts.has(postId)) {
                updatedLikedPosts.delete(postId);
            } else {
                updatedLikedPosts.add(postId);
            }
            setLikedPostIds(new Set(updatedLikedPosts));
            await updateUserLikedPosts(Array.from(updatedLikedPosts));
        } catch (error) {
            console.error("Failed to update liked posts:", error);
            alert("An error occurred while updating likes.");
        }
    };

    return (
        <div className="dashboard">
            {error && <p className="error-message">{error}</p>}

            <div className="search-filter-container">
                <div className="search-bar">
                    <input
                        type="text"
                        placeholder="Search posts..."
                        value={searchQuery}
                        onChange={handleSearchChange}
                    />
                </div>

                    <div className="filters-bar">
                        <div className="filter-group">
                            <label htmlFor="skillFilter">Filter by Skills:</label>
                            <Select
                                isMulti
                                options={allSkills.map(skill => ({
                                    value: skill.id,
                                    label: skill.skillName,
                                }))}
                                value={allSkills
                                    .filter(skill => selectedSkills.includes(skill.id))
                                    .map(skill => ({value: skill.id, label: skill.skillName}))}
                                onChange={(selectedOptions) =>
                                    setSelectedSkills(selectedOptions.map(option => option.value))
                                }
                                className="skill-select-dropdown"
                                classNamePrefix="react-select"
                                styles={{
                                    control: (base) => ({
                                        ...base,
                                        minWidth: "200px",
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

                        <div className="filter-group">
                            <label htmlFor="dateOrder">Sort by Date:</label>
                            <Select
                                options={[
                                    { value: "recent", label: "Most Recent" },
                                    { value: "oldest", label: "Oldest First" }
                                ]}
                                value={{ value: dateOrder, label: dateOrder === "recent" ? "Most Recent" : "Oldest First" }}
                                onChange={(selectedOption) => setDateOrder(selectedOption.value)}
                                className="date-select-dropdown"
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
                                    singleValue: (base) => ({
                                        ...base,
                                        color: "white",
                                    }),
                                }}
                            />

                        </div>
                    </div>

            </div>

            <NavigationButton
                to={`/users/${username}/profile`}
                label="My Profile"
                className="profile-button"
            />

            <NavigationButton
                to={`/users/${username}/notifications`}
                className="notification-icon-wrapper"
                label=""
            >
                <FaBell size={28} className="notification-icon"/>
                {chatRequests.length > 0 && (
                    <span className="notification-badge">{chatRequests.length}</span>
                )}
            </NavigationButton>

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

            <button className="plus-button" onClick={handlePostCreation}>
                <div className="plus-button-sign">+</div>
                <div className="plus-button-text">Create</div>
            </button>
        </div>
    );
};

export default Home;
