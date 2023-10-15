package com.sericulture.registration.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseWrapper<T> {

    T content;

    List<? extends Object> errorMessages = new ArrayList<>();

}
