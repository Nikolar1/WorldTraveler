package com.nikolar.worldtraveler.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckForPathResponse {
    private boolean pathExists;
    private String sldContent;
}
