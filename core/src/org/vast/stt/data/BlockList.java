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

package org.vast.stt.data;

import org.ogc.cdm.common.DataComponent;
import org.vast.data.AbstractDataBlock;


/**
 * <p><b>Title:</b><br/>
 * Block List
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO BlockList type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Apr 1, 2006
 * @version 1.0
 */
public class BlockList
{
    protected int size = 0;
    protected DataComponent blockStructure;
    protected BlockListItem currentBlock;
    protected BlockListItem firstBlock;
    protected BlockListItem lastBlock;
    //protected BlockListItem[] fastAccessBlocks; // use if random access needed
    protected boolean hasNext;
    
    
    public BlockList()
    {
        this.clear();        
    }
    
    
    public BlockListIterator getIterator()
    {
        return new BlockListIterator(this);
    }
    
    
    public void clear()
    {
        hasNext = false;
        firstBlock = null;
        lastBlock = null;
        currentBlock = null;
        size = 0;   
    }
    
    
    public void remove()
    {
        currentBlock.prevBlock.nextBlock = currentBlock.nextBlock;
        currentBlock.nextBlock.prevBlock = currentBlock.prevBlock;
        currentBlock = currentBlock.nextBlock;
        size--;
    }
    
    
    public void insertBlock(AbstractDataBlock dataBlock)
    {
        currentBlock = new BlockListItem(dataBlock, currentBlock, currentBlock.nextBlock);
        size++;
    }
    
    
    public void addBlock(AbstractDataBlock dataBlock)
    {
        lastBlock = new BlockListItem(dataBlock, lastBlock, null);
        
        if (firstBlock == null)
            firstBlock = lastBlock;
        
        size++;
    }
    
    
    public int getSize()
    {
        return size;
    }


    public DataComponent getBlockStructure()
    {
        return blockStructure;
    }


    public void setBlockStructure(DataComponent blockStructure)
    {
        this.blockStructure = blockStructure;
    }
}