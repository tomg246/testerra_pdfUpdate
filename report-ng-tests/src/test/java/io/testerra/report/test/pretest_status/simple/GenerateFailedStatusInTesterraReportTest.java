/*
 * Testerra
 *
 * (C) 2022, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package io.testerra.report.test.pretest_status.simple;

import eu.tsystems.mms.tic.testframework.exceptions.PageNotFoundException;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.report.FailureCorridor;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GenerateFailedStatusInTesterraReportTest extends TesterraTest {

    @Test
    @FailureCorridor.Mid
    public void test_Failed() {
        Assert.fail("Creating TestStatus 'Failed'");
    }

    @Test
    public void testAssertCollector() {
        AssertCollector.fail("failed1");
        AssertCollector.fail("failed2");
        AssertCollector.assertTrue(true, "passed1");
    }

    @Test
    @FailureCorridor.Mid
    public void test_failedPageNotFound() {
        throw new PageNotFoundException("Test page not reached.");
    }
}
