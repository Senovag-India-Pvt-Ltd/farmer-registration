package com.sericulture.registration.model.dto.village;

import lombok.Data;

@Data
public class VillageDTO {
    private String VillageName;

    public VillageDTO() {
    }

    public VillageDTO(String villageName) {
        VillageName = villageName;
    }
}