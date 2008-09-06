/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.ajaxng.markup.html;

import org.apache.wicket.Component;
import org.apache.wicket.ajaxng.AjaxEventBehavior;
import org.apache.wicket.ajaxng.AjaxRequestAttributes;
import org.apache.wicket.ajaxng.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

/**
 * A component that allows a trigger request to be triggered via html anchor tag
 * 
 * @since 1.2
 * 
 * @author Igor Vaynberg (ivaynberg)
 * @author Matej Knopp
 * @param <T> 
 * 
 */
public abstract class AjaxLink<T> extends AbstractLink implements IAjaxLink
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public AjaxLink(final String id)
	{
		this(id, null);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 */
	public AjaxLink(final String id, final IModel<T> model)
	{
		super(id, model);

		add(new AjaxEventBehavior("click")
		{
			private static final long serialVersionUID = 1L;

			
			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				onClick(target);
			}

			@Override
			public boolean isEnabled(Component component)
			{
				return isLinkEnabled();
			}
			
			@Override
			protected void updateAttributes(AjaxRequestAttributes attributes, Component component)
			{
				super.updateAttributes(attributes, component);
				AjaxLink.this.updateAttributes(attributes);
			}
		});
	}
	
	protected void updateAttributes(AjaxRequestAttributes attributes)
	{
		
	}
		
	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);

		if (isLinkEnabled())
		{
			// disable any href attr in markup
			if (tag.getName().equalsIgnoreCase("a") || tag.getName().equalsIgnoreCase("link") ||
				tag.getName().equalsIgnoreCase("area"))
			{
				tag.put("href", "#");
			}
		}
		else
		{
			disableLink(tag);
		}

	}

	/**
	 * Listener method invoked on the ajax request generated when the user clicks the link
	 * 
	 * @param target
	 */
	public abstract void onClick(final AjaxRequestTarget target);

	/**
	 * Gets model
	 * 
	 * @return model
	 */
	@SuppressWarnings("unchecked")
	public final IModel<T> getModel()
	{
		return (IModel<T>)getDefaultModel();
	}

	/**
	 * Sets model
	 * 
	 * @param model
	 */
	public final void setModel(IModel<T> model)
	{
		setDefaultModel(model);
	}

	/**
	 * Gets model object
	 * 
	 * @return model object
	 */
	@SuppressWarnings("unchecked")
	public final T getModelObject()
	{
		return (T)getDefaultModelObject();
	}

	/**
	 * Sets model object
	 * 
	 * @param object
	 */
	public final void setModelObject(T object)
	{
		setDefaultModelObject(object);
	}

}