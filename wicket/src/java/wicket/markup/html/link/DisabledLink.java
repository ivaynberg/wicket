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
package wicket.markup.html.link;

import wicket.markup.html.WebMarkupContainer;

/**
 * Handy class for holding markup to display when a link is disabled.
 * 
 * @see Link
 * @author Jonathan Locke
 */
public final class DisabledLink extends WebMarkupContainer
{
	/** Serial Version ID. */
	private static final long serialVersionUID = 5315730184113248127L;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of this component
	 */
	public DisabledLink(final String name)
	{
		super(name);
	}
}