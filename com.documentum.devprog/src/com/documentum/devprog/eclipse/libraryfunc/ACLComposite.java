/*
 * Created on Oct 31, 2008
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.devprog.eclipse.model.ACLInfo;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @author Karol Bryd (karol.bryd@metasys.pl)
 */
public class ACLComposite extends Composite {

    private TableViewer listViewer;
    private Text title;

    /**
     * @param parent
     * @param style
     */
    public ACLComposite(Composite parent, int style) {
        super(parent, style);
        createValueList(this);
    }

    private void createValueList(Composite parent) {
        GridData gridData = new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false);
        gridData.horizontalSpan = 1;
        gridData.heightHint = 150;
        gridData.minimumHeight = 150;
        gridData.minimumWidth = 400;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;

        title = new Text(parent, SWT.NONE);
        title.setBackground(parent.getBackground());
        title.setEditable(false);
        StyledText styledTextWidget = new StyledText(parent, SWT.NONE);
        styledTextWidget.setBackground(parent.getBackground());
        styledTextWidget.setCaret(null);

        this.setLayout(gridLayout);

        listViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION
                | SWT.H_SCROLL | SWT.V_SCROLL);
        listViewer.getTable().setLayoutData(gridData);

        final TableColumn accIndex = new TableColumn(listViewer.getTable(), SWT.FLAT);
        accIndex.setText("Accessor");
        accIndex.setWidth(240);

        TableColumn permValue = new TableColumn(listViewer.getTable(), SWT.FLAT);
        permValue.setText("Permission");
        permValue.setWidth(100);

        TableColumn epValue = new TableColumn(listViewer.getTable(), SWT.FLAT);
        epValue.setText("Ext Permission");
        epValue.setWidth(250);

        TableColumn typeValue = new TableColumn(listViewer.getTable(), SWT.FLAT);
        typeValue.setText("Type");
        typeValue.setWidth(150);

        listViewer.getTable().setHeaderVisible(true);

        listViewer.setContentProvider(new IStructuredContentProvider() {

            public Object[][] getElements(Object inputElement) {
                if (inputElement instanceof IDfACL) {
                    ACLInfo aclInfo = new ACLInfo((IDfACL) inputElement);
                    return aclInfo.asTableArray();
                } else
                    return new Object[][]{};
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
                Object[] row = (Object[]) element;
                if (columnIndex == 0) {
                    return (String) row[0];
                } else if (columnIndex == 1) {
                    return (String) row[1];
                } else if (columnIndex == 2) {
                    return (String) row[2];
                } else
                    return (String) row[3];
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

    public void setACL(IDfACL acl) throws DfException {
        if (title != null) {
            title.setText("ACL name: " + acl.getObjectName());
        }
        listViewer.setInput(acl);
    }
}
