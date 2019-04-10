package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.utils.ObjectUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class WebElementProxy extends ObjectUtils.PassThroughProxy<WebElement> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebElementProxy.class);

    private final WebDriver driver;

    public WebElementProxy(WebDriver driver, WebElement webElement) {
        super(webElement);
        this.driver = driver;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SessionContext sessionContext = WebDriverManager.getSessionContextFromWebDriver(driver);

        if (!method.getName().equals("toString")) {
            String msg = method.getName();
            if (args != null) {
                msg += " " + Arrays.stream(args).map(Object::toString).collect(Collectors.joining(" "));
            }
            ProxyUtils.log(LOGGER, sessionContext, msg);
        }

        ProxyUtils.updateSessionContextRelations(sessionContext);

        return method.invoke(target, args);
    }

    public WebElement getWrappedWebElement() {
        return target;
    }
}