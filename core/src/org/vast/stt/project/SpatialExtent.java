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

package org.vast.stt.project;

import org.vast.math.Vector3d;
import org.vast.stt.event.STTEvent;
import org.vast.stt.event.STTEventListener;
import org.vast.stt.event.STTEventListeners;
import org.vast.stt.event.STTEventProducer;

/**
 * <p><b>Title:</b><br/>
 * Spatial Extent
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Class for storing the definition of a spatial domain.
 * This mainly includes enveloppe coordinates and a crs.
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Nov 15, 2005
 * @version 1.0
 */
public class SpatialExtent implements STTEventProducer
{
	protected String crs;
	protected double minX;
	protected double maxX;
	protected double minY;
	protected double maxY;
	protected double minZ;
	protected double maxZ;
    protected boolean tilingEnabled;
    protected int xTiles = 1;
    protected int yTiles = 1;
    protected int zTiles = 1;
    protected STTEventListeners listeners;


    public SpatialExtent()
    {
        listeners = new STTEventListeners(1);    
    }
    
    
    /**
     * Returns an exact copy of this SpatialExtent
     * @return
     */
    public SpatialExtent copy()
    {
        SpatialExtent bbox = new SpatialExtent();
        
        bbox.crs = this.crs;
        bbox.minX = this.minX;
        bbox.minY = this.minY;
        bbox.minZ = this.minZ;
        bbox.maxX = this.maxX;
        bbox.maxY = this.maxY;
        bbox.maxZ = this.maxZ;        
        
        return bbox;
    }
    
    
    public Vector3d getCenter()
    {
        Vector3d center = new Vector3d();
        center.x = (minX + maxX) / 2;
        center.y = (minY + maxY) / 2;
        center.z = (minZ + maxZ) / 2;
        return center;
    }
    
    
    public double getDiagonalDistance()
    {
        double dx2 = (maxX - minX) * (maxX - minX);
        double dy2 = (maxY - minY) * (maxY - minY);
        double dz2 = (maxZ - minZ) * (maxZ - minZ);
        return Math.sqrt(dx2 + dy2 + dz2);
    }
    
    
    public boolean isNull()
    {
        if (minX != 0) return false;
        if (minY != 0) return false;
        if (minZ != 0) return false;
        if (maxX != 0) return false;
        if (maxY != 0) return false;
        if (maxZ != 0) return false;
        return true;
    }
    
    
    public void resizeToContain(Vector3d point)
    {
        if (minX > point.x) minX = point.x;
        if (minY > point.y) minY = point.y;
        if (minZ > point.z) minZ = point.z;
        
        if (maxX < point.x) maxX = point.x;
        if (maxY < point.y) maxY = point.y;
        if (maxZ < point.z) maxZ = point.z;
    }
    
    
    /**
     * Combines given extent with this extent
     * by computing the smallest rectangular
     * extent that contains both of them. 
     * @param bbox
     */
    public void add(SpatialExtent bbox)
    {
        checkCrs(bbox);
        
        if (minX > bbox.minX)
            minX = bbox.minX;        
        if (minY > bbox.minY)
            minY = bbox.minY;        
        if (minZ > bbox.minZ)
            minZ = bbox.minZ;
        if (maxX < bbox.maxX)
            maxX = bbox.maxX;
        if (maxY < bbox.maxY)
            maxY = bbox.maxY;
        if (maxZ < bbox.maxZ)
            maxZ = bbox.maxZ;
    }
    
    
    /**
     * Combines given extent with this extent
     * by computing the intersection of both.
     * TODO trim method description
     * @param bbox
     */
    public void intersect(SpatialExtent bbox)
    {
        checkCrs(bbox);        
        //TODO intersect method
    }
    
    
    /**
     * Finds out if given extent is included in this one.
     * Returns true if extent is completely contained
     * within this extent
     * @param bbox
     * @return
     */
    public boolean contains(SpatialExtent bbox)
    {
        checkCrs(bbox);
        //TODO contains method
        return true; 
    }
    
    
    /**
     * Finds out if given extent crosses this one
     * Returns true if so.
     * @param bbox
     * @return
     */
    public boolean cross(SpatialExtent bbox)
    {
        checkCrs(bbox);
        // TODO cross method
        return true;
    }
    
    
    /**
     * Checks if extents crs are compatible
     * @throws exception if not
     * @param bbox
     * @return
     */
    protected void checkCrs(SpatialExtent bbox)
    {
        if (crs != null && bbox.crs != null)
            if (!crs.equals(bbox.crs))
                throw new IllegalStateException("CRS must match");
    }
    
    
	public String getCrs()
	{
		return crs;
	}


	public void setCrs(String crs)
	{
		this.crs = crs;
	}


	public double getMaxX()
	{
		return maxX;
	}


	public void setMaxX(double maxX)
	{
		this.maxX = maxX;
	}


	public double getMaxY()
	{
		return maxY;
	}


	public void setMaxY(double maxY)
	{
		this.maxY = maxY;
	}


	public double getMaxZ()
	{
		return maxZ;
	}


	public void setMaxZ(double maxZ)
	{
		this.maxZ = maxZ;
	}


	public double getMinX()
	{
		return minX;
	}


	public void setMinX(double minX)
	{
		this.minX = minX;
	}


	public double getMinY()
	{
		return minY;
	}


	public void setMinY(double minY)
	{
		this.minY = minY;
	}


	public double getMinZ()
	{
		return minZ;
	}


	public void setMinZ(double minZ)
	{
		this.minZ = minZ;
	}


    public boolean isTilingEnabled()
    {
        return tilingEnabled;
    }


    public void setTilingEnabled(boolean tilingEnabled)
    {
        this.tilingEnabled = tilingEnabled;
    }


    public int getXTiles()
    {
        return xTiles;
    }


    public void setXTiles(int tiles)
    {
        xTiles = tiles;
    }


    public int getYTiles()
    {
        return yTiles;
    }


    public void setYTiles(int tiles)
    {
        yTiles = tiles;
    }


    public int getZTiles()
    {
        return zTiles;
    }


    public void setZTiles(int tiles)
    {
        zTiles = tiles;
    }
    
    
    public void addListener(STTEventListener listener)
    {
        listeners.add(listener);
    }


    public void removeListener(STTEventListener listener)
    {
        listeners.remove(listener);
    }


    public void removeAllListeners()
    {
        listeners.clear();
    }


    public void dispatchEvent(STTEvent event)
    {
        event.producer = this;
        listeners.dispatchEvent(event);
    }
}