package runners;



import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@CucumberOptions(features = "src/test/resources/features/db",
        glue = {"org.example.stepDefinitions.db","org.example.util"},
        plugin = { "pretty", "html:target/cucumber-reports/db","json:target/cucumber-reports/db.json" },
        monochrome=true,
        tags = "${cucumber.tags}")
@Test
public class DbTestRunnerTestCase extends AbstractTestNGCucumberTests {
    // No methods needed in an empty runner class.

}