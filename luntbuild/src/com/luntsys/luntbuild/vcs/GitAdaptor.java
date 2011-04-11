package com.luntsys.luntbuild.vcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

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
		// TODO Auto-generated method stub

	}

	public void label(Build build, Project antProject) {
		// TODO Auto-generated method stub

	}

	public Revisions getRevisionsSince(Date sinceDate,
			Schedule workingSchedule, Project antProject) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cleanupCheckout(Schedule workingSchedule, Project antProject) {
		antProject.log("Cleaning (the git way)... ");
		Commandline cmdLine = buildGitExecutable();
		try {
			cmdLine.clearArgs();
			cmdLine.createArgument().setValue("clean");
			cmdLine.createArgument().setValue("-d");
			cmdLine.createArgument().setValue("-x");
			cmdLine.createArgument().setValue("-f");
			new MyExecTask("clean", antProject, workingSchedule.getWorkDirRaw(), cmdLine, null, null, Project.MSG_INFO).execute();
		}
		catch( BuildException e ) {
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
