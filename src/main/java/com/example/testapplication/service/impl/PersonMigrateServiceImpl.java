package com.example.testapplication.service.impl;

import com.example.testapplication.entity.migration.MigrationErrorEnum;
import com.example.testapplication.entity.migration.PersonMigrate;
import com.example.testapplication.entity.migration.PersonMigrateInfo;
import com.example.testapplication.repository.PersonMigrateInfoRepository;
import com.example.testapplication.repository.PersonMigrateRepository;
import com.example.testapplication.service.PersonMigrateService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

@Component(PersonMigrateService.NAME)
public class PersonMigrateServiceImpl implements PersonMigrateService {
    private final static int batchSize = 200;

    private final PersonMigrateInfoRepository personMigrateInfoRepository;
    private final PersonMigrateRepository personMigrateRepository;

    @Autowired
    public PersonMigrateServiceImpl(PersonMigrateInfoRepository personMigrateInfoRepository,
                                    PersonMigrateRepository personMigrateRepository) {
        this.personMigrateInfoRepository = personMigrateInfoRepository;
        this.personMigrateRepository = personMigrateRepository;
    }

    @Override
    public PersonMigrateInfo migratePersonFromFileExcel(MultipartFile excelFile) {
        Workbook workbook;
        try {
            workbook = getWorkbook(excelFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get data from file", e);
        }

        PersonMigrateInfo personMigrateInfo = personMigrateInfoRepository.save(new PersonMigrateInfo());
        Sheet firstSheet = workbook.getSheetAt(0);
        ArrayList<PersonMigrate> personMigrateList = new ArrayList<>();
        int amountReceivedData = 0;

        for (Row nextRow : firstSheet) {
            if (nextRow.getRowNum() == 0) {
                continue;
            }

            Iterator<Cell> cellIterator = nextRow.cellIterator();
            PersonMigrate personMigrate = new PersonMigrate();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case _NONE:
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell) && cell.getColumnIndex() == 2) {
                            personMigrate.setBirthday(cell.getDateCellValue());
                        } else if (cell.getColumnIndex() == 0) {
                            personMigrate.setPersonalAccount((long) cell.getNumericCellValue());
                        }
                        break;
                    case STRING:
                        if (cell.getColumnIndex() == 1) {
                            personMigrate.setFullName(cell.getStringCellValue());
                        }
                        break;
                }
            }
            personMigrate.setPersonMigrateInfo(personMigrateInfo);
            MigrationErrorEnum migrationErrorEnum = validatePersonMigrate(personMigrate);
            if (migrationErrorEnum != null) {
                personMigrate.setMigrationError(migrationErrorEnum);
            }
            personMigrateList.add(personMigrate);
            amountReceivedData += 1;
            if (amountReceivedData == batchSize) {
                personMigrateRepository.saveAll(personMigrateList);
                amountReceivedData = 0;
                personMigrateList = new ArrayList<>();
            }
        }

        return personMigrateInfo;
    }

    @Nullable
    private MigrationErrorEnum validatePersonMigrate(PersonMigrate personMigrate) {
        if (personMigrate.getBirthday() == null) {
            return MigrationErrorEnum.INVALID_FIELD;
        } else if (personMigrate.getFullName() == null || personMigrate.getFullName().isEmpty()) {
            return MigrationErrorEnum.INVALID_FIELD;
        } else if (personMigrate.getPersonalAccount() == null) {
            return MigrationErrorEnum.INVALID_FIELD;
        }

        return null;
    }

    private Workbook getWorkbook(MultipartFile excelFile) throws IOException {
        String filename = excelFile.getResource().getFilename();
        if (filename == null) {
            throw new RuntimeException("Failed to get file name");
        }

        if (excelFile.getResource().getFilename().endsWith(".xls")) {
            return new HSSFWorkbook(excelFile.getInputStream());
        } else {
            return new XSSFWorkbook(excelFile.getInputStream());
        }
    }
}
