// src/test/java/runners/UiTestRunner.java
package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@CucumberOptions(features = "src/test/resources/features/ui",
        glue = {"com.github.qaPaschalE.stepDefinitions.ui","com.github.qaPaschalE.util"},
        tags = "@e2e",
        plugin = {
        "pretty", "html:target/cucumber-reports/ui" ,"json:target/cucumber-reports/ui.json"},
        monochrome=true)

@Test
public class UiTestRunnerTestCase extends AbstractTestNGCucumberTests{

}
