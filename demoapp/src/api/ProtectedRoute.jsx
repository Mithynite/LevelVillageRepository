import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute = () => {
    const token = localStorage.getItem('JWTAuthToken');
    const token_expiration = localStorage.getItem('JWTAuthTokenExpiration');
    const isTokenExpired = () => {
        if(!token_expiration) {
            return true;
        }
        const currentTime = Date.now();
        return currentTime > parseInt(token_expiration, 10); //
    }
        return token && !isTokenExpired() ? <Outlet/> : <Navigate to="/login" />; // Condition(expression1 && expression2) ? True : False
};
export default ProtectedRoute;