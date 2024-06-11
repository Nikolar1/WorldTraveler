package com.nikolar.worldtraveler.dto;

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
    private List<Long> neighbors;

}
