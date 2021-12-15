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


    @Override
    public String toString() {
        return "HydrantDto{" +
                "objectid=" + objectid +
                ", the_geom=" + the_geom +
                ", unitid='" + unitid + '\'' +
                ", boro=" + boro +
                ", point_x=" + point_x +
                ", point_y=" + point_y +
                ", cb='" + cb + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
