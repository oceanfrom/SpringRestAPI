package com.example.SpringRestAPI.services;

import com.example.SpringRestAPI.models.Person;
import com.example.SpringRestAPI.repositories.PeopleRepository;
import com.example.SpringRestAPI.utl.PersonNotFoundException;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @PostConstruct
    @Transactional
    public void loadPersonInDB() {
        Faker faker = new Faker();
        List<Person> people = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Person.builder()
                        .name(faker.name().fullName())
                        .age(faker.number().numberBetween(18, 100))
                        .email(faker.internet().emailAddress())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .createdWho("ADMIN")
                        .build())
                .toList();
        peopleRepository.saveAll(people);
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person) {
        enrichPerson(person);
        peopleRepository.save(person);
    }


    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreatedWho("ADMIN");
    }

}
