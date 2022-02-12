package com.example.testapplication.entity.migration;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class PersonMigrateInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "IS_MIGRATE")
    private Boolean isMigrate = false;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getMigrate() {
        return isMigrate;
    }

    public void setMigrate(Boolean migrate) {
        isMigrate = migrate;
    }
}
