# mybatis-demo

This project was created with [generator-springboot](https://github.com/chensoul/generator-springboot/).

## Build

The application can be built using the following command:

```bash
$ ./mvnw clean package
```

To ensure everything worked, run:

```bash
$ java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

## Others

### Format code

```bash
$ ./mvnw spotless:apply
```

### Repair Flyway Schema History Table

```bash
$ ./mvnw flyway:repair -Dflyway.url=jdbc:postgresql://localhost:5432/appdb -Dflyway.user=appuser -Dflyway.password=secret
```

### Code quality using Sonar

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```bash
$ docker compose -f docker-compose-sonar.yml up -d
```

Note: we have turned off forced authentication redirect for UI in [docker-compose-sonar.yml](docker-compose-sonar.yml)
for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using
the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven
plugin.

Then, run a Sonar analysis:

```bash
$ ./mvnw clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```bash
$ ./mvnw initialize sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Additionally, Instead of passing `sonar.password` and `sonar.login` as CLI arguments, these parameters can be configured
from [sonar-project.properties](sonar-project.properties) as shown below:

```
sonar.login=admin
sonar.password=admin
```

For more information, refer to
the [Code quality page](https://www.jhipster.tech/documentation-archive/v8.7.3/code-quality/).

### Docker Compose support

To start required services in Docker containers, run:

```bash
$ docker compose -f docker-compose.yml up -d
```

To stop and remove the containers, run:

```bash
$ docker compose -f docker-compose.yml down
```

[Spring Docker Compose Integration](https://docs.spring.io/spring-boot/reference/features/dev-services.html) is enabled
by default. It's possible to disable it in application.yml:

```yaml
spring:
  docker:
    compose:
      enabled: false
```

You can also fully dockerized your application and all the services that it depends on.
To achieve this, first build a Docker image of your app by running:

```bash
$ ./mvnw -ntp verify -DskipTests jib:dockerBuild
```

Or build a arm64 Docker image when using an arm64 processor os like MacOS with M1 processor family running:

```bash
$ ./mvnw -ntp verify -DskipTests jib:dockerBuild -Djib-maven-plugin.architecture=arm64
```

Then run:

```bash
$ docker compose -f docker-compose-app.yml up -d
```

## Useful Links

* Springdoc UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* Sonarqube UI: http://localhost:9001
* Zipkin UI: http://localhost:9411/
* Kibana: http://localhost:5601/
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (admin/admin)
