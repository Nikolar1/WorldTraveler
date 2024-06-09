package com.nikolar.worldtraveler.service;

import com.nikolar.worldtraveler.dto.CountryDto;
import com.nikolar.worldtraveler.dto.CountryGraph;
import com.nikolar.worldtraveler.dto.CountryPairDto;
import com.nikolar.worldtraveler.mapper.CountryMapper;
import com.nikolar.worldtraveler.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final CountryGraph countryGraph;

    private volatile Map<Long, CountryPairDto> countryPairs = new HashMap<>();
    private AtomicLong pairIndex = new AtomicLong(0L);

    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper){
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        countryGraph = new CountryGraph(countryMapper.entityProjectIdsForGraph(countryRepository.findAll()));
    }

    public CountryPairDto generateNewCountryPair(){
        CountryPairDto result =  countryGraph.generateNewCountryPair();
        Long index = pairIndex.incrementAndGet();
        result.setIndex(index);
        countryPairs.put(index, result);
        return result;
    }

    public boolean checkIfSetContainsPath(Long index, Set<Long> marked){
        CountryPairDto countryPairDto = countryPairs.get(index);
        return countryGraph.idSetContainsPath(countryPairDto.getFirstCountryId(), countryPairDto.getSecondCountryId(), marked);
    }

    public List<CountryDto> getAllCountries(){
        return countryMapper.entityToDto(countryRepository.findAll());
    }

    public CountryDto getCountryId(Long id){
        return countryMapper.entityToDto(countryRepository.findById(id).orElse(null));
    }


}
