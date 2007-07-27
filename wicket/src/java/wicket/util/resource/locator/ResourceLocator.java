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
package wicket.util.resource.locator;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.util.resource.IResource;
import wicket.util.string.Strings;

/**
 * Helper class that adds convenience methods to any IResourceLocator.
 * 
 * @author Juergen Donnerstag
 * @author Jonathan Locke
 */
public class ResourceLocator
{
	/** Logging */
	private static Log log = LogFactory.getLog(ResourceLocator.class);

	/** The resource locator */
	private IResourceLocator locator;

	/**
	 * Constructor
	 * 
	 * @param locator
	 *            The resource locator
	 */
	public ResourceLocator(IResourceLocator locator)
	{
		this.locator = locator;
	}

	/**
	 * Locate a resource based on a class and an extension.
	 * 
	 * @param c
	 *            Class next to which the resource should be found
	 * @param extension
	 *            Resource extension
	 * @return The resource
	 */
	public IResource locate(final Class c, final String extension)
	{
		return locate(c, null, Locale.getDefault(), extension);
	}

	/**
	 * Locate a resource based on a a class, a style, a locale and an extension.
	 * 
	 * @param c
	 *            Class next to which the resource should be found
	 * @param style
	 *            Any resource style, such as a skin style
	 * @param locale
	 *            The locale of the resource to load
	 * @param extension
	 *            Resource extension
	 * @return The resource
	 */
	public IResource locate(final Class c, final String style, final Locale locale,
			final String extension)
	{
		return locate(c.getName(), style, locale, extension);
	}

	/**
	 * Convenience method to load a resource. If no extension is specified, this
	 * convenience method will extract the extension from the path. If the
	 * extension does not start with a dot, one will be added automatically.
	 * 
	 * @param path
	 *            The path of the resource
	 * @param style
	 *            Any resource style, such as a skin style
	 * @param locale
	 *            The locale of the resource to load
	 * @param extension
	 *            The extension of the resource
	 * @return The resource
	 */
	public IResource locate(String path, final String style, final Locale locale,
			final String extension)
	{
		// If no extension specified, extract extension
		final String extensionString;
		if (extension == null)
		{
			extensionString = "." + Strings.lastPathComponent(path, '.');
			path = Strings.beforeLastPathComponent(path, '.');
		}
		else
		{
			if (extension.startsWith("."))
			{
				extensionString = extension;
			}
			else
			{
				extensionString = "." + extension;
			}
		}

		return locator.locate(path.replace('.', '/'), style, locale, extensionString);
	}
}