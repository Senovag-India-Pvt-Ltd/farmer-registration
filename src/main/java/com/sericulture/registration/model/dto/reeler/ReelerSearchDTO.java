package com.sericulture.registration.model.dto.reeler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelerSearchDTO {

    private Long reelerId;
    private String reelerName;
    private Object additionalColumn;

}