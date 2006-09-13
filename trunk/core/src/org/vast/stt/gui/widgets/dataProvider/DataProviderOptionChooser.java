package org.vast.stt.gui.widgets.dataProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.provider.swe.SWEProvider;
import org.vast.stt.gui.widgets.OptionChooser;


/**
 * <p><b>Title:</b><br/>
 * DataProviderOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 26, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  support advanced options      
 */

public class DataProviderOptionChooser extends OptionChooser {
	
	public DataProviderOptionChooser(Composite parent) {
		super(parent);
	}

	public void buildControls(Object providerObj){
		removeOldControls();
		
		//  TODO add support for SWEProvider...
		if (providerObj instanceof SWEProvider) {
			System.err.println("OptionChooser:  Provider type not supported yet: " + providerObj);
		} else 
			System.err.println("OptionChooser:  Provider type not recognized: " + providerObj);
		
		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	
	
}