/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.project.scene;


/**
 * <p><b>Title:</b>
 * Planet Camera Control
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Camera Controller for use with planet centric views.
 * In normal mode, it constrains the camera target on the
 * ellipsoid, so that a translation is effectively a circular
 * translation around the planet center and a rotation is
 * around the target on the surface.
 * (i.e. this is close to google earth and world wind behavior)
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 11, 2006
 * @version 1.0
 */
public class CameraControl_Planet extends CameraControl_Base
{
       
    public CameraControl_Planet(Scene scene)
    {
        super(scene);
    }
       

    
}
