/* 
 * 
 */

package com.luntsys.luntbuild.repliers;

import java.io.*;
import java.util.*;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.utility.Luntbuild;

/**
 * Encapsulates the logic for processing templates within Luntbuild.
 *
 * @author Dustin Hunter
 */
public abstract class TemplatedReplier extends Replier implements ReferenceInsertionEventHandler {
    /** logger */
    protected Log logger = null;
    /** template dir */
    public String templateDir = null;
    /** template file */
    public String templateFile = null;
    

    private Object ognlRoot = null;

    /** base template dir */
    public static final String TEMPLATE_BASE_DIR = Luntbuild.installDir + "/templates";
    private static final String QUOTE_FILE = "quotes.txt";
    private static final String TEMPLATE_DEF_FILE = "set-template.txt";
    private static final String DEFAULT_TEMPLATE = "default.vm";

    /**
     * Creates a templated replier.
     * 
     * @param logClass the log class
     * @param subdir the template subdir (in installdir/templates)
     */
    public TemplatedReplier(Class logClass, String subdir) {
        this.logger = LogFactory.getLog(logClass);
        this.templateDir = TEMPLATE_BASE_DIR + "/" + subdir;
        setTemplateFiles();
    }

    /**
     * Sets the template files from the default properties.
     */
    private void setTemplateFiles() {
    	setTemplateFiles("", "default");
    }

