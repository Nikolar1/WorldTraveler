package com.nikolar.worldtraveler.dto;

import com.nikolar.worldtraveler.model.Country;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryDto {
    private Long id;
    private String name;
    private Geometry geom;
    private List<Country> neighbors;

}
