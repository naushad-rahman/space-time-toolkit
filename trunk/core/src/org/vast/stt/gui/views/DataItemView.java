/***************************************************************
 (c) Copyright 2005, University of Alabama in Huntsville (UAH)
 ALL RIGHTS RESERVED

 This software is the property of UAH.
 It cannot be duplicated, used, or distributed without the
 express written consent of UAH.

 This software developed by the Vis Analysis Systems Technology
 (VAST) within the Earth System Science Lab under the direction
 of Mike Botts (mike.botts@atmos.uah.edu)
 ***************************************************************/

package org.vast.stt.gui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.project.DataEntry;
import org.vast.stt.project.DataItem;


/**
 * <p><b>Title:</b>
 * Data Item View
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Abstract base class for all DataItem Views.
 * This provides event handling and enforce the implementation of 
 * two other methods updateView() and clearView().
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Jul 10, 2006
 * @version 1.0
 */
public abstract class DataItemView extends ViewPart implements ISelectionListener, INullSelectionListener, STTEventListener
{
    protected DataItem item;
	

    public abstract void updateView();
    public abstract void clearView();
    
    
    protected void refreshView()
    {
        if (this.item != null)
            updateView();
        else
            clearView();
    }
    
    
    @Override
    public void createPartControl(Composite parent)
    {
        getSite().getPage().addPostSelectionListener(SceneTreeView.ID, this);
    }
    
    
    @Override
    public void setFocus()
    {
        refreshView();
    }
    
    
    @Override
    public void dispose()
    {
        getSite().getPage().removePostSelectionListener(this);
        
        if (item != null)
            item.removeListener(this);
    }
    
    
	/**
     * handle selection changes in scene tree
	 */
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		// handle case of null selection
        if (part == null || selection == null)
        {
            item = null;
            clearView();
        }
        
        if (part instanceof SceneTreeView)
		{
			DataEntry selectedEntry = (DataEntry)((IStructuredSelection)selection).getFirstElement();
			if (selectedEntry instanceof DataItem)
            {
			    DataItem selectedItem = (DataItem)selectedEntry;
                if (item != selectedItem)
                {
                    if (item != null)
                        item.removeListener(this);
                    
                    item = selectedItem;
                    item.addListener(this);
                    
                    refreshView();
                }
            }
            else
            {
                item = null;
                clearView();
            }
		}		
	}
    
    
    /**
     * handle data item events
     */
    public void handleEvent(STTEvent e)
    {       
        if (e.producer == this.item)
        {
            Runnable refresh = new Runnable()
            {
                public void run() {refreshView();}
            };
            
            getSite().getShell().getDisplay().asyncExec(refresh);
        }
    }
}