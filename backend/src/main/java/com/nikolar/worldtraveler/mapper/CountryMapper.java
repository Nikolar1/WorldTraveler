package com.nikolar.worldtraveler.mapper;

import com.nikolar.worldtraveler.dto.CountryDto;
import com.nikolar.worldtraveler.dto.CountryIdsForGraphDto;
import com.nikolar.worldtraveler.model.Country;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class CountryMapper {
    public Country dtoToEntity(CountryDto dto){
        if(dto == null){
            return null;
        }
        Country entity = new Country();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
//        entity.setGeom(dto.getGeom());
        entity.setNeighbors(dto
                .getNeighbors()
                .stream()
                .map(Country::new)
                .collect(Collectors.toList())
        );
        return entity;
    }

    public List<Country> dtoToEntity(List<CountryDto> dtoList){
        if(dtoList == null){
            return null;
        }
        if (dtoList.isEmpty()){
            return new LinkedList<Country>();
        }
        return dtoList
                .stream()
                .map(this::dtoToEntity)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public CountryDto entityToDto(Country entity){
        if(entity == null){
            return null;
        }
        CountryDto dto = new CountryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
//        dto.setGeom(entity.getGeom());
        dto.setNeighbors(entity
                .getNeighbors()
                .stream()
                .map(Country::getId)
                .collect(Collectors.toList())
        );
        return dto;
    }

    public List<CountryDto> entityToDto(List<Country> entityList){
        if(entityList == null){
            return null;
        }
        if (entityList.isEmpty()){
            return new LinkedList<CountryDto>();
        }
        return entityList
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public CountryIdsForGraphDto dtoProjectIdsForGraph(CountryDto dto){
        if(dto == null){
            return null;
        }
        CountryIdsForGraphDto projection = new CountryIdsForGraphDto();
        projection.setId(dto.getId());
        projection.setNeighborIds(dto.getNeighbors());
        return projection;
    }

    public List<CountryIdsForGraphDto> dtoProjectIdsForGraph(List<CountryDto> dtoList){
        if(dtoList == null){
            return null;
        }
        if (dtoList.isEmpty()){
            return new LinkedList<CountryIdsForGraphDto>();
        }
        return dtoList
                .stream()
                .map(this::dtoProjectIdsForGraph)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public CountryIdsForGraphDto entityProjectIdsForGraph(Country entity){
        if(entity == null){
            return null;
        }
        CountryIdsForGraphDto projection = new CountryIdsForGraphDto();
        projection.setId(entity.getId());
        projection.setNeighborIds(entity
                .getNeighbors()
                .stream()
                .map(Country::getId)
                .collect(Collectors.toList())
        );
        return projection;
    }

    public List<CountryIdsForGraphDto> entityProjectIdsForGraph(List<Country> countryList){
        if(countryList == null){
            return null;
        }
        if (countryList.isEmpty()){
            return new LinkedList<CountryIdsForGraphDto>();
        }
        return countryList
                .stream()
                .map(this::entityProjectIdsForGraph)
                .collect(Collectors.toCollection(LinkedList::new));
    }


}
