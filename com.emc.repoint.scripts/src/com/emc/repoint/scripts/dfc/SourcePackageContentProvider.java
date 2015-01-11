package com.emc.repoint.scripts.dfc;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
* @author Fabian Lee(fabian.lee@flatironssolutions.com)
*/

public class SourcePackageContentProvider implements ITreeContentProvider { //,IDeltaListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		return;
	}
	
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof SourcePackage) {
			SourcePackage box = (SourcePackage)parentElement;
			return box.getBoxes().toArray();
		}
		return EMPTY_ARRAY;
	}
	

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof SourcePackage) {
			return ((SourcePackage)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

}
