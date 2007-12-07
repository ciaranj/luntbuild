/*
 *
 */

package com.luntsys.luntbuild.repliers;

import java.util.Iterator;

import org.springframework.dao.DataAccessException;

import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;
import com.luntsys.luntbuild.vcs.Vcs;

/**
 * RSS API replier implementation.
 *
 * @author Jason Archer
 */
public class RSSReplier extends TemplatedReplier {

    /**
     * Constructs a new RSS replier.
     */
    public RSSReplier() {
        super(RSSReplier.class, "rss");
    }

	/**
     * Gets the display name for this replier.
     *
     * @return the display name for this replier
	 */
	public String getDisplayName() {
		return "RSS";
	}

	/** 
	 * Sets the source path to use.
	 * 
	 * @param source the source path
	 */
	public void setSource(String source) {
		this.source = source;
		this.sources = this.source.split("/");
		
		if (source.equals("")) {
			this.method = ROOT;
		} else if (sources.length == 1) {
			this.method = SCHEDULES;
		} else if (sources.length == 2) {
			this.method = BUILDS;
		} else if (sources.length == 3) {
			this.method = BUILD;
		}
	}

	/**
	 * Gets the full reply based on the chosen method.
	 * 
	 * @return the full reply message
	 */
	public String getReply() {
		if (source == null) {
			return null;
		}

		String reply = "";
		String header = "";
		String body = "";
		String footer = "</channel></rss>";

		// Construct feed
		header += "<rss version=\"2.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">";
		header += "<channel>";
		if (method == Replier.ROOT || method == Replier.SCHEDULES) {
			if (source.equals("")) {
				// Construct header
				header += "<title>Schedules on " + Luntbuild.getHostName() + "</title>";
				header += "<description>Schedules set up on " + Luntbuild.getHostName() + "</description>";

				// Get all schedules
				Iterator schedules = Luntbuild.getDao().loadSchedules().iterator();
				while (schedules.hasNext()) {
					Schedule schedule = (Schedule) schedules.next();
					body += getSchedule(schedule);
				}
			} else if (!source.equals("") && sources.length == 1) {
				// Construct header
				header += "<title>Schedules for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<description>Schedules set up for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</description>";

				try {
					// Get all schedules for this project
					Project project = Luntbuild.getDao().loadProject(sources[0]);
					Iterator schedules = project.getSchedules().iterator();
					while (schedules.hasNext()) {
						Schedule schedule = (Schedule) schedules.next();
						body += getSchedule(schedule);
					}
				} catch (DataAccessException e) {
					body = "<item>";
					body += "<title>Feed Error: Project not found</title>";
					body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
					body += "<link></link>";
					body += "<description>The project \"" + sources[0] + "\" could not be found.</description>";
					body += "</item>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<description>Luntbuild could not generate the requested feed due to the included errors.</description>";
				body = "<item>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
				body += "<link></link>";
				body += "<description>Invalid source \"" + escape(source) + "\" for \"schedules\"</description>";
				body += "</item>";
			}
		} else if (method == Replier.BUILDS) {
			if (source.equals("")) {
				// Construct header
				header += "<title>All builds on " + Luntbuild.getHostName() + "</title>";
				header += "<description>All builds on " + Luntbuild.getHostName() + "</description>";

				// Get all builds
				Iterator schedules = Luntbuild.getDao().loadSchedules().iterator();
				while (schedules.hasNext()) {
					Schedule schedule = (Schedule) schedules.next();
                    SearchCriteria searchCriteria = new SearchCriteria();
                    searchCriteria.setScheduleIds(new long[]{schedule.getId()});
                    Iterator builds = Luntbuild.getDao().searchBuilds(searchCriteria, 0, 0).iterator();
					while (builds.hasNext()) {
						Build build = (Build) builds.next();
						if (canNotify(build)) {
							body += getBuild(build);
						}
					}
				}
			} else if (!source.equals("") && sources.length == 1) {
				// Construct header
				header += "<title>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<description>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</description>";

				try {
					// Get all builds for this project
					Project project = Luntbuild.getDao().loadProject(sources[0]);
					Iterator schedules = project.getSchedules().iterator();
					while (schedules.hasNext()) {
						Schedule schedule = (Schedule) schedules.next();
                        SearchCriteria searchCriteria = new SearchCriteria();
                        searchCriteria.setScheduleIds(new long[]{schedule.getId()});
                        Iterator builds = Luntbuild.getDao().searchBuilds(searchCriteria, 0, 0).iterator();
						while (builds.hasNext()) {
							Build build = (Build) builds.next();
							if (canNotify(build)) {
								body += getBuild(build);
							}
						}
					}
				} catch (DataAccessException e) {
					body = "<item>";
					body += "<title>Feed Error: Project not found</title>";
					body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
					body += "<link></link>";
					body += "<description>The project \"" + sources[0] + "\" could not be found.</description>";
					body += "</item>";
				}
			} else if (sources.length == 2 || sources.length == 3) {
				// Construct header
				header += "<title>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<description>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</description>";

				try {
					// Get all builds for this schedule
					Schedule schedule = Luntbuild.getDao().loadSchedule(sources[0], sources[1]);
                    SearchCriteria searchCriteria = new SearchCriteria();
                    searchCriteria.setScheduleIds(new long[]{schedule.getId()});
                    Iterator builds = Luntbuild.getDao().searchBuilds(searchCriteria, 0, 0).iterator();
					while (builds.hasNext()) {
						Build build = (Build) builds.next();
						if (canNotify(build)) {
							body += getBuild(build);
						}
					}
				} catch (DataAccessException e) {
					body = "<item>";
					body += "<title>Feed Error: Schedule not found</title>";
					body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
					body += "<link></link>";
					body += "<description>The schedule \"" + sources[0] + "/" + sources[1] + "\" could not be found.</description>";
					body += "</item>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<description>Luntbuild could not generate the requested feed due to the included errors.</description>";
				body = "<item>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
				body += "<link></link>";
				body += "<description>Invalid source \"" + escape(source) + "\" for \"builds\"</description>";
				body += "</item>";
			}
		} else if (method == Replier.BUILD) {
			if (sources.length == 3) {
				// Construct header
				header += "<title>Build notification for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<description>Build notification for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</description>";

				try {
					// Get build
					Build build = Luntbuild.getDao().loadBuild(sources[0], sources[1], sources[2]);
					body += getBuild(build);
				} catch (DataAccessException e) {
					body = "<item>";
					body += "<title>Feed Error: Build not found</title>";
					body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
					body += "<link></link>";
					body += "<description>The build \"" + sources[0] + "/" + sources[1] + "/" + sources[2] + "\" could not be found.</description>";
					body += "</item>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<description>Luntbuild could not generate the requested feed due to the included errors.</description>";
				body = "<item>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
				body += "<link></link>";
				body += "<description>Invalid source \"" + escape(source) + "\" for \"build\"</description>";
				body += "</item>";
			}
		} else if (method == Replier.USERS) {
            if (source.equals("")) {
    			// Construct header
    			header += "<title>User list on " + Luntbuild.getHostName() + "</title>";
    			header += "<description>User list on " + Luntbuild.getHostName() + "</description>";
    
    			// Get all users
    			Iterator users = Luntbuild.getDao().loadUsers().iterator();
    			while (users.hasNext()) {
    				User user = (User) users.next();
    				body += getUser(user);
    			}
            } else {
                // Construct header
                header += "<title>Error Feed</title>";
                header += "<description>Luntbuild could not generate the requested feed due to the included errors.</description>";
                body = "<item>";
                body += "<title>Feed Error: Invalid source</title>";
                body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
                body += "<link></link>";
                body += "<description>Invalid source \"" + escape(source) + "\" for \"users\"</description>";
                body += "</item>";
            }
		} else if (method == Replier.USER) {
			if (!source.equals("") && sources.length == 1) {
				// Construct header
				header += "<title>User \"" + sources[0] + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<description>User \"" + sources[0] + "\" on " + Luntbuild.getHostName() + "</description>";

				try {
					// Get User
					User user = Luntbuild.getDao().loadUser(sources[0]);
					body += getUser(user);
				} catch (DataAccessException e) {
					body = "<item>";
					body += "<title>Feed Error: User not found</title>";
					body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
					body += "<link></link>";
					body += "<description>The user \"" + sources[0] + "\" could not be found.</description>";
					body += "</item>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<description>Luntbuild could not generate the requested feed due to the included errors.</description>";
				body = "<item>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
				body += "<link></link>";
				body += "<description>Invalid source \"" + escape(source) + "\" for \"user\"</description>";
				body += "</item>";
			}
		} else {
			// Construct header
			header += "<title>Error Feed</title>";
			header += "<description>Luntbuild could not generate the requested feed due to the included errors.</description>";
			body = "<item>";
			body += "<title>Feed Error: Unsupported method</title>";
			body += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
			body += "<link></link>";
			body += "<description>Unsupported method</description>";
			body += "</item>";
		}
		header += "<link>" + Luntbuild.getServletUrl() + "</link>";
		header += "<language>en</language>";

		reply = header + body + footer;

		return reply;
	}

	/**
	 * Escapes special characters in the XML.
	 * 
	 * @param text the XML
	 * @return the properly escaped XML
	 */
	protected String escape(String text) {
		text = text.replaceAll("&","&amp;");
		text = text.replaceAll("<","&lt;");
		text = text.replaceAll(">","&gt;");

		// Return escaped text
		return text;
	}

	/**
	 * Gets the "system" object containing system wide settings and users.
	 * 
	 * @return the system object
	 */
	public String getSystem() {
		String RSSItem = "";
		// TODO Auto-generated method stub

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the property object of the specified system level property.
	 * 
	 * @param property the property
	 * @return the property object
	 */
	public String getNotifierProperty(NotifierProperty property) {
		String RSSItem = "";
		// TODO Auto-generated method stub

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the property object of the specified user level property.
	 * 
	 * @param property the property
	 * @param user the user
	 * @return the property object
	 */
	public String getNotifierProperty(NotifierProperty property, User user) {
		String RSSItem = "";
		// TODO Auto-generated method stub

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the user object of the specified user.
	 * 
	 * @param user the user
	 * @return the user object
	 */
	public String getUser(User user) {
		String RSSItem = "";
		RSSItem += "<item>";
		RSSItem += "<title>" + escape(user.getName() + " (" + user.getFullname() + ")") + "</title>";
		RSSItem += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
		RSSItem += "<dc:date></dc:date>";
		RSSItem += "<link></link>";
		RSSItem += "<guid isPermaLink=\"false\">user" + user.getId() + "</guid>";
		RSSItem += "<category></category>";
		RSSItem += "<description></description>";
		RSSItem += "</item>";

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the project object of the specified project.
	 * 
	 * @param project the project
	 * @return the project object
	 */
	public String getProject(Project project) {
		String RSSItem = "";
		// TODO Auto-generated method stub

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the vcs object of the specified vcs.
	 * 
	 * @param vcs the vcs
	 * @return the vcs object
	 */
	public String getVcs(Vcs vcs) {
		String RSSItem = "";
		// TODO Auto-generated method stub

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the builder object of the specified builder.
	 * 
	 * @param builder the builder
	 * @return the builder object
	 */
	public String getBuilder(Builder builder) {
		String RSSItem = "";
		// TODO Auto-generated method stub

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the schedule object of the specified schedule.
	 * 
	 * @param schedule the schedule
	 * @return the schedule object
	 */
	public String getSchedule(Schedule schedule) {
		String RSSItem = "";
		RSSItem += "<item>";
		RSSItem += "<title>" + escape(constructTitle(schedule)) + "</title>";
		RSSItem += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
		RSSItem += "<dc:date>" + Luntbuild.DATE_DISPLAY_FORMAT_ISO.format(schedule.getStatusDate()) + "</dc:date>";
		RSSItem += "<link>" + escape(schedule.getUrl()) + "</link>";
		RSSItem += "<guid isPermaLink=\"false\">" + escape(schedule.getUrl() + Luntbuild.DATE_DISPLAY_FORMAT_ISO.format(schedule.getStatusDate())) + "</guid>";
		RSSItem += "<category>" + schedule.getProject().getName() + "</category>";
		RSSItem += "<description>" + escape(constructBody(schedule)) + "</description>";
		RSSItem += "</item>";

		// Return RSS item
		return RSSItem;
	}

	/**
	 * Gets the build object of the specified build.
	 * 
	 * @param build the build
	 * @return the build object
	 */
	public String getBuild(Build build) {
		String RSSItem = "";
		RSSItem += "<item>";
		RSSItem += "<title>" + escape(constructTitle(build)) + "</title>";
		RSSItem += "<dc:creator>Luntbuild@" + Luntbuild.getHostName() + "</dc:creator>";
		RSSItem += "<dc:date>" + Luntbuild.DATE_DISPLAY_FORMAT_ISO.format(build.getEndDate()) + "</dc:date>";
		RSSItem += "<link>" + escape(build.getUrl()) + "</link>";
		RSSItem += "<guid isPermaLink=\"true\">" + escape(build.getUrl()) + "</guid>";
		RSSItem += "<category></category>";
		RSSItem += "<description>" + escape(constructBody(build)) + "</description>";
		RSSItem += "</item>";

		// Return RSS item
		return RSSItem;
	}
}
