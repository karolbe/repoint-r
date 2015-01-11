/*
 * Created on Apr 15, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseListComposite extends Composite implements
		DragSourceListener, DropTargetListener {

	private TableViewer listViewer = null;

	private String[] columns = null;

	private Composite parentComposite = null;

	/**
	 * @param parent
	 * @param style
	 */
	public DocbaseListComposite(Composite parent, int style) {
		super(parent, style);

		super.setLayout(new FillLayout());

		this.parentComposite = parent;
		createTable(this);

	}

	public void resetTable() {
		disposeColumns();
		createColumns();
	}

	private void createTable(Composite parent) {

		listViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);

		// listViewer.getTable().setLinesVisible(true);
		listViewer.getTable().setHeaderVisible(true);

		// listViewer.setColumnProperties(getVisibleColumns());
		Table tbl = listViewer.getTable();

		// TODO Have to make this a class ColumnSpecs. This will make it simpler
		// to add/remove columns

		createColumns();

		/*
		 * FormData fd = new FormData(); fd.left = new FormAttachment(0,5);
		 * fd.right = new FormAttachment(100,-5); fd.top = new
		 * FormAttachment(searchComposite,5); fd.bottom = new
		 * FormAttachment(100,-5); listViewer.getTable().setLayoutData(fd);
		 */

		listViewer.setLabelProvider(new DocbaseItemListLabelProvider());

		listViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return new Object[] {};
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});

		listViewer.addDoubleClickListener(new DocbaseListEventListener());

		Transfer[] transferTypes = { FileTransfer.getInstance(),
				TextTransfer.getInstance() };
		listViewer.addDragSupport(
				DND.DROP_COPY | DND.DROP_LINK | DND.DROP_MOVE, transferTypes,
				this);
		listViewer.addDropSupport(
				DND.DROP_COPY | DND.DROP_LINK | DND.DROP_MOVE, transferTypes,
				this);

	}

	private void createColumns() {
		String[] cols = getColumns();
		for (int i = 0; i < cols.length; i++) {
			TableColumn col = new TableColumn(listViewer.getTable(), SWT.LEFT,
					(i));
			col.setText(cols[i].trim());
			col.setWidth(120);
			col.setResizable(true);
		}
	}

	private void disposeColumns() {
		if ((listViewer != null) && (listViewer.getTable() != null)) {
			listViewer.getTable().removeAll();
			TableColumn[] tc = listViewer.getTable().getColumns();
			for (int i = 0; i < tc.length; i++) {
				tc[i].dispose();
			}

		}
	}

	public TableViewer getListViewer() {
		return listViewer;
	}

	public void setListViewer(TableViewer listViewer) {
		this.listViewer = listViewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd
	 * .DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
		System.out
				.println("dragFinished: " + event.data + "," + event.dataType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd
	 * .DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		System.out.println("dragSetData: " + event.getSource() + ","
				+ event.dataType + "," + event.data);
		IStructuredSelection sel = (IStructuredSelection) listViewer
				.getSelection();
		if (sel.isEmpty() == false) {
			try {

				DocbaseItem di = (DocbaseItem) sel.getFirstElement();
				// event.data = di.getTypedObject().getString("r_object_id");

				event.data = new File[] { new File(
						"C:\\devprog\\SDC2007\\presentations\\SDC07_DocumentumAspects.ppt") };
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.
	 * DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		System.out.println("dragStart: " + event.getSource());
		System.out.println("dragStart: " + event.data + ", " + event.dataType);

		DragSource ds = (DragSource) event.getSource();

		System.out.println("dragStart - dragSource: " + ds.getData() + ","
				+ ds.getControl());

		IStructuredSelection sel = (IStructuredSelection) listViewer
				.getSelection();
		if (sel.isEmpty() == false) {
			event.doit = true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	public void dragEnter(DropTargetEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			event.detail = DND.DROP_COPY;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	public void dragLeave(DropTargetEvent event) {
		System.out.println("dragLeave: " + event.getSource());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse
	 * .swt.dnd.DropTargetEvent)
	 */
	public void dragOperationChanged(DropTargetEvent event) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	public void dragOver(DropTargetEvent event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.
	 * DropTargetEvent)
	 */
	public void drop(DropTargetEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
			String strData = (String) event.data;
			System.out.println("Dropped data: " + strData);

		} else if (FileTransfer.getInstance().isSupportedType(
				event.currentDataType)) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd
	 * .DropTargetEvent)
	 */
	public void dropAccept(DropTargetEvent event) {
	}

	public String[] getColumns() {

		return ListViewHelper.getColumns();

	}
}
