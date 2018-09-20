package eu.tsystems.mms.tic.testframework.report.test;

import eu.tsystems.mms.tic.testframework.annotations.FennecClassContext;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractFailurePointsPage;
import eu.tsystems.mms.tic.testframework.report.abstracts.AbstractResultTableFailureEntry;
import eu.tsystems.mms.tic.testframework.report.general.AbstractReportFailuresTest;
import eu.tsystems.mms.tic.testframework.report.general.ReportDirectory;
import eu.tsystems.mms.tic.testframework.report.general.SystemTestsGroup;
import eu.tsystems.mms.tic.testframework.report.model.ExitPointEntry;
import eu.tsystems.mms.tic.testframework.report.model.ResultTableFailureType;
import eu.tsystems.mms.tic.testframework.report.model.TestReportTwoExitPoints;
import eu.tsystems.mms.tic.testframework.report.model.TestResultHelper;
import eu.tsystems.mms.tic.testframework.report.workflows.GeneralWorkflow;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.annotations.BeforeMethod;

import java.util.List;

/**
 * Created by riwa on 04.04.2017.
 */
@FennecClassContext("View-ExitPoints")
public class ExitPointsPageTest extends AbstractReportFailuresTest {

    @BeforeMethod(alwaysRun = true)
    @Override
    public void initTestObjects() {
        this.failurePointType = ResultTableFailureType.EXIT_POINT;
        this.reportFilter = SystemTestsGroup.SYSTEMTESTSFILTER2;
        this.failurePointEntryTestObjects = getExpectedFailurePointEntries();
    }

    @Override
    protected int getNumberOfExpectedFailurePointsForReport() {
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                return TestReportTwoExitPoints.NUMBER_OF_EXIT_POINTS;
            default:
                throw new FennecRuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected int getNumberOfExpectedFailurePointsForTestResult(TestResultHelper.TestResultFailurePointEntryType entryType) {
        int counter = 0;
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                for (ExitPointEntry exitPoint:TestReportTwoExitPoints.getAllExitPointEntryTestObjects()) {
                    if (exitPoint.getFailurePointEntryType().equals(entryType)) {
                        counter++;
                    }
                }
                return counter;
            default:
                throw new FennecRuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected List<? extends AbstractResultTableFailureEntry> getExpectedFailurePointEntries() {
        switch (reportFilter) {
            case SystemTestsGroup.SYSTEMTESTSFILTER2:
                return TestReportTwoExitPoints.getAllExitPointEntryTestObjects();
            default:
                throw new FennecRuntimeException("Not implemented for Report: " + reportFilter);
        }
    }

    @Override
    protected void checkExpectedFailedMarkWorkflow(boolean intoReport) {
        final int position = 2;
        final AbstractResultTableFailureEntry failedEntry = failurePointEntryTestObjects.get(position - 1);
        AbstractFailurePointsPage failurePointsPage = openFailuresPointsPage(ReportDirectory.REPORT_DIRECTORY_2);
        failurePointsPage.assertExpectedFailsReportMark(failedEntry, intoReport);
    }

    @Override
    protected AbstractFailurePointsPage openFailuresPointsPage(ReportDirectory reportDirectory) {
        return GeneralWorkflow.doOpenBrowserAndReportExitPointsPage(WebDriverManager.getWebDriver(), PropertyManager.getProperty(reportDirectory.toString()));
    }

}