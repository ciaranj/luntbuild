/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.luntsys.luntbuild.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.luntsys.luntbuild.facades.Constants;

/**
 * Luntbuild logger.
 * 
 * <p>This piece of code is mainly stripped from {@link org.apache.tools.ant.DefaultLogger}.
 * It acts as a Ant logger to record all Luntbuild build logs.</p>
 *
 * @author robin shine
 */
public class LuntbuildLogger implements BuildLogger {

    private static final long serialVersionUID = 1L;
    
	/** Default maximum size of XML log before we stop converting to HTML */
    public static final long DEFAULT_MAX_XML_CONVERSION_LENGTH = 100000L;
    
    /**
     * When in direct mode, message will be written to log without any decoration and
     * ignores its priority level
     */
    boolean directMode;
    /**
     * Size of left-hand column for right-justified task name.
     *
     * @see #messageLogged(BuildEvent)
     */
    public static final int LEFT_COLUMN_SIZE = 12;

    /**
     * PrintStream to write non-error messages to
     */
    protected PrintStream out = null;
    protected String outPath = null;

    /**
     * PrintStream to write error messages to
     */
    protected PrintStream err = null;
    protected String errPath = null;

    /**
     * Lowest level of message to write out
     */
    protected int msgOutputLevel = Project.MSG_ERR;

    /**
     * Time of the start of the build
     */
    private long startTime = System.currentTimeMillis();

    /**
     * Line separator
     */
    protected static final String lSep = StringUtils.LINE_SEP;

    /**
     * Whether or not to use emacs-style output
     */
    protected boolean emacsMode = false;

    /** DocumentBuilder to use when creating the document to start with. */
    private final DocumentBuilder builder;

    /** The complete log document for this build. */
    private final Document doc;
    /**
     * When the build started.
     */
    private TimedElement buildElement = null;

    /** Utility class representing the time an element started. */
    private static class TimedElement {
        /**
         * Start time in milliseconds
         * (as returned by <code>System.currentTimeMillis()</code>).
         */
        public long startTime;
        /** Element created at the start time. */
        public Element element;

        /**
         * Gets the string representation of this element.
         * @return the string representation
         */
        public String toString() {
            return this.element.getTagName() + ":" + this.element.getAttribute("name");
        }
    }
    /** XML element name for a build. */
    private static final String BUILD_TAG = "build";
    /** XML element name for a message. */
    private static final String MESSAGE_TAG = "message";
    /** XML attribute name for a message priority. */
    private static final String PRIORITY_ATTR = "priority";
    /** XML attribute name for a target. */
    private static final String TARGET_ATTR = "target";
    /** XML attribute name for a task. */
    private static final String TASK_ATTR = "task";
    /** XML attribute name for a time. */
    private static final String TIME_ATTR = "time";
    /** XML attribute name for a builder. */
    private static final String BUILDER_ATTR = "builder";

    private String builderName = null;

    private Pattern targetPat = Pattern.compile("^\\s*([\\w-\\.]+):$");
    private Pattern taskPat = Pattern.compile("^\\s*\\[(\\w+)\\]\\s*(.*)$");
    private String curTarget = null;

    /**
     * Creates a new Luntbuild logger.
     */
    public LuntbuildLogger() {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        doc = builder.newDocument();
        this.buildElement = new TimedElement();
        this.buildElement.startTime = System.currentTimeMillis();
        this.buildElement.element = this.doc.createElement(BUILD_TAG);
    }

    /**
     * Sets the highest level of message this logger should respond to.
     * 
     * <p>Only messages with a message level lower than or equal to the
     * given level should be written to the log.</p>
     * 
     * <p>Constants for the message levels are in the
     * {@link Project} class. The order of the levels, from least
     * to most verbose, is <code>MSG_ERR</code>, <code>MSG_WARN</code>,
     * <code>MSG_INFO</code>, <code>MSG_VERBOSE</code>,
     * <code>MSG_DEBUG</code>.</p>
     * 
     * <p>The default message level for <code>DefaultLogger</code> is <code>MSG_ERR<code>.<p>
     *
     * @param level the logging level for the logger
     * @see Project#MSG_ERR
     * @see Project#MSG_WARN
     * @see Project#MSG_INFO
     * @see Project#MSG_VERBOSE
     * @see Project#MSG_DEBUG
     */
    public void setMessageOutputLevel(int level) {
        this.msgOutputLevel = level;
    }

    /**
     * Gets the current message level.
     * 
     * @return the message level
     */
    public int getMessageOutputLevel(){
        return this.msgOutputLevel;
    }

    /**
     * Sets the output stream to which this logger is to send its output.
     *
     * @param theOut the output stream, must not be <code>null</code>
     */
    public void setOutputPrintStream(PrintStream theOut) {
        this.out = theOut;
    }

