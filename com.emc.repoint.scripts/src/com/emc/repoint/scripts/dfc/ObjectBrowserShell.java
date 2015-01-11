package com.emc.repoint.scripts.dfc;

import java.lang.reflect.*;
import java.util.*;

//import org.apache.crimson.tree.ParseContext;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridData;
//import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;

import com.documentum.fc.common.DfLogger;

import bsh.*;
import bsh.classpath.*;
import bsh.util.*;

/**
 * Creates popup dialog for Object Browser interface
 * 
 *
 * @author Fabian Lee(fabian.lee@flatironssolutions.com)
*/

public class ObjectBrowserShell implements ISelectionChangedListener,SelectionListener {

	private Shell sShell = null;   //  @jve:decl-index=0:visual-constraint="10,10"
	private List classesList = null;
	private Tree testTree = null;
	private Label classesLabel = null;
	private Label methodsLabel = null;
	private Label packagesLabel = null;
	private List constructorsList = null;
	private List methodsList = null;
	private List fieldsList = null;
	private Label constructorsLabel = null;
	private Label fieldsLabel = null;
	

	Shell parent = null;
	BshClassPath classPath = null;
	Interpreter interpreter = null;
	BshClassManager mgr = null;  //  @jve:decl-index=0:
	
	// package selected in tree
	String selectedPackage = null;
	
	public ObjectBrowserShell(Shell parent,Interpreter interpreter) {
		
		this.interpreter = interpreter;
		this.parent = parent;
		createSShell();
	}

