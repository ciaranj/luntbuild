package com.luntsys.luntbuild.vcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import sun.security.action.GetLongAction;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.Revisions;

/**
 * Git VCS adaptor implementation.
 *
 * @author robin shine
 */
public class GitAdaptor extends Vcs {

	/**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
	static final long serialVersionUID = 1;
	private String repositoryUrl;
	private String gitDir;
	private String branch;

	public String getDisplayName() {
		return "Git";
	}

	public String getIconName() {
		return "git.png";
	}

	public List getVcsSpecificProperties() {
		List properties= new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Repository Url";
			}

			public String getDescription() {
				return "The Url to access the git repository at, for example, you can input " +
				"\"git@git.foo.com:foo.git\", or \"file:///c:/foo.git\"";
			}

			public String getValue() {
				return getRepositoryUrl();
			}

			public String getActualValue(){
				return getActualRepositoryUrl();
			}

			public void setValue(String value) {
				setRepositoryUrl(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Branch";
			}

			public String getDescription() {
				return "The branch in the repository to checkout leave blank for 'master'";
			}

            public boolean isRequired() {
                return false;
            }
			public String getValue() {
				return getBranch();
			}

			public void setValue(String value) {
				setBranch(value);
			}
		});
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Path for git executable";
            }

            public String getDescription() {
                return "The directory path, where your git executable file resides in. " +
                        "It should be specified here, if it does not exist in the system path.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getGitDir();
            }

			public void setValue(String value) {
                setGitDir(value);
            }
        });
		return properties;
	}

	public void checkoutActually(Build build, Project antProject) {

		// Start off assuming we want to checkout the current branch
		String versionToBuild=  Luntbuild.isEmpty(this.getBranch())? "master" :this.getBranch();

		if( build.isRebuild() ) {
			versionToBuild= Luntbuild.getLabelByVersion(build.getVersion());
		}
		boolean doClean= false;

        if (build.isRebuild() || build.isCleanBuild())
        	doClean= true;
        else
        	doClean= false;

        checkout(build, antProject, versionToBuild, doClean);

	}

	private void checkout(Build build, Project antProject, String versionToBuild, boolean doClean) {
		String directory= build.getSchedule().getWorkDirRaw();

		boolean isGitRepo= isGitRepository(antProject, directory);
		if( doClean && isGitRepo ) {
			try {
				doGitClean(antProject, directory);
			}
			catch(Exception e) {
				// Meh lets hope it worked.
			}
		}

		if(!isGitRepo) {
			doGitClone(antProject, directory);
		}
		else {
			try {
				doGitReset(antProject, directory);
			}
			catch(Exception e) {
				// meh lets hope it worked.
			}
		}

		doGitPull(antProject, directory);

		// At this point we've either cleaned ourselves up or cloned fresh.
		doGitCheckout(antProject, directory, versionToBuild);
	}

	private void doGitPull(Project antProject, String directory) {
		// Because tags could be deleted remotely we don't want to have stuff
		// here that ought not to be (it may get pushed back upstream, which would
		// get mighty confusing!) .. So we delete all our local tags and re-fetch em.
		antProject.log("Cleaning up local tags...");
		// First cull all the local tags (wqsteful after a clone, ho-hum)
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("tag");
		cmdLine.createArgument().setValue("-l");
		final List tags= new ArrayList();
		new MyExecTask("ListTags", antProject,  directory, cmdLine, null, null, Project.MSG_DEBUG){
	        public void handleStdout(String line) {
	            tags.add(line);
	        }
		}.execute();
		if( tags.size() >0 ) {
			for(int i=0;i<tags.size();i++) {
				cmdLine.clearArgs();
				cmdLine.createArgument().setValue("tag");
				cmdLine.createArgument().setValue("-d");
				cmdLine.createArgument().setValue((String)tags.get(i));
				new MyExecTask("DeleteTag", antProject,  directory, cmdLine, null, null, Project.MSG_DEBUG).execute();
			}
		}

		antProject.log("Fetching new commits,branches and referenced tags, removing dead branches");
		// Pull down all the branch refs (pruning out dead branches)
		cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("pull");
		cmdLine.createArgument().setValue("--prune");
		cmdLine.createArgument().setValue("origin");
		new MyExecTask("pull", antProject,  directory, cmdLine, null, null, Project.MSG_DEBUG).execute();

	}

	private void doGitCheckout(Project antProject, String directory, String versionToBuild) {
		antProject.log("Checking out: "+ versionToBuild);
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("checkout");
		cmdLine.createArgument().setValue(versionToBuild);
		new MyExecTask("reset", antProject,  directory, cmdLine, null, null, Project.MSG_INFO).execute();
	}

	private boolean isGitRepository(Project antProject, String directory) {
		boolean isRepository= true;
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("status");
	    final StringBuffer buffer = new StringBuffer();
	    try {
			new MyExecTask("status", antProject,  directory, cmdLine, null, null, Project.MSG_DEBUG){
		        public void handleStdout(String line) {
		            buffer.append(line);
		            buffer.append("\n");
		        }
			}.execute();
	    }
	    catch(Exception e) {
	    	//Swallowed....
	    	isRepository= false;
	    }
		String result= buffer.toString();
		if( result.startsWith("fatal:") ) {
			antProject.log("Dir: '"+ directory + "' appears not to be a repository, received: "+ result + "\n... from git status");
			isRepository= false;
		}
		return isRepository;
	}

	private void doGitReset(Project antProject, String directory ) {
		antProject.log("Resetting (the git way)... ");
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("reset");
		cmdLine.createArgument().setValue("--hard");
		new MyExecTask("reset", antProject,  directory, cmdLine, null, null, Project.MSG_INFO).execute();
	}
	private void doGitClone(Project antProject, String directory) {
		antProject.log("Cloning git repository from: "+ getRepositoryUrl());
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("clone");
		cmdLine.createArgument().setValue(getRepositoryUrl());
		cmdLine.createArgument().setValue(".");
		new MyExecTask("clone", antProject,  directory, cmdLine, null, null, Project.MSG_INFO).execute();
	}

	private void doGitClean(Project antProject, String directory) {
		antProject.log("Cleaning (the git way)... ");
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("clean");
		cmdLine.createArgument().setValue("-d");
		cmdLine.createArgument().setValue("-x");
		cmdLine.createArgument().setValue("-f");
		new MyExecTask("clean", antProject,  directory, cmdLine, null, null, Project.MSG_INFO).execute();
	}

	public void label(Build build, Project antProject) {
		antProject.log("Tagging new version: " + Luntbuild.getLabelByVersion(build.getVersion()));
		// Tag it
		Commandline cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("tag");
		cmdLine.createArgument().setValue(Luntbuild.getLabelByVersion(build.getVersion()));
		new MyExecTask("Tag", antProject,  build.getSchedule().getWorkDirRaw(), cmdLine, null, null, Project.MSG_INFO).execute();

		antProject.log("Pushing version tag upstream");
		// Push it.
		cmdLine = buildGitExecutable();
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("push");
		cmdLine.createArgument().setValue("origin");
		cmdLine.createArgument().setValue(Luntbuild.getLabelByVersion(build.getVersion()));
		new MyExecTask("Push", antProject,  build.getSchedule().getWorkDirRaw(), cmdLine, null, null, Project.MSG_INFO).execute();

		// The local tags will get blitzed on the next pull /clean anyway so forget about bothering with cleanup ;)
	}

	public Revisions getRevisionsSince(Date sinceDate,
			Schedule workingSchedule, Project antProject) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cleanupCheckout(Schedule workingSchedule, Project antProject) {
		try {
			doGitClean( antProject, workingSchedule.getWorkDirRaw() );
		}
		catch( Exception e ) {
			antProject.log("Problem doing the clean, the git way: '"+ e.getMessage() +"' falling back to old-skool delete");
			super.cleanupCheckout(workingSchedule, antProject);
		}
	}


	// I don't need or care about modules, so screw-em ;)
	public Module createNewModule() {
		return null;
	}

	public Module createNewModule(Module module) {
		return null;
	}


	public void saveToFacade(VcsFacade facade) {
		GitAdaptorFacade gitFacade= (GitAdaptorFacade)facade;
		gitFacade.setRepositoryUrl(this.getRepositoryUrl());
		gitFacade.setGitDir(this.getGitDir());
		gitFacade.setBranch( this.getBranch() );
	}

	public void loadFromFacade(VcsFacade facade) {
		if(!(facade instanceof GitAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName() );
		GitAdaptorFacade gitFacade = (GitAdaptorFacade) facade;
		setRepositoryUrl( gitFacade.getRepositoryUrl() );
		setGitDir( gitFacade.getGitDir() );
		setBranch( gitFacade.getBranch() );
	}

	public VcsFacade constructFacade() {
		return new GitAdaptorFacade();
	}

	/**
	 * Gets the repository URL.
	 *
	 * @return the URL to the repository
	 */
	private String getRepositoryUrl() {
		return this.repositoryUrl;
	}

