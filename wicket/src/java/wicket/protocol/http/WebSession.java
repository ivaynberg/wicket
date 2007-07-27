/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.protocol.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

import wicket.Application;
import wicket.IRequestCycleFactory;
import wicket.Request;
import wicket.RequestCycle;
import wicket.Response;
import wicket.Session;

/**
 * Session subclass for HTTP protocol which holds an HttpSession object and
 * provides access to that object via getHttpSession(). A method which abstracts
 * session invalidation is also provided via invalidate().
 * 
 * @author Jonathan Locke
 */
public class WebSession extends Session
{
	/** The underlying HttpSession object */
	private transient javax.servlet.http.HttpSession httpSession;

	/** The request cycle factory for the session */
	private transient IRequestCycleFactory requestCycleFactory;

	/** The attribute in the HttpSession where this WebSession object is stored */
	private transient String sessionAttributePrefix;

	/** True, if session has been invalidated */
	private transient boolean sessionInvalid = false;
	
	/**
	 * Constructor
	 * 
	 * @param application
	 *            The application
	 */
	protected WebSession(final Application application)
	{
		super(application);
	}

	/**
	 * @return The underlying HttpSession object
	 */
	public HttpSession getHttpSession()
	{
		return httpSession;
	}

	/**
	 * Invalidates this session
	 */
	public void invalidate()
	{
		try
		{
			httpSession.invalidate();
		}
		catch (IllegalStateException e)
		{
			// Ignore
		}
		
		sessionInvalid = true;
	}

	/**
	 * Replicates this session to the cluster if it has changed.
	 */
	public final void updateCluster()
	{
	    if (sessionInvalid == false)
	    {
	        super.updateCluster();
	    }
	}
	
	/**
	 * @see Session#getAttribute(String)
	 */
	protected Object getAttribute(final String name)
	{
		return httpSession.getAttribute(sessionAttributePrefix + "-" + name);
	}

	/**
	 * @see Session#getAttributeNames()
	 */
	protected List getAttributeNames()
	{
		final List list = new ArrayList();
		final Enumeration names = httpSession.getAttributeNames();
		final String prefix = sessionAttributePrefix + "-";
		while (names.hasMoreElements())
		{
			final String name = (String)names.nextElement();
			if (name.startsWith(prefix))
			{
				list.add(name.substring(prefix.length()));
			}
		}
		return list;
	}

	/**
	 * @see wicket.Session#getRequestCycleFactory()
	 */
	protected IRequestCycleFactory getRequestCycleFactory()
	{
		if (requestCycleFactory == null)
		{
			this.requestCycleFactory = new IRequestCycleFactory()
			{
				public RequestCycle newRequestCycle(Session session, Request request,
						Response response)
				{
					// Respond to request
					return new WebRequestCycle((WebSession)session, (WebRequest)request,
							(WebResponse)response);
				}
			};
		}
		return requestCycleFactory;
	}

	/**
	 * @see wicket.Session#removeAttribute(java.lang.String)
	 */
	protected void removeAttribute(final String name)
	{
		httpSession.removeAttribute(sessionAttributePrefix + "-" + name);
	}

	/**
	 * @see Session#setAttribute(String, Object)
	 */
	protected void setAttribute(final String name, final Object object)
	{
		httpSession.setAttribute(sessionAttributePrefix + "-" + name, object);
	}

	/**
	 * Initializes this session for a request
	 * 
	 * @param httpSession
	 *            The http session to attach
	 * @param sessionAttributePrefix
	 *            The session attribute name
	 */
	final void init(final HttpSession httpSession, final String sessionAttributePrefix)
	{
		// Set session attribute name
		this.sessionAttributePrefix = sessionAttributePrefix;

		// Attach / reattach http servlet session
		this.httpSession = httpSession;

		// Set the current session
		set(this);
	}
}