    /**
     * Sets the template files from the specified property. If the property does not exist, the default
     * properties will be used.  If the default properties do not exist, the default file names will be used.
     * 
     * @param templatePropertyName the property name to use
     */
    private void setTemplateFiles(String templatePropertyName, String templateProperty) {
        File f = new File(this.templateDir + "/" + TEMPLATE_DEF_FILE);
        if (!f.exists()) {
            this.logger.error("Unable to find template definition file " + f.getPath());
            this.templateFile = DEFAULT_TEMPLATE;
            return;
        }
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            props.load(in);
        } catch (IOException e) {
            this.logger.error("Unable to read template definition file " + f.getPath());
            this.templateFile = DEFAULT_TEMPLATE;
            return;
        } finally {
            if (in != null) try { in.close(); } catch (Exception e) {/*Ignore*/}
        }
        this.templateFile = props.getProperty(templatePropertyName + "_" + templateProperty);
        if (this.templateFile == null) this.templateFile = props.getProperty(templateProperty);
        if (this.templateFile == null) this.templateFile = DEFAULT_TEMPLATE;
    }

    /**
     * Initializes Velocity and uses the specified property for template files.
     * 
     * @param templatePropertyName the property name to use
     * @throws Exception from {@link Velocity#init(Properties)}
     */
    private void init(String templatePropertyName, String templateProperty) throws Exception {
        Properties props = new Properties();
        props.put("file.resource.loader.path", this.templateDir);
        props.put("runtime.log", "velocity.log");
        Velocity.init(props);
        setTemplateFiles(templatePropertyName, templateProperty);
    }

    /**
     * Processes the template for a build notification.
     * 
     * @param build the build
     * @param ctx a Velocity context
     * @throws Exception from {@link Velocity#getTemplate(String)}
     */
    private String processTemplate(Build build, VelocityContext ctx) throws Exception {
        return processTemplate(Velocity.getTemplate(this.templateFile), build, ctx);
    }

    /**
     * Processes the template for a schedule notification.
     * 
     * @param schedule the schedule
     * @param ctx a Velocity context
     * @throws Exception from {@link Velocity#getTemplate(String)}
     */
    private String processTemplate(Schedule schedule, VelocityContext ctx) throws Exception {
        return processTemplate(Velocity.getTemplate(this.templateFile), schedule, ctx);
    }

    /**
     * Processes the template for a build notification.
     * 
     * @param template the template
     * @param build the build
     * @param ctx a Velocity context
     * @throws Exception from {@link Velocity#init()}
     * @throws Exception from {@link #createContext(Build, VelocityContext)}
     * @throws Exception from {@link Template#merge(org.apache.velocity.context.Context, Writer)}
     */
    private String processTemplate(Template template, Build build, VelocityContext ctx) throws Exception {

        Velocity.init();

        VelocityContext context = createContext(build, ctx);
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(context);
        this.ognlRoot = build;

        // Process the template
        StringWriter writer = null;
        try {
            writer = new StringWriter();

            if (template != null)
                template.merge(context, writer);
            return writer.toString();
        } finally {
            writer.close();
        }
    }

    /**
     * Processes the template for a schedule notification.
     * 
     * @param template the template
     * @param schedule the schedule
     * @param ctx a Velocity context
     * @throws Exception from {@link Velocity#init()}
     * @throws Exception from {@link #createContext(Schedule, VelocityContext)}
     * @throws Exception from {@link Template#merge(org.apache.velocity.context.Context, Writer)}
     */
    private String processTemplate(Template template, Schedule schedule, VelocityContext ctx) throws Exception {

        Velocity.init();

        VelocityContext context = createContext(schedule, ctx);
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(context);
        this.ognlRoot = schedule;

        // Process the template
        StringWriter writer = null;
        try {
            writer = new StringWriter();

            if (template != null)
                template.merge(context, writer);
            return writer.toString();
        } finally {
            writer.close();
        }
    }

    /**
     * @inheritDoc
     */
    public Object referenceInsert(String reference, Object value) {
        if (value != null) return value;
        try {
            if (reference.startsWith("${")) {
                reference = reference.substring(2, reference.length() - 1);
            }
            Object expression = Ognl.parseExpression(reference);
            Map ctx = Ognl.createDefaultContext(this.ognlRoot);
            Object ognlValue;
            ognlValue = Ognl.getValue(expression, ctx, this.ognlRoot, String.class);
            return ognlValue;
        } catch (OgnlException ex) {
            return value;
        }
    }

	/**
     * Populates the context with the variables which are exposed to the build template.
     * 
     * @param build the build
     * @param ctx the Velocity context
     * @throws Exception from {@link #extractRootUrl(String)}
     */
    private VelocityContext createContext(Build build, VelocityContext ctx) throws Exception {
        VelocityContext context = new VelocityContext(ctx);

        context.put("luntbuild_webroot", extractRootUrl(build.getUrl()));

        // Project Info
        context.put("build_project", build.getSchedule().getProject().getName());
        context.put("build_project_desc", build.getSchedule().getProject().getDescription());

        // Schedule Info
        context.put("build_schedule", build.getSchedule().getName());
        context.put("build_schedule_desc", build.getSchedule().getDescription());
        context.put("build_schedule_url", build.getSchedule().getUrl());
        context.put("build_schedule_status", Constants.getScheduleStatusText(build.getSchedule().getStatus()));
        context.put("build_schedule_status_date",
                Luntbuild.DATE_DISPLAY_FORMAT.format(build.getSchedule().getStatusDate()));

        // Build Info
        context.put("build_url", build.getUrl());
        context.put("build_version", build.getVersion());
        context.put("build_status", Constants.getBuildStatusText(build.getStatus()));
        context.put("build_isSuccess", new Boolean(build.getStatus() == Constants.BUILD_STATUS_SUCCESS));
        context.put("build_isFailure", new Boolean(build.getStatus() == Constants.BUILD_STATUS_FAILED));
        context.put("build_changelist", build.getChangelist());

        // Time Info
        context.put("build_start", Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()));
        context.put("build_end", Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()));
        long diffSec = (build.getEndDate().getTime()-build.getStartDate().getTime())/1000;
        context.put("build_duration", "" + diffSec + " seconds");

		// Output Info
        context.put("build_publishdir", build.getPublishDir());
        context.put("build_artifactsdir", build.getArtifactsDir());
        context.put("build_junit_reportdir", build.getJunitHtmlReportDir());

        // Log Info
        context.put("build_revisionlog_url", build.getRevisionLogUrl());
        context.put("build_revisionlog_text", readFile(build.getPublishDir()
                + File.separator + BuildGenerator.REVISION_LOG));
        context.put("build_buildlog_url", build.getBuildLogUrl());
        context.put("build_buildlog_text", readFile(build.getPublishDir()
                + File.separator + BuildGenerator.BUILD_LOG));
        context.put("luntbuild_systemlog_url", Luntbuild.getSystemLogUrl());

        context.put("build_type",
                com.luntsys.luntbuild.facades.Constants.getBuildTypeText(build.getBuildType()));
        context.put("build_labelstrategy",
                com.luntsys.luntbuild.facades.Constants.getLabelStrategyText(build.getLabelStrategy()));
        context.put("luntbuild_servlet_url", Luntbuild.getServletUrl());

        // Just for fun
        try {
        	context.put("build_randomquote", getRandomQuote(this.templateDir));
        }
        catch (Exception ex) {
            // If we fail, this should not affect the rest of the message
        }
        return context;
    }

    /**
     * Populates the context with the variables which are exposed to the schedule template.
     * 
     * @param schedule the schedule
     * @param ctx the Velocity context
     * @throws Exception from {@link #extractRootUrl(String)}
     */
    private VelocityContext createContext(Schedule schedule, VelocityContext ctx) throws Exception {
        VelocityContext context = new VelocityContext(ctx);

        context.put("luntbuild_webroot", extractRootUrl(schedule.getUrl()));

        // Project Info
        context.put("schedule_project", schedule.getProject().getName());
        context.put("schedule_project_desc", schedule.getProject().getDescription());

        // Schedule Info
        context.put("schedule_name", schedule.getName());
        context.put("schedule_desc", schedule.getDescription());
        context.put("schedule_url", schedule.getUrl());
        context.put("schedule_status", Constants.getScheduleStatusText(schedule.getStatus()));
        context.put("schedule_status_date",
                Luntbuild.DATE_DISPLAY_FORMAT.format(schedule.getStatusDate()));

		// Output Info
        context.put("schedule_publishdir", schedule.getPublishDir());

        // Log Info
        context.put("luntbuild_systemlog_url", Luntbuild.getSystemLogUrl());
        context.put("schedule_type",
                com.luntsys.luntbuild.facades.Constants.getBuildTypeText(schedule.getBuildType()));
        context.put("schedule_labelstrategy",
                com.luntsys.luntbuild.facades.Constants.getLabelStrategyText(schedule.getLabelStrategy()));
        context.put("luntbuild_servlet_url", Luntbuild.getServletUrl());

        return context;
    }

    /**
     * Creates a message title for a schedule notification.
     * 
     * @param schedule the schedule
     * @return the message title
     */
    protected String constructTitle(Schedule schedule) {
        String scheduleDesc = schedule.getProject().getName() + "/" + schedule.getName();
        return "[luntbuild] schedule \"" + scheduleDesc + "\"";
    }

    /**
     * Creates a message body for a schedule notification.
     * 
     * @param schedule the schedule
     * @return the message body
     */
    protected String constructBody(Schedule schedule) {
        return constructBody(schedule, null);
    }

    /**
     * Creates a message title for a build notification.
     * 
     * @param build the build
     * @return the message title
     */
    protected String constructTitle(Build build) {
        String buildDesc = build.getSchedule().getProject().getName() +
        "/" + build.getSchedule().getName() + "/" + build.getVersion();
        return "[luntbuild] build of \"" + buildDesc +
        "\" " + com.luntsys.luntbuild.facades.Constants.getBuildStatusText(build.getStatus());
    }

    /**
     * Creates a message body for a build notification for subscribed users.
     * 
     * @param build the build
     * @return the message body
     */
    protected String constructBody(Build build) {
        VelocityContext context = new VelocityContext();
        context.put("build_user_msg",
                "You have received this email because you asked to be notified.");
        return constructBody(build, context);
    }

    /**
     * Creates a message body for a schedule notification.
     * 
     * @param schedule the schedule
     * @param ctx the Velocity context
     * @return the message body
     */
    private String constructBody(Schedule schedule, VelocityContext ctx) {
        try {
            init(schedule.getProject().getName().replaceAll(" ","_") + "_"
            	+ schedule.getName().replaceAll(" ","_"), "scheduleTemplate");
            return processTemplate(schedule, ctx);
        }
        catch (ResourceNotFoundException rnfe) {
            this.logger.error("Could not load template file: " + this.templateFile
                    + "\nTemplateDir = " + this.templateDir, rnfe);
            return "Could not load template file: " + this.templateFile + "\nTemplateDir = " +
            this.templateDir;
        }
        catch (ParseErrorException pee) {
            this.logger.error("Unable to parse template file: " + this.templateFile +
                    "\nTemplateDir = " + this.templateDir, pee);
            return "Unable to parse template file: " + this.templateFile + "\nTemplateDir = " +
            this.templateDir;
        }
        catch(Exception ex) {
            // Wrap in a runtime exception and throw it up the stack
            this.logger.error("Failed to process template", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a message body for a build notification.
     * 
     * @param build the build
     * @param ctx the Velocity context
     * @return the message body
     */
    private String constructBody(Build build, VelocityContext ctx) {
        try {
            init(build.getSchedule().getProject().getName().replaceAll(" ","_") + "_"
            	+ build.getSchedule().getName().replaceAll(" ","_"), "buildTemplate");
            return processTemplate(build, ctx);
        }
        catch (ResourceNotFoundException rnfe) {
            this.logger.error("Could not load template file: " + this.templateFile
                    + "\nTemplateDir = " + this.templateDir, rnfe);
            return "Could not load template file: " + this.templateFile + "\nTemplateDir = " +
            this.templateDir;
        }
        catch (ParseErrorException pee) {
            this.logger.error("Unable to parse template file: " + this.templateFile +
                    "\nTemplateDir = " + this.templateDir, pee);
            return "Unable to parse template file: " + this.templateFile + "\nTemplateDir = " +
            this.templateDir;
        }
        catch(Exception ex) {
            // Wrap in a runtime exception and throw it up the stack
            this.logger.error("Failed to process template", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the contents of a file.
     * 
     * @param filename the name of the file
     * @return the contents of the file
     * @throws Exception if the file cannot be read
     */
    private static final String readFile(String filename) throws Exception {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(filename);
            if (!file.exists())
                throw new Exception("Cannot load system file: " + filename +
                        "\nFull Path = " + file.getAbsolutePath());

            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            StringBuffer sbuf = new StringBuffer();
            int readin = 0;
            byte[] buf = new byte[1024];
            while ((readin = bis.read(buf)) > 0) {
                sbuf.append(new String(buf, 0, readin));
            }

            return sbuf.toString();
        }
        catch (Exception e) {
        }
        finally {
            if (bis != null) bis.close();
            if (fis != null) fis.close();
        }
        
        return "[File not found.]";
    }

    /**
     * Gets a random quote from the quote file in the specified directory.
     * 
     * @param templateDir the template directory
     * @return a random quote, or <code>null</code> if the quote file could not be found
     * @throws Exception if the quote file cannot be read
     */
    private static final String getRandomQuote(String templateDir) throws Exception {
    	try {
    		String quotes = readFile(templateDir + "/" + QUOTE_FILE);
            StringTokenizer tokenizer = new StringTokenizer(quotes, "\r");
            int tokens = tokenizer.countTokens();
            int index = (int)(Math.random() * tokens);
            while (--index > 1) tokenizer.nextToken();
            return tokenizer.nextToken();
        }
        catch(FileNotFoundException ex) {
        	// If the files not there, the just ignore it
            return null;
        }
    }
}
