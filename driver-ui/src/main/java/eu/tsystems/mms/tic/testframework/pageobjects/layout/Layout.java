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
 package eu.tsystems.mms.tic.testframework.pageobjects.layout;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

@Deprecated
public abstract class Layout implements ILayout {

    private boolean innerBorders = false;

    private Layout(boolean innerBorders) {
        this.innerBorders = innerBorders;
    }

    public static ILayout inner() {
        /*
        Dummy impl.
         */
        ILayout layout = new Layout(true) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                //dummy
            }

            @Override
            public String toStringText() {
                return null;
            }
        };

        return layout;
    }

    public static ILayout outer() {
        /*
        Dummy impl.
         */
        ILayout layout = new Layout(false) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                //dummy
            }

            @Override
            public String toStringText() {
                return null;
            }
        };

        return layout;
    }

    @Fails(validFor = "unsupportedBrowser", description = "Unsupported Browser")
    public LayoutBorders getElementLayoutBorders(UiElement guiElement) {
        if (innerBorders) {
            Map<String, Long> borders = JSUtils.getElementInnerBorders(guiElement);
            if (borders != null) {
                return new LayoutBorders(borders.get("left"), borders.get("right"), borders.get("top"), borders.get("bottom"));
            }
            return new LayoutBorders(0, 0, 0, 0);
        } else {
            AtomicReference<Point> atomicLocation = new AtomicReference<>();
            AtomicReference<Dimension> atomicSize = new AtomicReference<>();
            guiElement.findWebElement(webElement -> {
                atomicLocation.set(webElement.getLocation());
                atomicSize.set(webElement.getSize());
            });
            Point location = atomicLocation.get();
            Dimension size = atomicSize.get();
            int left = location.getX();
            int right = left + size.getWidth();
            int top = location.getY();
            int bottom = top + size.getHeight();
            return new LayoutBorders(left, right, top, bottom);
        }
    }

    public ILayout leftOf(final UiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.left;
                long reference = distLB.left;
                configuredAssert.assertTrue(actual < reference, ">" + actualGE + "< is left of >" + distanceGE + "< (" + actual + "<" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "leftOf " + distanceGE;
            }
        };
    }

    public String toString() {
        return toStringText();
    }

    public ILayout above(final UiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.top;
                long reference = distLB.top;
                configuredAssert.assertTrue(actual < reference, ">" + actualGE + "< is above >" + distanceGE + "< (" + actual + "<" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "above " + distanceGE;
            }
        };
    }

    @Override
    public ILayout rightOf(final UiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.right;
                long reference = distLB.right;
                configuredAssert.assertTrue(actual > reference, ">" + actualGE + "< is right of >" + distanceGE + "< (" + actual + ">" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "rightOf " + distanceGE;
            }
        };
    }

    public ILayout below(final UiElement distanceGE) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.bottom;
                long reference = distLB.bottom;
                configuredAssert.assertTrue(actual > reference, ">" + actualGE + "< is below >" + distanceGE + "< (" + actual + ">" + reference + ")");
            }

            @Override
            public String toStringText() {
                return "below " + distanceGE;
            }
        };
    }

    @Override
    public ILayout sameTop(final UiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.top;
                long reference = distLB.top;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same top coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same top coords as " + distanceGE;
            }
        };
    }

    @Override
    public ILayout sameBottom(final UiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.bottom;
                long reference = distLB.bottom;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same bottom coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same bottom coords as " + distanceGE;
            }
        };
    }

    @Override
    public ILayout sameLeft(final UiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.left;
                long reference = distLB.left;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same left coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same left coords as " + distanceGE;
            }
        };
    }

    @Override
    public ILayout sameRight(final UiElement distanceGE, final int delta) {
        return new Layout(this.innerBorders) {
            @Override
            public void checkOn(UiElement actualGE, Assertion configuredAssert) {
                LayoutBorders actLB = getElementLayoutBorders(actualGE);
                LayoutBorders distLB = getElementLayoutBorders(distanceGE);
                long actual = actLB.right;
                long reference = distLB.right;

                long max = reference + delta;
                long min = reference - delta;
                configuredAssert.assertTrue(actual <= max && actual >= min, ">" + actualGE + "< has same right coords as >" + distanceGE + "< (" + actual + "==" + reference + " +-" + delta + ")");
            }

            @Override
            public String toStringText() {
                return "same right coords as " + distanceGE;
            }
        };
    }

}
