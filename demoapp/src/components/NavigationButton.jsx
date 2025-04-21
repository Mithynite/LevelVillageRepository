import React from "react";
import { useNavigate } from "react-router-dom";

const NavigationButton = ({ to = "/home", label = "Return", className = "", children }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(to);
    };

    return (
        <button onClick={handleClick} className={className}>
            {children || label}
        </button>
    );
};

export default NavigationButton;
