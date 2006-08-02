package org.vast.stt.gui.widgets.symbolizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vast.stt.gui.widgets.OptionChooser;
import org.vast.stt.gui.widgets.OptionController;
import org.vast.stt.project.DataItem;
import org.vast.ows.sld.*;


/**
 * <p><b>Title:</b><br/>
 * StyleOptionChooser
 * </p>
 *
 * <p><b>Description:</b><br/>
 *	StyleOptionChooser is a composite that holds label/control pairs for 
 *  selecting options for a particular Styler type.   
 *
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Jan 18, 2006
 * @version 1.0
 * 
 * TODO  add chooser/mapping widget when user selects + (add) style
 * TODO  add support for other Stylers (Polygon, Raster) 
 * TODO  support advanced options      
 */
public class SymbolizerOptionChooser extends OptionChooser
{
	//  Need to keep controllers in memory and just rebuild their
	//  controls as needed, so basic and advanced options can
	//  co-exist and change together
	private OptionListener optListener;
    private DataItem item;

	public SymbolizerOptionChooser(Composite parent, OptionListener ol) {
		super(parent);
		this.optListener = ol;
	}
    
    public void setDataItem(DataItem item)
    {
        this.item = item;
    }

	public void buildControls(Object stylerObj){
		Symbolizer sym = (Symbolizer) stylerObj;
		removeOldControls();

		OptionController optionController = null;
		if(sym instanceof PointSymbolizer) {
			optionController = new BasicPointController(optComp, (PointSymbolizer)sym);
		} else if (sym instanceof LineSymbolizer) {
			optionController = new BasicLineController(optComp, (LineSymbolizer)sym);
		} else if (sym instanceof GridSymbolizer) {
			optionController = new BasicGridController(optComp, (GridSymbolizer)sym);
		} else if (sym instanceof RasterSymbolizer) {
			optionController = new BasicRasterController(optComp, (RasterSymbolizer)sym);
		} else if (sym instanceof TextSymbolizer) {
			optionController = new BasicLabelController(optComp, (TextSymbolizer)sym);
		} else if (sym instanceof TextureSymbolizer) {
			optionController = new BasicTextureController(optComp, (TextureSymbolizer)sym);
		} else 
			System.err.println("Styler not supported yet: " + sym);
		
		if(optionController == null)
			return;
		optionController.addSelectionListener(optListener);
        optionController.setDataItem(item);
		optListener.setBasicController(optionController);

		optComp.layout(true);		
		optScr.setMinSize(optComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		optComp.redraw();
	}	

}
