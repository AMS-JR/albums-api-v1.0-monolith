package com.afrosofttech.rest_jwt_demo.util.constants;

public enum Authority {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    USER, // can do crud on self objects, can also read anything
    ADMIN // can do crud on any objects
}
