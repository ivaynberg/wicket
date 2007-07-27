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
package wicket.examples.springframework2;

import wicket.examples.WicketExamplePage;
import wicket.markup.html.basic.Label;

/**
 * Simple example for transparent access of a Spring managed bean.l
 * 
 * @author Martin Fey
 */
public class SpringWebPage extends WicketExamplePage
{

    /** Creates a new instance of SpringWebPage */
    public SpringWebPage()
    {
        // set the applicationcontext for further use
        add(new Label("message", new SpringBeanModel("message")));
        add(new Label("user", new SpringBeanPropertyModel(UserModel.class, "forename")));
        add(new Label("user2", new SpringBeanPropertyModel("user", "surname")));
    }
}