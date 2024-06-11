import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { MapContainer, WMSTileLayer, GeoJSON } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

const MapPage = () => {
    const backendURL = "http://localhost:8085";
    const geoserverURL = "http://localhost:8080/geoserver";
    const [countryPair, setCountryPair] = useState(null);
    const [allCountries, setAllCountries] = useState([]);
    const [markedCountries, setMarkedCountries] = useState(new Set());
    const [countryName, setCountryName] = useState('');

    useEffect(() => {
        // Fetch initial data
        axios.get(`${backendURL}/allCountries`).then(response => setAllCountries(response.data));
    }, []);

    useEffect(() => {
        // Fetch initial data
        if (allCountries.length > 0 && !countryPair) {
            axios.get(`${backendURL}/countryPair`).then(response => {
                const countryPair = response.data;
                countryPair.firstCountryName = allCountries.find(x => x.id === countryPair.firstCountryId).name;
                countryPair.secondCountryName = allCountries.find(x => x.id === countryPair.secondCountryId).name;
                setCountryPair(countryPair);
            });
        }
    }, [allCountries]);

    const handleCountrySearch = () => {
        axios.get(`${backendURL}/countryByName?name=${countryName}`).then(response => {
            const country = response.data;
            if (country) {
                setMarkedCountries(prev => new Set(prev).add(country.id));
                checkForPath([...markedCountries, country.id]);
            } else {
                alert('Country not found');
            }
        });
    };

    const checkForPath = (countrySet) => {
        axios.post(`${backendURL}/checkIfSetContainsPath`, {
            index: countryPair.index,
            marked: countrySet,
        }).then(response => {
            if (response.data) {
                alert('Congratulations you found a path!!');
            }
        });
    };

    const onEachFeature = (feature, layer) => {
        const countryId = feature.properties.id;
        const isMarked = markedCountries.has(countryId) ||
            countryId === countryPair.firstCountryId ||
            countryId === countryPair.secondCountryId;
        layer.setStyle({
            fillColor: isMarked ? 'blue' : 'gray',
            fillOpacity: 1,
            color: 'white',
            weight: 1,
        });
    };

    return (
        <div>
            {countryPair && (
                <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                    <h2>Find the shortest path between {countryPair.firstCountryName} and {countryPair.secondCountryName}</h2>
                </div>
            )}
            <div style={{ height: '70vh' }}>
                <MapContainer style={{ height: '100%' }} center={[20, 0]} zoom={2}>
                    <WMSTileLayer
                        url={`${geoserverURL}/ne/wms`}
                        layers="ne:countryLayer"
                        format="image/png"
                        transparent={true}
                    />
                </MapContainer>
            </div>
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
                <input
                    type="text"
                    value={countryName}
                    onChange={e => setCountryName(e.target.value)}
                    placeholder="Enter country name"
                />
                <button onClick={handleCountrySearch}>Search</button>
            </div>
        </div>
    );
};

export default MapPage;
