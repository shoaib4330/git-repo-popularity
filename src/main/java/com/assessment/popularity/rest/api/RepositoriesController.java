package com.assessment.popularity.rest.api;

import com.assessment.popularity.service.RepositoriesService;
import com.assessment.popularity.dto.SourceRepositoryDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/repositories")
public class RepositoriesController {

    private final RepositoriesService repositoriesService;

    public RepositoriesController(RepositoriesService repositoriesService) {
        this.repositoriesService = repositoriesService;
    }

    @GetMapping("/scored")
    public List<SourceRepositoryDto> getScoredRepositories(@RequestParam String repositoryName, @RequestParam String language, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime earliestCreationDate) {
        return repositoriesService.processRepositories(repositoryName, language, earliestCreationDate);
    }

}
