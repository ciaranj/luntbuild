package com.luntsys.luntbuild.luntclipse.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 *
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 1.5 $
 * @since 	 0.0.1
 *
 */
public class LuntclipsePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

    private boolean applyPerformed = false;

	/**
	 * Preference page
	 */
	public LuntclipsePreferencePage() {
		super(GRID);
		setPreferenceStore(LuntclipsePlugin.getDefault().getPreferenceStore());
		setDescription("Luntclipse preference page");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
        addField(
                new StringFieldEditor(PreferenceHelper.P_LUNTBUILD_NUM_RETRIES, "Retries on Login:", 5,
                        getFieldEditorParent()));
        addField(
                new StringFieldEditor(PreferenceHelper.P_LUNTBUILD_CONNECTION_TIMEOUT,
                        "Connection timeout (in sec):", 10,
                        getFieldEditorParent()));
        addField(
                new StringFieldEditor(PreferenceHelper.P_LUNTBUILD_REFRESH_TIME, "Refresh Interval (s):", 30,
                        getFieldEditorParent()));
        addField(
                new BooleanFieldEditor(PreferenceHelper.P_LUNTBUILD_NOTIFY_TRAY,
                        " Use Notify Tray (requires restart)", getFieldEditorParent()));
        addField(
                new BooleanFieldEditor(PreferenceHelper.P_LUNTBUILD_ALWAYS_RUN_NOTIFY_TRAY,
                        " Run Notify Tray even if View is Hidden (requires restart)", getFieldEditorParent()));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

    protected void performApply() {
        super.performOk();
        LuntclipsePlugin.getDefault().setRefreshJobDelay();
        this.applyPerformed = true;
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if(!this.applyPerformed){
            performApply();
        }
        return true;
    }
}