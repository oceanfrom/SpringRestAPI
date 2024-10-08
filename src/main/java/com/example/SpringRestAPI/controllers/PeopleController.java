package com.example.SpringRestAPI.controllers;

import com.example.SpringRestAPI.dto.PersonDTO;
import com.example.SpringRestAPI.models.Person;
import com.example.SpringRestAPI.services.PeopleService;
import com.example.SpringRestAPI.utl.PersonErrorResponse;
import com.example.SpringRestAPI.utl.PersonNotCreatedExeption;
import com.example.SpringRestAPI.utl.PersonNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.executable.ValidateOnExecution;
import org.aspectj.weaver.patterns.PerObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PeopleController {

    private final ModelMapper modelMapper;
    private final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService, View error, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/users")
    public List<PersonDTO> users() {
        // .map(p -> modelMapper.map(p, PersonDTO.class))
        return peopleService.findAll().stream().map(this::converToPersonDTO).collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public PersonDTO user(@PathVariable int id) {
        return converToPersonDTO(peopleService.findOne(id));
    }

    @PostMapping("/users/add")
    public ResponseEntity<HttpStatus> addUser(@RequestBody @Valid PersonDTO personDTO,
                                              BindingResult bindingResult) {
;       if(bindingResult.hasErrors()) {
           StringBuilder errorMsg = new StringBuilder();
           List<FieldError> fieldErrors = bindingResult.getFieldErrors();
           for (FieldError error : fieldErrors) {
               errorMsg.append(error.getField())
                       .append(" - ").append(error.getDefaultMessage())
                       .append(";");
           }
           throw new PersonNotCreatedExeption(errorMsg.toString());
        }
        peopleService.save(convertToPerson(personDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO converToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse("Person with this id wasnt found",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedExeption e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(), System.currentTimeMillis()
        );
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
