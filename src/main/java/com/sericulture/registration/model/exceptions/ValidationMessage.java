package com.sericulture.registration.model.exceptions;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ValidationMessage extends Message{
    @NonNull
    private String label;
    private String locale;

    private String code;

    public ValidationMessage(String label, String message){
        super(message);
        this.label = label;
    }
}
