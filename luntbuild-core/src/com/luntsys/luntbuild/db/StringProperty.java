/**
 *
 */
package com.luntsys.luntbuild.db;

/**
 * @author lubosp
 *
 */
public class StringProperty implements IStringProperty {

	private String displayName;
	private String name;
	private String value;
	private boolean multiline;
	private boolean required;
	private boolean secret;

	public StringProperty() {
		this(null, null, null, false, false, false);
	}

	public StringProperty(String name, String value) {
		this(name, name, value, false, false, false);
	}

	public StringProperty(String name, String displayName, String value, boolean multiline, boolean required, boolean secret) {
		this.name = name;
		this.displayName = displayName;
		this.value = value;
		this.multiline = multiline;
		this.required = required;
		this.secret = secret;
	}

	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.db.IStringProperty#getDisplayName()
	 */
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.db.IStringProperty#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.db.IStringProperty#isMultiline()
	 */
	public boolean isMultiline() {
		return multiline;
	}

	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.db.IStringProperty#isRequired()
	 */
	public boolean isRequired() {
		return required;
	}

	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.db.IStringProperty#isSecret()
	 */
	public boolean isSecret() {
		return secret;
	}

	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.db.IStringProperty#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setMultiline(boolean multiline) {
		this.multiline = multiline;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setSecret(boolean secret) {
		this.secret = secret;
	}

}
