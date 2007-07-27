/*
 * $Id: FormComponentFeedbackBorder.java,v 1.3 2005/01/02 21:58:21 jonathanlocke
 * Exp $ $Revision$ $Date$
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
package wicket.markup.html.form.validation;

import wicket.feedback.ContainerFeedbackMessageFilter;
import wicket.feedback.IFeedback;
import wicket.feedback.IFeedbackMessageFilter;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.border.Border;

/**
 * A border that can be placed around a form component to indicate when the
 * bordered child has a validation error. A child of the border named
 * "errorIndicator" will be shown and hidden depending on whether the child has
 * an error. A typical error indicator might be a little red asterisk.
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public class FormComponentFeedbackBorder extends Border implements IFeedback
{
	/** The error indicator child which should be shown if an error occurs. */
	private final ErrorIndicator errorIndicator;

	/** Visible property cache. */
	private boolean visible;

	/**
	 * Error indicator that will be shown whenever there is an error-level
	 * message for the collecting component.
	 */
	private final class ErrorIndicator extends WebMarkupContainer
	{
		/**
		 * Construct.
		 * 
		 * @param id
		 *            component id
		 */
		public ErrorIndicator(String id)
		{
			super(id);
		}

		/**
		 * @see wicket.Component#isVisible()
		 */
		public boolean isVisible()
		{
			return visible;
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            See Component
	 */
	public FormComponentFeedbackBorder(final String id)
	{
		super(id);
		add(errorIndicator = new ErrorIndicator("errorIndicator"));
	}

	/**
	 * @see wicket.feedback.IFeedback#updateFeedback()
	 */
	public void updateFeedback()
	{
		// Get the messages for the current page
		visible = getPage().getFeedbackMessages().messages(getMessagesFilter()).size() != 0;
	}

	/**
	 * @return Let subclass specify some other filter
	 */
	protected IFeedbackMessageFilter getMessagesFilter()
	{
		return new ContainerFeedbackMessageFilter(this);
	}
}