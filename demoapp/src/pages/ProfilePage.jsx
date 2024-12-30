import React,{useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import NavigationButton from "../components/NavigationButton.jsx";
import {fetchUserProfile} from "../api/UserService.js";


const ProfilePage = () => {
    const navigate = useNavigate();
    const { id } = useParams(); // Extract the dynamic parameter from the URL
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);

    const fetchUser = async () => {
        try{
            const specificUser = await fetchUserProfile();
            setUser(specificUser);
        }catch(error){
            console.error("Error appeared while fetching the user: ", error);
            setError(error.message + "Something went wrong while fetching the post!");
        }finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        fetchUser(id)
    }, [id]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

return(
    <div className="profile-container">
        <NavigationButton to="/home" label="Go Back to Home" />
        <div className="user-box1">
            <div className="bio">bio</div>
            <div className="user-skills">your skills</div>
        </div>
        <div className="user-box2">
            <div className="username">{user.username}</div>
            <div className="email">Email: {user.email}</div>
            <div className="liked-posts">liked posts</div>
            <div className="saved-posts">saved posts</div>
        </div>
    </div>
);
};
export default ProfilePage;