package org.apache.wicket.ajax.rsh;

import org.apache.wicket.request.resource.JavascriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class RealSimpleHistoryInitReference extends JavascriptResourceReference
{
	private static final long serialVersionUID = 1L;

	/**
	 * Singleton instance of this reference
	 */
	public static final ResourceReference INSTANCE = new RealSimpleHistoryInitReference();

	private RealSimpleHistoryInitReference()
	{
		super(RealSimpleHistoryInitReference.class, "init.js");
	}
}
