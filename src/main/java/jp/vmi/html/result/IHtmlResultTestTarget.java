package jp.vmi.html.result;

import jp.vmi.selenium.selenese.Selenese.Type;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.result.CommandResultList;

/**
 * Test target interface for HTML result.
 */
public interface IHtmlResultTestTarget {

    /**
     * Get selenese name.
     *
     * @return selenese name.
     */
    String getName();

    /**
     * Get selenese type.
     *
     * @return selenese type.
     */
    Type getType();

    /**
     * Get selenese result.
     *
     * @return selenese result.
     */
    Result getResult();

    /**
     * Get filename of test-case.
     *
     * @return filename.
     */
    String getFilename();

    /**
     * Get command list.
     *
     * @return command list.
     */
    CommandList getCommandList();

    /**
     * Get test-case result list.
     *
     * @return test-case result list.
     */
    CommandResultList getResultList();
}
