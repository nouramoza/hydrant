package com.bookiply.interview.assignment.web.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NearestHydrantsToFireDto {
    private Double totalFirehosesLength;

    private List<SelectedHydrantDto> hydrants;

}
