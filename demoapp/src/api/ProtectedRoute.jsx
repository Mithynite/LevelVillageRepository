import React, { useEffect, useState } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

const ProtectedRoute = () => {
    const token = localStorage.getItem('JWTAuthToken');
    const expiration = localStorage.getItem('JWTAuthTokenExpiration');
    const location = useLocation();
    const [isValid, setIsValid] = useState(null); // null = loading, true = valid, false = invalid

    useEffect(() => {
        const validateToken = async () => {
            if (!token || !expiration) {
                setIsValid(false);
                return;
            }

            const now = Date.now();
            const expiryTime = Number(expiration);
            if (isNaN(expiryTime) || now >= expiryTime) {
                console.log("Token expired locally");
                localStorage.removeItem('JWTAuthToken');
                localStorage.removeItem('JWTAuthTokenExpiration');
                setIsValid(false);
                return;
            }

            try {
                const response = await fetch(`http://localhost:8080/api/validate/${token}`);
                if (response.ok) {
                    setIsValid(true);
                } else {
                    console.log("Token validation failed: Token invalid");
                    localStorage.removeItem('JWTAuthToken');
                    localStorage.removeItem('JWTAuthTokenExpiration');
                    setIsValid(false);
                }
            } catch (error) {
                console.error("Error validating token:", error);
                setIsValid(false);
            }
        };

        validateToken();
    }, [token, expiration]);

    if (isValid === null) return null; // Or loading spinner

    return isValid ? (
        <Outlet />
    ) : (
        <Navigate to="/login" replace state={{ from: location }} />
    );
};

export default ProtectedRoute;
