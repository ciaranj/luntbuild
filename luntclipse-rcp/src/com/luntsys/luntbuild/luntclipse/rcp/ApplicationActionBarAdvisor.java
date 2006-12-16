package com.luntsys.luntbuild.luntclipse.rcp;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction preferencesAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction helpTocAction;
	private UpdateAction updateAction;
    private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

        this.preferencesAction = ActionFactory.PREFERENCES.create(window);
        this.exitAction = ActionFactory.QUIT.create(window);
        this.helpTocAction = ActionFactory.HELP_CONTENTS.create(window);
        this.updateAction = new UpdateAction(window);
        this.aboutAction = ActionFactory.ABOUT.create(window);

		register(this.preferencesAction);
        register(this.exitAction);
        register(this.helpTocAction);
		register(this.updateAction);
        register(this.aboutAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		fileMenu.add(this.preferencesAction);
        fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        fileMenu.add(this.exitAction);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        // Welcome or intro page would go here
        helpMenu.add(helpTocAction);
        helpMenu.add(this.updateAction);
        helpMenu.add(this.aboutAction);
        menuBar.add(helpMenu);
	}

}

class UpdateAction extends Action implements IWorkbenchWindowActionDelegate  {
	private IWorkbenchWindow window;

	public UpdateAction(IWorkbenchWindow window) {
		this.window = window;
		setId("org.eclipse.schedule.newUpdates");
		setText("&Check for Updates...");
		setToolTipText("Search for updates...");
	}

	public void run() {
		openInstaller(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
	}

		public void run(IAction action) {
		openInstaller(window);
	}

	private void openInstaller(final IWorkbenchWindow window) {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
			public void run() {
				UpdateJob job = new UpdateJob("Search for new updates",
						getSearchRequest());
				UpdateManagerUI.openInstaller(window.getShell(), job);
			}
		});
	}

	private UpdateSearchRequest getSearchRequest() {
		UpdateSearchRequest result = new UpdateSearchRequest(
				UpdateSearchRequest.createDefaultSiteSearchCategory(),
				new UpdateSearchScope());
		result.addFilter(new BackLevelFilter());
		result.addFilter(new EnvironmentFilter());
		UpdateSearchScope scope = new UpdateSearchScope();
		try {
			String homeBase = "http://www.pmease.com/quiclipse-update";
			URL url = new URL(homeBase);
			scope.addSearchSite("Quiclipse site", url, null);
		} catch (MalformedURLException e) {
			// skip bad URLs
		}
		result.setScope(scope);
		return result;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

	public void dispose() {
		// do nothing
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
