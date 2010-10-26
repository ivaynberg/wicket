package org.apache.wicket.ajax.rsh;

import org.apache.wicket.request.resource.JavascriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class RealSimpleHistoryJson2007Reference extends JavascriptResourceReference
{
	private static final long serialVersionUID = 1L;

	/**
	 * Singleton instance of this reference
	 */
	public static final ResourceReference INSTANCE = new RealSimpleHistoryJson2007Reference();

	private RealSimpleHistoryJson2007Reference()
	{
		super(RealSimpleHistoryJson2007Reference.class, "json2007.js");
	}
}
