package com.bookiply.interview.assignment.web.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SelectedHydrantDto {
    private String unitId;

    private Double distanceToFire;
}
