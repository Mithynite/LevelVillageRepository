import React, { useState } from 'react';
import '../styles/common-style.css';
import {useNavigate} from "react-router-dom";
import {registerUser} from "../api/authService.jsx";

const WelcomePage = () => {
    const navigate = useNavigate();

    const navigateToLoginPage= async () => {
        navigate('/login');
    };
    return (
        <div className="welcome-page">
            <div className="left-container">
                <h1>Welcome to Level Village!</h1>
                <button>
                        <span className="start-button-box">
                            Start now
                        </span>
                </button>
                <p>for free!</p>
            </div>

            <div className="info-cards">
                <div className="box box1">
                    <h2>Share your knowledge</h2>
                    <p>Teach everyone your unique skills and abilities. Collect positive feedback!</p>
                </div>
                <div className="box box2">
                    <h2>Level up your skills</h2>
                    <p>Cooperate with other users and expand your knowledge in every possible way!</p>
                </div>
                <div className="box box3">
                    <h2>Find contacts</h2>
                    <p>You will meet many inspiring people along the way. You may eventually become friends!</p>
                </div>
            </div>
            <div>
                <button onClick={navigateToLoginPage} className="login-button">Login</button>
            </div>
        </div>
    );
};

export default WelcomePage;
