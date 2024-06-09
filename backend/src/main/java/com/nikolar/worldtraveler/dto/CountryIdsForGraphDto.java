package com.nikolar.worldtraveler.dto;

import lombok.*;

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
