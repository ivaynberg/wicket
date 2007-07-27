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

import wicket.Resource;
import wicket.util.resource.IResource;

/**
 * A resource is something that implements IResourceListener.
 * 
 * @author Jonathan Locke
 */
public abstract class WebResource extends Resource
{	
	/**
	 * @see Resource#getResource()
	 */
	protected abstract IResource getResource();
}