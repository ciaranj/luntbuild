package com.luntsys.luntbuild.vcs;

import java.util.Date;
import java.util.List;

import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
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

	public String getDisplayName() {
		return "Git";
	}

	public String getIconName() {
		return "git.png";
	}

	public List getVcsSpecificProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkoutActually(Build build, Project antProject) {
		// TODO Auto-generated method stub

	}

	public void label(Build build, Project antProject) {
		// TODO Auto-generated method stub

	}

	public Module createNewModule() {
		// TODO Auto-generated method stub
		return null;
	}

	public Module createNewModule(Module module) {
		// TODO Auto-generated method stub
		return null;
	}

	public Revisions getRevisionsSince(Date sinceDate,
			Schedule workingSchedule, Project antProject) {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveToFacade(VcsFacade facade) {
		// TODO Auto-generated method stub

	}

	public void loadFromFacade(VcsFacade facade) {
		// TODO Auto-generated method stub

	}

	public VcsFacade constructFacade() {
		// TODO Auto-generated method stub
		return null;
	}
}
