package com.bookiply.interview.assignment.web.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class FireInfoDto {
    private PointDto theGeom;

    private Double latitude;

    private Double longitude;

    @NonNull
    private Long numberOfFireTrucks;

}
