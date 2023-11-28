package com.sericulture.registration.model.dto.farmer;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerFamilyDTO {
    private Long farmerFamilyId;
    private String farmerFamilyName;
    private Long relationshipId;
    private Long farmerId;
    private String relationshipName;
}
