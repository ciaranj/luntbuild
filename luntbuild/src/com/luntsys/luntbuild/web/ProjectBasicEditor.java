/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-6
 * Time: 15:58:02
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package com.luntsys.luntbuild.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Role;
import com.luntsys.luntbuild.db.RolesMapping;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.security.InternalRoles;
import com.luntsys.luntbuild.utility.BaseUserSelectionModel;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.vcs.Vcs;
import com.luntsys.luntbuild.web.selectionmodels.VcsAdaptorSelectionModel;

/**
 * This component responsible for editing basic information about a project, such as
 * project name, description, version control system informaiton, etc.
 *
 * @author robin shine
 */
public abstract class ProjectBasicEditor extends BaseComponent {
    
    private static Log logger = LogFactory.getLog(ProjectBasicEditor.class);
    
	private Class vcsAdaptor;
    
	/**
	 * save the edit results
	 * @param cycle
	 */
	public void save(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		// verifies user input
		Project project = getProjectPage().getProject();
		if (Luntbuild.isEmpty(project.getName())){
			setErrorMsg("Field \"Name\" should not be empty!");
			return;
		}
		project.setName(project.getName().trim());

		try{
			project.getVcs().validateProjectLevelProperties();
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
			return;
		}
		getProjectPage().saveData();
		Luntbuild.getSchedService().rescheduleBuilds();
		getBasicTab().setCurrentAction(null); // set the null action to display the project information
	}

	/**
	 * Cancels the editing action. If the project is new creating, the top level home page
	 * will be activated, otherwise, basic information about current project is displayed.
	 * @param cycle
	 */
	public void cancel(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		Project project = getProjectPage().getProject();
		if (project.getId() == 0){ // creating new project
			Home home = (Home)cycle.getPage("Home");
			home.activateExternalPage(null, cycle);
			cycle.activate(home);
		} else{
			getProjectPage().loadData(); // reload data to discard changes
			getBasicTab().setCurrentAction(null);
		}
	}

	public ProjectPage getProjectPage(){
		return (ProjectPage)getPage();
	}

	public ProjectBasicTab getBasicTab(){
		return (ProjectBasicTab)getContainer();
	}

	public void setVcsAdaptor(Class vcsAdaptor){
		this.vcsAdaptor  = vcsAdaptor; // temporarily save the class for later use
	}

	public Class getVcsAdaptor(){
		return getProjectPage().getProject().getVcs().getClass();
	}

