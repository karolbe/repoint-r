package com.documentum.devprog.eclipse.common;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * Created by kbryd on 2/20/16.
 */
public class ExternalDocbaseDialog extends Dialog {

    private Text docbrokerHost;
    private Combo docbaseList;
    private Text loginText;
    private Text passText;

    private String selectedDocbase;

    protected ExternalDocbaseDialog(Shell parentShell) {
        super(parentShell);
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.widthHint = 400;
        gd.heightHint = 300;
        composite.setLayoutData(gd);

        composite.setLayout(new FormLayout());

        Label label = new Label(composite, SWT.NONE);
        label.setText("Docbroker:");
        label.setLayoutData(PluginHelper.getFormData(5, 20, 0, 25));
        label.setAlignment(SWT.CENTER);

        docbrokerHost = new Text(composite, SWT.BORDER);
        docbrokerHost.setLayoutData(PluginHelper.getFormData(5, 20, 25, 80));

        Button addExternalDocbase = new Button(composite, SWT.BORDER);
        addExternalDocbase.setText("OK");
        addExternalDocbase.setLayoutData(PluginHelper.getFormData(5, 20, 80, 100));
        addExternalDocbase.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent selectionEvent) {
                updateDocbaseList();
            }

            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });

        Label docbaseLabel = new Label(composite, SWT.NONE);
        docbaseLabel.setText("Docbase:");
        docbaseLabel.setLayoutData(PluginHelper.getFormData(30, 45, 0, 25));

        docbaseList = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        docbaseList.setLayoutData(PluginHelper.getFormData(30, 45, 25, 80));

        Label loginLabel = new Label(composite, SWT.NONE);
        loginLabel.setText("Login:");
        loginLabel.setLayoutData(PluginHelper.getFormData(45, 60, 0, 25));
        loginText = new Text(composite, SWT.BORDER);
        loginText.setLayoutData(PluginHelper.getFormData(45, 60, 25, 80));

        Label passLabel = new Label(composite, SWT.NONE);
        passLabel.setText("Password:");

        passLabel.setLayoutData(PluginHelper.getFormData(60, 75, 0, 25));
        passText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        passText.setLayoutData(PluginHelper.getFormData(60, 75, 25, 80));

        return parent;
    }

    @Override
    protected void okPressed() {
        selectedDocbase = docbaseList.getText();
        String login = loginText.getText();
        String password = loginText.getText();
        String docbroker = docbrokerHost.getText();
        super.okPressed();
        PluginState.addExternalIdentity(login, password, null, selectedDocbase, docbroker, true);
    }

    private void updateDocbaseList() {
        java.util.List<String> availableDocbases = PluginHelper.getDocbrokerDocbases(docbrokerHost.getText());

        for(String d : availableDocbases) {
            docbaseList.add(d);
        }

        if(availableDocbases.size() == 1) {
            docbaseList.select(0);
        }
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.getString("ExternalDocbaseDialog.1")); //$NON-NLS-1$
    }

    public String getDocbase() {
        return selectedDocbase;
    }
}
