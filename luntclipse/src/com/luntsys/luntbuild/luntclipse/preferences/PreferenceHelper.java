package com.luntsys.luntbuild.luntclipse.preferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;

/**
 * Preferenece Helper class to manage preference store.
 *
 * @author Lubos Pochman
 *
 */
public class PreferenceHelper {

    /** Luntbuild preferences keys */
    public static final String P_LUNTBUILD_NAMES_LIST = "P_LUNTBUILD_NAMES_LIST";
    public static final String P_LUNTBUILD_NAME = "P_LUNTBUILD_NAME";
    public static final String P_LUNTBUILD_USER = "P_LUNTBUILD_USER";
    public static final String P_LUNTBUILD_PASSWORD = "P_LUNTBUILD_PASSWORD";
    public static final String P_LUNTBUILD_URL = "P_LUNTBUILD_URL";
    public static final String P_LUNTBUILD_NOTIFY_CONDITION = "P_LUNTBUILD_NOTIFY_CONDITION";
    public static final String P_LUNTBUILD_PAUSED = "P_LUNTBUILD_PAUSED";
    public static final String P_LUNTBUILD_REFRESH_TIME = "P_REFRESH_TIME";
    public static final String P_LUNTBUILD_VERSION = "P_LUNTBUILD_VERSION";
    public static final String P_LUNTBUILD_NOTIFY_CONNECTION = "P_LUNTBUILD_NOTIFY_CONNECTION";
    public static final String P_LUNTBUILD_NOTIFY_TRAY = "P_LUNTBUILD_NOTIFY_TRAY";
    public static final String P_LUNTBUILD_ALWAYS_RUN_NOTIFY_TRAY = "P_LUNTBUILD_ALWAYS_RUN_NOTIFY_TRAY";


    /** Returns list of stored connections
     * @return list of stored connections
     */
    public static ArrayList getConnections() {
        ArrayList list = new ArrayList();

        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();

        // Get names list
        String names = store.getString(P_LUNTBUILD_NAMES_LIST);
        StringTokenizer tok = new StringTokenizer(names, ",");
        while(tok.hasMoreTokens()) {
            String name = tok.nextToken();

            list.add(getConnection(name));
        }

        return list;
    }

    /** Gets connection by name
     * @param name name
     * @return connection data
     */
    public static ConnectionData getConnection(String name) {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        ConnectionData data = new ConnectionData();
        data.setName(name);
        data.setUser(store.getString(P_LUNTBUILD_USER + "_" + name));
        data.setPassword(store.getString(P_LUNTBUILD_PASSWORD + "_" + name));
        data.setUrl(store.getString(P_LUNTBUILD_URL + "_" + name));
        data.setVersion(store.getString(P_LUNTBUILD_VERSION + "_" + name));
        data.setNotifyCondition(store.getString(P_LUNTBUILD_NOTIFY_CONDITION + "_" + name));
        data.setPaused(Boolean.valueOf(store.getString(P_LUNTBUILD_PAUSED + "_" + name)));
        return data;
    }

    /** Gets connection by name
     * @param name name
     * @return connection data
     */
    public static void saveConnection(ConnectionData data) {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        // Set connection values
        store.setValue(P_LUNTBUILD_NAME + "_" + data.getName(), data.getName());
        store.setValue(P_LUNTBUILD_USER + "_" + data.getName(), data.getUser());
        store.setValue(P_LUNTBUILD_PASSWORD + "_" + data.getName(), data.getPassword());
        store.setValue(P_LUNTBUILD_URL + "_" + data.getName(), data.getUrl());
        store.setValue(P_LUNTBUILD_VERSION + "_" + data.getName(), data.getVersion());
        store.setValue(P_LUNTBUILD_NOTIFY_CONDITION + "_" + data.getName(), data.getNotifyCondition().toString());
        store.setValue(P_LUNTBUILD_PAUSED + "_" + data.getName(), Boolean.toString(data.isPaused()));
    }

    /** Add connection
     * @param data data
     */
    public static void addConnection(ConnectionData data) {

        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();

        // Add to names list
        String names = store.getString(P_LUNTBUILD_NAMES_LIST);
        StringBuffer buf = new StringBuffer(names);
        if (buf.length() > 0) buf.append(",");
        buf.append(data.getName());
        store.setValue(P_LUNTBUILD_NAMES_LIST, buf.toString());

        // Set connection values
        store.setValue(P_LUNTBUILD_NAME + "_" + data.getName(), data.getName());
        store.setValue(P_LUNTBUILD_USER + "_" + data.getName(), data.getUser());
        store.setValue(P_LUNTBUILD_PASSWORD + "_" + data.getName(), data.getPassword());
        store.setValue(P_LUNTBUILD_URL + "_" + data.getName(), data.getUrl());
        store.setValue(P_LUNTBUILD_VERSION + "_" + data.getName(), data.getVersion());
        store.setValue(P_LUNTBUILD_NOTIFY_CONDITION + "_" + data.getName(), data.getNotifyCondition().toString());
        store.setValue(P_LUNTBUILD_PAUSED + "_" + data.getName(), Boolean.toString(data.isPaused()));
    }

    /** Remove connection
     * @param data data
     */
    public static void removeConnection(ConnectionData data) {

        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();

        // Remove from names list
        String names = store.getString(P_LUNTBUILD_NAMES_LIST);
        StringTokenizer tok = new StringTokenizer(names, ",");
        StringBuffer buf = new StringBuffer();
        boolean isFirst = true;
        while(tok.hasMoreTokens()) {
            if (!isFirst)
                buf.append(",");
            else
                isFirst = false;
            String name = tok.nextToken();
            if (!name.equals(data.getName())) buf.append(name);
        }
        store.setValue(P_LUNTBUILD_NAMES_LIST, buf.toString());

        // Reset connection values
        store.setValue(P_LUNTBUILD_NAME + "_" + data.getName(), "");
        store.setValue(P_LUNTBUILD_USER + "_" + data.getName(), "");
        store.setValue(P_LUNTBUILD_PASSWORD + "_" + data.getName(), "");
        store.setValue(P_LUNTBUILD_URL + "_" + data.getName(), "");
        store.setValue(P_LUNTBUILD_VERSION + "_" + data.getName(), "");
        store.setValue(P_LUNTBUILD_NOTIFY_CONDITION + "_" + data.getName(), data.getNotifyCondition().toString());
        store.setValue(P_LUNTBUILD_PAUSED + "_" + data.getName(), Boolean.toString(data.isPaused()));
    }

    /**
     * Remove all connections
     */
    public static void removeAllConnections() {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        store.setValue(P_LUNTBUILD_NAMES_LIST, "");
    }

    /**
     * @return refresh time in msec
     */
    public static int getRefreshTime() {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        return store.getInt(P_LUNTBUILD_REFRESH_TIME) * 1000;
    }

    public static boolean useNotifyTray() {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        return store.getBoolean(P_LUNTBUILD_NOTIFY_TRAY);
    }

    public static boolean alwaysRunNotifierTray() {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        return store.getBoolean(P_LUNTBUILD_ALWAYS_RUN_NOTIFY_TRAY);
    }

    public static String getNotifyConfiguration() {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        return store.getString(P_LUNTBUILD_NOTIFY_CONNECTION);
    }

    public static void setNotifyConfiguration(String configName) {
        IPreferenceStore store = LuntclipsePlugin.getDefault().getPreferenceStore();
        store.setValue(P_LUNTBUILD_NOTIFY_CONNECTION, configName);
    }

}
