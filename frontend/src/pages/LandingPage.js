import React from 'react';
import { Link } from 'react-router-dom';
import './LandingPage.css';

const LandingPage = () => {
    return (
        <div className="landing-container">
            <Link to="/map">
                <button className="landing-button">BEGIN</button>
            </Link>
        </div>
    );
};

export default LandingPage;
