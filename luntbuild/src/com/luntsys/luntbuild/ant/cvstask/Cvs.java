/*
 * Copyright  2000-2002,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.luntsys.luntbuild.ant.cvstask;

/**
 * Performs operations on a CVS repository.
 *
 * <p>original 1.20</p>
 *
 * <p>NOTE: This implementation has been moved to <code>AbstractCvsTask</code> with
 * the addition of some accessors for extensibility.</p>
 *
 *
 * @author costin@dnt.ro
 * @author stefano@apache.org
 * @author Wolfgang Werner
 *         <a href="mailto:wwerner@picturesafe.de">wwerner@picturesafe.de</a>
 * @author Kevin Ross
 *         <a href="mailto:kevin.ross@bredex.com">kevin.ross@bredex.com</a>
 *
 * @since Ant 1.1
 * @see AbstractCvsTask
 * @ant.task category="scm"
 */
public class Cvs extends AbstractCvsTask {

    /**
     * CVS Task - now implemented by the Abstract CVS Task base class.
     */
    public Cvs() {
    	super();
    }
}
