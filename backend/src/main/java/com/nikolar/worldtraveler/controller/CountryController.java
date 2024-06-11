package com.nikolar.worldtraveler.controller;

import com.nikolar.worldtraveler.dto.CountryDto;
import com.nikolar.worldtraveler.dto.CountryPairDto;
import com.nikolar.worldtraveler.request.CheckForPathRequest;
import com.nikolar.worldtraveler.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping("/countryPair")
    public ResponseEntity<CountryPairDto> getCountryPair(){
        return new ResponseEntity<>(countryService.generateNewCountryPair(), HttpStatus.OK);
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
    public ResponseEntity<Boolean> summarize(@RequestBody CheckForPathRequest checkForPathRequest){
        boolean response = countryService.checkIfSetContainsPath(checkForPathRequest.getIndex(), checkForPathRequest.getMarked());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
