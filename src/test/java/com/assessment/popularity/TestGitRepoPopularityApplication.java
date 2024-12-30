package com.assessment.popularity;

import org.springframework.boot.SpringApplication;

public class TestGitRepoPopularityApplication {

	public static void main(String[] args) {
		SpringApplication.from(PopularityApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
