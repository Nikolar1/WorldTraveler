package com.nikolar.worldtraveler.dto;

import java.util.*;
import java.util.stream.Collectors;

import com.nikolar.worldtraveler.model.Country;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryGraph {
    private Map<Long, Set<Long>> neighbours;
    private int numberOfVertices;
    private int numberOfEdges;

    public CountryGraph(List<CountryDto> countries){
        numberOfVertices = countries.size();
        neighbours = new TreeMap<>();
        numberOfEdges = 0;
        for( CountryDto country : countries){
            numberOfEdges+=country.getNeighbors().size();
            Set<Long> neighboursSet = country
                    .getNeighbors()
                    .stream()
                    .map(Country::getId)
                    .collect(Collectors.toCollection(HashSet::new));
            neighbours.put(country.getId(), neighboursSet);
        }
        numberOfEdges = numberOfEdges / 2;
    }

}
