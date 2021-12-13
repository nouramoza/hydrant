package com.bookiply.interview.assignment.web.dto;

import lombok.*;

import java.awt.*;
import java.awt.geom.Point2D;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HydrantDto {
    private Long objectId;
    private PointDto theGeom;
    private String unitId;
    private Double pointX;
    private Double pointY;
    private String cb;
    private Double latitude;
    private Double longtitude;

}
