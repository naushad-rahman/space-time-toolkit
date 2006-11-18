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

package org.vast.stt.renderer.JFreeChart;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.vast.stt.style.DataStyler;


public class ChartXYDataset implements XYDataset
{
    protected DataStyler styler;


    public ChartXYDataset(DataStyler styler)
    {
        this.styler = styler;
    }
    
    
    public DomainOrder getDomainOrder()
    {
        return DomainOrder.ASCENDING;
    }


    public int getItemCount(int arg0)
    {
        return 100;
    }


    public Number getX(int series, int item)
    {
        return new Double(getXValue(series, item));
    }


    public double getXValue(int series, int item)
    {
        return Math.PI*((double)item)/50;
    }


    public Number getY(int series, int item)
    {
        return new Double(getYValue(series, item));
    }


    public double getYValue(int series, int item)
    {
        return Math.sin(Math.PI*((double)item)/50);
    }


    public int getSeriesCount()
    {
        return 1;
    }


    public Comparable getSeriesKey(int series)
    {
        return "Series1";
    }


    public int indexOf(Comparable key)
    {
        return 0;
    }


    public void addChangeListener(DatasetChangeListener arg0)
    {
        // TODO Auto-generated method stub        
    }


    public void removeChangeListener(DatasetChangeListener arg0)
    {
        // TODO Auto-generated method stub        
    }


    public DatasetGroup getGroup()
    {
        return null;
    }


    public void setGroup(DatasetGroup arg0)
    {       
    }


    public DataStyler getStyler()
    {
        return styler;
    }


    public void setStyler(DataStyler styler)
    {
        this.styler = styler;
    }

}