    /**
     * Sets the output file to which this logger is to send its output.
     * 
     * @param path the path to the output file
     * @throws FileNotFoundException if log file not found
     */
    public void setOutputPath(String path) throws FileNotFoundException {
        this.outPath = path;
        setOutputPrintStream(new PrintStream(new FileOutputStream(path)));
    }

    /**
     * Gets the output file to which this logger is to send its output.
     * 
     * @return the path to the output file
     */
    public String getOutputPath() {
        return this.outPath;
    }

    /**
     * Gets the log.
     * 
     * @return the log
     */
    public Node getLog() {
        return this.buildElement.element;
    }

    /**
     * Sets the output stream to which this logger is to send its error messages.
     *
     * @param theErr the error stream, must not be <code>null</code>
     */
    public void setErrorPrintStream(PrintStream theErr) {
        this.err = theErr;
    }

    /**
     * Sets the output file to which this logger is to send its error messages.
     * 
     * @param path the path to the output file
     * @throws FileNotFoundException if log file not found
     */
    public void setErrorPath(String path) throws FileNotFoundException {
        this.errPath = path;
        setErrorPrintStream(new PrintStream(new FileOutputStream(path)));
    }

    /**
     * Gets the output file to which this logger is to send its error messages.
     * 
     * @return the path to the output file
     */
    public String getErrorPath() {
        return this.errPath;
    }

    /**
     * Sets this logger to produce emacs (and other editor) friendly output.
     * 
     * @param emacsMode set <code>true</code> if output is to be unadorned so that
     *                  emacs and other editors can parse files names, etc.
     */
    public void setEmacsMode(boolean emacsMode) {
        this.emacsMode = emacsMode;
    }

    /**
     * Sets the name of the builder.
     * 
     * @param name the name
     */
    public void setBuilderName(String name) {
        this.builderName = name;
    }

