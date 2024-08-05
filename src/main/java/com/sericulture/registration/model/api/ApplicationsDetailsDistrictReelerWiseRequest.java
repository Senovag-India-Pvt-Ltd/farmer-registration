package com.sericulture.registration.model.api;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ApplicationsDetailsDistrictReelerWiseRequest extends ApplicationFormBaseRequest {
    private int reelerId;
    private int marketId;
    private int districtId;

}
