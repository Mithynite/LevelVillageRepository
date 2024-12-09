import React from "react";
import { useNavigate } from "react-router-dom";

const NavigationButton = ({ to = "/home", label = "Return" }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(to);
    };

    return (
        <button onClick={handleClick} className="return-button">
            {label}
        </button>
    );
};

export default NavigationButton;
