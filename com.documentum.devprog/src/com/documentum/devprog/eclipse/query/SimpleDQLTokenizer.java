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
 * Created on Sep 22, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.query;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class was adapted from the RETokenizer at the Java Developers Almanac
 * website. http://javaalmanac.com/egs/java.util.regex/Tokenize.html
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class SimpleDQLTokenizer implements Iterator {
	// Holds the original input to search for tokens
	private CharSequence input;

	// Used to find tokens
	private Matcher matcher;

	// If true, the String between tokens are returned
	private boolean returnDelims;

	// The current delimiter value. If non-null, should be returned
	// at the next call to next()
	private String delim;

	// The current matched value. If non-null and delim=null,
	// should be returned at the next call to next()
	private String match;

	// The value of matcher.end() from the last successful match.
	private int lastEnd = 0;

	// value of matcher.start() of the current match.
	private int curStart = 0;

	// private String patternStr =
	// "(select\b|\bfrom\b|\bwhere\b|\bin\b|\bdocument\b|\bfolder|cabinet|id|";

	private String patternStr = "(\\s+|\\(|\\))";

	public SimpleDQLTokenizer(CharSequence input, boolean returnDelims) {
		// Save values
		this.input = input;
		this.returnDelims = returnDelims;

		// Compile pattern and prepare input
		Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(input);
	}

	// Returns true if there are more tokens or delimiters.
	public boolean hasNext() {
		if (matcher == null) {
			return false;
		}
		if (delim != null || match != null) {
			return true;
		}
		if (matcher.find()) {
			if (returnDelims) {
				delim = input.subSequence(lastEnd, matcher.start()).toString();
			}
			// match = matcher.group();
			lastEnd = matcher.end();
			curStart = matcher.start() - (delim.length());
		} else if (returnDelims && lastEnd < input.length()) {
			delim = input.subSequence(lastEnd, input.length()).toString();
			lastEnd = input.length();

			// Need to remove the matcher since it appears to automatically
			// reset itself once it reaches the end.
			matcher = null;
		}
		return delim != null || match != null;
	}

	// Returns the next token (or delimiter if returnDelims is true).
	public Object next() {
		String result = null;

		if (delim != null) {
			result = delim;
			delim = null;
		} else if (match != null) {
			result = match;
			match = null;
		}

		QueryWord qw = new QueryWord();
		qw.word = result;
		qw.start = curStart;
		return qw;
	}

	// Returns true if the call to next() will return a token rather
	// than a delimiter.
	public boolean isNextToken() {
		return delim == null && match != null;
	}

	// Not supported.
	public void remove() {
		throw new UnsupportedOperationException();
	}
}