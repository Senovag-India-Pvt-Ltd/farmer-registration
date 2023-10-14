package com.sericulture.registration.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorResponse extends ResponseBody {
    List<String> Message;

    ErrorType errorType;

}