	/**
	 * This method initializes sShell
	 */
	public void createSShell() {
		
		sShell = new Shell();
		
		sShell.setText("Class Browser");
		sShell.setLayout(null);
		sShell.setSize(new Point(797, 510));

		testTree = new Tree(sShell, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		testTree.setBounds(new Rectangle(5, 28, 286, 370));
		TreeViewer treeViewer = new TreeViewer(testTree);
		treeViewer.setContentProvider(new SourcePackageContentProvider());
		treeViewer.setLabelProvider(new SourcePackageLabelProvider());
		treeViewer.addSelectionChangedListener(this);
		
		
		classesList = new List(sShell, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		classesList.setBounds(new Rectangle(311, 27, 215, 370));
		classesList.addSelectionListener(this);
		
		
		
		classesLabel = new Label(sShell, SWT.NONE);
		classesLabel.setBounds(new Rectangle(312, 7, 160, 14));
		classesLabel.setText("Classes");
		methodsLabel = new Label(sShell, SWT.NONE);
		methodsLabel.setBounds(new Rectangle(540, 140, 170, 24));
		methodsLabel.setText("Methods");
		constructorsLabel = new Label(sShell, SWT.NONE);
		constructorsLabel.setBounds(new Rectangle(540, 7, 165, 14));
		constructorsLabel.setText("Constructors");
		fieldsLabel = new Label(sShell, SWT.NONE);
		fieldsLabel.setBounds(new Rectangle(540, 277, 168, 17));
		fieldsLabel.setText("Fields");
		
		packagesLabel = new Label(sShell, SWT.NONE);
		packagesLabel.setBounds(new Rectangle(8, 7, 140, 17));
		packagesLabel.setText("Packages");
		constructorsList = new List(sShell, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		constructorsList.setBounds(new Rectangle(540, 28, 169, 104));
		methodsList = new List(sShell, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		methodsList.setBounds(new Rectangle(540, 162, 169, 104));
		fieldsList = new List(sShell, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		fieldsList.setBounds(new Rectangle(540, 297, 169, 104));
		
		// get classpath from interpreter
		mgr = interpreter.getNameSpace().getClassManager();
		ClassManagerImpl cmImpl = (ClassManagerImpl) mgr;
		try {
			classPath = cmImpl.getClassPath();
		} catch (ClassPathException e) {
            DfLogger.error(this, "Problem getting classpath", null, e);
            MessageDialog.openError(null,"Problem getting classpath",null);
		}
        Set set = classPath.getPackagesSet();
		treeViewer.setInput(createTreeNodes(set));
		//treeViewer.expandAll();
		
		sShell.pack();
	    sShell.open();
	}
	

    
	public SourcePackage createTreeNodes(Set pkgSet) {
		
		// sort list of all package names
        String ps[] = toSortedStrings(pkgSet);
        // hash lets us know what has already been created
        Map allNodes = new HashMap();

        // root node we will return at the end
		SourcePackage root = new SourcePackage();

		for (int i = 0; i < ps.length; i++) {

			// split up packages by period, create nodes for each
			String nodes[] = ps[i].split("\\.");
			String currentName = "";
			String parentName = null;
			for(int j=0;j<nodes.length;j++) {
				
				// save parent name for use later
				parentName = currentName;

				// gradually create fully qual name
				if(currentName.length()>1)
					currentName = currentName + ".";
				currentName = currentName + nodes[j];
				
				// if node does not already exist, create
				if(!allNodes.containsKey(currentName)) {
					// create child
					SourcePackage child = new SourcePackage(nodes[j]); //nodes[j]);
					
					// now pull the parent
					// no parent means we add to root
					SourcePackage parent = (SourcePackage) allNodes.get(parentName);
					if(parent==null)
						parent = root;
					parent.addBox(child);
					
					allNodes.put(currentName, child);
				} // if node doesn't already exist
				
			} // each individual package name
			
		} // each full package name in classpath

		return root;
	}

	public void selectionChanged(SelectionChangedEvent event) {
        try
        {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Object selObj = selection.getFirstElement();
            SourcePackage box = (SourcePackage) selObj;
            
            String fullname = box.getFullName();
            selectedPackage = fullname;
			//MessageDialog.openInformation(parent, "full",fullname);

            // show all classes 
            Set classes = classPath.getClassesForPackage(fullname);
            
            // sort array
            String arr[] = (String[]) classes.toArray(new String[0]);
            Arrays.sort(arr);
            
            classesList.removeAll();
            for(int i=0;i<arr.length;i++) {
            	String s = (String) arr[i];
            	
            	// skip inner classes
            	if(s.indexOf("$")!=-1)
            		continue;

            	// only show name of class
            	int perpos = s.lastIndexOf(".");
            	if(perpos!=-1)
            		s = s.substring(perpos+1);
            		
            	classesList.add(s);
            }
            classesList.redraw();
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
		
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		return;
	}

	public void widgetSelected(SelectionEvent event) {

		List list = (List) event.getSource();
		if(list==classesList) {
			constructorsList.removeAll();
			methodsList.removeAll();
			fieldsList.removeAll();
			
			String classname = classesList.getSelection()[0];
			if(!"<unpackaged>".equals(selectedPackage)) {
				classname = selectedPackage + "." + classname;
			}
	        Class selectedClass = mgr.classForName(classname);
	        //MessageDialog.openInformation(parent, "full",classname);	        
	        
	        String constructors[] = parseConstructors(selectedClass.getConstructors());
	        for(int i=0;i<constructors.length;i++) {
	        	if(constructors[i].indexOf(".")!=-1)
	        		constructors[i] = constructors[i].substring(selectedPackage.length()+1);
	        	constructorsList.add(constructors[i]);
	        }
	        
	        String methods[] = parseMethods(selectedClass.getMethods());
	        Arrays.sort(methods);
	        for(int i=0;i<methods.length;i++) {
	        	methodsList.add(methods[i]);
	        }
	        
	        String fields[] = parseFields(selectedClass.getFields());
	        Arrays.sort(fields);
	        for(int i=0;i<fields.length;i++) {
	        	fieldsList.add(fields[i]);
	        }
			
			constructorsList.redraw();
			methodsList.redraw();
			fieldsList.redraw();
		}

	}

    String[] parseMethods(Method amethod[])
    {
        String as[] = new String[amethod.length];
        for(int i = 0; i < as.length; i++)
            as[i] = StringUtil.methodString(amethod[i].getName(), amethod[i].getParameterTypes());

        return as;
    }

    String[] parseFields(Field afield[])
    {
        String as[] = new String[afield.length];
        for(int i = 0; i < as.length; i++)
            as[i] = afield[i].getName();

        return as;
    }

    String[] parseConstructors(Constructor aconstructor[])
    {
        String as[] = new String[aconstructor.length];
        for(int i = 0; i < as.length; i++) {
            as[i] = StringUtil.methodString(aconstructor[i].getName(), aconstructor[i].getParameterTypes());
        }

        return as;
    }
	
    String[] toSortedStrings(Collection collection)
    {
        ArrayList arraylist = new ArrayList(collection);
        String as[] = (String[])arraylist.toArray(new String[0]);
        return StringUtil.bubbleSort(as);
    }
	

}
