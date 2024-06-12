import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { MapContainer, WMSTileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

const MapPage = () => {
    const backendURL = "http://localhost:8085";
    const geoserverURL = "http://localhost:8080/geoserver";
    const [countryPair, setCountryPair] = useState(null);
    const [allCountries, setAllCountries] = useState([]);
    const [markedCountries, setMarkedCountries] = useState(new Set());
    const [countryName, setCountryName] = useState('');
    const [sldBody, setSldBody] = useState('');

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
                setMarkedCountries([countryPair.firstCountryId,countryPair.secondCountryId])
                console.log(countryPair)
            });
        }
    }, [allCountries]);

    const handleCountrySearch = () => {
        axios.get(`${backendURL}/countryByName?name=${countryName}`).then(response => {
            const country = response.data;
            if (country) {
                setMarkedCountries(prev => {
                    const newSet = new Set(prev);
                    newSet.add(country.id);
                    updateSLD(newSet);
                    return newSet;
                });
            } else {
                alert('Country not found');
            }
        });
    };

    const updateSLD = (countrySet) => {
        axios.post(`${backendURL}/checkIfSetContainsPath`, {
            index: countryPair.index,
            marked: Array.from(countrySet),
        }).then(response => {
            setSldBody(response.data.sldContent);
            if (response.data.pathExists) {
                alert(`Congratulations you found a path!! \n Shortest path was ${countryPair.path.length}`);
            }
        }).catch(error => {
            console.error("There was an error checking the path: ", error);
        });
    };

    return (
        <div>
            {countryPair && (
                <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                    <h2>Find the shortest path between {countryPair.firstCountryName} and {countryPair.secondCountryName}</h2>
                </div>
            )}
            {sldBody && (<div style={{ height: '70vh' }}>
                <MapContainer style={{ height: '100%' }} center={[20, 0]} zoom={2}>
                    <WMSTileLayer
                        url={`${geoserverURL}/ne/wms`}
                        layers="ne:country"
                        format="image/png"
                        transparent={true}
                        params={{ sld_body: sldBody }}
                    />
                </MapContainer>
            </div>)}
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
