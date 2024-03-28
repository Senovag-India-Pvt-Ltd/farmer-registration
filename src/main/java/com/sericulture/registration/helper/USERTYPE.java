package com.sericulture.registration.helper;

import lombok.Getter;

@Getter
public enum USERTYPE {

    REELER(2),

    MO(0);

    private int type ;

    USERTYPE(int type) {
        this.type = type;
    }
}