import React,{useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import NavigationButton from "../components/NavigationButton.jsx";

const ProfilePage = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState({});

    const fetchUser = async () => {
        try{
            const specificUser = await fetchUserById(id);
        }
    }

return(
    <div className="profile-container">
        <NavigationButton to="/home" label="Go Back to Home" />
        <div className="user-box1">
            <div className="bio">bio</div>
            <div className="user-skills">your skills</div>
        </div>
        <div className="user-box2">
            <div className="username">username</div>
            <div className="email">email</div>
            <div className="liked-posts">liked posts</div>
            <div className="saved-posts">saved posts</div>
        </div>
    </div>
);
};
export default ProfilePage;