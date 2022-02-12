package com.example.testapplication.service;


import com.example.testapplication.entity.migration.PersonMigrateInfo;
import org.springframework.web.multipart.MultipartFile;

public interface PersonMigrateService {
    String NAME = "PersonMigrateService";

    PersonMigrateInfo migratePersonFromFileExcel(MultipartFile excelFile);
}
