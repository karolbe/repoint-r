package com.documentum.devprog.eclipse.common;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.documentum.devprog.eclipse.DevprogPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DevprogPlugin.getDefault()
				.getPreferenceStore();
		/*
		 * store.setDefault(PreferenceConstants.P_BOOLEAN, true);
		 * store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
		 * store.setDefault(PreferenceConstants.P_STRING, "Default value");
		 */
		store.setDefault(PreferenceConstants.P_PORT, "4445");
		store.setDefault(PreferenceConstants.P_BUFFER_SIZE, "5000");
		store.setDefault(PreferenceConstants.P_TRACE_DELAY, 100);

		store.setDefault(
				PreferenceConstants.P_ATTR_LIST,
				"UPPER(object_name),r_object_id,object_name,title,r_link_cnt,r_object_type,a_content_type,r_lock_owner,r_creation_date,r_creator_name,owner_name");
		store.setDefault(PreferenceConstants.P_VISIBLE_COLUMNS,
				"object_name,r_object_type,r_creation_date,r_creator_name,owner_name");
		store.setDefault(PreferenceConstants.P_SHOW_ALL_OBJECTS, true);
	}

}
