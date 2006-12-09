package com.luntsys.luntbuild.luntclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;

/**
 * Class used to initialize default preference values.
 *
 * @author 	 Roman Pichl�k
 * @version  $Revision: 1.5 $
 * @since 	 0.0.1
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store =
            LuntclipsePlugin.getDefault().getPreferenceStore();

        store.setDefault(PreferenceHelper.P_LUNTBUILD_REFRESH_TIME, "30");
        store.setDefault(PreferenceHelper.P_LUNTBUILD_NOTIFY_TRAY, true);
        store.setDefault(PreferenceHelper.P_LUNTBUILD_ALWAYS_RUN_NOTIFY_TRAY, true);
        store.setDefault(PreferenceHelper.P_LUNTBUILD_NOTIFY_CONNECTION, "None");
	}

}
