package com.sericulture.registration.model.api;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ApplicationsDetailsDistrictWiseRequest extends ApplicationFormBaseRequest {
    private int farmerId;

    private int districtId;

}
