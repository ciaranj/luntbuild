package net.mccg.lunt;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.facades.lb12.BuildFacade;
import com.luntsys.luntbuild.facades.lb12.ScheduleFacade;

public class LuntServiceImpl implements LuntService{
	private static final String LUNT_URL = "/luntbuild/app.do?service=hessian";
	private static final String FORWARD_SLASH = "/";
	private static final String ARTIFACTS_DIRECTORY = "artifacts";
	private static final String LUNT_FILE_URL_PREFIX = "/luntbuild/publish/";
	private ILuntbuild luntbuild;
	private LuntProject project;

	public LuntServiceImpl(final LuntProject project) throws Exception {
		init(project);
	}

	void init(final LuntProject project) throws Exception {
		try {
			this.project = project;
			HessianProxyFactory factory = getHessianProxyFactory();
			factory.setUser(project.getLuntUsername());
			factory.setPassword(project.getLuntPassword());
			luntbuild = (ILuntbuild) factory.create(com.luntsys.luntbuild.facades.ILuntbuild.class, project.getLuntServer() + LUNT_URL);
		} catch (MalformedURLException e) {
			throw new Exception("Unable to instantiate " + this.getClass().getName(), e);
		}
	}

	HessianProxyFactory getHessianProxyFactory() {
		return new HessianProxyFactory();
	}

	public String getLatestGreenVersion(final String projectName, final String scheduleName) throws Exception {
		ScheduleFacade sf = luntbuild.getScheduleByName(projectName, scheduleName);
		if (null == sf)
			throw new Exception("Cannot find project " + projectName + " with schedule " + scheduleName);
		BuildFacade bf = luntbuild.getLastSuccessBuild(sf);
		if (null == bf)
			throw new Exception("Cannot find successful build for project " + projectName + " with schedule " + scheduleName);
		return bf.getVersion();
	}
	
	public String getArtifactUrl() throws Exception {
	       String url = project.getLuntServer() + LUNT_FILE_URL_PREFIX + project.getProject() + FORWARD_SLASH + project.getSchedule() + FORWARD_SLASH;
	       String version = getLatestGreenVersion(project.getProject(), project.getSchedule());
	       return url + version + FORWARD_SLASH + ARTIFACTS_DIRECTORY + FORWARD_SLASH + project.getArtifact();
	}
}
