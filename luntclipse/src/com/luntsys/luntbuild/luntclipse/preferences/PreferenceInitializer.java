package com.luntsys.luntbuild.luntclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;

/**
 * Class used to initialize default preference values.
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 378 $
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

        store.setDefault(PreferenceHelper.P_LUNTBUILD_NUM_RETRIES, "3");
	}

}
