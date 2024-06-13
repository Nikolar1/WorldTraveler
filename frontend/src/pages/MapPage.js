import React, { useEffect, useState, useRef, memo } from 'react';
import axios from 'axios';
import { MapContainer, WMSTileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import './MapPage.css';

const MapComponent = memo(({ geoserverURL, sldBody }) => {
    const layerRef = useRef(null);

    useEffect(() => {
        if (layerRef.current) {
            layerRef.current.setParams({ sld_body: sldBody });
        }
    }, [sldBody]);

    return (
        <MapContainer className="map-container" center={[20, 0]} zoom={2}>
            <WMSTileLayer
                ref={layerRef}
                url={`${geoserverURL}/ne/wms`}
                layers="ne:country"
                format="image/png"
                transparent={true}
                params={{ sld_body: sldBody }}
            />
        </MapContainer>
    );
});

const MapPage = () => {
    const backendURL = "http://localhost:8085";
    const geoserverURL = "http://localhost:8080/geoserver";
    const [countryPair, setCountryPair] = useState(null);
    const [allCountries, setAllCountries] = useState([]);
    const [markedCountries, setMarkedCountries] = useState(new Set());
    const [countryName, setCountryName] = useState('');
    const [sldBody, setSldBody] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        axios.get(`${backendURL}/allCountries`).then(response => setAllCountries(response.data));
    }, []);

    useEffect(() => {
        if (allCountries.length > 0 && !countryPair) {
            axios.get(`${backendURL}/countryPair`).then(response => {
                const countryPair = response.data;
                countryPair.firstCountryName = allCountries.find(x => x.id === countryPair.firstCountryId).name;
                countryPair.secondCountryName = allCountries.find(x => x.id === countryPair.secondCountryId).name;
                setCountryPair(countryPair);
                setSldBody(countryPair.sldContent);
                setMarkedCountries(new Set([countryPair.firstCountryId, countryPair.secondCountryId]));
            });
        }
    }, [allCountries, countryPair]);

    const handleCountrySearch = () => {
        axios.get(`${backendURL}/countryByName?name=${countryName}`).then(response => {
            const country = response.data;
            if (country) {
                const newSet = new Set(markedCountries);
                newSet.add(country.id);
                setMarkedCountries(newSet);
            } else {
                alert('Country not found');
            }
        });
    };

    useEffect(() => {
        if (markedCountries && markedCountries.size > 2){
            setLoading(true)
            axios.post(`${backendURL}/checkIfSetContainsPath`, {
                index: countryPair.index,
                marked: Array.from(markedCountries),
            }).then(response => {
                if (response.data.sldContent !== sldBody) {
                    setSldBody(response.data.sldContent);
                }
                if (response.data.pathExists) {
                    alert(`Congratulations you found a path!! \n Shortest path was ${countryPair.path.length-2} countries. \n Your path was ${markedCountries.size-2} countries.`);
                }
                setLoading(false)
            }).catch(error => {
                console.error("There was an error checking the path: ", error)
                setLoading(false)
            });
        }
    }, [markedCountries]);

    const handleInputChange = (e) => {
        const value = e.target.value;
        setCountryName(value);
        if (value) {
            const filteredSuggestions = allCountries.filter(country => country.name.toLowerCase().startsWith(value.toLowerCase())).slice(0, 5);
            setSuggestions(filteredSuggestions);
        } else {
            setSuggestions([]);
        }
    };

    const handleSuggestionClick = (suggestion) => {
        setCountryName(suggestion.name);
        setSuggestions([]);
    };

    return (
        <div>
            {countryPair ? (
                <div className="map-page-header">
                    <h2>Find the shortest path between {countryPair.firstCountryName} and {countryPair.secondCountryName}</h2>
                </div>
            ) : (
                <div className="map-page-loading">
                    <p>Loading...</p>
                </div>
            )}
            <div className="map-container">
                <MapComponent geoserverURL={geoserverURL} sldBody={sldBody} />
            </div>
            <div className="map-page-input">
                <input
                    type="text"
                    value={countryName}
                    onChange={handleInputChange}
                    placeholder="Enter country name"
                />
                <button onClick={handleCountrySearch}>Guess</button>
                {suggestions.length > 0 && (
                    <ul className="suggestions-list">
                        {suggestions.map(suggestion => (
                            <li
                                key={suggestion.id}
                                onClick={() => handleSuggestionClick(suggestion)}
                                className="suggestion-item"
                            >
                                {suggestion.name}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
            {loading && (
                <div className="map-page-loading">
                    <p>Loading...</p>
                </div>
            )}
        </div>
    );
};

export default MapPage;
