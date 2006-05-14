package com.luntsys.luntbuild.luntclipse.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
        layout.setFixed(true);

        IFolderLayout folder =
            layout.createFolder("folder", IPageLayout.BOTTOM, 1.0f, layout.getEditorArea());
        folder.addView("com.luntsys.luntbuild.luntclipse.views.LuntbuildView");
	}

}
