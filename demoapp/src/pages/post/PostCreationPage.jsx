import React, { useState, useEffect } from "react";
import { createPost } from "../../api/PostService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import { fetchSkills } from "../../api/SkillService.jsx";

const PostCreationPage = () => {

    const loggedInUsername = localStorage.getItem("username");
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        username: loggedInUsername,
        skills: [],
        date: "",
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);
    const [skills, setSkills] = useState([]);
    const [selectedSkills, setSelectedSkills] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const allSkills = await fetchSkills();
                setSkills(allSkills);
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to fetch skills.");
                setLoading(false);
            }
        };
    })

    useEffect(() => {
        const fetchData = async () => {
            try {
                const allSkills = await fetchSkills();
                setSkills(allSkills);
                console.log(allSkills);
            } catch (err) {
                console.error("Error fetching data:", err);
                setError("Failed to fetch skills.");
                setLoading(false);
            }
        };

        fetchData(); // ✅ <-- This is what was missing!
    }, []);


    // Handle Input Changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    const handleSkillChange = (e) => {
        const selectedOptions = [...e.target.selectedOptions].map(option => Number(option.value));
        setSelectedSkills(selectedOptions);
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
                username: formData.username,
                skills: selectedSkills,
                date: new Date().toISOString(),
            };
            console.log(newPost);
            await createPost(newPost);
            setSuccess(true);

            // Clear form after successful submission but keep username
            setFormData({
                title: "",
                description: "",
                username: loggedInUsername,
                skills: [],
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
                <label>Related Skills (select up to 5):
                    <select
                        multiple
                        value={selectedSkills}
                        onChange={handleSkillChange}
                        className="skill-dropdown"
                    >
                        {skills.map(skill => (
                            <option key={skill.id} value={skill.id}>{skill.skillName}</option>
                        ))}
                    </select>
                </label>

                <button type="button" onClick={handlePostCreation} className="form-button">
                    Create Post
                </button>
            </form>
        </div>
    );
};

export default PostCreationPage;
