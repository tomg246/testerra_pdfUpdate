/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package eu.tsystems.mms.tic.testframework.webdrivermanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.Constants;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSetupException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.Defaults;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.internal.utils.TimingInfosCollector;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.report.model.BrowserInformation;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.sikuli.TesterraWebDriver;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.anthavio.phanbedder.Phanbedder;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class DesktopWebDriverFactory extends WebDriverFactory<DesktopWebDriverRequest> implements Loggable {

    public static final TimingInfosCollector STARTUP_TIME_COLLECTOR = new TimingInfosCollector();

    private static File phantomjsFile = null;

    @Override
    protected DesktopWebDriverRequest buildRequest(WebDriverRequest request) {
        DesktopWebDriverRequest r;
        if (request instanceof DesktopWebDriverRequest) {
            r = (DesktopWebDriverRequest) request;
        } else if (request instanceof UnspecificWebDriverRequest) {
            r = new DesktopWebDriverRequest();
            r.copyFrom(request);
        } else {
            throw new TesterraSystemException(request.getClass().getSimpleName() + " is not allowed here");
        }

        /*
        set webdriver mode
         */
        if (r.webDriverMode == null) {
            r.webDriverMode = WebDriverManager.config().webDriverMode;
        }

        /*
        build endpoint stuff
         */
        String host = StringUtils.getFirstValidString(r.seleniumServerHost, PropertyManager.getProperty(TesterraProperties.SELENIUM_SERVER_HOST), "localhost");
        String port = StringUtils.getFirstValidString(r.seleniumServerPort, PropertyManager.getProperty(TesterraProperties.SELENIUM_SERVER_PORT), "4444");
        String url = StringUtils.getFirstValidString(r.seleniumServerURL, PropertyManager.getProperty(TesterraProperties.SELENIUM_SERVER_URL), "http://" + host + ":" + port + "/wd/hub");

        // set backwards
        try {
            URL url1 = new URL(url);
            host = url1.getHost();
            port = url1.getPort() + "";
        } catch (MalformedURLException e) {
            log().error("INTERNAL ERROR: Could not parse URL", e);
        }
        r.seleniumServerHost = host;
        r.seleniumServerPort = port;
        r.seleniumServerURL = url;

        return r;
    }

    @Override
    protected DesiredCapabilities buildCapabilities(DesiredCapabilities preSetCaps, DesktopWebDriverRequest desktopWebDriverRequest) {
        return DesktopWebDriverCapabilities.createCapabilities(WebDriverManager.config(), preSetCaps, desktopWebDriverRequest);
    }

    @Override
    public WebDriver getRawWebDriver(DesktopWebDriverRequest request, DesiredCapabilities desiredCapabilities) {
        /*
        start the session
         */
        WebDriver driver = startSession(request, desiredCapabilities);

        if (request.baseUrl != null && !request.baseUrl.isEmpty()) {
            URL baseUrl;
            try {
                baseUrl = new URL(request.baseUrl);
                log().info("Opening baseUrl: " + baseUrl.toString());
                StopWatch.startPageLoad(driver);
                driver.get(baseUrl.toString());
            } catch (MalformedURLException e) {
                log().warn(String.format("Won't open baseUrl: '%s': %s", request.baseUrl, e.getMessage()), e);
            } catch (Exception e) {
                if (StringUtils.containsAll(e.getMessage(), true, "Reached error page", "connectionFailure")) {
                    throw new TesterraRuntimeException("Could not start driver session, because of unreachable url: " + request.baseUrl, e);
                }
                throw e;
            }
        }
        return driver;
    }

    private WebDriver startSession(DesktopWebDriverRequest desktopWebDriverRequest, DesiredCapabilities desiredCapabilities) {

        /*
        if there is a factories entry for the requested browser, then create the new (raw) instance here and wrap it directly in EventFiringWD
         */
        if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
            String threadName = Thread.currentThread().getId() + "";
            String testMethodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();

            if (testMethodName != null) {
                WebDriver driver = DriverStorage.getDriverByTestMethodName(testMethodName, threadName);
                if (driver != null) {
                    log().info("Re-Using WebDriver for " + testMethodName + ": " + threadName + " driver: " + driver);

                    // cleanup session
                    driver.manage().deleteAllCookies();

                    /*
                    Open url
                     */
                    final String baseUrl = WebDriverManagerUtils.getBaseUrl(desktopWebDriverRequest.baseUrl);
                    log().info("Opening baseUrl with reused driver: " + baseUrl);
                    StopWatch.startPageLoad(driver);
                    driver.get(baseUrl);

                    return driver;
                } else {
                    return newWebDriver(desktopWebDriverRequest, desiredCapabilities);
                }
            }
        } else {
            /*
            regular branch to create a new web driver session
             */
            return newWebDriver(desktopWebDriverRequest, desiredCapabilities);
        }

        throw new TesterraSystemException("WebDriverManager is in a bad state. Please report this to the tt. developers.");
    }

    @Override
    public void setupSession(EventFiringWebDriver eventFiringWebDriver, DesktopWebDriverRequest request) {
        final String browser = request.browser;

        // add event listeners
        eventFiringWebDriver.register(new VisualEventDriverListener());
        eventFiringWebDriver.register(new EventLoggingEventDriverListener());

        /*
         start StopWatch
          */
        StopWatch.startPageLoad(eventFiringWebDriver);

        WebDriverManagerConfig config = WebDriverManager.config();
        WebDriver.Window window = eventFiringWebDriver.manage().window();
        /*
         Maximize
         */
        if (config.maximize) {
            log().debug("Trying to maximize window");
            try {
                Dimension originWindowSize = window.getSize();
                // Maximize to detect window size
                window.maximize();
                if (config.maximizePosition != Position.CENTER) {
                    log().debug(String.format("Setting maximized window position to: %s", config.maximizePosition));
                    Point targetPosition = new Point(0, 0);
                    switch (config.maximizePosition) {
                        case LEFT:
                            targetPosition.x = -originWindowSize.width;
                            break;
                        case RIGHT:
                            targetPosition.x = window.getSize().width + 1;
                            break;
                        case TOP:
                            targetPosition.y = -originWindowSize.height;
                            break;
                        case BOTTOM:
                            targetPosition.y = window.getSize().height + 1;
                            break;
                    }
                    log().debug(String.format("Move window to: %s", targetPosition));
                    window.setPosition(targetPosition);
                    // Re-maximize
                    window.maximize();
                }
            } catch (Throwable t1) {
                log().error("Could not maximize window", t1);
                setWindowSizeBasedOnDisplayResolution(window, browser);
            }
        } else {
            setWindowSizeBasedOnDisplayResolution(window, browser);
        }

        if (!Browsers.safari.equalsIgnoreCase(browser)) {
            int pageLoadTimeout = Constants.PAGE_LOAD_TIMEOUT_SECONDS;
            int scriptTimeout = PropertyManager.getIntProperty(TesterraProperties.WEBDRIVER_TIMEOUT_SECONDS_SCRIPT, 120);
            try {
                eventFiringWebDriver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                log().error("Could not set Page Load Timeout", e);
            }
            try {
                eventFiringWebDriver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                log().error("Could not set Script Timeout", e);
            }
        } else {
            log().warn("Not setting timeouts for Safari.");
        }
    }

    private void setWindowSizeBasedOnDisplayResolution(WebDriver.Window window, String browser) {
        log().debug("Trying to set window size to: " + Defaults.DISPLAY_RESOLUTION);
        String[] split = Defaults.DISPLAY_RESOLUTION.split("x");
        int width = Integer.valueOf(split[0]);
        int height = Integer.valueOf(split[1]);
        try {
            window.setSize(new Dimension(width, height));
        } catch (Throwable t2) {
            log().error("Could not set window size", t2);

            if (Browsers.edge.equals(browser)) {
                log().debug("Edge Browser was requested, trying a second workaround");

                Timer timer = new Timer(500, 5000);
                ThrowablePackedResponse<Object> response = timer.executeSequence(new Timer.Sequence<Object>() {
                    @Override
                    public void run() throws Throwable {
                        setSkipThrowingException(true);
                        log().debug("Trying setPosition() and setSize()");
                        try {
                            window.setPosition(new Point(0, 0));
                            window.setSize(new Dimension(width, height));
                            log().debug("Yup, success!");
                        } catch (Exception e) {
                            log().warn("Nope. Got error: " + e.getMessage());
                            throw e;
                        }
                    }
                });

                if (!response.isSuccessful()) {
                    log().error("Finally, could not set Edge Window size", response.getThrowable());
                }
            }
        }
    }

    private WebDriver newWebDriver(DesktopWebDriverRequest desktopWebDriverRequest, DesiredCapabilities capabilities) {
        String sessionKey = desktopWebDriverRequest.sessionKey;
        final String url = desktopWebDriverRequest.seleniumServerURL;
        final String browser = desktopWebDriverRequest.browser;

        org.apache.commons.lang3.time.StopWatch sw = new org.apache.commons.lang3.time.StopWatch();
        sw.start();

        /*
         * Remote or local
         */
        WebDriver newDriver;
        if (desktopWebDriverRequest.webDriverMode == WebDriverMode.remote) {
            /*
             ##### Remote
             */

            URL remoteAddress;
            try {
                remoteAddress = new URL(url);
            } catch (final MalformedURLException e) {
                throw new TesterraRuntimeException("MalformedUrlException while building Remoteserver URL: " + url, e);
            }

            /*
             * Start a new web driver session.
             */
            try {
                if (browser.equals(Browsers.htmlunit)) {
                    capabilities.setBrowserName(BrowserType.HTMLUNIT);
                    capabilities.setJavascriptEnabled(false);
                    log().info("Starting HtmlUnitRemoteWebDriver.");
                    newDriver = new RemoteWebDriver(remoteAddress, capabilities);
                } else {
                    newDriver = startNewWebDriverSession(browser, capabilities, remoteAddress, sessionKey);
                }
            } catch (final TesterraSetupException e) {
                int ms = Constants.WEBDRIVER_START_RETRY_TIME_IN_MS;
                log().error(String.format("Error starting WebDriver. Trying again in %d seconds", (ms/1000)), e);
                TimerUtils.sleep(ms);
                newDriver = startNewWebDriverSession(browser, capabilities, remoteAddress, sessionKey);
            }

        } else {
            log().warn("Local WebDriver setups may cause side effects. It's highly recommended to use a remote Selenium configurations for all environments!");
            /*
             ##### Local
             */
            newDriver = startNewWebDriverSession(browser, capabilities, null, sessionKey);
        }

        /*
        Log session id
         */
        SessionId webDriverSessionId = ((RemoteWebDriver) newDriver).getSessionId();
        desktopWebDriverRequest.storedSessionId = webDriverSessionId.toString();
        desktopWebDriverRequest.sessionContext.sessionId = desktopWebDriverRequest.storedSessionId;

        /*
        Log User Agent and executing host
         */
        NodeInfo nodeInfo = DesktopWebDriverUtils.getNodeInfo(desktopWebDriverRequest);
        desktopWebDriverRequest.storedExecutingNode = nodeInfo;
        WebDriverManager.addExecutingSeleniumHostInfo(sessionKey + ": " + nodeInfo.toString());
        sw.stop();

        BrowserInformation browserInformation = WebDriverManagerUtils.getBrowserInformation(newDriver);
        log().info(String.format(
            "Started %s (session key=%s, remote session id=%s, node=%s, user agent=%s) in %s",
            newDriver.getClass().getSimpleName(),
            sessionKey,
            webDriverSessionId,
            nodeInfo.toString(),
            browserInformation.getUserAgent(),
            sw.toString()
        ));
        STARTUP_TIME_COLLECTOR.add(new TimingInfo("SessionStartup", "", sw.getTime(TimeUnit.MILLISECONDS), System.currentTimeMillis()));

        return newDriver;
    }

    /**
     * Remote when remoteAdress != null, local need browser to be set.
     *
     * @param browser       .
     * @param capabilities  .
     * @param remoteAddress .
     * @return.
     */
    private WebDriver startNewWebDriverSession(
            String browser,
            DesiredCapabilities capabilities,
            URL remoteAddress,
            String sessionKey
    ) {


        /*
         * This is the standard way of setting the browser locale for Selenoid based sessions
         * @see https://aerokube.com/selenoid/latest/#_per_session_environment_variables_env
         */
        //        final Locale browserLocale = Locale.getDefault();
        //        desiredCapabilities.setCapability("env",
        //            String.format(
        //                "[\"LANG=%s.UTF-8\", \"LANGUAGE=%s\", \"LC_ALL=%s.UTF-8\"]",
        //                browserLocale,
        //                browserLocale.getLanguage(),
        //                browserLocale
        //            )
        //        );

        UserAgentConfig userAgentConfig = WebDriverManager.getUserAgentConfig(browser);
        Capabilities finalCapabilities;
        Class<? extends RemoteWebDriver> driverClass;

        switch (browser) {
            case Browsers.firefox:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (userAgentConfig != null) userAgentConfig.configure(firefoxOptions);
                firefoxOptions.merge(capabilities);
                //firefoxOptions.addPreference("intl.accept_languages", String.format("%s-%s", browserLocale.getLanguage(), browserLocale.getCountry()));
                finalCapabilities = firefoxOptions;
                driverClass = FirefoxDriver.class;
                break;
            case Browsers.ie:
                InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                if (userAgentConfig != null) userAgentConfig.configure(ieOptions);
                ieOptions.merge(capabilities);
                ieOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                finalCapabilities = ieOptions;
                driverClass = InternetExplorerDriver.class;
                break;
            case Browsers.chrome:
            case Browsers.chromeHeadless:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (userAgentConfig != null) userAgentConfig.configure(chromeOptions);
                chromeOptions.merge(capabilities);
                //Map<String, Object> prefs = new HashMap<>();
                //prefs.put("intl.accept_languages", String.format("%s_%s", browserLocale.getLanguage(), browserLocale.getCountry()));
                //chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.addArguments("--no-sandbox");
                if (browser.equals(Browsers.chromeHeadless)) {
                    chromeOptions.setHeadless(true);
                }
                finalCapabilities = chromeOptions;
                driverClass = ChromeDriver.class;
                break;
            case Browsers.phantomjs:
                File phantomjsFile = getPhantomJSBinary();
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsFile.getAbsolutePath());
                capabilities.setBrowserName(BrowserType.PHANTOMJS);
                capabilities.setJavascriptEnabled(true);

                String[] args = {
                        "--ssl-protocol=any"
                };
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);
                finalCapabilities = capabilities;
                driverClass = PhantomJSDriver.class;
                break;
            case Browsers.safari:
                SafariOptions safariOptions = new SafariOptions();
                if (userAgentConfig != null) userAgentConfig.configure(safariOptions);
                safariOptions.merge(capabilities);
                finalCapabilities = safariOptions;
                driverClass = SafariDriver.class;
                break;
            case Browsers.edge:
                EdgeOptions edgeOptions = new EdgeOptions();
                if (userAgentConfig != null) userAgentConfig.configure(edgeOptions);
                edgeOptions.merge(capabilities);
                /**
                 * @todo What is this platform capability for?
                 */
                final String platform = null;
                edgeOptions.setCapability("platform", platform);
                finalCapabilities = edgeOptions;
                driverClass = EdgeDriver.class;
                break;
            default:
                throw new TesterraSystemException("Browser must be set through SystemProperty 'browser' or in test.properties file! + is: " + browser);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        log().info(String.format(
                "Starting WebDriver (session key=%s) on %s with capabilities:\n%s",
                sessionKey,
                remoteAddress,
                gson.toJson(finalCapabilities.asMap())
        ));
        log().debug(String.format("Starting (session key=%s) here", sessionKey), new Throwable());

        WebDriver driver;
        try {
            if (remoteAddress != null) {
                final HttpCommandExecutor httpCommandExecutor = new HttpCommandExecutor(new HashMap<>(), remoteAddress, new TesterraHttpClientFactory());
                driver = new TesterraWebDriver(httpCommandExecutor, finalCapabilities);
                ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
            } else {
                Constructor<? extends RemoteWebDriver> constructor = driverClass.getConstructor(Capabilities.class);
                driver = constructor.newInstance(finalCapabilities);
            }
        } catch (Exception e) {
            WebDriverSessionsManager.SESSION_STARTUP_ERRORS.put(new Date(), e);
            throw new TesterraSetupException("Error starting browser session", e);
        }

        return driver;
    }

    private File getPhantomJSBinary() {
        if (phantomjsFile == null) {
            log().info("Unpacking phantomJS...");
            try {
                phantomjsFile = Phanbedder.unpack(); //Phanbedder to the rescue!
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("failed to make target directory")) {
                    File tmp = new File(FileUtils.getTempDirectory(), "phantomjs" + System.currentTimeMillis());
                    phantomjsFile = Phanbedder.unpack(tmp);
                } else {
                    throw e;
                }
            }
            log().info("Unpacked phantomJS to: " + phantomjsFile);
        }
        return phantomjsFile;
    }
}
