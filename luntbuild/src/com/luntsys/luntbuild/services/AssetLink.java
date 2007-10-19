package com.luntsys.luntbuild.services;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.request.RequestContext;
import org.apache.tapestry.util.io.DataSqueezer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Define a link to an asset that may be generated as part of a page render.
 */
public class AssetLink implements ILink {
	protected IRequestCycle requestCycle;
	private String servletPath;
	private String assetLocation;

	/**
	 * Creates a asset link.
	 * 
	 * @param requestCycle the request cycle
	 * @param servletPath the servlet path of the asset
	 * @param assetLocation the real path of the asset
	 * @throws ApplicationRuntimeException
	 */
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
	 * Gets the relative URL to the asset. A relative URL may include a leading slash,
	 * but omits the scheme, host and port portions of a full URL.
	 * 
	 * @return the relative URL
	 * @throws ApplicationRuntimeException if encoding fails
	 */
	public String getURL() {
		return constructURL(new StringBuffer());
	}

	/**
	 * Gets the relative URL to the asset.
	 * 
	 * @param anchor to be appended to the URL, may be <code>null</code>
	 * @param includeParameters if <code>true</code>, parameters are included
	 * @return the relative URL
	 * @throws ApplicationRuntimeException if encoding fails
	 */
	public String getURL(String anchor, boolean includeParameters) {
		return constructURL(new StringBuffer());
	}

	/**
	 * Gets the absolute URL to the asset, using default scheme, server and port,
	 * including parameters, and no anchor.
	 * 
	 * @return the absolute URL
	 * @throws ApplicationRuntimeException if encoding fails
	 */
	public String getAbsoluteURL() {
		return getAbsoluteURL(null, null, 0, null, true);
	}

	/**
	 * Gets the absolute URL to the asset.
	 * 
	 * @param scheme the scheme (protocol)
	 * @param server the server/hostname
	 * @param port the port
	 * @param anchor to be appended to the URL, may be <code>null</code>
	 * @param includeParameters if <code>true</code>, parameters are included
	 * @return the absolute URL
	 * @throws ApplicationRuntimeException if encoding fails
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
	 * Gets the array of parameters names (in no specified order).
	 * 
	 * @return nothing, this is not implemented
	 * @throws RuntimeException because this has not been implemented
	 */
	public String[] getParameterNames() {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Gets the values of the named parameter.
	 * 
	 * @param name the parameter name
	 * @return nothing, this is not implemented
	 * @throws RuntimeException because this has not been implemented
	 */
	public String[] getParameterValues(String name) {
		throw new RuntimeException("Not implemented");
	}
}
