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
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.IValidator;

public class ClientSideValidation implements IClientSideValidation
{

	public void renderHead(Form<?> form, IHeaderResponse response)
	{
		form.visitFormComponents(new IVisitor<FormComponent<?>, Void>()
		{
			public void component(FormComponent<?> object, IVisit<Void> visit)
			{
				for (IValidator<?> validator : object.getValidators())
				{
					if (validator instanceof IClientSideValidator)
					{
						process(object, (IClientSideValidator<?>)validator, response);
					}
				}
			}

			private <T> void process(FormComponent<?> object, IClientSideValidator<?> validator,
				IHeaderResponse response)
			{
				FormComponent<T> component = (FormComponent<T>)object;
				IClientSideRule<T> rule = (IClientSideRule<T>)validator.getClientSideRule();
				if (rule.supports(component, null))// TODO CLIENTSIDE component tag is null
				{
					process(component, rule, response);
				}
			}

			private <T> void process(FormComponent<T> component, IClientSideRule<T> rule,
				IHeaderResponse response)
			{

			}

		});
	}

}
