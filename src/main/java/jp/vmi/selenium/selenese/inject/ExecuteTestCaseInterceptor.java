package jp.vmi.selenium.selenese.inject;

import java.util.Objects;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.junit.result.JUnitResultHolder;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 * Interceptor for logging and recoding test-case result.
 */
public class ExecuteTestCaseInterceptor extends AbstractExecuteTestCaseInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestCaseInterceptor.class);

    @Override
    protected Result invoke(MethodInvocation invocation, ITestCase testCase, Selenese parent, Context context) throws Throwable {
        JUnitResult jUnitResult;
        if (context instanceof JUnitResultHolder) {
            jUnitResult = ((JUnitResultHolder) context).getJUnitResult();
            jUnitResult.startTestCase(parent, testCase);
        } else {
            jUnitResult = null;
        }
        StopWatch sw = testCase.getStopWatch();
        boolean hasParent = parent instanceof TestCase;
        LogRecorder clr = testCase.getLogRecorder();
        LogRecorder plr = null;
        if (hasParent) {
            plr = clr;
            clr = ((TestCase) parent).getLogRecorder();
            testCase.setLogRecorder(clr);
        } else if (clr == null) {
            clr = new LogRecorder(context.getPrintStream());
            testCase.setLogRecorder(clr);
        }
        try {
            sw.start();
            if (!testCase.isError()) {
                log.info("Start: {}", testCase);
                clr.info("Start: " + testCase);
            }
            if (testCase instanceof TestCase) {
                String baseURL = Objects.toString(context.getOverridingBaseURL(), ((TestCase) testCase).getBaseURL());
                log.info("baseURL: {}", baseURL);
                clr.info("baseURL: " + baseURL);
            }
            try {
                Result result = (Result) invocation.proceed();
                if (jUnitResult != null) {
                    if (result.isSuccess())
                        jUnitResult.setSuccess(testCase);
                    else
                        jUnitResult.setFailure(testCase, result.getMessage(), null);
                }
                return result;
            } catch (Throwable t) {
                String msg = t.getMessage();
                log.error(msg);
                clr.error(msg);
                if (jUnitResult != null)
                    jUnitResult.setError(testCase, msg, t.toString());
                throw t;
            } finally {
                sw.end();
                if (!testCase.isError()) {
                    String msg = "End(" + sw.getDurationString() + "): " + testCase;
                    log.info(msg);
                    clr.info(msg);
                }
                if (jUnitResult != null)
                    jUnitResult.endTestCase(testCase);
            }
        } finally {
            if (hasParent)
                testCase.setLogRecorder(plr);
        }
    }
}
