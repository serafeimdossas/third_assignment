package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.highlight.HighlightHandler;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class HighlightInterceptor extends AbstractDoCommandInterceptor {

    @Override
    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
        if (context instanceof HighlightHandler) {
            HighlightHandler handler = (HighlightHandler) context;
            handler.unhighlight();
            if (handler.isHighlight()) {
                int i = 0;
                Locator[] plocs = command.extractLocators(curArgs);
                for (Locator ploc : plocs)
                    handler.highlight(ploc, HighlightStyle.ELEMENT_STYLES[i++]);
            }
        }
        return (Result) invocation.proceed();
    }
}
