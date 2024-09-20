package com.sericulture.registration.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketWiseReelerCountResponse extends ResponseBody {

    private String marketName;
    private String reelerCount;

}
