package com.example.test.controller;

import com.example.test.domain.Vacancy;
import com.example.test.repo.VacancyRepo;
import com.example.test.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {
    @Autowired
    private VacancyService vacancyService;

    private final VacancyRepo vacancyRepo;

    @Autowired
    public MainController(VacancyRepo vacancyRepo) {
        this.vacancyRepo = vacancyRepo;
    }

    @GetMapping("/noBD")
    public List<Vacancy> getAllNoBD() {
        return vacancyService.noDB("https://kaluga.hh.ru", 50);
    }

    @GetMapping("/BD")
    public List<Vacancy> getAllBD() {
        return vacancyRepo.findAll();
    }

    @GetMapping("/setBD")
    public List<Vacancy> SetVacancy() {
        return vacancyService.setDB("https://kaluga.hh.ru", 50);
    }
}
