/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import wicket.PageParameters;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.model.PropertyModel;


/**
 */
public class MarkupInheritanceBase_4 extends WebPage 
{
	private int counter = 0;

	/**
	 * Construct.
	 * @param parameters
	 */
	public MarkupInheritanceBase_4(final PageParameters parameters)
	{
		add(new Label("label1", new PropertyModel(this, "counter")));
		add(new Link("link")
		{
			public void onClick()
			{
				counter++;
			}
		});
	}

	/**
	 * Gets the counter.
	 * @return counter
	 */
	public int getCounter()
	{
		return counter;
	}

	/**
	 * Sets the counter.
	 * @param counter counter
	 */
	public void setCounter(int counter)
	{
		this.counter = counter;
	}
}