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

import org.vast.data.AbstractDataBlock;
import org.vast.ows.sld.RasterChannel;
import org.vast.ows.sld.ScalarParameter;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.data.BlockListItem;


/**
 * <p><b>Title:</b><br/>
 * Texture Mapping Styler
 * </p>
 *
 * <p><b>Description:</b><br/>
 * 
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class TextureStyler extends AbstractStyler
{
	protected TextureSymbolizer symbolizer;
    protected TexturePatchGraphic patch;
    protected RasterPixelGraphic pixel;
    protected GridPointGraphic point;
    protected ListInfo gridBlocks, texBlocks;
    protected int[] gridIndex = new int[4];
    protected int[] texIndex = new int[4];
    protected boolean newPatch = true;
    
    
	public TextureStyler()
	{
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        patch = new TexturePatchGraphic();
        point.a = 255;
	}
    
    
    public TexturePatchGraphic nextTile()
    {
        // update patch length
        if (dataLists[0].indexOffset >= 0)
        {
            int gridListSize = gridBlocks.blockIterator.getList().getSize();
            int texListSize = texBlocks.blockIterator.getList().getSize();
            
            if (newPatch & gridListSize > 0)
            {
                newPatch = false;
                                
                // force update if more blocks were added
                if (gridListSize != patch.grid.length)
                    patch.updated = true;
                else
                    patch.updated = false;
                                
                // adjust patch grid length and texture height
                patch.grid.length = gridListSize;
                patch.texture.height = texListSize;
                
                // get first blocks as references
                // and call next to get var size array data
                patch.grid.block = gridBlocks.blockIterator.next();
                gridBlocks.blockIndexer.setData(patch.grid.block.getData());
                gridBlocks.blockIndexer.reset();
                gridBlocks.blockIndexer.next();                
                patch.texture.block = texBlocks.blockIterator.next();
                texBlocks.blockIndexer.setData(patch.texture.block.getData());
                texBlocks.blockIndexer.reset();
                texBlocks.blockIndexer.next();
                
                return patch;
            }
            else
                return null;
        }
        
        // if no more items one of the lists, just return null
        if (!(gridBlocks.blockIterator.hasNext() && texBlocks.blockIterator.hasNext()))
        {
            clearBlockData();
            return null;
        }
        
        // get grid block for next tile
        BlockListItem nextGrid = gridBlocks.blockIterator.next();
        
        // setup grid indexer with new data
        AbstractDataBlock nextGridBlock = nextGrid.getData();
        gridBlocks.blockIndexer.setData(nextGridBlock);
        gridBlocks.blockIndexer.reset();
        gridBlocks.blockIndexer.next(); // to make sure we visit array size data
        
        // if texture is coming from a different list
        // get next texture block
        BlockListItem nextTexture;
        if (gridBlocks == texBlocks)
            nextTexture = nextGrid;
        else
        {
            nextTexture = texBlocks.blockIterator.next();
            
            // setup texture indexer with new data 
            AbstractDataBlock nextTexBlock = nextTexture.getData();
            texBlocks.blockIndexer.setData(nextTexBlock);
            texBlocks.blockIndexer.reset();
            texBlocks.blockIndexer.next(); // to make sure we visit array size data
        }
        
        // copy current item in the patch object
        patch.grid.block = nextGrid;
        patch.texture.block = nextTexture;
        
        // see what's needed on this block
        prepareBlock(nextGrid);
        
        return patch;
    }
	   
    
    public RasterPixelGraphic getPixel(int x, int y)
    {
        texIndex[2] = x;
        texIndex[3] = y;
        
        texBlocks.getData(texIndex);
        
        // scale to 0-255 if values are normalized
        if (symbolizer.hasNormalizedColors())
        {
            pixel.r *= 255;
            pixel.g *= 255;
            pixel.b *= 255;
            pixel.a *= 255;
        }
        
        return pixel;
    }
    
    
    public GridPointGraphic getGridPoint(int u, int v)
    {
        point.x = point.y = point.z = 0.0;
        gridIndex[0] = u;
        gridIndex[1] = v;
        
        gridBlocks.getData(gridIndex);
        
        // compute texture coordinates
        point.tx = (float)u / (float)(patch.grid.width-1);
        point.ty = (float)v / (float)(patch.grid.length-1);
        
        // adjust geometry to fit projection
        projection.adjust(geometryCrs, point);
        
        return point;
    }
    
    /*
    public GridPointGraphic getGridPoint(double u, double v)
    {
        point.x = point.y = point.z = 0.0;
        
        double u_hig = Math.ceil(u);
        double u_low = Math.floor(u);
        double v_hig = Math.ceil(v);
        double v_low = Math.floor(v);
        double coef_u = (u - u_low) / (u_hig - u_low);
        double coef_v = (v - v_low) / (v_hig - v_low);
        
        double x11, x21, x12, x22;
        double y11, y21, y12, y22;
        double z11, z21, z12, z22;
        
        gridIndex[0] = (int)u_low;
        gridIndex[1] = (int)v_low;        
        gridBlocks.getData(gridIndex);
        x11 = point.x;
        y11 = point.y;
        z11 = point.z;
        
        gridIndex[0] = (int)u_hig;
        gridIndex[1] = (int)v_low;        
        gridBlocks.getData(gridIndex);
        x21 = point.x;
        y21 = point.y;
        z21 = point.z;
        
        gridIndex[0] = (int)u_low;
        gridIndex[1] = (int)v_hig;        
        gridBlocks.getData(gridIndex);
        x12 = point.x;
        y12 = point.y;
        z12 = point.z;
        
        gridIndex[0] = (int)u_hig;
        gridIndex[1] = (int)v_hig;        
        gridBlocks.getData(gridIndex);
        x22 = point.x;
        y22 = point.y;
        z22 = point.z;
        
        double x1, y1, z1;
        double x2, y2, z2;
        
        // compute bilinear interpolation between points
        x1 = x11 + coef_u * (x21 - x11);
        y1 = y11 + coef_u * (y21 - y11);
        z1 = z11 + coef_u * (z21 - z11);
        
        gridIndex[0] = (int)u_low;
        gridIndex[1] = (int)v_low;        
        gridBlocks.getData(gridIndex);
        x1 = point.x;
        y1 = point.y;
        z1 = point.z;
        
        
        point.x = x;
        point.y = y;
        point.z = z;
        
        // adjust geometry to fit projection
        projection.adjust(geometryCrs, point);
        
        return point;
    }*/
    
    
    private BlockListItem nextGridBlock()
    {
        ListInfo listInfo = dataLists[0];
        
        // if no more items in the list, just return null
        if (!listInfo.blockIterator.hasNext())
            return null;
        
        // otherwise get the next item
        BlockListItem nextItem = listInfo.blockIterator.next();

        // setup indexer with new data 
        AbstractDataBlock nextBlock = nextItem.getData();
        listInfo.blockIndexer.setData(nextBlock);
        listInfo.blockIndexer.reset();
        //listInfo.blockIndexer.getData(0,0,0);
        
        // see what's needed on this block
        prepareBlock(nextItem);
        
        return nextItem;
    }
    
    
    private GridPointGraphic nextPoint()
    {
        if (gridBlocks.blockIndexer.hasNext())
        {
            point.x = point.y = point.z = 0.0;            
            dataLists[0].blockIndexer.next();
            
            // adjust geometry to fit projection
            projection.adjust(geometryCrs, point);
            
            return point;
        }
        
        return null;
    }
    
    
    @Override
    public void computeBoundingBox()
    {
        this.resetIterators();
        PrimitiveGraphic point;
        
        while (nextGridBlock() != null)
            while ((point = nextPoint()) != null)
                addToExtent(point);
    }


    @Override
	public void updateDataMappings()
	{
        boolean colors = false;
        ScalarParameter param;
        RasterChannel channel;
        String propertyName = null;   
        
        // reset all parameters
        pixel = new RasterPixelGraphic();
        point = new GridPointGraphic();
        patch.getTexture().bands = 3;
        this.clearAllMappers();
        
        // grid width array
        propertyName = this.symbolizer.getGridDimensions().get("width");
        if (propertyName != null)
        {
            addPropertyMapper(propertyName, new GridWidthMapper(0, patch.getGrid(), null));
        }
        
        // grid length array
        propertyName = this.symbolizer.getGridDimensions().get("length");
        if (propertyName != null)
        {
            if (propertyName.equals("/"))
                dataLists[0].indexOffset = 1;
            else
                addPropertyMapper(propertyName, new GridLengthMapper(1, patch.getGrid(), null));
        }
        
        // grid geometry X
        param = this.symbolizer.getGeometry().getX();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericXMapper(point, param.getMappingFunction()));
            }
        }
        
        // grid geometry Y
        param = this.symbolizer.getGeometry().getY();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericYMapper(point, param.getMappingFunction()));
            }
        }
        
        // grid geometry Z
        param = this.symbolizer.getGeometry().getZ();
        if (param != null)
        {
            propertyName = param.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericZMapper(point, param.getMappingFunction()));
            }
        }
                
        // make sure we keep a handle to the block list containing grid data
        gridBlocks = dataLists[dataLists.length-1];
        
        // raster width array
        propertyName = this.symbolizer.getRasterDimensions().get("width");
        if (propertyName != null)
        {
            addPropertyMapper(propertyName, new RasterWidthMapper(2, patch.getTexture(), null));
        }
        
        // raster height array
        propertyName = this.symbolizer.getRasterDimensions().get("height");
        if (propertyName != null)
        {
            if (propertyName.equals("/"))
                dataLists[1].indexOffset = 3;
            else
                addPropertyMapper(propertyName, new RasterHeightMapper(3, patch.getTexture(), null));
        }
        
        // global texture opacity
        param = this.symbolizer.getOpacity();
        if (param != null)
        {
            if (param.isConstant())
            {
                Object value = param.getConstantValue();
                pixel.a = (Float)value * 255;
            }
            else
            {
                propertyName = param.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericAlphaMapper(pixel, param.getMappingFunction()));
                }
            }
        }
        
        // pixel red
        channel = this.symbolizer.getRedChannel();
        if (channel != null)       
        {
            colors = true;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericRedMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel green
        channel = this.symbolizer.getGreenChannel();
        if (channel != null)
        {
            colors = true;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericGreenMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel blue
        channel = this.symbolizer.getBlueChannel();
        if (channel != null)
        {
            colors = true;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericBlueMapper(pixel, channel.getMappingFunction()));
            }
        }
        
        // pixel gray
        if (!colors)
        {
            channel = this.symbolizer.getGrayChannel();
            if (channel != null)
            {
                patch.getTexture().bands = 1;
                propertyName = channel.getPropertyName();
                if (propertyName != null)
                {
                    addPropertyMapper(propertyName, new GenericGrayMapper(pixel, channel.getMappingFunction()));
                }
            }
        }
        
        // pixel alpha
        channel = this.symbolizer.getAlphaChannel();
        if (channel != null)
        {
            patch.getTexture().bands++;
            propertyName = channel.getPropertyName();
            if (propertyName != null)
            {
                addPropertyMapper(propertyName, new GenericAlphaMapper(pixel, channel.getMappingFunction()));
            }
        }
        
                
        // make sure we keep a handle to the block list containing image data
        texBlocks = dataLists[dataLists.length-1];
	}
    
    
    @Override
    public void resetIterators()
    {
        super.resetIterators();
        newPatch = true;
    }
	
	
	public TextureSymbolizer getSymbolizer()
	{
		return symbolizer;
	}


	public void setSymbolizer(Symbolizer sym)
	{
		this.symbolizer = (TextureSymbolizer)sym;
        this.setCrs(sym.getGeometry().getCrs());
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