	/**
	 * Gets the repository Url.  This method will parse OGNL variables.
	 *
	 * @return the URL to the repository.
	 */
	private String getActualRepositoryUrl() {
		return OgnlHelper.evaluateScheduleValue(getRepositoryUrl());
	}

	/**
	 * Sets the repository URL.
	 *
	 * @param url the URL
	 */
    public void setRepositoryUrl(String url) {
        this.repositoryUrl= url;
    }

    /**
     * Gets the path to the Git executable
     *
     * @return the path to the Git executable
     */
    private String getGitDir() {
    	return this.gitDir;
	}

    /**
     * Sets the path to the Git executable
     *
     * @param gitDir the path to the Git Executable
     */
    private void setGitDir(String gitDir) {
    	this.gitDir= gitDir;
	}

    /**
     * Retrieves the branch we want to build.
     *
     * @return the branch.
     */
    private String getBranch() {
    	return this.branch;
    }

    /**
     * Sets the branch of the Repository we care about building.
     *
     * @param branch the branch to build
     */
    private void setBranch(String branch) {
    	this.branch= branch;
    }

	/**
	 * Constructs the executable part of a commandline object.
	 *
	 * @return the commandline object
	 */
    protected Commandline buildGitExecutable() {
        Commandline cmdLine = new Commandline();
        if (Luntbuild.isEmpty(getGitDir()))
            cmdLine.setExecutable("git");
        else
            cmdLine.setExecutable(Luntbuild.concatPath(getGitDir(), "git"));
        return cmdLine;
    }

}
