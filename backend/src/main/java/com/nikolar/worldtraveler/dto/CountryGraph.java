package com.nikolar.worldtraveler.dto;

import com.nikolar.worldtraveler.service.CountryService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CountryGraph {
    private final Logger logger = LoggerFactory.getLogger(CountryGraph.class);
    private final Random random = new Random();
    private final Map<Long, Set<Long>> neighbours;
    private final List<List<Long>> connectedComponents;
    @Getter
    private final int numberOfVertices;
    @Getter
    private final int numberOfEdges;

    public CountryGraph(List<CountryIdsForGraphDto> countries){
        logger.info("Creating country graph");
        numberOfVertices = countries.size();
        neighbours = new HashMap<>();
        int numberOfEdges = 0;
        for( CountryIdsForGraphDto country : countries){
            numberOfEdges+=country.getNeighborIds().size();
            neighbours.put(country.getId(), new HashSet<>(country.getNeighborIds()));
        }
        this.numberOfEdges = numberOfEdges / 2;
        connectedComponents = generateConnectedComponents();
    }

    private List<List<Long>> generateConnectedComponents(){
        logger.info("Generating connected components");
        List<List<Long>> connectedComponents = new LinkedList<>();
        Set<Long> marked = new HashSet<>();
        for( Long country: neighbours.keySet()){
            if (!marked.contains(country)){
                List<Long> connectedComponent = new LinkedList<>();
                findConnectedComponentOfCountry(country, connectedComponent, marked, neighbours);
                connectedComponents.add(connectedComponent);
            }
        }
        return connectedComponents;
    }

    private void findConnectedComponentOfCountry(Long country, List<Long> connectedComponent, Set<Long> marked, Map<Long, Set<Long>> neighbours){
        marked.add(country);
        for(Long neighbour : neighbours.get(country)){
            if (!marked.contains(neighbour)){
                connectedComponent.add(neighbour);
                findConnectedComponentOfCountry(neighbour, connectedComponent, marked, neighbours);
            }
        }
    }

    //Create a copy of the graph map and the value set objects as to ensure that the sets cannot be edited from the outside
    public Map<Long, Set<Long>> getNeighbours(){
        Map<Long, Set<Long>> neighboursCopy = new HashMap<>();
        for( Map.Entry<Long, Set<Long>> entry: neighbours.entrySet()){
            neighboursCopy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return neighboursCopy;
    }

    public CountryPairDto generateNewCountryPair(){
        List<Long> connectedComponnent = connectedComponents.get(random.nextInt(connectedComponents.size()));
        CountryPairDto countryPairDto = null;
        do{
            Long firstId = connectedComponnent.get(random.nextInt(connectedComponnent.size()));
            Long secondId = connectedComponnent.get(random.nextInt(connectedComponnent.size()));
            if (!firstId.equals(secondId) && !neighbours.get(firstId).contains(secondId)){
                countryPairDto = new CountryPairDto();
                countryPairDto.setFirstCountryId(firstId);
                countryPairDto.setSecondCountryId(secondId);
                countryPairDto.setPath(findShortestPath(firstId, secondId));
                if (countryPairDto.getPath().isEmpty())
                    countryPairDto = null;
            }
        }while (countryPairDto == null);
        logger.info("Generated country pair for country ids: 1:" + countryPairDto.getFirstCountryId() + ", 2:" + countryPairDto.getSecondCountryId());
        return countryPairDto;
    }

    private Set<Long> findShortestPath(Long firstId, Long secondId){
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Long> predecessors = new HashMap<>();
        Set<Long> marked = new HashSet<>();

        queue.add(firstId);
        marked.add(firstId);
        predecessors.put(firstId, null);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (current.equals(secondId)) {
                logger.info("Path found for country ids: 1:" + firstId + ", 2:" + secondId);
                return reconstructPath(predecessors, secondId);
            }
            for (Long neighbor : neighbours.get(current)) {
                if (!marked.contains(neighbor)) {
                    queue.add(neighbor);
                    marked.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            }
        }
        logger.info("No path found for country ids: 1:" + firstId + ", 2:" + secondId);
        return Collections.emptySet();
    }

    private Set<Long> reconstructPath(Map<Long, Long> predecessors, Long target) {
        Set<Long> path = new HashSet<>();
        for (Long current = target; current != null; current = predecessors.get(current)) {
            path.add(current);
        }
        return path;
    }

    public boolean idSetContainsPath(Long firstId, Long secondId, Set<Long> marked){
        if (marked.isEmpty() || !(marked.contains(firstId) && marked.contains(secondId)) || (!firstId.equals(secondId) && marked.size() <= 2)) {
            logger.info("The invalid set passed for country ids: 1:" + firstId + ", 2:" + secondId);
            return false;
        }
        Map<Long, Set<Long>> neighboursInPath = new HashMap<>();
        for (Long country : marked){
            Set<Long> countryNeighbours = new HashSet<>();
            for (Long neighbour : neighbours.get(country)){
                if (marked.contains(neighbour)){
                    countryNeighbours.add(neighbour);
                }
            }
            neighboursInPath.put(country, countryNeighbours);
        }
        Set<Long> visited = new HashSet<>();
        List<Long> connectedComponent = new LinkedList<>();
        findConnectedComponentOfCountry(firstId, connectedComponent, visited, neighboursInPath);
        return connectedComponent.contains(secondId);
    }


}
