package jp.vmi.selenium.selenese.locator;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class CSSHandlerTest {

    @Test
    public void testCSSSelector() {
        assertThat(
            getFixedCssSelector("css=div.tag-1. > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"),
            is(equalTo("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]")));

        assertThat(
            getFixedCssSelector("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]"),
            is(equalTo("css=div.tag-1 > div.col-md-12 > div.form-group > div.col-md-5 > div.bootstrap-tagsinput > input[type=\"text\"]")));
    }

    private String getFixedCssSelector(String css) {
        CSSHandler cssHandler = new CSSHandler();
        DummyDriver dummyDriver = new DummyDriver();
        cssHandler.handle(dummyDriver, css);
        String byString = dummyDriver.by.toString();
        return byString.substring(byString.indexOf(':') + 2);
    }
}
