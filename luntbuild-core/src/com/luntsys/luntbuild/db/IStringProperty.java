package com.luntsys.luntbuild.db;

public interface IStringProperty {

	public String getDisplayName();

	public String getValue();

	public void setValue(String value);

	public boolean isRequired();

	public boolean isSecret();

	public boolean isMultiline();
}
