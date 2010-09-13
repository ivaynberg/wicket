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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.IValidator;

public class JavaScriptValidation implements IClientSideValidation
{
	public <T> void configure(Form<?> form, final IHeaderResponse response)
	{
		form.visitFormComponents(new IVisitor<FormComponent<T>, Void>()
		{
			public void component(FormComponent<T> component, IVisit<Void> visit)
			{
				if (component.isVisibleInHierarchy() && component.isEnabledInHierarchy())
				{
					final ComponentTag tag = component.getMarkupTag();

					List<IJavaScriptRule<? super T>> supported = new ArrayList<IJavaScriptRule<? super T>>(
						getRules(component));

					Iterator<IJavaScriptRule<? super T>> it = supported.iterator();
					while (it.hasNext())
					{
						if (!it.next().supports(component, component.getMarkupTag()))
						{
							it.remove();
						}
					}

					configureRules(component, supported, response);
				}
			}
		});
	}

	protected <T> void configureRules(FormComponent<T> component,
		Collection<IJavaScriptRule<? super T>> rules, IHeaderResponse response)
	{
		component.setOutputMarkupId(true);
		contributeResources(response);
		for (IJavaScriptRule<? super T> rule : rules)
		{
			defineRule(rule, response);
		}
		attachRules(component, rules, response);
	}

	protected void contributeResources(IHeaderResponse response)
	{
		response.renderJavascriptReference(new PackageResourceReference(JavaScriptValidation.class,
			"validation.js"));
	}

	protected <T> CharSequence defineRule(IJavaScriptRule<? super T> rule, IHeaderResponse response)
	{
		CharSequence def = rule.getDefinition();
		CharSequence name = rule.getName();
		response.renderJavascript(String.format("Wicket.Validation.define('%s',%s);", name, def),
			rule.getClass().getName() + ".def");
		return name;
	}

	protected <T> void attachRules(FormComponent<T> component,
		Collection<IJavaScriptRule<? super T>> rules, IHeaderResponse response)
	{
		StringBuilder validator = new StringBuilder();
		validator.append("Wicket.Event.add(Wicket.$('")
			.append(component.getMarkupId())
			.append("'), 'blur', ");
		appendRulesInvocationFunction(component, rules, validator);
		validator.append(");");
		response.renderOnDomReadyJavascript(validator.toString());
	}

	protected <T> void appendRulesInvocationFunction(FormComponent<T> component,
		Collection<IJavaScriptRule<? super T>> rules, StringBuilder buffer)
	{
		buffer.append("function() { ");
		for (IJavaScriptRule<? super T> rule : rules)
		{
			appendRuleInvocation(rule, component, buffer);
		}
		buffer.append("}");

	}


	protected <T> void appendRuleInvocation(IJavaScriptRule<? super T> rule, FormComponent<T> fc,
		StringBuilder buffer)
	{
		buffer.append(String.format("Wicket.Validation.validate(this, '%s', %s);", rule.getName(),
			rule.getParameters(fc)));
	}

	protected <T> List<IJavaScriptRule<? super T>> getRules(FormComponent<T> component)
	{
		List<IJavaScriptRule<? super T>> rules = new ArrayList<IJavaScriptRule<? super T>>();

		for (IValidator<? super T> validator : component.getValidators())
		{
			if (validator instanceof IJavaScriptCapableValidator)
			{
				rules.add(((IJavaScriptCapableValidator<? super T>)validator).getClientSideRule());
			}
		}

		return rules;
	}
}
