package com.nikolar.worldtraveler.dto;

import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryPairDto {
    private Long index;
    private Long firstCountryId;
    private Long secondCountryId;
    private Set<Long> path;
    private String sldContent;
}
