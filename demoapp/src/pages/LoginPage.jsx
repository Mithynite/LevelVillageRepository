import React, { useState } from 'react';
import { loginUser } from '../api/authService';
import '../styles/common-style.css';
import {useNavigate} from "react-router-dom";


const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (event) => {
        event.preventDefault();
        try {
            await loginUser({ username, password });
            alert('Logged in successfully!');
            navigate('/dashboard');
        } catch (error) {
            console.error('Login failed:', error);
            alert('Invalid username or password');
        }
    };


    return (
        <div className="login-box">
            <p>Login</p>
            <form onSubmit={handleLogin}>
                <div className="user-box">
                    <input name="username" type="text" placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)}
                           required/>

                </div>
                <div className="user-box">

                    <input name="password" type="password" placeholder="Password" value={password}
                           onChange={(e) => setPassword(e.target.value)} required/>
                </div>
                <button type="submit" className="btn">
                Submit
                </button>
            </form>
            <p>Don't have an account? <a href="/signup" className="a2">Sign up!</a></p>
        </div>
);
};

export default LoginPage;
