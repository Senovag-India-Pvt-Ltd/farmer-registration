package com.sericulture.registration.model.dto.fruitsApi;

import lombok.Data;

@Data
public class FruitsFarmerDTO {
    private String FarmerId;

    public FruitsFarmerDTO() {
    }

    public FruitsFarmerDTO(String farmerId) {
        FarmerId = farmerId;
    }
}
