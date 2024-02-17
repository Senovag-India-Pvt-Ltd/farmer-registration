package com.sericulture.registration.model.dto.fruitsApi;

import lombok.Data;

@Data
public class FruitsTokenDTO {
    private String username;
    private String password;
    private String grant_type;

    public FruitsTokenDTO() {
    }

    public FruitsTokenDTO(String username, String password, String grant_type) {
        this.username = username;
        this.password = password;
        this.grant_type = grant_type;
    }
}
