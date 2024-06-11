import React from 'react';
import { Link } from 'react-router-dom';

const LandingPage = () => {

    return (
        <div style={styles.container}>
            <Link to="/map">
                <button style={styles.button}>BEGIN</button>
            </Link>
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: 'gray',
    },
    button: {
        fontSize: '32px',
        padding: '20px 40px',
        backgroundColor: 'darkblue',
        color: 'white',
        border: 'none',
        borderRadius: '5px',
        cursor: 'pointer',
    },
};

export default LandingPage;
