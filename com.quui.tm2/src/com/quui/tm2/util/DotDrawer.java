package com.quui.tm2.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Class for drawing dot graphs, using an given dot installation, which is
 * called using Runtime.exec()
 * @author fsteeg
 */
public class DotDrawer {

    // some config values

    public String DOT_CALL = "dot"; //$NON-NLS-1$

    private String OUTPUT_FORMAT = "-T";

    public final String VAR = "-o";

    public String RESULT_PNG; //$NON-NLS-1$

    public String DOT_FILE; //$NON-NLS-1$

    public String DOT_APP_PATH;

    public String OUTPUT_FOLDER;

    String[] COMMANDS;

    private static final String CAPTION_DOT_SELECT_SHORT = "Short"; //$NON-NLS-1$

    private static final String CAPTION_DOT_SELECT_LONG = "Long"; //$NON-NLS-1$

    private String INPUT_FOLDER = null;

    protected boolean DONE;

    private Logger logger;

    /**
     * @param outputFolder The path to write the result to
     * @param dotLocation
     */
    public DotDrawer(String inputFolder, String outputFolder, String in,
            String out, String dotLocation) {
        OUTPUT_FOLDER = outputFolder;// + File.separator;
        INPUT_FOLDER = inputFolder;// + File.separator;
        DOT_FILE = in;
        RESULT_PNG = out;
        DOT_APP_PATH = dotLocation;
        try {
            COMMANDS = new String[] {
                    DOT_APP_PATH + DOT_CALL,
                    OUTPUT_FORMAT,
                    VAR,
                    new File(new URL(OUTPUT_FOLDER + RESULT_PNG).toURI())
                            .getAbsolutePath(),
                    new File(new URL(INPUT_FOLDER + DOT_FILE).toURI())
                            .getAbsolutePath() };
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        logger = AmasLogger.singleton(this.getClass());
    }

    /**
     * Calls dot to render the image from the generated dot-file
     * @return
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public int renderImage(String format) throws InvocationTargetException,
            InterruptedException {
        if (this.OUTPUT_FORMAT.length() <= 2) this.OUTPUT_FORMAT = this.OUTPUT_FORMAT
                + format;
        COMMANDS[1] = OUTPUT_FORMAT;
        logger.debug("Will use command:");
        for (String command : COMMANDS) {
            logger.debug("command: " + command);
        }
        Runtime runtime = Runtime.getRuntime();
        Process p = null;
        try {
            p = runtime.exec(COMMANDS);
            p.waitFor();
            DONE = true;
        } catch (Exception x) {
            x.printStackTrace();
        }
        int exitValue = p.exitValue();
        logger.debug("Exit status: " + exitValue); //$NON-NLS-1$
        if (exitValue != 0) { throw new IllegalStateException(String.format(
                "Unsucessful DOT call '%s', resulted in error code: %s", Arrays
                        .asList(COMMANDS), exitValue)); }
        return exitValue;
    }
}
