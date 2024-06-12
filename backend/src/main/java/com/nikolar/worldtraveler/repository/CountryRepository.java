package com.nikolar.worldtraveler.repository;

import com.nikolar.worldtraveler.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Country getCountryByName(String name);
    Optional<Country> findCountryByName(String name);

    List<Country> findCountriesByIdIsIn(List<Long> ids);
}
