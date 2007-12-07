package com.luntsys.luntbuild.scrapers;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;

import com.luntsys.luntbuild.db.Build;

/**
 * Visual studio build log scraper.
 *
 * @author kevin.lin@smartbombinteractive.com
 */
public class MSVSScraper {

    /**
     * After the scrape, context will contain a list of MSVSSolution objects
     * under the name "build_vs_solutions"
     *
     * @see MSVSSolution
     */
    public void scrape(
        String buildText, Build build, VelocityContext context) {

        solutions = new Vector();
        currentSolution = null;

    	String[] lines = buildText.split("\\n");
    	for(int index = 0; index < lines.length; index++)
        {
            scrapeLine(lines[index], context);
        }

        if(currentSolution != null)
        {
            solutions.add(currentSolution);
            currentSolution = null;
        }
    	
        context.put("build_vs_solutions", solutions);
        solutions = null;
    }

    private String stripQuotes(String text) {
        return text.replaceAll("['\"]", "");
    }

    private void scrapeLine(String line, VelocityContext context) {

        Matcher matcher = solutionPattern.matcher(line);
			
        if(matcher.find() && matcher.groupCount() == 2)
        {
            if(currentSolution == null)
            {
                currentSolution = new MSVSSolution();
            }

            currentSolution.setPath(stripQuotes(matcher.group(1)));
            currentSolution.setConfiguration(stripQuotes(matcher.group(2)));
        }
        else if(currentSolution != null)
        {
            matcher = projectPattern.matcher(line);
            if(matcher.find() && matcher.groupCount() == 3)
            {
                MSVSProject project = new MSVSProject();
                project.setName(stripQuotes(matcher.group(1)));
                project.setResults(
                    Integer.parseInt(matcher.group(2)), //error
                    Integer.parseInt(matcher.group(3))  //warning
                    );

                currentSolution.getProjects().add(project);
            }
            else
            {
                matcher = solutionEndPattern.matcher(line);
                if(matcher.find() && matcher.groupCount() == 3)
                {
                    currentSolution.setResults(
                        Integer.parseInt(matcher.group(1)), //succeeded
                        Integer.parseInt(matcher.group(2)), //failed
                        Integer.parseInt(matcher.group(3))  //skipped
                        ); 
	    				
                    solutions.add(currentSolution);
                    currentSolution = null;
                }
            }
        }
    }

    private MSVSSolution currentSolution = null;
    private Vector solutions = null;

	//If found, capture group 1 = solution name (possibly quoted), 2 = configuration name (possibly quoted)
    private static final Pattern solutionPattern =
    	Pattern.compile("((?:['\"].*sln['\"])|(?:\\S*\\.sln))" +
    			".*/build\\s*((?:['\"].*['\"])|(?:\\S*))", Pattern.CASE_INSENSITIVE);
    
    //If found, capture group 1 = project name (non-punctuation or _-), 2 = error count, 3 = warning count
    private static final Pattern projectPattern =
    	Pattern.compile("\\s*([[^\\p{Punct}][_\\-]]+?)\\s*-" + 
    			".*?(\\d+)\\s*error.*?(\\d+)\\s*warning", Pattern.CASE_INSENSITIVE);
    
    //If found, capture group 1 = succeeded count, 2 = failed count, 3 = skipped count
    private static final Pattern solutionEndPattern =
    	Pattern.compile(".*?(\\d+)\\s*succeeded.*?(\\d+)\\s*failed.*?(\\d+)\\s*skipped", Pattern.CASE_INSENSITIVE);
}