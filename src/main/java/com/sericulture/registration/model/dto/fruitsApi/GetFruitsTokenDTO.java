package com.sericulture.registration.model.dto.fruitsApi;

import lombok.Data;

@Data
public class GetFruitsTokenDTO {
    String access_token;
    String token_type;
    Integer expires_in;
    String error;
    String error_description;

    public GetFruitsTokenDTO() {
    }

    public GetFruitsTokenDTO(String access_token, String token_type, Integer expires_in, String error, String error_description) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.error = error;
        this.error_description = error_description;
    }
}
