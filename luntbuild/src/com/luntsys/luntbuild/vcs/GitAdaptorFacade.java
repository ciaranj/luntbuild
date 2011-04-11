package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.facades.lb12.VcsFacade;

/**
 * Git VCS Adaptorfacade
 *
 * @author ciaran jessup
 * @see com.luntsys.luntbuild.vcs.GitAdaptor
 */
public class GitAdaptorFacade extends VcsFacade {

	private String gitDir;
	private String repositoryUrl;
	private String branch;

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.GitAdaptor";
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getGitDir() {
		return gitDir;
	}

	public String getBranch() {
		return branch;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl= repositoryUrl;
	}

	public void setGitDir(String gitDir) {
		this.gitDir= gitDir;
	}

	public void setBranch(String branch) {
		this.branch= branch;
	}
}
