package com.pyxis.petstore.domain.product;

public class Attachment {

    private final String fileName;

    public Attachment(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String toString() {
        return fileName;
    }
}
