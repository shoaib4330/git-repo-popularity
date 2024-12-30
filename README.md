# Repository Popularity Assessment

## Description
This project retrieves repositories from GitHub using their search API and scores each repo based on certain criteria. This spring application exposes an endpoint which returns scored Repositories as json.

Note: I have used PAT (personal access token) of my GitHub account to access GitHub APIs. And that is injected from <b>envrionment variable</b>


In `application.yml` you'll see the following:
````
github:
    access:
        token: ${PERSONAL_ACCESS_TOKEN} # Being set as environment variable. To send with API calls to GitHub for Authorization
````


### Documentation
API exposed by this application:

``
GET /repositories/scored
``

Parameters accepted by the API:
- `@RequestParam String repositoryName` <b>Required
- `@RequestParam String language, @RequestParam(required = false)` Required
- `@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime earliestCreationDate` Optional </b>

To communicate with GitHub Spring WebClient is used. And it is configured in `AppConfig` class. `GithubClient` class is responsible for communication with GitHub to execute search API request.


### Scoring
`RepositoryService` class calculates the score for each retrieved repository. Based on the following criteria:
- A base score is assigned to repo based on the last push date.
  - If in last 30 days, base score = 100 eventually multiplied by RECENCY_FACTOR_WEIGHT = 10
  - If in last 180 days, base score = 50 eventually multiplied by RECENCY_FACTOR_WEIGHT = 10
  - If greater 180 days, base score = 10 eventually multiplied by RECENCY_FACTOR_WEIGHT = 10
- Number of forks multiplied by FORK_FACTOR_WEIGHT = 5
- Number of stars multiplied by STARS_FACTOR_WEIGHT = 3
- Number of watchers multiplied by WATCHERS_FACTOR_WEIGHT = 2

## Unit Tests
Added for: 
- `GithubClient` Communicates with GitHub
- `RepositoryService` Does Scoring of the repositories


## Improvements Pending (Things that could be added)
- Exception handling (proper exceptions, error responses & exception handlers)
- Defining custom Jackson Object Mapper instead of using annotations everywhere
- Better validation using javax validation
- Previously I have used Feign & Rest template. With this project I used WebClient, I'm sure there are adjustments and best practices around it which could be added.
- Testing (due to limited time, I've added just the minimal tests, further tests would be added)

