package com.example.test.repo;

import com.example.test.domain.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepo extends JpaRepository<Vacancy, Long> {
}
