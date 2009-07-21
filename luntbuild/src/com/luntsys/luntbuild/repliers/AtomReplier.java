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
 * Atom API replier implementation.
 *
 * @author Jason Archer
 */
public class AtomReplier extends TemplatedReplier {

    /**
     * Constructor
     */
    public AtomReplier() {
        super(AtomReplier.class, "rss");
    }

	/**
     * Gets the display name for this replier.
     *
     * @return the display name for this replier
	 */
	public String getDisplayName() {
		return "Atom";
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
		String footer = "</feed>";
		String[] sources = source.split("/");

		// Construct feed
		header += "<feed version=\"1.0\" xml:lang=\"en\" xmlns=\"http://www.w3.org/2005/Atom\">";
		if (method == Replier.ROOT || method == Replier.SCHEDULES) {
			if (source.equals("")) {
				// Construct header
				header += "<title>Schedules on " + Luntbuild.getHostName() + "</title>";
				header += "<subtitle>Schedules set up on " + Luntbuild.getHostName() + "</subtitle>";

				// Get all schedules
				Iterator schedules = Luntbuild.getDao().loadSchedules().iterator();
				while (schedules.hasNext()) {
					Schedule schedule = (Schedule) schedules.next();
					body += getSchedule(schedule);
				}
			} else if (!source.equals("") && sources.length == 1) {
				// Construct header
				header += "<title>Schedules for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<subtitle>Schedules set up for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</subtitle>";

				try {
					// Get all schedules for this project
					Project project = Luntbuild.getDao().loadProject(sources[0]);
					Iterator schedules = project.getSchedules().iterator();
					while (schedules.hasNext()) {
						Schedule schedule = (Schedule) schedules.next();
						body += getSchedule(schedule);
					}
				} catch (DataAccessException e) {
					body = "<entry>";
					body += "<title>Feed Error: Project not found</title>";
					body += "<author>";
					body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
					body += "<email></email>";
					body += "</author>";
					body += "<link href=\"\"/>";
					body += "<content type=\"text\">The project \"" + sources[0] + "\" could not be found.</content>";
					body += "</entry>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<subtitle>Luntbuild could not generate the requested feed due to the included errors.</subtitle>";
				body = "<entry>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<author>";
				body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
				body += "<email></email>";
				body += "</author>";
				body += "<link href=\"\"/>";
				body += "<content type=\"text\">Invalid source \"" + source + "\" for \"schedules\"</content>";
				body += "</entry>";
			}
		} else if (method == Replier.BUILDS) {
			if (source.equals("")) {
				// Construct header
				header += "<title>All builds on " + Luntbuild.getHostName() + "</title>";
				header += "<subtitle>All builds on " + Luntbuild.getHostName() + "</subtitle>";

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
				header += "<subtitle>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</subtitle>";

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
					body = "<entry>";
					body += "<title>Feed Error: Project not found</title>";
					body += "<author>";
					body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
					body += "<email></email>";
					body += "</author>";
					body += "<link href=\"\"/>";
					body += "<content type=\"text\">The project \"" + sources[0] + "\" could not be found.</content>";
					body += "</entry>";
				}
			} else if (sources.length == 2 || sources.length == 3) {
				// Construct header
				header += "<title>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<subtitle>Builds for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</subtitle>";

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
					body = "<entry>";
					body += "<title>Feed Error: Schedule not found</title>";
					body += "<author>";
					body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
					body += "<email></email>";
					body += "</author>";
					body += "<link href=\"\"/>";
					body += "<content type=\"text\">The schedule \"" + sources[0] + "/" + sources[1] + "\" could not be found.</content>";
					body += "</entry>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<subtitle>Luntbuild could not generate the requested feed due to the included errors.</subtitle>";
				body = "<entry>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<author>";
				body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
				body += "<email></email>";
				body += "</author>";
				body += "<link href=\"\"/>";
				body += "<content type=\"text\">Invalid source \"" + escape(source) + "\" for \"builds\"</content>";
				body += "</entry>";
			}
		} else if (method == Replier.BUILD) {
			if (sources.length == 3) {
				// Construct header
				header += "<title>Build notification for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</title>";
				header += "<subtitle>Build notification for \"" + escape(source) + "\" on " + Luntbuild.getHostName() + "</subtitle>";

				try {
					// Get build
					Build build = Luntbuild.getDao().loadBuild(sources[0], sources[1], sources[2]);
					body += getBuild(build);
				} catch (DataAccessException e) {
					body = "<entry>";
					body += "<title>Feed Error: Build not found</title>";
					body += "<author>";
					body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
					body += "<email></email>";
					body += "</author>";
					body += "<link href=\"\"/>";
					body += "<content type=\"text\">The build \"" + sources[0] + "/" + sources[1] + "/" + sources[2] + "\" could not be found.</content>";
					body += "</entry>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<subtitle>Luntbuild could not generate the requested feed due to the included errors.</subtitle>";
				body = "<entry>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<author>";
				body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
				body += "<email></email>";
				body += "</author>";
				body += "<link href=\"\"/>";
				body += "<content type=\"text\">Invalid source \"" + escape(source) + "\" for \"build\"</content>";
				body += "</entry>";
			}
		} else if (method == Replier.USERS) {
            if (source.equals("")) {
    			// Construct header
    			header += "<title>User list on " + Luntbuild.getHostName() + "</title>";
    			header += "<subtitle>User list on " + Luntbuild.getHostName() + "</subtitle>";
    
    			// Get all users
    			Iterator users = Luntbuild.getDao().loadUsers().iterator();
    			while (users.hasNext()) {
    				User user = (User) users.next();
    				body += getUser(user);
    			}
            } else {
                // Construct header
                header += "<title>Error Feed</title>";
                header += "<subtitle>Luntbuild could not generate the requested feed due to the included errors.</subtitle>";
                body = "<entry>";
                body += "<title>Feed Error: Invalid source</title>";
                body += "<author>";
                body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
                body += "<email></email>";
                body += "</author>";
                body += "<link href=\"\"/>";
                body += "<content type=\"text\">Invalid source \"" + escape(source) + "\" for \"users\"</content>";
                body += "</entry>";
            }
		} else if (method == Replier.USER) {
			if (!source.equals("") && sources.length == 1) {
				// Construct header
				header += "<title>User list on " + Luntbuild.getHostName() + "</title>";
				header += "<subtitle>User list on " + Luntbuild.getHostName() + "</subtitle>";

				try {
					// Get user
					User user = Luntbuild.getDao().loadUser(sources[1]);
					body += getUser(user);
				} catch (DataAccessException e) {
					body = "<entry>";
					body += "<title>Feed Error: User not found</title>";
					body += "<author>";
					body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
					body += "<email></email>";
					body += "</author>";
					body += "<link href=\"\"/>";
					body += "<content type=\"text\">The user \"" + sources[1] + "\" could not be found.</content>";
					body += "</entry>";
				}
			} else {
				// Construct header
				header += "<title>Error Feed</title>";
				header += "<subtitle>Luntbuild could not generate the requested feed due to the included errors.</subtitle>";
				body = "<entry>";
				body += "<title>Feed Error: Invalid source</title>";
				body += "<author>";
				body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
				body += "<email></email>";
				body += "</author>";
				body += "<link href=\"\"/>";
				body += "<content type=\"text\">Invalid source \"" + escape(source) + "\" for \"user\"</content>";
				body += "</entry>";
			}
		} else {
			// Construct header
			header += "<title>Error Feed</title>";
			header += "<subtitle>Luntbuild could not generate the requested feed due to the included errors.</subtitle>";
			body = "<entry>";
			body += "<title>Feed Error: Unsupported method</title>";
			body += "<author>";
			body += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
			body += "<email></email>";
			body += "</author>";
			body += "<link href=\"\"/>";
			body += "<content type=\"text\">Unsupported method</content>";
			body += "</entry>";
		}
		header += "<link href=\"" + Luntbuild.getServletUrl() + "\"/>";
		header += "<author>";
		header += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
		header += "<email></email>";
		header += "</author>";
		header += "<id>" + Luntbuild.getServletUrl() + "</id>";

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
		String AtomEntry = "";
		// TODO Auto-generated method stub

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the property object of the specified system level property.
	 * 
	 * @param property the property
	 * @return the property object
	 */
	public String getNotifierProperty(NotifierProperty property) {
		String AtomEntry = "";
		// TODO Auto-generated method stub

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the property object of the specified user level property.
	 * 
	 * @param property the property
	 * @param user the user
	 * @return the property object
	 */
	public String getNotifierProperty(NotifierProperty property, User user) {
		String AtomEntry = "";
		// TODO Auto-generated method stub

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the user object of the specified user.
	 * 
	 * @param user the user
	 * @return the user object
	 */
	public String getUser(User user) {
		String AtomEntry = "";
		AtomEntry += "<entry>";
		AtomEntry += "<title>" + escape(user.getName() + " (" + user.getFullname() + ")") + "</title>";
		AtomEntry += "<author>";
		AtomEntry += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
		AtomEntry += "<email></email>";
		AtomEntry += "</author>";
		AtomEntry += "<updated></updated>";
		AtomEntry += "<link href=\"\"/>";
		AtomEntry += "<id>user" + user.getId() + "</id>";
		AtomEntry += "<category></category>";
		AtomEntry += "<content type=\"html\" mode=\"escaped\">";
		//AtomEntry += escape(constructBody(build));
		AtomEntry += "</content>";
		AtomEntry += "</entry>";

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the project object of the specified project.
	 * 
	 * @param project the project
	 * @return the project object
	 */
	public String getProject(Project project) {
		String AtomEntry = "";
		// TODO Auto-generated method stub

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the vcs object of the specified vcs.
	 * 
	 * @param vcs the vcs
	 * @return the vcs object
	 */
	public String getVcs(Vcs vcs) {
		String AtomEntry = "";
		// TODO Auto-generated method stub

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the builder object of the specified builder.
	 * 
	 * @param builder the builder
	 * @return the builder object
	 */
	public String getBuilder(Builder builder) {
		String AtomEntry = "";
		// TODO Auto-generated method stub

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the schedule object of the specified schedule.
	 * 
	 * @param schedule the schedule
	 * @return the schedule object
	 */
	public String getSchedule(Schedule schedule) {
		String AtomEntry = "";
		AtomEntry += "<entry>";
		AtomEntry += "<title>" + escape(constructTitle(schedule)) + "</title>";
		AtomEntry += "<author>";
		AtomEntry += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
		AtomEntry += "<email></email>";
		AtomEntry += "</author>";
		AtomEntry += "<updated>" + Luntbuild.DATE_DISPLAY_FORMAT_ISO.format(schedule.getStatusDate()) + "</updated>";
		AtomEntry += "<link href=\"" + escape(schedule.getUrl()) + "\"/>";
		AtomEntry += "<id>" + escape(schedule.getUrl() + Luntbuild.DATE_DISPLAY_FORMAT_ISO.format(schedule.getStatusDate())) + "</id>";
		AtomEntry += "<category>" + schedule.getProject().getName() + "</category>";
		AtomEntry += "<content type=\"html\" mode=\"escaped\">";
		AtomEntry += escape(constructBody(schedule));
		AtomEntry += "</content>";
		AtomEntry += "</entry>";

		// Return Atom entry
		return AtomEntry;
	}

	/**
	 * Gets the build object of the specified build.
	 * 
	 * @param build the build
	 * @return the build object
	 */
	public String getBuild(Build build) {
		String AtomEntry = "";
		AtomEntry += "<entry>";
		AtomEntry += "<title>" + escape(constructTitle(build)) + "</title>";
		AtomEntry += "<author>";
		AtomEntry += "<name>Luntbuild@" + Luntbuild.getHostName() + "</name>";
		AtomEntry += "<email></email>";
		AtomEntry += "</author>";
		AtomEntry += "<updated>" + Luntbuild.DATE_DISPLAY_FORMAT_ISO.format(build.getEndDate()) + "</updated>";
		AtomEntry += "<link href=\"" + escape(build.getUrl()) + "\"/>";
		AtomEntry += "<id>" + escape(build.getUrl()) + "</id>";
		AtomEntry += "<category></category>";
		AtomEntry += "<content type=\"html\" mode=\"escaped\">";
		AtomEntry += escape(constructBody(build));
		AtomEntry += "</content>";
		AtomEntry += "</entry>";

		// Return Atom entry
		return AtomEntry;
	}
}
