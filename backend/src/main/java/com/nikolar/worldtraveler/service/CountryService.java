package com.nikolar.worldtraveler.service;

import com.nikolar.worldtraveler.dto.CountryDto;
import com.nikolar.worldtraveler.dto.CountryGraph;
import com.nikolar.worldtraveler.dto.CountryPairDto;
import com.nikolar.worldtraveler.mapper.CountryMapper;
import com.nikolar.worldtraveler.model.Country;
import com.nikolar.worldtraveler.repository.CountryRepository;
import com.nikolar.worldtraveler.request.CheckForPathRequest;
import com.nikolar.worldtraveler.response.CheckForPathResponse;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CountryGraph countryGraph;
    private final GeoServerConfigurationService geoServerConfigurationService;

    private final Map<Long, CountryPairDto> countryPairs = new HashMap<>();
    private final AtomicLong pairIndex = new AtomicLong(0L);

    private final Logger logger = LoggerFactory.getLogger(CountryService.class);

    @Value("${sld.template.path}")
    private String sldTemplatePath;


    public CountryService(@Value("${geojson.filepath}") String geoJsonFilePath, CountryRepository countryRepository, CountryMapper countryMapper, GeoServerConfigurationService geoServerConfigurationService){
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        loadCountriesFromGeoJson(geoJsonFilePath);
        countryGraph = new CountryGraph(countryMapper.entityProjectIdsForGraph(countryRepository.findAll()));
        logger.info("All spatial data initialized");
        this.geoServerConfigurationService = geoServerConfigurationService;
        this.geoServerConfigurationService.configureGeoServer();
        logger.info("Ready to receive requests.");
    }


    private void loadCountriesFromGeoJson(String filePath) {
        if (!countryRepository.findAll().isEmpty()){
            logger.info("Database isn't empty no need for loading of geojson data.");
            return;
        }
        logger.info("Loading in countries from .geojson file.");
        try (InputStream inputStream = new FileInputStream(filePath)) {
            JSONTokener tokenizer = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokenizer);
            JSONArray features = object.getJSONArray("features");

            List<Country> countries = new LinkedList<>();

            GeoJsonReader reader = new GeoJsonReader();
            int serbiaIndex = 0;

            Geometry kosovo = null;

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                String admin = properties.getString("ADMIN");
                String geoJson = feature.getJSONObject("geometry").toString();
                Geometry geometry = reader.read(geoJson);
                if (admin.equals("Kosovo")){
                    logger.info("Pretender geom removed");
                    kosovo = geometry;
                    continue;
                }
                Country country = new Country();
                country.setName(admin);
                country.setGeom(geometry);
                country.setNeighbors(new LinkedList<>());
                countries.add(country);
                if (country.getName().equals("Republic of Serbia")){
                    serbiaIndex = countries.size()-1;
                }
            }
            logger.info("Returning de-jure territory");
            Country serbia = countries.get(serbiaIndex);
            serbia.setGeom(serbia.getGeom().union(kosovo));

            logger.info("Saving initial country values");
            serbia.setName("Serbia");
            countryRepository.saveAll(countries);
            logger.info("Pulling saved country values");
            countries = countryRepository.findAll();

            Map<String, Set<String>> wronglyNeighbouringCountries = new HashMap<>();
            wronglyNeighbouringCountries.put("France", Set.of("Brazil", "Suriname"));
            wronglyNeighbouringCountries.put("Brazil", Set.of("France"));
            wronglyNeighbouringCountries.put("Suriname", Set.of("France"));

            logger.info("Finding country connections");
            for (Country firstCountry : countries) {
                Geometry firstCountryGeom = firstCountry.getGeom();
                for (Country secondCountry : countries) {
                    if (!firstCountry.getName().equals(secondCountry.getName())
                            && !(
                                    wronglyNeighbouringCountries.containsKey(firstCountry.getName()) &&
                                    !wronglyNeighbouringCountries.get(firstCountry.getName()).contains(secondCountry.getName())
                            )
                    ){
                        Geometry secondCountryGeom = secondCountry.getGeom();
                        if (firstCountryGeom.getEnvelopeInternal().intersects(secondCountryGeom.getEnvelopeInternal())
                                && firstCountryGeom.intersects(secondCountryGeom)){
                            firstCountry.getNeighbors().add(secondCountry);
                        }
                    }
                }
            }
            logger.info("Saving country connections");
            countryRepository.saveAll(countries);
            logger.info("Checking data integrity");
            if (countryRepository.findAll().size() == 254)
                logger.info("All countries accounted for, integrity check successful");
            else
                logger.error("Wrong number of countries present, integrity check failed");

            logger.info("Checking data validity");
            serbia = countryRepository.getCountryByName("Serbia");
            boolean isDataValid = serbia.getNeighbors()
                    .stream()
                    .anyMatch((Country country) -> country.getName().equals("Albania"));
            if(isDataValid){
                logger.info("Data is valid");
            }else
                logger.error("Invalid data found");
        } catch (Exception e) {
            logger.error("Error while attempting to read .geojson file.", e);
        }
    }


    public Optional<CountryPairDto> generateNewCountryPair(){
        Long index = pairIndex.incrementAndGet();
        logger.info("Generating new country pair for index: " + index);
        CountryPairDto result =  countryGraph.generateNewCountryPair();
        result.setIndex(index);
        Optional<String> sldContent = generateSldContent(Set.of(result.getFirstCountryId(), result.getSecondCountryId()));
        if (sldContent.isEmpty())
            return Optional.empty();
        countryPairs.put(index, result);
        result.setSldContent(sldContent.get());
        return Optional.of(result);
    }

    public Optional<CheckForPathResponse> checkIfSetContainsPath(Long index, Set<Long> marked){
        logger.info("Getting sld template");
        Optional<String> sldContent = generateSldContent(marked);
        if (sldContent.isEmpty()){
            return Optional.empty();
        }
        CountryPairDto countryPairDto = countryPairs.get(index);
        logger.info("Checking if passed set contains a path for pair on index: " + index);
        if(countryGraph.idSetContainsPath(countryPairDto.getFirstCountryId(), countryPairDto.getSecondCountryId(), marked)){
            countryPairs.remove(index);
            logger.info("Passed set for index: " + index + ", contains a path");
            return Optional.of(new CheckForPathResponse(true, sldContent.get()));
        }
        logger.info("Passed set for index: " + index + ", does not contain a path");
        return Optional.of(new CheckForPathResponse(false, sldContent.get()));
    }

    public List<CountryDto> getAllCountries(){
        logger.info("Getting all countries");
        return countryMapper.entityToDto(countryRepository.findAll());
    }

    public CountryDto getCountryById(Long id){
        logger.info("Getting country by id: " + id);
        return countryMapper.entityToDto(countryRepository.findById(id).orElse(null));
    }

    public CountryDto getCountryByName(String name){
        logger.info("Getting country by name: " + name);
        return countryMapper.entityToDto(countryRepository.findCountryByName(name).orElse(null));
    }

    private Optional<String> generateSldContent(Set<Long> marked) {
        try {
            String sldTemplate = Files.readString(Paths.get(sldTemplatePath));
            List<CountryDto> countryDtos = countryMapper.entityToDto(countryRepository.findCountriesByIdIsIn(marked.stream().toList()));
            String highlightedCountriesFilter = countryDtos.stream()
                    .map(countryDto -> "<ogc:PropertyIsEqualTo><ogc:PropertyName>name</ogc:PropertyName><ogc:Literal>" + countryDto.getName() + "</ogc:Literal></ogc:PropertyIsEqualTo>")
                    .collect(Collectors.joining());
            return Optional.of(sldTemplate.replace("${highlightedCountries}", highlightedCountriesFilter));
        } catch (IOException e) {
            logger.error("Failed to retrieve sld template");
            return Optional.empty();
        }
    }



}
