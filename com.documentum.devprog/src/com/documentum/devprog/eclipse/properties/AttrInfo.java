/*******************************************************************************
 * Copyright (c) 2005-2006, EMC Corporation 
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided that 
 * the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * - Neither the name of the EMC Corporation nor the names of its 
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *******************************************************************************/

/*
 * Created on Jul 19, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.properties;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * Class represents attribute data. Each instance corresponds to one row of the
 * properties view.
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class AttrInfo {

	private ArrayList repVals = new ArrayList();

	private boolean isObject = true;

	private boolean isModified = false;

	public AttrInfo(String name, String value, String type, boolean isCustom) {
		this.name = name;
		this.value = value;
		this.dataType = type;
		this.custom = isCustom;
	}

	public AttrInfo(String name, String dataType, boolean single,
			boolean isCustom) {
		this.name = name;
		this.dataType = dataType;
		this.repeating = single;
		this.custom = isCustom;
	}

	public AttrInfo() {
	}

	private String name = null;

	private String value = null;

	private String dataType = null;

	private boolean custom = false;

	private boolean repeating = false;

	public boolean isCustom() {
		return custom;
	}

	public String getDataType() {
		return dataType;
	}

	public String getName() {
		return name;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public String getValue() {
		return value;
	}

	public boolean isObject() {
		return isObject;
	}

	void setCustom(boolean custom) {
		this.custom = custom;
	}

	void setDataType(String dataType) {
		this.dataType = dataType;
	}

	void setName(String name) {
		this.name = name;
	}

	void setRepeating(boolean single) {
		this.repeating = single;
	}

	void setValue(String value) {
		this.value = value;
		repVals.clear();
		repVals.add(value);
	}

	void addValue(String value) {
		repVals.add(value);
	}

	String[] getValues() {
		return (String[]) repVals.toArray(new String[] {});
	}

	void setValues(String[] vals) {
		repVals.clear();
		repVals.addAll(Arrays.asList(vals));
	}

	public void setObject(boolean isObject) {
		this.isObject = isObject;
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

}
