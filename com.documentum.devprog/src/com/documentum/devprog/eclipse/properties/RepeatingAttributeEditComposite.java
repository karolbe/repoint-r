/*
 * Created on Oct 31, 2008
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.devprog.eclipse.common.PluginHelper;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RepeatingAttributeEditComposite extends Composite {
	private TableViewer listViewer;

	private Button addValue;

	private Button moveUp;

	private Button moveDown;

	private Button editValue;

	private Button removeValue;

	private Label title;

	private ArrayList values = new ArrayList();

	private AttrInfo attr;

	/**
	 * @param parent
	 * @param style
	 */
	public RepeatingAttributeEditComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FormLayout());
		createLabel(this);
		createButtons(this);
		createValueList(this);
	}

	public void setAttributeValues(AttrInfo attr) {
		this.attr = attr;

		values.addAll(Arrays.asList(attr.getValues()));
		if (title != null) {
			title.setText("Repeating Attribute: " + attr.getName());
		}
		listViewer.setInput(values);
	}

	public String[] getValues() {
		return (String[]) values.toArray(new String[] {});
	}

	private void createLabel(Composite parent) {
		title = new Label(parent, SWT.FLAT);
		FormData fd = PluginHelper.getFormData(0, 10, 0, 100);
		fd.height = 30;
		title.setLayoutData(fd);
	}

	private void createButtons(Composite parent) {
		Composite butComp = new Composite(parent, SWT.NONE);
		FormData fd = PluginHelper.getFormData(10, 20, 0, 100);
		fd.height = 15;
		butComp.setLayoutData(fd);

		butComp.setLayout(new GridLayout(5, false));
		addValue = new Button(butComp, SWT.PUSH);
		addValue.setText("Add Value");
		addValue.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				InputDialog idlg = new InputDialog(Display.getDefault()
						.getActiveShell(), "New Value", "Enter New Value for "
						+ attr.getName(), "", new AttrValueInputValidator(attr));
				int status = idlg.open();
				if (status == InputDialog.OK) {
					String val = idlg.getValue();
					values.add(val);
					listViewer.setInput(values);
				}
			}

		});

		moveUp = new Button(butComp, SWT.PUSH);
		moveUp.setText("Move Up");
		moveUp.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {

				String sel = getSelection();
				int index = values.indexOf(sel);
				if (index != 0) {
					String prev = (String) values.get(index - 1);
					values.set(index, prev);
					values.set(index - 1, sel);
					listViewer.setInput(values);

				}

			}

		});

		moveDown = new Button(butComp, SWT.PUSH);
		moveDown.setText("Move Down");
		moveDown.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {

				String sel = getSelection();
				int index = values.indexOf(sel);
				if (index != (values.size() - 1)) {
					String next = (String) values.get(index + 1);
					values.set(index, next);
					values.set(index + 1, sel);
					listViewer.setInput(values);

				}

			}

		});

		editValue = new Button(butComp, SWT.PUSH);
		editValue.setText("Edit Value");
		editValue.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				String sel = getSelection();
				if (sel != null) {
					int index = values.indexOf(sel);
					InputDialog idlg = new InputDialog(Display.getDefault()
							.getActiveShell(), "Repeating Attribute Value",
							"Enter Value for " + attr.getName(), sel,
							new IInputValidator() {

								public String isValid(String newText) {
									if (newText == null
											|| newText.length() == 0) {
										return "Please Enter a Value";
									} else {
										return null;
									}
								}

							});
					int status = idlg.open();
					if (status == InputDialog.OK) {
						String newVal = idlg.getValue();
						values.set(index, newVal);
						listViewer.setInput(values);

					}
				}

			}

		});

		removeValue = new Button(butComp, SWT.PUSH);
		removeValue.setText("Remove Value");
		removeValue.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				String sel = getSelection();
				values.remove(sel);
				listViewer.setInput(values);

			}
		});
	}

	private void createValueList(Composite parent) {
		listViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd = PluginHelper.getFormData(20, 100, 0, 100);
		listViewer.getTable().setLayoutData(fd);

		TableColumn tcIndex = new TableColumn(listViewer.getTable(), SWT.FLAT);
		tcIndex.setText("Index");
		tcIndex.setWidth(50);

		TableColumn tcValue = new TableColumn(listViewer.getTable(), SWT.FLAT);
		tcValue.setText("Value");
		tcValue.setWidth(150);

		listViewer.getTable().setHeaderVisible(true);

		listViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				System.out.println("Repeating Attr Val Content prov called: "
						+ inputElement);
				if (inputElement instanceof ArrayList) {
					System.out.println("inputElement is ArrayList");
					ArrayList lst = (ArrayList) inputElement;
					return lst.toArray();
				} else
					return new Object[] {};
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});

		listViewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				System.out.println("getcolumntext: " + element + ","
						+ columnIndex);
				if (columnIndex == 0) {
					return "" + values.indexOf(element);
				} else if (columnIndex == 1) {
					return (String) element;
				} else
					return "";
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

		});
	}

	private String getSelection() {
		IStructuredSelection sel = (IStructuredSelection) listViewer
				.getSelection();
		if (sel.isEmpty() == false) {
			String str = (String) sel.getFirstElement();
			return str;
		}
		return null;
	}

}