    /**
     * Responds to a build being started by just remembering the current time.
     * 
     * @param event ignored
     */
    public void buildStarted(BuildEvent event) {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Responds to a build being finished by printing a success condition,
     * errors that occurred during the build, and the duration of the build.
     * 
     * @param event an event with any relevant extra information, must not be <code>null</code>
     */
    public void buildFinished(BuildEvent event) {
        Throwable error = event.getException();
        StringBuffer message = new StringBuffer();

        if (error == null) {
            message.append(StringUtils.LINE_SEP);
            message.append("BUILD SUCCESSFUL");
        } else {
            message.append(StringUtils.LINE_SEP);
            message.append("BUILD FAILED");
            message.append(StringUtils.LINE_SEP);

            if (Project.MSG_VERBOSE <= this.msgOutputLevel
                    || !(error instanceof BuildException)) {
                message.append(StringUtils.getStackTrace(error));
            } else {
                if (error instanceof BuildException) {
                    message.append(error.toString()).append(lSep);
                } else {
                    message.append(error.getMessage()).append(lSep);
                }
            }
        }
        message.append(StringUtils.LINE_SEP);
        message.append("Total time: ");
        message.append(formatTime(System.currentTimeMillis() - this.startTime));

        String msg = message.toString();
        if (error == null) {
            printMessage(msg, this.out, Project.MSG_VERBOSE);
        } else {
            printMessage(msg, this.err, Project.MSG_ERR);
        }
    }

    /**
     * Responds to a target being started by logging a message if this
     * logger allows information-level messages.
     * 
     * @param event an event with any relevant extra information, must not be <code>null</code>
     */
    public void targetStarted(BuildEvent event) {
        if (Project.MSG_INFO <= this.msgOutputLevel
                && !event.getTarget().getName().equals("")) {
            String msg = StringUtils.LINE_SEP
                    + event.getTarget().getName() + ":";
            printMessage(msg, this.out, event.getPriority());
        }
    }

    /**
     * Responds to a target being finished.
     * 
     * @param event ignored
     */
    public void targetFinished(BuildEvent event) {
    }

    /**
     * Responds to a task being started.
     * 
     * @param event ignored
     */
    public void taskStarted(BuildEvent event) {
    }

    /**
     * Responds to a task being finished.
     * 
     * @param event ignored
     */
    public void taskFinished(BuildEvent event) {
    }

    /**
     * Logs a message for the specified event, if the priority is suitable.
     * In non-emacs mode, task level messages are prefixed by the
     * task name which is right-justified.
     *
     * @param event a <code>BuildEvent</code> containing message information, must not be <code>null</code>
     */
    public void messageLogged(BuildEvent event) {
        if (directMode) {
            printMessage(event.getMessage(), out, event.getPriority());
            messageLoggedXml(event, builderName);
        } else {
            int priority = event.getPriority();
            // Filter out messages based on priority
            if (priority <= msgOutputLevel) {
                StringBuffer message = new StringBuffer();
                if (event.getTask() != null && !emacsMode) {
                    // Print out the name of the task if we're in one
                    String name = event.getTask().getTaskName();
                    String label = "[" + name + "] ";
                    int size = LEFT_COLUMN_SIZE - label.length();
                    StringBuffer tmp = new StringBuffer();
                    for (int i = 0; i < size; i++) {
                        tmp.append(" ");
                    }
                    tmp.append(label);
                    label = tmp.toString();

                    BufferedReader r = null;
                    try {
                        r = new BufferedReader(new StringReader(event.getMessage()));
                        String line = r.readLine();
                        boolean first = true;
                        while (line != null) {
                            if (!first) {
                                message.append(StringUtils.LINE_SEP);
                            }
                            first = false;
                            message.append(label).append(line);
                            line = r.readLine();
                        }
                    } catch (IOException e) {
                        // shouldn't be possible
                        message.append(label).append(event.getMessage());
                    } finally {
                        if (r != null) try {r.close();} catch (Exception e) {}
                    }
                    messageLoggedXml(event, builderName);
                } else {
                    message.append(event.getMessage());
                    messageLoggedXml(event, builderName);
                }

                String msg = message.toString();
                if (priority != Project.MSG_ERR) {
                    printMessage(msg, out, priority);
                } else {
                    printMessage(msg, err, priority);
                }
            }
        }
    }

    /**
     * Fired when a message is logged, this adds a message element to the
     * most appropriate parent element (task, target or build) and records
     * the priority and text of the message.
     *
     * @param event an event with any relevant extra information, must not be <code>null</code>
     * @param bname the builder name
     */
    public void messageLoggedXml(BuildEvent event, String bname) {
        String msg = event.getMessage();
        if (msg == null || msg.trim().length() == 0) return;

        // Filter out target name message from XML
        Matcher m = this.targetPat.matcher(msg);
        if (m.matches()) {
            this.curTarget = m.group(1);
            return;
        }

        // Check target name
        if (event.getTarget() != null) {
            curTarget = event.getTarget().getName();
        }

        // Begin message construction
        Element messageElement = doc.createElement(MESSAGE_TAG);

        // Set priority
        String name = "debug";
        switch (event.getPriority()) {
            case Project.MSG_ERR:
                name = "error";
                break;
            case Project.MSG_WARN:
                name = "warn";
                break;
            case Project.MSG_INFO:
                name = "info";
                break;
            default:
                name = "debug";
                break;
        }
        messageElement.setAttribute(PRIORITY_ATTR, name);

        // Set builder name
        if (bname != null)
            messageElement.setAttribute(BUILDER_ATTR, bname);

        // Set target
        if (curTarget != null)
            messageElement.setAttribute(TARGET_ATTR, curTarget);

        // Set task
        m = taskPat.matcher(msg);
        if (m.matches()) {
            if (event.getTask() != null) {
                messageElement.setAttribute(TASK_ATTR, event.getTask().getTaskName());
            } else {
                messageElement.setAttribute(TASK_ATTR, m.group(1));
            }
            msg = m.group(2);
        }

        // Set main body
        msg = Luntbuild.xmlEncodeEntities(msg);
        StringBuffer message = new StringBuffer();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new StringReader(msg));
            String line = r.readLine();
            boolean first = true;
            while (line != null) {
                if (!first) message.append("</br>");
                first = false;
                message.append(line);
                line = r.readLine();
            }
        } catch (IOException e) {
            // shouldn't be possible
            message.append(msg);
        } finally {
            if (r != null) try {r.close();} catch (Exception e) {}
        }

