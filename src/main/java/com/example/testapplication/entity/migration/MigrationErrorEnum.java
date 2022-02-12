package com.example.testapplication.entity.migration;

import com.sun.istack.Nullable;

public enum MigrationErrorEnum {

    INVALID_FIELD("invalid_field");

    private String id;

    MigrationErrorEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static MigrationErrorEnum fromId(String id) {
        for (MigrationErrorEnum at : MigrationErrorEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
