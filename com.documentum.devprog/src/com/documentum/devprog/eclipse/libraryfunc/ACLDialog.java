/*
 * Created on Nov 1, 2008
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Karol Bryd (karol.bryd@metasys.pl)
 */
public class ACLDialog extends Dialog {

    private final IDfACL objACL;
    private ACLComposite editComposite;
    private DocbaseItem docbaseItem;
    private TableViewer repoTree;

    /**
     * @param parentShell
     */
    public ACLDialog(Shell parentShell, IDfACL objACL) {
        super(parentShell);
        this.objACL = objACL;

    }

    protected Control createDialogArea(Composite parent) {
        editComposite = new ACLComposite(parent, SWT.NONE);
        try {
            editComposite.setACL(objACL);
        } catch (DfException e) {
            e.printStackTrace();
        }
        return editComposite;
    }

    public void setDocbaseItem(DocbaseItem docbaseItem) {
        this.docbaseItem = docbaseItem;
    }

    public void setRepoTree(TableViewer repoTree) {
        this.repoTree = repoTree;
    }
}