        // Add message to log
        Text messageText = doc.createCDATASection(message.toString());
        messageElement.appendChild(messageText);
        buildElement.element.appendChild(messageElement);
    }

    /**
     * Formats a duration in milliseconds into a string.
     *
     * @param millis the length of time to format, in milliseconds
     * @return the time as a formatted string
     * @see DateUtils#formatElapsedTime(long)
     */
    protected static String formatTime(final long millis) {
        return DateUtils.formatElapsedTime(millis);
    }

    /**
     * Prints a message to a PrintStream.
     *
     * @param message  the message to print, should not be <code>null</code>
     * @param stream   a <code>PrintStream</code> to print the message to, must not be <code>null</code>
     * @param priority the priority of the message (Ignored in this implementation)
     */
    protected void printMessage(final String message,
                                final PrintStream stream,
                                final int priority) {
        if (stream != null)
            stream.println(message);
    }

    /**
     * Checks if this logger is in direct mode.
     * 
     * @return <code>true</code> if this logger is in direct mode
     */
    public boolean isDirectMode() {
        return this.directMode;
    }

    /**
     * Sets this logger to direct mode.
     * 
     * @param directMode set <code>true</code> to turn on direct mode for this logger
     */
    public void setDirectMode(boolean directMode) {
        this.directMode = directMode;
        this.curTarget = null;
    }

    /**
     * Writes the current log of this logger to an XML log file.
     * 
     * @param outFilename the name of the output XML file
     * @param xslUri the URI of the XSL stylesheet for XML to HTML conversion, can be <code>null</code>
     * @throws BuildException if unable to write the log file
     */
    public void logXml(String outFilename, String xslUri) {
        long totalTime = System.currentTimeMillis() - this.buildElement.startTime;
        this.buildElement.element.setAttribute(TIME_ATTR,
                DateUtils.formatElapsedTime(totalTime));

        if (xslUri == null) {
            xslUri = Luntbuild.getServletRootUrl() + "/log.xsl";
        } else {
            String installDir = Luntbuild.installDir.replaceAll("\\\\","\\\\\\\\");
            xslUri = xslUri.replaceAll(installDir, Luntbuild.getServletRootUrl());
        }
        Writer output = null;
        OutputStream stream = null;
        try {
            // specify output in UTF8 otherwise accented characters will blow
            // up everything
            stream = new FileOutputStream(outFilename);
            output = new OutputStreamWriter(stream, "UTF8");
            output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            if (xslUri.length() > 0) {
                output.write("<?xml-stylesheet type=\"text/xsl\" href=\""
                        + xslUri + "\"?>\n\n");
            }
            (new DOMElementWriter()).write(this.buildElement.element, output, 0, "\t");
            output.flush();
        } catch (IOException exc) {
            throw new BuildException("Unable to write log file", exc);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if(stream != null) try {stream.close();} catch (Exception e) {}
        }
    }
    
    public static long getMaxXmlConversionLength() {
		String maxXmlConversionLengthText = (String) Luntbuild.getProperties().get(Constants.MAX_XML_CONVERSION_LENGTH);
        try {
            long maxXmlConversionLength = new Long(maxXmlConversionLengthText).longValue();
            if (maxXmlConversionLength <= 0)
                return DEFAULT_MAX_XML_CONVERSION_LENGTH;
            else
                return maxXmlConversionLength;
        } catch (NumberFormatException e) {
            return DEFAULT_MAX_XML_CONVERSION_LENGTH;
        }
    }

    /**
     * Writes the current log of this logger to XML, text and HTML log files.
     * 
     * @param xmlFilename the name of the output XML file
     * @param xslUri the URI of the XSL stylesheet for XML to HTML conversion, must not be <code>null</code>
     * @param outFilename the name of the output HTML file
     * @param textFilename the name of the output text file
     */
    public void logHtml(String xmlFilename, String xslUri, String outFilename, String textFilename) {

        logXml(xmlFilename, xslUri);

        File xmlFile = new File(xmlFilename);
        if (xslUri == null || xmlFile.length() > getMaxXmlConversionLength()) {
            logHtmlFromText(textFilename, outFilename);
            return;
        }
        File xsltFile = new File(xslUri);
        // JAXP reads data using the Source interface
        Source xmlSource = new StreamSource(xmlFile);
        Source xsltSource = new StreamSource(xsltFile);

        try {
            // the factory pattern supports different XSLT processors
            TransformerFactory transFact = TransformerFactory.newInstance();
            Transformer trans = transFact.newTransformer(xsltSource);

            // Output
            PrintStream pout = null;
            try {
                pout = new PrintStream(new FileOutputStream(outFilename));
                trans.transform(xmlSource, new StreamResult(pout));
            } catch (Exception ex) {
                this.err.println("Can't open output file " + outFilename);
                logHtmlFromText(textFilename, outFilename);
            } finally {
                if (pout != null) {
                    pout.close();
                }
            }
        } catch (Throwable e) {
            logHtmlFromText(textFilename, outFilename);
        }
    }

    /**
     * Writes a log as HTML from a text log file.
     * 
     * @param textFilename the name of the input text file
     * @param outFilename the name of the output HTML file
     */
    public static void logHtmlFromText(String textFilename, String outFilename) {

        PrintStream pout = null;
        FileReader in = null;
        try {
            pout = new PrintStream(new FileOutputStream(outFilename));
            pout.println("<html>");
            pout.println("<body bgcolor='#FFFFFF' topmargin='6' leftmargin='6'>");
            pout.println("<pre>");
            in = new FileReader(textFilename);
            int ch;
            while ((ch = in.read()) != -1) pout.print((char)ch);
            pout.println("</pre>");
            pout.println("</html>");
        } catch (Exception ex) {
            System.err.println("Can't open output file " + outFilename);
        } finally {
            if (pout != null) {
                pout.close();
            }
            if (in != null) try {in.close();} catch (Exception e) {}
        }
    }
    
    public void close() {
        if (err != null) {
            err.close();
        }
        if (out != null) {
            out.close();
        }
    }
}
