package com.sericulture.registration.model.dto.fruitsApi;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FruitsTokenDTO {
    @Pattern(regexp = "^[a-zA-Z0-9_.@]*$", message = "Username must contain only letters and numbers")
    private String username;
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Grant type must contain only letters and numbers")
    private String grant_type;

    public FruitsTokenDTO() {
    }

    public FruitsTokenDTO(String username, String password, String grant_type) {
        this.username = username;
        this.password = password;
        this.grant_type = grant_type;
    }
}
