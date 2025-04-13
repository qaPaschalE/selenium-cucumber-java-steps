// src/test/java/runners/ApiTestRunner.java
package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@CucumberOptions(features = "src/test/resources/features/api",
        glue = {"com.github.qaPaschalE.stepDefinitions.api", "com.github.qaPaschalE.util"},
        plugin = { "pretty", "html:target/cucumber-reports/api","json:target/cucumber-reports/api.json" },
        monochrome=true,
        tags = "${cucumber.tags}")
@Test
public class ApiTestRunnerTestCase extends AbstractTestNGCucumberTests {


}
