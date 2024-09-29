package com.sericulture.registration.model.api;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CheckInspectionStatusRequest extends RequestBody {

    private int farmerId;
    private String fruitsId;
}