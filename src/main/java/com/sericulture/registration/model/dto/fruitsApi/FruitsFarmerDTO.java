package com.sericulture.registration.model.dto.fruitsApi;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FruitsFarmerDTO {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer id must contain only letters and numbers")
    private String FarmerId;

    public FruitsFarmerDTO() {
    }

    public FruitsFarmerDTO(String farmerId) {
        FarmerId = farmerId;
    }
}
