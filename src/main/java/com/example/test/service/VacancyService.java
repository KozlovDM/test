package com.example.test.service;

import com.example.test.domain.Vacancy;
import com.example.test.repo.VacancyRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class VacancyService {
    private final VacancyRepo vacancyRepo;

    @Autowired
    public VacancyService(VacancyRepo vacancyRepo) {
        this.vacancyRepo = vacancyRepo;
    }

    public List<Vacancy> setDB(String siteAddress, int numberVacancy) {
        try {
            List<Vacancy> vacancies = getVacancys(siteAddress, numberVacancy);
            vacancyRepo.deleteAll();
            vacancyRepo.saveAll(vacancies);
            return vacancies;
        } catch (IOException e) {
            return null;
        }
    }

    public List<Vacancy> noDB(String siteAddress, int numberVacancy) {
        try {
            return getVacancys(siteAddress, numberVacancy);
        } catch (IOException e) {
            return null;
        }
    }

    private List<Vacancy> getVacancys(String siteAddress, int numberVacancy) throws IOException {
        Document document = Jsoup.connect(siteAddress + "/search/vacancy?clusters=true&enable_snippets=true")
                .get();
        Elements elements = document.getElementsByClass("vacancy-serp-item");
        while (elements.size() < numberVacancy) {
            String next = document.getElementsContainingOwnText("дальше").attr("href");
            document = Jsoup.connect(siteAddress + next).get();
            elements.addAll(document.getElementsByClass("vacancy-serp-item"));
        }

        List<Vacancy> vacancies = new LinkedList<>();
        for (Element element : elements) {
            if (vacancies.size() <= numberVacancy) {
                Vacancy vacancy = new Vacancy();
                String description;
                vacancy.setHeadline(getData("bloko-link HH-LinkModifier", 0, element));
                vacancy.setEmployer(getData("bloko-link bloko-link_secondary", 0, element));
                vacancy.setLocation(getData("vacancy-serp-item__meta-info", 2, element));

                vacancy.setLink(element.getElementsByClass("bloko-link HH-LinkModifier")
                        .get(0).attr("href"));

                vacancy.setSalary(getVal("vacancy-serp-item__compensation", 0, element));
                vacancy.setDate(getVal("vacancy-serp-item__publication-date", 0, element));


                description = element.getElementsByClass("vacancy-serp-item__info")
                        .get(1).childNode(0).childNode(0).toString();
                description += element.getElementsByClass("vacancy-serp-item__info")
                        .get(1).childNode(1).childNode(0).toString();
                vacancy.setDescription(description);


                vacancies.add(vacancy);
            } else {
                return vacancies;
            }
        }
        return vacancies;
    }

    private String getData(String className, int elementNumber, Element element) {
        return element.getElementsByClass(className).get(elementNumber).childNode(0).toString();
    }

    private String getVal(String className, int elementNumber, Element element) {
        if (!element.getElementsByClass(className).isEmpty()) {
            return element.getElementsByClass(className).get(elementNumber).childNode(0).toString();
        }
        return null;
    }
}