	/**
	 * Called when user select a different vcs adaptor type. It will changes vcs class of the
	 * underlying project object
	 * @param cycle
	 */
	public void changeVcsAdaptor(IRequestCycle cycle){
		if (isSaveOrCancelTriggered())
			return;
		Project project = getProjectPage().getProject();
		Class oldVcsAdaptor = project.getVcs().getClass();
		if (oldVcsAdaptor == vcsAdaptor)
			return;
		try{
			Vcs vcs = (Vcs)vcsAdaptor.newInstance();
			project.setVcs(vcs);
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract int getPropertyIndex();

	public abstract boolean isSaveOrCancelTriggered();

	public abstract void setSaveOrCancelTriggered(boolean saveOrCancelTriggered);

	public String getPropertyNameCssClass() {
		if (getPropertyIndex() % 2 == 0)
			return "propertyEditorName1";
		else
			return "propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getPropertyIndex() % 2 == 0)
			return "propertyEditorValue1";
		else
			return "propertyEditorValue2";
	}

	public String getPropertyEditorTailCssClass() {
		if (getProjectPage().getProject().getVcs().getProjectLevelProperties().size() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}

    public String getPaletteCssClass(int ix) {
        
        int absIx = (getProjectPage().getProject().getVcs().getProjectLevelProperties().size() + getPropertyIndex()) + ix;
        
        if (absIx % 2 == 0)
            return "palette1";
        else
            return "palette2";
                    
    }

    public String getPaletteFieldCssClass(int ix) {
        
        int absIx = (getProjectPage().getProject().getVcs().getProjectLevelProperties().size() + getPropertyIndex()) + ix;
        
        if (absIx % 2 == 0)
            return "propertyEditorName1";
        else
            return "propertyEditorName2";
                    
    }

    public String getNotifierTitle2CssClass() {
        int absIx = (getProjectPage().getProject().getVcs().getProjectLevelProperties().size() + getPropertyIndex());
        
        if (absIx % 2 == 0)                
            return "notifierTitle1";
        else
            return "notifierTitle2";
    }
    
    
    public String getNotifierTitleCssClass() {
            return "notifierTitle1";
    }
    
    
	public IPropertySelectionModel getVcsAdaptorSelectionModel() {
        
		return new VcsAdaptorSelectionModel();
	}
    
    // prj specific user role mapping support 
    
    public IPropertySelectionModel getAdminUserSelectionModel() {
        
        return new BaseUserSelectionModel(getAvailableUsers());
    }

    public IPropertySelectionModel getBuildUserSelectionModel() {
        
        return new BaseUserSelectionModel(getAvailableUsers());
    }

    public IPropertySelectionModel getViewUserSelectionModel() {

        return new BaseUserSelectionModel(getAvailableUsers());
    }
        
    public List getAdminUsers() {
        
        return getMappedRolesUserList(InternalRoles.ROLE_PRJ_ADMIN);
    }

    public void setAdminUsers(List adminUsers) {
        putMappedRolesUserList(adminUsers, InternalRoles.ROLE_PRJ_ADMIN);
    }       

    public List getBuildUsers() {        
        return getMappedRolesUserList(InternalRoles.ROLE_BUILD_ADMIN);        
    }

    public void setBuildUsers(List buildUsers) {        
        putMappedRolesUserList(buildUsers, InternalRoles.ROLE_BUILD_ADMIN);
    }
        
    public List getViewUsers() {        
        return getMappedRolesUserList(InternalRoles.ROLE_VIEWER);
    }

    public void setViewUsers(List viewUsers) {
        putMappedRolesUserList(viewUsers, InternalRoles.ROLE_VIEWER);        
    }
    
    /**
     * save all new mapped roles to project
     * 
     * @param userlist
     * @param roleName
     */
    private void putMappedRolesUserList(List userlist, String roleName)
    {
        // remove all existing roles 
        Project project = getProjectPage().getProject();
        
        Set rolemappings = project.getRolesMappings();
        
        if ( rolemappings != null)
        {
            Iterator iter = rolemappings.iterator();
            
            while ( iter.hasNext())
            {
                RolesMapping rolemapping = (RolesMapping)iter.next();
                
                if (rolemapping.getRole().getName().equals(roleName))
                {                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug(roleName+" : removed rolemapping : "+rolemapping.getRole().getName()+" from list for user : "+rolemapping.getUser().getName());    
                    }
                                    
                    iter.remove();
                }            
            }            
        }
        else
        {
            rolemappings = new HashSet();
        }
                
        if ( userlist != null)
        {
            // find dbbased matching role
            Role role = getMatchingRole(roleName);                       
            
            Iterator iter = userlist.iterator();
            
            while ( iter.hasNext())
            {
                User user = (User)iter.next();
                
                RolesMapping rm = new RolesMapping();
                
                rm.setUser(user);
                rm.setProject(project);
                rm.setRole(role);

                if (logger.isDebugEnabled())
                {
                    logger.debug(roleName+" : adding rolemapping : "+rm.getRole().getName()+" from list for user : "+rm.getUser().getName());    
                }
                                
                rolemappings.add(rm);
            }                    
        }
    }
    
    /**
     *  split project related rolemapping to 
     *  separate list for handling by model
     */
    private List getMappedRolesUserList(String roleName)
    {                
        List usersWithAssignedRoles = new ArrayList();
        
        Project project = getProjectPage().getProject();

        Set rml = project.getRolesMappings();
        
        if ( rml != null)
        {
            Iterator iter = rml.iterator();
            
            while (iter.hasNext())
            {
                RolesMapping rm = (RolesMapping) iter.next();
                
                User user = rm.getUser();
                Role role = rm.getRole();
                
                if (role.getName().equals(roleName))
                {
                    usersWithAssignedRoles.add(user);
                }
            }            
        }
                
        return usersWithAssignedRoles;
    }
    
    private List getAvailableUsers() {
        
        List users = Luntbuild.getDao().loadUsersWithoutVirtual();
        return users;
    }
    
    private Role getMatchingRole(String roleName)
    {
        List internalRoles = InternalRoles.getRoles();
                
        Iterator iter = internalRoles.iterator();
        boolean found = false;
        Role role = null;
        
        while ( iter.hasNext() && (found == false))
        {
            role = (Role)iter.next();
            
            found = role.getName().equals(roleName);
        }
        
        return role;        
    }
    
}
