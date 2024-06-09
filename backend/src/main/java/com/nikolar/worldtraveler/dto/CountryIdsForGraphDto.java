package com.nikolar.worldtraveler.dto;

import com.nikolar.worldtraveler.model.Country;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryIdsForGraphDto {
    private Long id;
    private List<Long> neighborIds;
}
