package com.nikolar.worldtraveler.request;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckForPathRequest {
    private Long index;
    private Set<Long> marked;
}
