package com.luntsys.luntbuild.scrapers;

import org.apache.velocity.VelocityContext;
import com.luntsys.luntbuild.db.Build;

/**
 * Interface for a build log scraper.
 *
 * @author kevin.lin@smartbombinteractive.com
 */
public interface Scraper {

    /**
     * Scrape the build text.  After the call, context should contain
     * any new information scraped from the build log.
     * @param buildText the build log
     * @param build build information
     * @param context the Velocity template context
     */
    public void scrape(String buildText,
                       Build build, VelocityContext context);
}