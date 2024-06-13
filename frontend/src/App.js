import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import MapPage from './pages/MapPage';

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/map" element={<MapPage/>}/>
        </Routes>
      </Router>
  );
}

export default App;
