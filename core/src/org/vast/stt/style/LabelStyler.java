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

package org.vast.stt.style;

import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextSymbolizer;


/**
 * <p><b>Title:</b><br/>
 * Label Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Converts source data to a sequence of LabelGraphic objects
 * that the renderer can access and render sequentially. 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class LabelStyler extends AbstractStyler
{
    protected LabelGraphic label;
    protected TextSymbolizer symbolizer;
    protected int labelDensity = 10;
    protected int labelSpacing;
    	
	
	public LabelStyler()
	{
        label = new LabelGraphic();
	}
    
    
    public LabelGraphic nextPoint()
    {
        if (dataLists[0].blockIndexer.hasNext)
        {
            dataLists[0].blockIndexer.getNext();
            return label;
        }
        
        return null;
    }


	public void updateDataMappings()
	{
        ScalarParameter param;
        String propertyName;
        Object value;
        
        // reset all parameters
        label = new LabelGraphic();
        this.clearAllMappers();
               
        // geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();          
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericXMapper(label, param.getMappingFunction()));                
            }
        }
        
        //geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericYMapper(label, param.getMappingFunction()));                
            }
        }
        
        // geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();            
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericZMapper(label, param.getMappingFunction()));                
            }
        }
        
        // color - red 
        param = symbolizer.getFill().getColor().getRed();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.r = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericRedMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // color - green 
        param = symbolizer.getFill().getColor().getGreen();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.g = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGreenMapper(label, param.getMappingFunction()));               
                }
            }
        }
        
        // color - blue 
        param = symbolizer.getFill().getColor().getBlue();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.b = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericBlueMapper(label, param.getMappingFunction()));             
                }
            }
        }
        
        // color - alpha 
        param = symbolizer.getFill().getColor().getAlpha();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.a = (Float)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // label text
        param = this.symbolizer.getLabel();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.text = (String)value;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LabelTextMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // text size
        param = this.symbolizer.getFont().getSize();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.size = ((Float)value).intValue();
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new LabelSizeMapper(label, param.getMappingFunction()));              
                }
            }
        }
        
        // label orientation
        param = null;//this.symbolizer.getPlacement().getRotation();
        if (param != null)
        {
            if (param.isConstant())
            {
                value = param.getConstantValue();
                label.orientation = (Float)value;
            }
            else
            {
                if (!param.isMapped())
                {
                    propertyName = param.getPropertyName();
                    if (propertyName != null)
                    {
                        addPropertyMapper(propertyName, new LabelOrientationMapper(label, param.getMappingFunction()));              
                    }
                }
            }
        }
	}
	
	
	public TextSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (TextSymbolizer)sym;
	}
	
	
	public void accept(StylerVisitor visitor)
	{
        dataNode = dataItem.getDataProvider().getDataNode();
        
        if (dataNode.isNodeStructureReady())
        {
            if (dataLists.length == 0)
                updateDataMappings();
                        
    		visitor.visit(this);
        }
	}
}