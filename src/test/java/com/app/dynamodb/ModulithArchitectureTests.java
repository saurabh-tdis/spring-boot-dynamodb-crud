package com.app.dynamodb;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithArchitectureTests {

    ApplicationModules modules = ApplicationModules.of(SpringBootDynamodbCrudApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }

    @Test
    void createApplicationModuleModel() {
        modules.forEach(System.out::println);
    }
}