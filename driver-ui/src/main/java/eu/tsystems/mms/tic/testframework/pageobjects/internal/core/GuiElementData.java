/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.internal.ExecutionLog;
import eu.tsystems.mms.tic.testframework.logging.LogLevel;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.pageobjects.filter.WebElementFilter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.TimerWrapper;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnhb on 21.12.2016.
 */
public class GuiElementData {

    public final By by;
    @Deprecated // Evil, should never be used!!!
    public final GuiElement guiElement;

    public final WebDriver webDriver;
    public String name;
    public final ExecutionLog executionLog;
    public final List<WebElementFilter> webElementFilters;
    public int timeoutInSeconds;
    public final TimerWrapper timerWrapper;
    public WebElement webElement;
    public final FrameLogic frameLogic;
    public final int timerSleepTimeInMs = 500;
    public boolean sensibleData = false;
    public GuiElementCore parent;
    public int index = -1;
    public LogLevel logLevel = LogLevel.DEBUG;
    public LogLevel storedLogLevel = logLevel;
    public String browser;
    public boolean shadowRoot = false;

    public GuiElementData(WebDriver webDriver, String name, FrameLogic frameLogic, By by, GuiElement guiElement) {
        this.webDriver = webDriver;
        this.name = name;
        this.by = by;
        this.guiElement = guiElement;
        this.executionLog = new ExecutionLog();
        this.timeoutInSeconds = POConfig.getUiElementTimeoutInSeconds();
        this.frameLogic = frameLogic;

        this.webElementFilters = new ArrayList<>();
        // Central Timer Object which is used by all sequence executions
        this.timerWrapper = new TimerWrapper(timerSleepTimeInMs, timeoutInSeconds, webDriver, executionLog);
    }

    public Logger getLogger() {
        return LoggerFactory.getLogger("GuiElement" + (name == null ? "" : " " + name));
    }

    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        timerWrapper.setTimeoutInSeconds(timeoutInSeconds);
    }

    public boolean hasName() {
        return !StringUtils.isEmpty(name);
    }

    @Override
    public String toString() {
        String toString = by.toString();
        if (parent != null) {
            toString += " child of " + parent;
        }
        if (webElementFilters != null && !webElementFilters.isEmpty()) {
            toString += " with WebElementFilter" + (webElementFilters.size() > 1 ? "s" : "") + " { ";
            for (WebElementFilter webElementFilter : webElementFilters) {
                toString += webElementFilter.toString() + ", ";
            }
            // cut the last comma with space
            toString = toString.substring(0, toString.length() - 2) + " }";
        }

        if (hasName()) {
            toString = ">" + name + "< (" + toString + ")";
        }

        if (hasFrameLogic()) {
            String frameString = " inside Frames={";
            if (frameLogic.hasFrames()) {
                for (GuiElement frame : frameLogic.getFrames()) {
                    frameString += frame.toString() + ", ";
                }
            } else {
                frameString += "autodetect, ";
            }
            frameString = frameString.substring(0, frameString.length() - 2);
            toString = toString + frameString + "}";
        }
        return toString;
    }

    public boolean hasFrameLogic() {
        return frameLogic != null;
    }

    public GuiElementData copy() {
        FrameLogic frameLogic = null;
        if (this.frameLogic != null) {
            frameLogic = new FrameLogic(webDriver, this.frameLogic.getFrames());
        }
        GuiElementData guiElementData = new GuiElementData(webDriver, this.name, frameLogic, by, this.guiElement);
        guiElementData.webElementFilters.addAll(this.webElementFilters);
        return guiElementData;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        // store previous log level away
        this.storedLogLevel = this.logLevel;
        // set new log level
        this.logLevel = logLevel;
    }

    public void resetLogLevel() {
        this.logLevel = this.storedLogLevel;
    }

}