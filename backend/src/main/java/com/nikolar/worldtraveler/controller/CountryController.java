package com.nikolar.worldtraveler.controller;

import com.nikolar.worldtraveler.dto.CountryDto;
import com.nikolar.worldtraveler.dto.CountryPairDto;
import com.nikolar.worldtraveler.request.CheckForPathRequest;
import com.nikolar.worldtraveler.response.CheckForPathResponse;
import com.nikolar.worldtraveler.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping("/countryPair")
    public ResponseEntity<?> getCountryPair(){
        Optional<CountryPairDto> result = countryService.generateNewCountryPair();
        if (result.isEmpty())
            return new ResponseEntity<>("Failed to generate country pair", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(result.get(), HttpStatus.OK);
    }

    @GetMapping("/country")
    public ResponseEntity<CountryDto> getCountry(@RequestParam Long id){
        return new ResponseEntity<>(countryService.getCountryById(id), HttpStatus.OK);
    }

    @GetMapping("/countryByName")
    public ResponseEntity<CountryDto> getCountryByName(@RequestParam String name){
        return new ResponseEntity<>(countryService.getCountryByName(name), HttpStatus.OK);
    }

    @GetMapping("/allCountries")
    public ResponseEntity<List<CountryDto>> getAllCountries(){
        return new ResponseEntity<>(countryService.getAllCountries(), HttpStatus.OK);
    }

    @PostMapping("/checkIfSetContainsPath")
    public ResponseEntity<?> summarize(@RequestBody CheckForPathRequest checkForPathRequest){
        if (checkForPathRequest.getIndex() == null || checkForPathRequest.getMarked() == null || checkForPathRequest.getMarked().isEmpty()){
            return new ResponseEntity<>("Missing index or marked countries", HttpStatus.BAD_REQUEST);
        }
        Optional<CheckForPathResponse> response = countryService.checkIfSetContainsPath(checkForPathRequest.getIndex(), checkForPathRequest.getMarked());
        if (response.isPresent()){
            return new ResponseEntity<>(response.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Error occurred while checking the path", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
