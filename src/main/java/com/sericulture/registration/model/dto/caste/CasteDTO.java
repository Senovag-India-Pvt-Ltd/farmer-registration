package com.sericulture.registration.model.dto.caste;

import lombok.Data;

@Data
public class CasteDTO {
    private String Caste;

    public CasteDTO() {
    }

    public CasteDTO(String caste) {
        Caste = caste;
    }
}