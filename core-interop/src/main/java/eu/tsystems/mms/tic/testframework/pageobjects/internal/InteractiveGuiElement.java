package eu.tsystems.mms.tic.testframework.pageobjects.internal;

public interface InteractiveGuiElement extends TestableGuiElement {
    InteractiveGuiElement select(Boolean select);
    InteractiveGuiElement click();
    InteractiveGuiElement clickJS();
    InteractiveGuiElement doubleClick();
    InteractiveGuiElement doubleClickJS();
    InteractiveGuiElement rightClick();
    InteractiveGuiElement rightClickJS();
    InteractiveGuiElement highlight();
    InteractiveGuiElement swipe(int offsetX, int offSetY);
    InteractiveGuiElement select();
    InteractiveGuiElement deselect();
    InteractiveGuiElement type(String text);
    InteractiveGuiElement sendKeys(CharSequence... charSequences);
    InteractiveGuiElement clear();
    InteractiveGuiElement mouseOver();
    InteractiveGuiElement mouseOverJS();
}