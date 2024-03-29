package com.company.Server.json;

public enum JSONType {
    OBJECT("объект"),
    ARRAY("массив"),
    STRING("строка"),
    NUMBER("число"),
    BOOLEAN("логический");

    String name;

    JSONType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
