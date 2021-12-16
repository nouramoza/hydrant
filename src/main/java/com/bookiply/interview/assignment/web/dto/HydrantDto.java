package com.bookiply.interview.assignment.web.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HydrantDto {
    private Long objectid;

    private PointDto the_geom;

    private String unitid;

    private Long boro;

    private Double point_x;

    private Double point_y;

    private String cb;

    private Double latitude;

    private Double longitude;
}
