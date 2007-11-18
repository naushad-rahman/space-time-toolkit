/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.*;
import org.vast.stt.commands.*;
import org.vast.stt.gui.dialogs.DataProviderJob;
import org.vast.stt.gui.views.ScenePageInput;
import org.vast.stt.project.Project;
import org.vast.stt.project.STTDisplay;
import org.vast.stt.project.scene.Scene;
import org.vast.stt.project.tree.DataItemIterator;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.DataProvider;


public class ProjectMenu implements IWorkbenchWindowActionDelegate
{
    class OpenPageRunnable implements Runnable
    {
        private Project project;
        
        public OpenPageRunnable(Project project)
        {
            this.project = project;
        }
        
        public void run()
        {
            try
            {
                ArrayList<STTDisplay> displayList = project.getDisplayList();
                for (int i=0; i<displayList.size(); i++)
                {
                    STTDisplay nextDisplay = displayList.get(i);
                    
                    // open a new page  a WorldScene
                    if (nextDisplay instanceof WorldScene)
                    {
                        WorldScene scene = (WorldScene)nextDisplay;
                        
                        // keep list of providers we did so we don't do them twice
                        LinkedList processedProviders = new LinkedList<DataProvider>();
                        
                        // add job progress listener to all providers
                        DataItemIterator it = ((Scene)nextDisplay).getDataTree().getItemIterator();
                        while (it.hasNext())
                        {
                            DataProvider provider = it.next().getDataProvider();
                            
                            if (!processedProviders.contains(provider))
                            {
                                if (provider.getSpatialExtent().getUpdater() == null)
                                    new DataProviderJob(provider);
                                processedProviders.add(provider);
                            }
                        }
                        
                        // create scene page input and open new page
                        ScenePageInput pageInput = new ScenePageInput(scene);
                        IWorkbenchWindow oldWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        oldWindow.openPage("STT.Perspective", pageInput);
                        
                        // close empty page
                        if (oldWindow.getActivePage().getInput() == null)
                            oldWindow.close();
                    }
                }
            }
            catch (WorkbenchException e)
            {
                e.printStackTrace();
            }
        }
    };


    public ProjectMenu()
    {
    }


    public void run(IAction action)
    {
        String actionId = action.getId();
        String url = null;

        
        // close project 
        if (actionId.endsWith("CloseProject"))
        {
            // TODO close project
            return;
        }        
        
        // open file chooser window
        if (actionId.endsWith("OpenProject"))
        {
            FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
            String path = fileDialog.open();
            if (path != null)
                url = "file:///" + path.replace('\\', '/');
            else return;
        }
        else if (actionId.endsWith("OpenTestProject"))
        {
            url = "file:///D:/Projects/NSSTC/STT3/projects/SoCal.xml";
        }

        // launch OpenProject command in separate thread
        final OpenProject command = new OpenProject();
        command.setUrl(url);
        Runnable readProject = new Runnable()
        {
            public void run()
            {
                // launch command to read project
                command.execute();
                Project newProject = command.getProject();
                                
                // open new GUI page asynchronously
                Runnable openPage = new OpenPageRunnable(newProject);
                PlatformUI.getWorkbench().getDisplay().asyncExec(openPage);
            }
        };

        Thread thread = new Thread(readProject);
        thread.start();
    }


    public void selectionChanged(IAction action, ISelection selection)
    {
    }


    public void dispose()
    {
    }


    public void init(IWorkbenchWindow window)
    {
    }
}