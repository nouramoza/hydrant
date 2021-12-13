package com.bookiply.interview.assignment.web.dto;

import lombok.*;

import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InputDto {
    @NonNull
    private Point theGeom;
    @NonNull
    private Long numberOfFireTrucks;

}
