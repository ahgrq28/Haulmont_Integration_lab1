package com.example.testapplication.repository;

import com.example.testapplication.entity.migration.PersonMigrate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PersonMigrateRepository extends JpaRepository<PersonMigrate, UUID> {

    @Query("SELECT p FROM PersonMigrate p WHERE p.isMigrate = false and p.isDuplicate = false and p.migrationError is null and " +
            "p.personMigrateInfo.id = :personMigrateInfoId and p.personalAccount not in (select pe.personalAccount from Person pe) " +
            "order by p.personalAccount DESC")
    List<PersonMigrate> retrieveByNoMigrate(@Param("personMigrateInfoId") UUID personMigrateInfoId, Pageable pageable);
}
