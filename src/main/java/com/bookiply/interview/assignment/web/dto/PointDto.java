package com.bookiply.interview.assignment.web.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PointDto {
    @NonNull
    private Double x;
    @NonNull
    private Double y;
}
