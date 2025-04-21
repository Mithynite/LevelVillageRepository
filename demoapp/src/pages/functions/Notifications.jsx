import React, { useState, useEffect } from "react";
import "../../styles/common-style.css";
import { useNavigate } from "react-router-dom";
import NavigationButton from "../../components/NavigationButton.jsx";
import { getMyIncomingChatRequests, respondToChatRequest } from "../../api/ChatService.jsx";
import { FaBell } from "react-icons/fa";

const Notifications = () => {
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                // Fetch Chat Requests (can add more types later)
                const chatRequests = await getMyIncomingChatRequests();
                const formattedChatRequests = chatRequests.map(req => ({
                    id: req.id,
                    type: "chat-request",
                    data: req,
                }));

                // Placeholder for other future notification types
                const allNotifications = [...formattedChatRequests];

                setNotifications(allNotifications);
            } catch (err) {
                console.error("Failed to fetch notifications:", err);
                setError("Could not load notifications.");
            }
        };

        fetchNotifications();
    }, []);

    const handleResponse = async (requestId, accepted) => {
        try {
            await respondToChatRequest(requestId, accepted ? "ACCEPTED" : "DECLINED");
            setNotifications(prev => prev.filter(n => n.id !== requestId));
        } catch (err) {
            console.error("Failed to respond to chat request:", err);
            alert("Something went wrong.");
        }
    };

    const goToProfile = (username) => navigate(`/users/${username}/profile`);
    const goToPost = (postId) => navigate(`/posts/${postId}`);

    return (
        <div className="notifications-page">
            <NavigationButton to="/home" label="Back to Home" className="navigation-button" />
            <h2 className="notifications-header">
                <FaBell className="notifications-icon" />
                Your Notifications
            </h2>

            <div className="notifications-container">
                {error && <p className="error-message">{error}</p>}

                {notifications.length === 0 ? (
                    <p className="no-requests-message">No new notifications.</p>
                ) : (
                    notifications.map((notification) => {
                        const { id, type, data } = notification;

                        switch (type) {
                            case "chat-request":
                                return (
                                    <div key={id} className="notification-card">
                                        <p>
                                            <span>Chat request</span>
                                        </p>
                                        <p>
                                            <span className="link" onClick={() => goToProfile(data.sender.username)}>
                                                <strong>From: </strong>
                                                {data.sender.username}
                                            </span>
                                        </p>
                                        <p>
                                            <strong>The post: </strong>
                                            <span className="link" onClick={() => goToPost(data.receiverPost.id)}>
                                                {data.receiverPost.title}
                                            </span>
                                        </p>
                                            <div className="response-buttons">
                                                <button className="accept-btn"
                                                        onClick={() => handleResponse(id, true)}>Accept
                                                </button>
                                                <button className="reject-btn"
                                                        onClick={() => handleResponse(id, false)}>Reject
                                                </button>
                                            </div>
                                    </div>
                            );

                            // 👇 Future example case
                            // case "like":
                            //     return (
                            //         <div key={id} className="notification-card">
                            //             <p>
                            //                 <strong>{data.likerUsername}</strong> liked your post:{" "}
                            //                 <span className="link" onClick={() => goToPost(data.postId)}>{data.postTitle}</span>
                            //             </p>
                            //         </div>
                            //     );

                            default:
                                return null;
                        }
                    })
                )}
            </div>
        </div>
    );
};

export default Notifications;
