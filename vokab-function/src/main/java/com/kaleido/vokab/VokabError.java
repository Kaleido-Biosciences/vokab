/*
 * Copyright (c) 2019. Kaleido Biosciences. All Rights Reserved.
 */

package com.kaleido.vokab;

public class VokabError {
    private String message;

    public VokabError(String message, String... args) {
        this.message = String.format(message, args);
    }

    public VokabError(Exception e) {
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }
}
