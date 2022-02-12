package com.example.testapplication.schedulingtasks;

import com.example.testapplication.entity.Person;
import com.example.testapplication.entity.migration.PersonMigrate;
import com.example.testapplication.entity.migration.PersonMigrateInfo;
import com.example.testapplication.repository.PersonMigrateInfoRepository;
import com.example.testapplication.repository.PersonMigrateRepository;
import com.example.testapplication.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonMigrateScheduledTasks {

    private final PersonMigrateInfoRepository personMigrateInfoRepository;
    private final PersonMigrateRepository personMigrateRepository;
    private final PersonRepository personRepository;

    @Autowired
    public PersonMigrateScheduledTasks(PersonMigrateInfoRepository personMigrateInfoRepository,
                                       PersonMigrateRepository personMigrateRepository,
                                       PersonRepository personRepository) {
        this.personMigrateInfoRepository = personMigrateInfoRepository;
        this.personMigrateRepository = personMigrateRepository;
        this.personRepository = personRepository;
    }

    @Scheduled(fixedRate = 500)
    public void migratingPersonToTheSystem() {
        PersonMigrateInfo personMigrateInfo = personMigrateInfoRepository.retrieveByNoMigrate();
        if (personMigrateInfo == null || personMigrateInfo.getMigrate()) {
            return;
        }

        List<PersonMigrate> personMigrates = personMigrateRepository
                .retrieveByNoMigrate(personMigrateInfo.getId(), PageRequest.of(0, 200));
        if (personMigrates.isEmpty()) {
            personMigrateInfo.setMigrate(true);
            personMigrateInfoRepository.save(personMigrateInfo);
        }

        ArrayList<Long> actualPersonalAccountList = new ArrayList<>();
        ArrayList<PersonMigrate> actualPersonMigrateList = new ArrayList<>();
        ArrayList<Person> personList = new ArrayList<>();
        for (PersonMigrate personMigrate : personMigrates) {
            Long personalAccount = personMigrate.getPersonalAccount();
            if (actualPersonalAccountList.contains(personalAccount)) {
                personMigrate.setDuplicate(true);
                actualPersonMigrateList.add(personMigrate);
                continue;
            }
            personMigrate.setMigrate(true);
            actualPersonMigrateList.add(personMigrate);
            actualPersonalAccountList.add(personalAccount);
            personList.add(personMigrateConverter(personMigrate));
        }

        personMigrateRepository.saveAll(actualPersonMigrateList);
        personRepository.saveAll(personList);

    }

    private Person personMigrateConverter(PersonMigrate personMigrate) {
        Person person = new Person();
        person.setBirthday(personMigrate.getBirthday());
        person.setPersonalAccount(personMigrate.getPersonalAccount());
        person.setFullName(personMigrate.getFullName());
        return person;
    }
}
