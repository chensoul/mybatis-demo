package com.mycompany.myapp;

import static com.tngtech.archunit.lang.conditions.ArchConditions.beAnnotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "com.mycompany.myapp")
public class ArchUnitTest {
    @Test
    void shouldFollowLayeredArchitecture() {
        noClasses()
                .that()
                .areMetaAnnotatedWith(RestController.class)
                .should()
                .dependOnClassesThat()
                .areMetaAnnotatedWith(TableName.class)
                .because("controllers should use DTOs in stead of entities");

        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Web")
                .definedBy("..web..")
                .layer("Config")
                .definedBy("..config..")
                .layer("Service")
                .definedBy("..service..")
                .layer("Persistence")
                .definedBy("..repository..")
                .whereLayer("Web")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Service")
                .mayOnlyBeAccessedByLayers("Config", "Web")
                .whereLayer("Persistence")
                .mayOnlyBeAccessedByLayers("Service");
    }

    @Test
    void shouldNotUseFieldInjection() {
        ArchCondition<JavaField> BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION = ArchConditions.<JavaField>beAnnotatedWith(
                        "org.springframework.beans.factory.annotation.Autowired")
                .or(beAnnotatedWith("com.google.inject.Inject"))
                .or(beAnnotatedWith("javax.inject.Inject"))
                .or(beAnnotatedWith("javax.annotation.Resource"))
                .or(beAnnotatedWith("jakarta.inject.Inject"))
                .or(beAnnotatedWith("jakarta.annotation.Resource"))
                .as("be annotated with an injection annotation");

        noFields()
                .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
                .as("no classes should use field injection")
                .because(
                        "field injection is considered harmful; use constructor injection or setter injection instead; "
                                + "see https://stackoverflow.com/q/39890849 for detailed explanations");
    }

    @Test
    void shouldNotUseJunit4Classes() {
        noClasses()
                .should()
                .accessClassesThat()
                .resideInAnyPackage("org.junit")
                .because("Tests should use Junit5 instead of Junit4");

        noMethods()
                .should()
                .beAnnotatedWith("org.junit.Test")
                .orShould()
                .beAnnotatedWith("org.junit.Ignore")
                .because("Tests should use Junit5 instead of Junit4");
    }

    @Test
    void shouldFollowNamingConvention() {
        classes().that().areMetaAnnotatedWith(RestController.class).should().haveSimpleNameEndingWith("Controller");

        methods()
                .that()
                .areDeclaredInClassesThat()
                .areMetaAnnotatedWith(RestController.class)
                .should()
                .beMetaAnnotatedWith(RequestMapping.class)
                .orShould()
                .beMetaAnnotatedWith(ExceptionHandler.class);

        //        classes()
        //                .that().haveSimpleNameEndingWith("Service")
        //                .should().beInterfaces()
        //                .because("service contracts should be public interfaces and implementations should be
        // hidden");

        classes()
                .that()
                .resideInAPackage("com.mycompany.myapp.repository")
                .should()
                .haveSimpleNameEndingWith("Repository");

        classes()
                .that()
                .resideInAPackage("com.mycompany.myapp.service")
                .should()
                .haveSimpleNameEndingWith("Service");

        methods()
                .that()
                .areDeclaredInClassesThat()
                .areMetaAnnotatedWith(RestController.class)
                .and()
                .haveNameStartingWith("get")
                .should()
                .beMetaAnnotatedWith(GetMapping.class);
    }
}
