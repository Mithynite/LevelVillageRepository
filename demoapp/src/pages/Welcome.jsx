import React from 'react';
import '../styles/common-style.css';
import {useNavigate} from "react-router-dom";
import NavigationButton from "../components/NavigationButton.jsx";
import LVIcon from "../assets/icon-components/LVIcon.jsx";

const Welcome = () => {
    const navigate = useNavigate();

    const navigateToSignUpPage = () => {
        navigate('/signup');
    };

    return (
        <div className="welcome-page">

            <div className="info-cards">
                <LVIcon></LVIcon>
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
            <div className="left-container">
                <h1>Welcome to Level Village!</h1>
                <button onClick={navigateToSignUpPage}>
                        <span className="start-button-box">
                            Start now
                        </span>
                </button>
                <p>for free!</p>
            </div>
            <NavigationButton to="/login" label="Login"
                            className="navigation-button login-button"></NavigationButton>
        </div>
    );
};

export default Welcome;
