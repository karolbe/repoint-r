package com.emc.repoint.scripts.dfc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * 
 * Stores information on source package for Object Browser
 * 
 * @author Fabian Lee(fabian.lee@flatironssolutions.com)
*/


public class SourcePackage  {
	protected SourcePackage parent;	
	protected String name;	
	protected List boxes;

	
	public SourcePackage() {
		boxes = new ArrayList();
	}
	
	public SourcePackage(String name) {
		this();
		this.name = name;
	}
	
	public String getFullName() {
		String s = "";
		SourcePackage current = this;
		while(current.getParent()!=null) {
			if(s.length()>1) 
				s = "." + s;
			s = current.getName() + s;

			// traverse down
			current = current.getParent();
		}
		
		return s;
	}
	
	public List getBoxes() {
		return boxes;
	}
	
	public SourcePackage getParent() { 
		return parent;
	}
	public String getName() { return name; }
	
	protected void addBox(SourcePackage box) {
		boxes.add(box);
		box.parent = this;
	}
	
	protected void removeBox(SourcePackage box) {
		boxes.remove(box);
		//box.addListener(NullDeltaListener.getSoleInstance());
	}

	/** Answer the total number of items the
	 * receiver contains. */
	public int size() {
		return getBoxes().size();
	}
	

}
