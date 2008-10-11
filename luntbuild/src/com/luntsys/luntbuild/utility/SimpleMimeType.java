/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-6-29
 * Time: 16:09:22
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */

package com.luntsys.luntbuild.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple MIME type querying class. For a more complete
 * MIME type lookup, call {@link javax.servlet.ServletContext#getMimeType(String)}.
 */
public class SimpleMimeType {
	private static final Map mimeTypes;

	static {
		mimeTypes = new HashMap();
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("html", "text/html");
		mimeTypes.put("txt", "text/plain");
		mimeTypes.put("xml", "text/xml");
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/javascript");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("bmp", "image/bmp");
		mimeTypes.put("tif", "image/tiff");
		mimeTypes.put("tiff", "image/tiff");
	}

	/**
	 * Gets the MIME type for the specified file extension.
	 * 
	 * @param extension the file extension
	 * @return the MIME type, or <code>null</code> if the file extension is unknown
	 */
	public static String getMimeType(String extension) {
		return (String) mimeTypes.get(extension);
	}
}
