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
 *
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.fc.common.DfLogger;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Implements logic of a Most Recently User(MRU) list.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class MRUQueue implements Serializable {

	/**
	 * Contains the MRUItems in a Queue. Maps from the items creation time to
	 * the MRUItem. <br>
	 * Mapping: <br>
	 * MRUItem.time => MRUItem
	 */
	private TreeMap m_mruItemQueue = null;

	/**
	 * Contains the keys of the MRUItems in a queue. <br>
	 * Mapping is: <br>
	 * MRUKey => mruItem
	 */
	private HashMap m_keyQueue = null;

	/**
	 * The capacity of the MRU Queue. It represents the max number of most
	 * recently used items that will be stored by this queue.
	 */
	private int m_intCapacity = 5; // default value

	/**
	 * Flag that indicates whether the MRUList has changed.
	 */
	private boolean m_blChanged = false;

	/**
	 * Instantiates the MRU item and key queues.
	 * 
	 * @param capacity
	 */
	public MRUQueue(int capacity) {
		m_intCapacity = capacity;
		m_mruItemQueue = new TreeMap();
		m_keyQueue = new HashMap(m_intCapacity);

	}

	/**
	 * Instantiates the MRU item and key queues.
	 * 
	 */
	public MRUQueue() {
		m_mruItemQueue = new TreeMap();
		m_keyQueue = new HashMap(m_intCapacity);
	}

	/**
	 * sets the number of most recently used entries to be remembered by this
	 * queue.
	 * 
	 * @param capacity
	 */
	public void setCapacity(int capacity) {
		m_intCapacity = capacity;
	}

	/**
	 * returns the number of most recently used entries to be remembered by this
	 * queue.
	 * 
	 * @return queue capacity.
	 */
	public int getCapacity() {
		return m_intCapacity;
	}

	/**
	 * Check whether list has changed since last access.
	 * 
	 */
	public boolean hasChanged() {
		return m_blChanged;
	}

	/**
	 * PDL: addToMRUQueue <br>
	 * - Check whether item already exists in the queue <br>
	 * - if yes, remove old one
	 * 
	 * <br>
	 * - Check if queue is full. <br>
	 * If yes, remove the oldest item <br>
	 * Remove the key entry from the KeyQueue. <br>
	 * - Add the new item to the queue. <br>
	 * Add the key entry to the key queue.
	 * 
	 * @param mruItem
	 *            The item to add to the queue.
	 */
	public void addToMRUQueue(MRUItem mruItem) {
		String key = mruItem.getKey();
		DfLogger.debug(this, "key: " + key, null, null);
		if (m_keyQueue.containsKey(key)) // already present
		{
			DfLogger.debug(this, "already contains key:", null, null);
			MRUItem oldItem = (MRUItem) m_keyQueue.get(key);
			m_keyQueue.remove(key);
			Long time = new Long(oldItem.getTime());
			if (m_mruItemQueue.containsKey(time)) {
				m_mruItemQueue.remove(time);
			} else {
				DfLogger.warn(this,
						"Key mismatch. item contained in keyQ but not in mruQ",
						null, null);
			}

		}

		if (m_mruItemQueue.size() == m_intCapacity) // queue full. remove
		// oldest.
		{
			Long oldestTime = (Long) m_mruItemQueue.firstKey();
			MRUItem oldestItem = (MRUItem) m_mruItemQueue.get(oldestTime);
			String oldestKey = oldestItem.getKey();
			m_keyQueue.remove(oldestKey);
			m_mruItemQueue.remove(oldestTime);
		}

		m_mruItemQueue.put(new Long(mruItem.getTime()), mruItem);
		m_keyQueue.put(mruItem.getKey(), mruItem);
		DfLogger.debug(this, "Just set has changed to true", null, null);
		m_blChanged = true;
	}

	/**
	 * This method returns the latest element in the queue. It does not remove
	 * this item from the queue.
	 * 
	 * @return The latest item in the queue or null if queue is empty.
	 */
	public MRUItem peek() {
		Object key = m_mruItemQueue.lastKey();
		MRUItem item = (MRUItem) m_mruItemQueue.get(key);
		return item;
	}

	/**
	 * Gets the last item in the queue. Use peek to get first item. Note this
	 * item does not remove item from queue.
	 * 
	 * @return
	 */
	public MRUItem tail() {
		Object key = m_mruItemQueue.firstKey();
		MRUItem mi = (MRUItem) m_mruItemQueue.get(key);
		return mi;
	}

	/**
	 * Pops the latest item in the queue. The item is removed from the MRU
	 * Queue.
	 * 
	 * @return The latest item in the queue or null if queue is empty
	 */
	public MRUItem pop() {
		Object key = m_mruItemQueue.lastKey();
		MRUItem item = (MRUItem) m_mruItemQueue.get(key);
		if (item != null) {
			m_mruItemQueue.remove(key);
			m_keyQueue.remove(item.getKey());
		}
		return item;
	}

	/**
	 * Gets the next latest item from key.
	 * 
	 * @param key
	 *            MRUItem#key
	 * @return Next latest item or null if one does not exist.
	 */
	public MRUItem getNext(String key) {
		try {
			MRUItem mi = (MRUItem) m_keyQueue.get(key);
			Long time = new Long(mi.getTime());
			SortedMap sm = m_mruItemQueue.headMap(time);
			if (sm == null || sm.size() == 0) {
				return null;
			}

			Object lastKey = sm.lastKey();
			if (lastKey == null) {
				return null;
			}

			MRUItem nextMi = (MRUItem) sm.get(lastKey);
			return nextMi;
		} catch (Exception ex) {
			DfLogger.warn(this, "Error getting next item in MRUQ : ", null, ex);
			return null;
		}
	}

	/**
	 * Gets the first item latest than key.
	 * 
	 * @param key
	 *            MRUItem#key
	 * @return Previous latest item or null if one does not exist.
	 */
	public MRUItem getPrevious(String key) {
		try {
			MRUItem mi = (MRUItem) m_keyQueue.get(key);
			if (mi != null) {
				Long time = new Long(mi.getTime());
				SortedMap sm = m_mruItemQueue.tailMap(time);
				Object[] vals = sm.values().toArray();
				MRUItem prevMi = null;
				if (vals.length > 1) {
					prevMi = (MRUItem) vals[1];
				}
				// added on Mar 27,06
				/*
				 * else { Object lkey = (MRUItem) m_mruItemQueue.lastKey();
				 * prevMi = (MRUItem) m_mruItemQueue.get(lkey); }
				 */
				return prevMi;
			} else {
				return null;
			}
		} catch (Exception ex) {
			DfLogger.warn(this, "Error obtaining previous item in MRUQ", null,
					ex);
			return null;
		}
	}

	/**
	 * Returns a LinkedList containing all the MRU Items. The latest item is in
	 * the first position and the oldest in the last position. The size of the
	 * list can be less than or equal to the capacity of the MRU Queue. Thus,
	 * its not necessary that it always be equal to the capacity of the
	 * MRUQueue.
	 * 
	 * @return List of MRU Items sorted from latest to oldest.
	 */
	public LinkedList getMRUList() {

		LinkedList mruList = new LinkedList();
		Collection mruColl = m_mruItemQueue.values();

		Object[] mruArr = mruColl.toArray();
		for (int i = mruArr.length - 1; i >= 0; i--) {
			mruList.add(mruArr[i]);
		}
		DfLogger.debug(this, "Just set haschanged to false", null, null);
		m_blChanged = false; // since the list has been accessed now.
		return mruList;

	}

}