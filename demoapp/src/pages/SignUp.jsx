import React, { useState } from 'react';
import { registerUser } from '../api/AuthService.jsx';
import '../styles/common-style.css';
import { useNavigate } from "react-router-dom";
import NavigationButton from "../components/NavigationButton.jsx";

const SignUpPage = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const MAX_USERNAME_CHAR_LENGTH = 50;

    const handleSignUp = async (event) => {
        event.preventDefault();

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (username.length > MAX_USERNAME_CHAR_LENGTH) {
            setErrorMessage(`Username cannot exceed ${MAX_USERNAME_CHAR_LENGTH} characters.`);
            return;
        }

        if (!emailRegex.test(email)) {
            setErrorMessage('Please enter a valid email address.');
            return;
        }

        if (password !== confirmPassword) {
            setErrorMessage('Passwords do not match!');
            return;
        }

        try {
            await registerUser({ username, email, password });
            setErrorMessage('');
            navigate('/login');
        } catch (error) {
            console.error('Signup failed:', error);
            setErrorMessage(
                error?.response?.data?.message || 'Failed to register. Please try again.'
            );
        }
    };

    return (
        <div className="login-box">
            <p>Sign Up</p>
            <form onSubmit={handleSignUp}>
                <div className="user-box">
                    <input
                        placeholder="Username"
                        name="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <small style={{ color: username.length > MAX_USERNAME_CHAR_LENGTH ? 'red' : 'gray' }}>
                        {username.length}/{MAX_USERNAME_CHAR_LENGTH}
                    </small>
                </div>

                <div className="user-box">
                    <input
                        placeholder="Email"
                        name="email"
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div className="user-box">
                    <input
                        placeholder="Password"
                        name="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <div className="user-box">
                    <input
                        placeholder="Confirm password"
                        name="confirmPassword"
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>

                {errorMessage && (
                    <p style={{ color: 'red', fontSize: '0.9em', textAlign: 'center' }}>
                        {errorMessage}
                    </p>
                )}

                <button type="submit" className="btn">
                    Submit
                </button>
            </form>
            <p>Already have an account? <a href="/login" className="a2">Login here!</a></p>
        </div>
    );
};

export default SignUpPage;
