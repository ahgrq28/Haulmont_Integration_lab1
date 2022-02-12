package com.example.testapplication.entity.migration;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

@Entity
public class PersonMigrate {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "PERSONAL_ACCOUNT")
    private Long personalAccount;

    @Column(name = "FULL_NAMEE")
    private String fullName;

    @Column(name = "BIRTHDAY")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    @Column(name = "IS_MIGRATE")
    private Boolean isMigrate = false;

    @Column(name = "IS_DUPLICATE")
    private Boolean isDuplicate = false;

    @Column(name = "MIGRATION_ERROR")
    private String migrationError;

    @ManyToOne
    @JoinColumn(name = "person_migrate_info_id", nullable = false)
    private PersonMigrateInfo personMigrateInfo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(Long personalAccount) {
        this.personalAccount = personalAccount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getMigrate() {
        return isMigrate;
    }

    public void setMigrate(Boolean migrate) {
        isMigrate = migrate;
    }

    public PersonMigrateInfo getPersonMigrateInfo() {
        return personMigrateInfo;
    }

    public void setPersonMigrateInfo(PersonMigrateInfo personMigrateInfo) {
        this.personMigrateInfo = personMigrateInfo;
    }

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean isDuplicate) {
        isDuplicate = isDuplicate;
    }

    public MigrationErrorEnum getMigrationError() {
        return MigrationErrorEnum.fromId(migrationError);
    }

    public void setMigrationError(MigrationErrorEnum migrationError) {
        this.migrationError = migrationError.getId();
    }
}
