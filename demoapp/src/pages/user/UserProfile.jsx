import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import NavigationButton from "../../components/NavigationButton.jsx";
import { fetchUserProfile, updateUserProfile, getUserLikedPosts } from "../../api/UserService.jsx";
import { FaDiscord, FaInstagram, FaLinkedin } from "react-icons/fa";

const UserProfile = () => {
    const { username } = useParams();
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        bio: "",
        discord: "",
        instagram: "",
        linkedIn: "",
    });
    const [validationErrors, setValidationErrors] = useState({});
    const [error, setError] = useState(null);
    const [likedPosts, setLikedPosts] = useState([]);
    const MAX_USER_BIO_CHAR_LENGTH = 400;
    const isBioTooLong = formData.bio.length > MAX_USER_BIO_CHAR_LENGTH;

    const loggedInUsername = localStorage.getItem("username");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const userProfile = await fetchUserProfile(username);

                setUser({ ...userProfile });

                setFormData({
                    bio: userProfile.bio || "",
                    discord: userProfile.discord || "",
                    instagram: userProfile.instagram || "",
                    linkedIn: userProfile.linkedIn || "",
                });

                if (username === loggedInUsername) {
                    const likedPostsData = await getUserLikedPosts();
                    setLikedPosts(likedPostsData);
                }

                setLoading(false);
            } catch (err) {
                console.error("Error fetching data:", err);
                setLoading(false);
            }
        };

        if (username) {
            fetchData();
        }
    }, [username]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setValidationErrors(prev => ({ ...prev, [name]: null }));
    };

    const validateFields = () => {
        const errors = {};
        if (formData.discord && !/^.{3,32}#\d{4}$/.test(formData.discord)) {
            errors.discord = "Invalid Discord format. Use Username#1234";
        }
        if (formData.instagram && !/^https:\/\/(www\.)?instagram\.com\/.+$/.test(formData.instagram)) {
            errors.instagram = "Instagram must start with https://instagram.com/";
        }
        if (formData.linkedIn && !/^https:\/\/(www\.)?linkedin\.com\/.+$/.test(formData.linkedIn)) {
            errors.linkedIn = "LinkedIn must start with https://linkedin.com/";
        }
        return errors;
    };

    const handleProfileUpdate = async () => {
        const errors = validateFields();
        if (Object.keys(errors).length > 0) {
            setValidationErrors(errors);
            return;
        }

        try {
            const updatedData = {
                bio: formData.bio,
                discord: formData.discord,
                instagram: formData.instagram,
                linkedIn: formData.linkedIn,
            };

            await updateUserProfile(username, updatedData);

            setUser(prevUser => ({
                ...prevUser,
                ...updatedData,
            }));

            setIsEditing(false);
        } catch (err) {
            console.error("Error updating profile:", err);
            setError("Failed to update profile.");
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="profile-page">
            <NavigationButton to="/home" label="Go Back to Home" className="form-button" />

            <h1>{user?.username}</h1>
            <p><strong>Email:</strong> {user?.email}</p>

            {isEditing && loggedInUsername === username ? (
                <div className="profile-edit">
                    {/* form fields stay the same */}
                    <form>
                        {/* Bio */}
                        <label className="label-container">Bio:
                            <textarea
                                name="bio"
                                value={formData.bio}
                                onChange={handleInputChange}
                                className="bio-input"
                                maxLength={MAX_USER_BIO_CHAR_LENGTH}
                            />
                            <div
                                className="char-counter"
                                style={{color: isBioTooLong ? "red" : "gray", fontSize: "0.85rem"}}
                            >
                                {formData.bio.length} / {MAX_USER_BIO_CHAR_LENGTH} characters
                            </div>

                        </label>

                        {/* Discord */}
                        <label className="label-container">Discord:
                            <input
                                type="text"
                                name="discord"
                                value={formData.discord}
                                onChange={handleInputChange}
                            />
                            {validationErrors.discord && (
                                <p style={{ color: "red" }}>{validationErrors.discord}</p>
                            )}
                        </label>

                        {/* Instagram */}
                        <label className="label-container">Instagram URL:
                            <input
                                type="text"
                                name="instagram"
                                value={formData.instagram}
                                onChange={handleInputChange}
                            />
                            {validationErrors.instagram && (
                                <p style={{ color: "red" }}>{validationErrors.instagram}</p>
                            )}
                        </label>

                        {/* LinkedIn */}
                        <label className="label-container">LinkedIn URL:
                            <input
                                type="text"
                                name="linkedIn"
                                value={formData.linkedIn}
                                onChange={handleInputChange}
                            />
                            {validationErrors.linkedIn && (
                                <p style={{ color: "red" }}>{validationErrors.linkedIn}</p>
                            )}
                        </label>
                    </form>

                    <div style={{display: "flex", gap: "10px", marginTop: "10px"}}>
                        <button
                            onClick={handleProfileUpdate}
                            className="form-button"
                            disabled={isBioTooLong}
                            style={{
                                backgroundColor: isBioTooLong ? "#ccc" : "",
                                cursor: isBioTooLong ? "not-allowed" : "pointer",
                            }}
                        >
                            Save Changes
                        </button>

                        <button onClick={() => setIsEditing(false)} className="form-button">Cancel</button>
                    </div>
                </div>
            ) : (
                <div className="profile-display">
                    <p><strong>Bio:</strong> {user?.bio || "No bio available"}</p>

                    <div className="contact-info">
                        <h3 style={{color:"goldenrod"}}>Contact:</h3>
                        <ul className="contact-list">
                            {user?.discord && (
                                <li className="contact-item discord">
                                    <FaDiscord className="contact-icon" /> <span>{user.discord}</span>
                                </li>
                            )}
                            {user?.instagram && (
                                <li className="contact-item instagram">
                                    <FaInstagram className="contact-icon" />
                                    <a href={user.instagram} target="_blank" rel="noopener noreferrer">{user.instagram}</a>
                                </li>
                            )}
                            {user?.linkedIn && (
                                <li className="contact-item linkedin">
                                    <FaLinkedin className="contact-icon" />
                                    <a href={user.linkedIn} target="_blank" rel="noopener noreferrer">LinkedIn</a>
                                </li>
                            )}
                        </ul>
                    </div>

                    {(loggedInUsername === username || username) && (
                        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
                            {loggedInUsername === username && (
                                <>
                                    <button onClick={() => setIsEditing(true)} className="form-button">Edit Profile</button>
                                    <NavigationButton
                                        to={`/users/${username}/liked-posts`}
                                        label="View Liked Posts"
                                        className="form-button"
                                    />
                                    <NavigationButton
                                        to={`/users/${username}/posts`}
                                        label="View User's Posts"
                                        className="form-button"
                                    />
                                </>
                            )}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default UserProfile;
