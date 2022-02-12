package com.example.testapplication.controller;

import com.example.testapplication.entity.Person;
import com.example.testapplication.entity.migration.PersonMigrateInfo;
import com.example.testapplication.repository.PersonMigrateInfoRepository;
import com.example.testapplication.repository.PersonRepository;
import com.example.testapplication.service.PersonMigrateService;
import com.example.testapplication.service.impl.PasswordGenerator;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/media")
public class MediaController {
    private final static String INFORMATION = "Creator Andrey Kabanov. Loading data from excel";
    private final static int ITERATIONS = 10;
    private final static int MEMORY = 65536;
    private final static int PARALLELISM = 1;

    private Argon2 argon2 = Argon2Factory.create();
    private final PersonMigrateService personMigrateService;
    private final PersonMigrateInfoRepository personMigrateInfoRepository;
    private final PersonRepository personRepository;
    private final PasswordGenerator passwordGenerator;

    @Autowired
    public MediaController(PersonMigrateService personMigrateService,
                           PersonMigrateInfoRepository personMigrateInfoRepository,
                           PersonRepository personRepository,
                           PasswordGenerator passwordGenerator) {
        this.personMigrateService = personMigrateService;
        this.personMigrateInfoRepository = personMigrateInfoRepository;
        this.personRepository = personRepository;
        this.passwordGenerator = passwordGenerator;
    }

    @GetMapping("/information")
    public ResponseEntity<String> information() {
        return new ResponseEntity<>(INFORMATION, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PersonMigrateInfo personMigrateInfo = personMigrateService.migratePersonFromFileExcel(file);
        return new ResponseEntity(personMigrateInfo.getId().toString(), HttpStatus.OK);
    }

    @GetMapping("/upload/info")
    public ResponseEntity<String> uploadInfo(@RequestParam(required = true)  UUID id) {
        Optional<PersonMigrateInfo> migrateInfoOptional = personMigrateInfoRepository.findById(id);
        if (migrateInfoOptional.isEmpty()) {
            return new ResponseEntity("Data does not exist", HttpStatus.BAD_REQUEST);
        }
        PersonMigrateInfo personMigrateInfo = migrateInfoOptional.get();
        if (personMigrateInfo.getMigrate()) {
            return new ResponseEntity("Data uploaded successfully", HttpStatus.OK);
        }

        return new ResponseEntity("Please wait, data is still loading", HttpStatus.OK);
    }

    @GetMapping("/person/pwd")
    public ResponseEntity<Map<String, String>> getPersonPwd(@RequestParam(required = true) UUID id) {
        List<Person> personList = personRepository.retrieveByMigrateInfo(id, PageRequest.of(0, 200));
        Map<String, String> response = new HashMap<>();
        List<Person> actualPersonList = personList.stream().parallel()
                .peek(person -> {
                    person.setActive(true);
                    String pwd = passwordGenerator.generate();
                    response.put(person.getPersonalAccount().toString(), pwd);
                    String hash = argon2.hash(ITERATIONS, MEMORY, PARALLELISM, pwd);
                    person.setPassword(hash);
                }).collect(Collectors.toList());

        personRepository.saveAll(actualPersonList);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
