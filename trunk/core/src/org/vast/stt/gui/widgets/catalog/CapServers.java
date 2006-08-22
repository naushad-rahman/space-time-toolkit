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

package org.vast.stt.gui.widgets.catalog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.dom.AttributeMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.vast.io.xml.DOMReader;
import org.vast.io.xml.DOMReaderException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <p><b>Title:</b>
 *  CapServers
 * </p>
 *
 * <p><b>Description:</b><br/>
 *  CapServers holds all the server Info for a user
 *  
 *  TODO  Ensure that capServers stays synched between editCapServDialog.capServerTree
 *        and CapabilitiesWidget.  Should just be a matter of forcing CapServer reread 
 *        every time a change is made in editCapServerDialog
 *  
 * </p>
 *
 * <p>Copyright (c) 2006</p>
 * @author Tony Cook
 * @date Aug 17, 2006
 * @version 1.0
 */

public class CapServers {
	List<ServerInfo> wmsServers, wcsServers, wfsServers, sosServers;
	public enum ServiceType { WMS, WCS, WFS, SOS };
	
	public CapServers(){
		//  Init ArrayLists
		wmsServers = new ArrayList<ServerInfo>(5);
		wcsServers = new ArrayList<ServerInfo>(5);
		wfsServers = new ArrayList<ServerInfo>(5);
		sosServers = new ArrayList<ServerInfo>(5);
	}
	
	public String [] getServiceTypes(){
		ServiceType[] types = ServiceType.values();
		String [] typesStr = new String[types.length];
		for(int i =0; i<types.length; i++)
			typesStr[i] = types[i].toString();
		
		return typesStr;
	}
	
	public String [] getServers(String type){
		for(ServiceType t : ServiceType.values()){
			if(type.equals(t.toString())) 
				return getServers(t);	
		}
		return null;
	}
	
	public String [] getServers(ServiceType type){
		int i = 0;
		String [] servers = null;
		switch(type){
		case WMS:
			servers = new String[wmsServers.size()];
			for(ServerInfo info : wmsServers)
				servers[i++] = info.name;
			break;
		case WCS:
			servers = new String[wcsServers.size()];
			for(ServerInfo info : wcsServers)
				servers[i++] = info.name;
			break;
		case WFS:
			servers = new String[wfsServers.size()];
			for(ServerInfo info : wfsServers)
				servers[i++] = info.name;
			break;
		case SOS:
			servers = new String[sosServers.size()];
			for(ServerInfo info : sosServers)
				servers[i++] = info.name;
			break;
		default:
			System.err.println("CapServes.getServers();  Type not supported:" + type);
		}
		return servers;
	}
	
	public void loadServerData() {
		try	{
		    readServers();
		} catch (DOMReaderException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"STT Error", "Servers.xml file not found.  Capabilities Servers are empty.");
			return;
		}
		
		
	}	
	
	public void readServers() throws DOMReaderException {
		InputStream is = this.getClass().getResourceAsStream("Servers.xml");
		DOMReader dom = new DOMReader(is, false);
		Element rootElt = dom.getRootElement();
		
		NodeList serverNodes = rootElt.getElementsByTagName("Server");
		Element server, urlElt;
		AttributeMap serverAtts;
		int numServers = serverNodes.getLength();
		ServerInfo si;
		String type;
		for(int i=0; i<numServers; i++){
			server = (Element)serverNodes.item(i);
			serverAtts = (AttributeMap)server.getAttributes();
			si = new ServerInfo();
			si.name = server.getAttribute("name");
			si.version = server.getAttribute("version");
			type = server.getAttribute("type");
			si.setServiceType(type);
			urlElt = (Element)server.getElementsByTagName("URL").item(0);
			si.url = urlElt.getTextContent();
			//  Add ServerInfo to List
			switch(si.type){
			case WMS:
				wmsServers.add(si);
				break;
			case WCS:
				wcsServers.add(si);
				break;
			case WFS:
				wfsServers.add(si);
				break;
			case SOS:
				sosServers.add(si);
				break;
			}
		}
	}		
	
	//  TODO rewrite cap file if changes are made
	public void writeServerData(){
		
	}
}
