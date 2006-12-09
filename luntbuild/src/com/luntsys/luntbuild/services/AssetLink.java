package com.luntsys.luntbuild.services;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.request.RequestContext;
import org.apache.tapestry.util.io.DataSqueezer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AssetLink implements ILink {
	protected IRequestCycle requestCycle;
	private String servletPath;
	private String assetLocation;

	public AssetLink(IRequestCycle requestCycle, String servletPath, String assetLocation) {
		this.requestCycle = requestCycle;
		this.servletPath = servletPath;

		DataSqueezer squeezer = requestCycle.getEngine().getDataSqueezer();

		try {
			this.assetLocation = squeezer.squeeze(assetLocation);
		} catch (IOException e) {
			throw new ApplicationRuntimeException(e);
		}
	}

	/**
	 * @see org.apache.tapestry.engine.ILink#getURL()
	 */
	public String getURL() {
		return constructURL(new StringBuffer());
	}

	/**
	 * @see org.apache.tapestry.engine.ILink#getURL(java.lang.String, boolean)
	 */
	public String getURL(String anchor, boolean includeParameters) {
		return constructURL(new StringBuffer());
	}

	/**
	 * @see org.apache.tapestry.engine.ILink#getAbsoluteURL()
	 */
	public String getAbsoluteURL() {
		return getAbsoluteURL(null, null, 0, null, true);
	}

	/**
	 * @see org.apache.tapestry.engine.ILink#getAbsoluteURL(java.lang.String, java.lang.String, int, java.lang.String, boolean)
	 */
	public String getAbsoluteURL(String scheme, String server, int port, String anchor, boolean includeParameters) {
		RequestContext context = this.requestCycle.getRequestContext();
		StringBuffer buffer = new StringBuffer();

		buffer.append((scheme != null) ? scheme : context.getScheme());
		buffer.append("://");
		buffer.append((server != null) ? server : context.getServerName());
		buffer.append(':');
		buffer.append((port != 0) ? port : context.getServerPort());

		return constructURL(buffer);
	}

	private String constructURL(StringBuffer buffer) {
		RequestContext context = this.requestCycle.getRequestContext();

		buffer.append(context.getRequest().getContextPath()).append('/').append(this.servletPath);

		String encoding = this.requestCycle.getEngine().getOutputEncoding();

		buffer.append("?location=");

		try {
			buffer.append(URLEncoder.encode(assetLocation, encoding));
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationRuntimeException(e);
		}

		return this.requestCycle.encodeURL(buffer.toString());
	}

	/**
	 * @see org.apache.tapestry.engine.ILink#getParameterNames()
	 */
	public String[] getParameterNames() {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * @see org.apache.tapestry.engine.ILink#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		throw new RuntimeException("Not implemented");
	}
}
