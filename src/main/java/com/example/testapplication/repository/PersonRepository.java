package com.example.testapplication.repository;

import com.example.testapplication.entity.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    @Query("SELECT pe FROM Person pe where pe.personalAccount in (SELECT p.personalAccount FROM PersonMigrate p where " +
            "p.personMigrateInfo.id = :personMigrateInfoId) and pe.isActive = false and pe.password is null  " +
            "order by pe.personalAccount DESC")
    List<Person> retrieveByMigrateInfo(@Param("personMigrateInfoId") UUID personMigrateInfoId, Pageable pageable);
}
