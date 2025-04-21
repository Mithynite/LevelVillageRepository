import React, { useState, useEffect } from "react";
import { createPost } from "../../api/PostService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import { fetchSkills } from "../../api/SkillService.jsx";
import Select from "react-select";

const PostCreation = () => {

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

    const MAX_POST_DESCRIPTION_LENGTH = 400;
    const MAX_TITLE_LENGTH = 50;

    const isDescriptionTooLong = formData.description.length > MAX_POST_DESCRIPTION_LENGTH;
    const isTitleTooLong = formData.title.length > MAX_TITLE_LENGTH;


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

        fetchData();
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

        if (selectedOptions.length <= 5) {
            setSelectedSkills(selectedOptions);
        } else {
            alert("You can select up to 5 skills only.");
        }
    };


    // Validate Input
    const validateForm = () => {
        if (!formData.title.trim()) {
            setError("Title cannot be empty.");
            return false;
        }
        if (formData.title.length > MAX_TITLE_LENGTH) {
            setError(`Title must not be above ${MAX_TITLE_LENGTH} characters.`);
            return false;
        }

        if (!formData.description.trim()) {
            setError("Description cannot be empty.");
            return false;
        }
        if (formData.description.length > MAX_POST_DESCRIPTION_LENGTH) {
            setError(`Description must not be above ${MAX_POST_DESCRIPTION_LENGTH} characters.`);
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
                    <div
                        className="char-counter"
                        style={{
                            color: isTitleTooLong ? "red" : "gray",
                            fontSize: "0.85rem",
                            marginTop: "4px",
                        }}
                    >
                        {formData.title.length} / {MAX_TITLE_LENGTH} characters
                    </div>

                </label>

                <label>
                    Description:
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleInputChange}
                        required
                    ></textarea>
                    <div
                        className="char-counter"
                        style={{
                            color: isDescriptionTooLong ? "red" : "gray",
                            fontSize: "0.85rem",
                            marginTop: "4px",
                        }}
                    >
                        {formData.description.length} / {MAX_POST_DESCRIPTION_LENGTH} characters
                    </div>

                </label>
                <div className="form-group" style={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "flex-start",
                    justifyContent: "flex-start",
                    gap: "0.5rem",
                    marginBottom: "1rem"
                }}>
                    <label htmlFor="skills">Related Skills (select up to 5):</label>
                    <div style={{width: "90%"}}>
                        <Select
                            inputId="skills"
                            isMulti
                            options={skills.map(skill => ({
                                value: skill.id,
                                label: skill.skillName,
                            }))}
                            value={skills
                                .filter(skill => selectedSkills.includes(skill.id))
                                .map(skill => ({value: skill.id, label: skill.skillName}))}
                            onChange={(selectedOptions) => {
                                if (selectedOptions.length <= 5) {
                                    setSelectedSkills(selectedOptions.map(option => option.value));
                                } else {
                                    alert("You can select up to 5 skills only.");
                                }
                            }}
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
                </div>

                <button
                    type="button"
                    onClick={handlePostCreation}
                    className="form-button"
                    disabled={isTitleTooLong || isDescriptionTooLong}
                    style={{
                        backgroundColor: isTitleTooLong || isDescriptionTooLong ? "#ccc" : "",
                        cursor: isTitleTooLong || isDescriptionTooLong ? "not-allowed" : "pointer",
                    }}
                >
                    Create Post
                </button>

            </form>
        </div>
    );
};

export default PostCreation;
