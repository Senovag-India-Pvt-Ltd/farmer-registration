package com.sericulture.registration.model.api;

import com.sericulture.registration.model.exceptions.Message;
import lombok.*;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.SuperCall;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse extends ResponseBody {
    List<? extends Message> Message;

    ErrorType errorType;

}

