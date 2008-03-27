package com.luntsys.luntbuild.web.selectionmodels;

import com.luntsys.luntbuild.facades.Constants;
import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * Tapestry selection model for jabber server type
 *
 * @author Lubos Pochman
 */
public class JabberServerTypeSelection implements IPropertySelectionModel {
    public int getOptionCount() {
        return Constants.NUM_JABBER_SERVER_TYPE;
    }

    public Object getOption(int index) {
        return new Integer(index);
    }

    public String getLabel(int index) {
        return Constants.getJabberServerType(index);
    }

    public String getValue(int index) {
        return getOption(index).toString();
    }

    public Object translateValue(String value) {
        return new Integer(value);
    }
}
