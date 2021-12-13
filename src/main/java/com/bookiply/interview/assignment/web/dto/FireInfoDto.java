package com.bookiply.interview.assignment.web.dto;

import lombok.*;

import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FireInfoDto {
    @NonNull
    private PointDto theGeom;
    @NonNull
    private Long numberOfFireTrucks;

}
