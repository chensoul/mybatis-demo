# mybatis-demo

This project was created with [generator-springboot](https://github.com/chensoul/generator-springboot/).

## Testing requirements

Testcontainers is used for running the integration tests. Due
to the reuse flag, the container will not shut down after the tests. It can be stopped manually if needed.

## Build

The application can be tested and built using the following command:

```
mvnw clean package
```

Start your application with the following command - here with the profile `prod`:

```
java -Dspring.profiles.active=prod -jar ./target/mybatis-demo-0.0.1-SNAPSHOT.jar
```

If required, a Docker image can be created with the Spring Boot plugin. Add `SPRING_PROFILES_ACTIVE=prod` as
environment variable when running the container.

```
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=chensoul/mybatis-demo
```

## Format code

```bash
$ ./mvnw spotless:apply
```

## Repair Flyway Schema History Table

```bash
$ ./mvnw flyway:repair -Dflyway.url=jdbc:postgresql://localhost:5432/mybatis-demo -Dflyway.user=postgres -Dflyway.password=P4ssword!
```

## Code quality using Sonar

Sonar is used to analyse code quality. You can run a Sonar analysis with SonarCloud:

```bash
$ ./mvnw sonar:sonar -Dsonar.token=${SONAR_TOKEN} -Dsonar.projectKey=mybatis-demo
```

Or You can start a local Sonar server (accessible on http://localhost:9001) with:

```bash
$ docker compose -f docker-compose-sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [docker-compose-sonar.yml](docker-compose-sonar.yml)
for out of the box experience while trying out SonarQube, for real use cases turn it back on.

Then, run a Sonar analysis:

```bash
$ ./mvnw sonar:sonar -Dsonar.token=${SONAR_TOKEN} -Dsonar.projectKey=mybatis-demo -Dsonar.host=http://localhost:9001 
```

For more information, refer to
the [Code quality page](https://www.jhipster.tech/documentation-archive/v8.7.3/code-quality/).

## Further readings

* [Maven docs](https://maven.apache.org/guides/index.html)
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [MyBatis Plus reference](https://baomidou.com/introduce/)
