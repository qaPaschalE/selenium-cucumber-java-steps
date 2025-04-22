// src/test/java/runners/UiTestRunner.java
package runners;

import com.github.qaPaschalE.util.ConfigLoader;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;

@CucumberOptions(
        features = "src/test/resources/features/ui",
        glue = {"com.github.qaPaschalE.stepDefinitions.ui", "com.github.qaPaschalE.util"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/ui",
                "json:target/cucumber-reports/ui.json"
        },
        monochrome = true
)
public class UiTestRunnerTestCase extends AbstractTestNGCucumberTests {

    @BeforeClass
    public void setup() {
        // Dynamically set the Cucumber tags using ConfigLoader
        String tags = ConfigLoader.getTags();
        System.setProperty("cucumber.tags", tags);
    }
}