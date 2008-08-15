package com.luntsys.luntbuild.facades.lb20;

public class DynamicClearcaseAdaptorFacade extends BaseClearcaseAdaptorFacade {

    private String m_mvfsPath;
    
    private String m_projectPath;

    public String getMvfsPath() {
        return m_mvfsPath;
    }

    public void setMvfsPath(String viewPath) {
        m_mvfsPath = viewPath;
    }

    public String getProjectPath() {
        return m_projectPath;
    }

    public void setProjectPath(String projectPath) {
        m_projectPath = projectPath;
    }

}
