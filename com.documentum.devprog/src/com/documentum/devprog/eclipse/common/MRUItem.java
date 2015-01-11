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
 * Created on Aug 20, 2003
 * Documentum Developer Program 2003
 *
 */
package com.documentum.devprog.eclipse.common;

import java.io.Serializable;

/**
 * The class represents a MRU List item. The MRU Queue contains zero or more of
 * these.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class MRUItem implements Serializable {

	private String m_strData = null;
	private long m_lTime = 0;

	/**
    *
    */
	private String m_strKey;

	public MRUItem() {
		super();
	}

	public MRUItem(String key, String data, long time) {
		m_strKey = key;
		m_strData = data;
		m_lTime = time;
	}

	/**
	 * @param data
	 */
	void setData(String data) {
		m_strData = data;
	}

	public String getData() {
		return m_strData;
	}

	public void setTime(long time) {
		m_lTime = time;
	}

	public long getTime() {
		return m_lTime;
	}

	void setKey(String key) {
		m_strKey = key;
	}

	public String getKey() {
		return m_strKey;
	}
}
