package com.example.testapplication.repository;

import com.example.testapplication.entity.migration.PersonMigrateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PersonMigrateInfoRepository extends JpaRepository<PersonMigrateInfo, UUID> {

    @Query("SELECT p FROM PersonMigrateInfo p WHERE p.isMigrate = false order by p.id")
    PersonMigrateInfo retrieveByNoMigrate();
}
