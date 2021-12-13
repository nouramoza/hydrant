package com.bookiply.interview.assignment.web.dto;


import lombok.*;

import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PointDto {
    private String type;
    private Double[] coordinates;

}
