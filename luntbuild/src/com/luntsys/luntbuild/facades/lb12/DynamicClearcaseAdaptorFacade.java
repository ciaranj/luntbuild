package com.luntsys.luntbuild.facades.lb12;

/**
 * Dynamic Clearcase VCS adaptor facade.  This adaptor does not support modules.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.DynamicClearcaseAdaptor
 */
public class DynamicClearcaseAdaptorFacade extends BaseClearcaseAdaptorFacade {

    private String m_mvfsPath;
    
    private String m_projectPath;

    /**
     * Gets the MVFS path, where all Clearcase views are accessible.
     * 
     * @return the MVFS path
     */
    public String getMvfsPath() {
        return m_mvfsPath;
    }

    /**
     * Sets the MVFS path, where all Clearcase views are accessible.
     * 
     * @param viewPath the MVFS path
     */
    public void setMvfsPath(String viewPath) {
        m_mvfsPath = viewPath;
    }

    /**
     * Gets the project path.
     * 
     * @return the project path
     */
    public String getProjectPath() {
        return m_projectPath;
    }

    /**
     * Sets the project path.
     * 
     * @param projectPath the project path
     */
    public void setProjectPath(String projectPath) {
        m_projectPath = projectPath;
    }
}
