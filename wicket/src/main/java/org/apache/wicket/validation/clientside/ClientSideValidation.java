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
package org.apache.wicket.validation.clientside;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.IValidator;

public class ClientSideValidation implements IClientSideValidation
{

	public void renderHead(Form<?> form, final IHeaderResponse response)
	{
		form.visitFormComponents(new IVisitor<FormComponent<?>, Void>()
		{
			public void component(FormComponent<?> object, IVisit<Void> visit)
			{
				if (object.isVisibleInHierarchy() && object.isEnabledInHierarchy())
				{
					for (IValidator<?> validator : object.getValidators())
					{
						if (validator instanceof IClientSideValidator)
						{
							process(object, (IClientSideValidator<?>)validator, response);
						}
					}
				}
			}
		});
	}

	private <T> void process(FormComponent<?> object, IClientSideValidator<?> validator,
		IHeaderResponse response)
	{
		FormComponent<T> component = (FormComponent<T>)object;
		IClientSideRule<T> rule = (IClientSideRule<T>)validator.getClientSideRule();
		Object tag = component.getMarkup().find(component.getId());
		if (rule.supports(component, component.getMarkupTag()))
		{
			process(component, rule, response);
		}
	}

	private <T> void process(FormComponent<T> component, IClientSideRule<T> rule,
		IHeaderResponse response)
	{
		component.setOutputMarkupId(true);

		response.renderJavascriptReference(new PackageResourceReference(ClientSideValidation.class,
			"validation.js"));

		// define the rule
		CharSequence def = rule.getDefinition();
		CharSequence name = rule.getName();
		response.renderJavascript(String.format("Wicket.Validation.define('%s',%s);", name, def),
			rule.getClass().getName() + ".def");

		// attach the rule
		response.renderOnDomReadyJavascript(String.format(
			"Wicket.Event.add(Wicket.$('%s'), 'blur',function() { Wicket.Validation.validate(this, '%s', %s); });",
			component.getMarkupId(), name, rule.getParameters(component)));


	}
}
