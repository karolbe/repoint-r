package com.documentum.devprog.eclipse.model;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfPermitTypeString;

/**
 * Created by kbryd on 2/19/16.
 *
 * @author Karol Bryd (karol.bryd@metasys.pl)
 */
public class ACLInfo {
    static final String[] permNames = new String[]{"",
            IDfACL.DF_PERMIT_NONE_STR,
            IDfACL.DF_PERMIT_BROWSE_STR,
            IDfACL.DF_PERMIT_READ_STR,
            IDfACL.DF_PERMIT_RELATE_STR,
            IDfACL.DF_PERMIT_VERSION_STR,
            IDfACL.DF_PERMIT_WRITE_STR,
            IDfACL.DF_PERMIT_DELETE_STR};

    static final String[] permitTypes = new String[]{IDfPermitTypeString.ACCESS_PERMIT,
            IDfPermitTypeString.EXTENDED_PERMIT,
            IDfPermitTypeString.APPLICATION_PERMIT,
            IDfPermitTypeString.ACCESS_RESTRICTION,
            IDfPermitTypeString.EXTENDED_RESTRICTION,
            IDfPermitTypeString.APPLICATION_RESTRICTION,
            IDfPermitTypeString.REQUIRED_GROUP,
            IDfPermitTypeString.REQUIRED_GROUP_SET
    };

    private IDfACL acl;

    public ACLInfo(IDfACL acl) {
        this.acl = acl;
    }

    public Object[][] asTableArray() {
        try {
            Object[][] arr = new Object[this.acl.getAccessorCount()][4];

            for (int n = 0; n < acl.getAccessorCount(); n++) {
                arr[n][0] = acl.getAccessorName(n);
                arr[n][1] = permNames[acl.getAccessorPermit(n)];
                arr[n][2] = acl.getAccessorXPermitNames(n);
                arr[n][3] = permitTypes[acl.getAccessorPermitType(n)];
            }
            return arr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Object[][]{};
    }
}
