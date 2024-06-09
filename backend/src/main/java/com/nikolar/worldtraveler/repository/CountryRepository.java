package com.nikolar.worldtraveler.repository;

import com.nikolar.worldtraveler.dto.CountryIdsForGraphDto;
import com.nikolar.worldtraveler.model.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
    @Query("SELECT c.id as id, n.id as neighborIds FROM Country c LEFT JOIN c.neighbors n WHERE c.id = :countryId")
    List<CountryIdsForGraphDto> findCountryWithNeighborsIds(@Param("countryId") Long countryId);
}
