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
 * Created on Sep 21, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This Regex works - 
 * (select\b).*?(\bfrom\b).*?(\bIN\b).*?(\bDOCUMENT|ASSEMBLY\b).*?(\bID\s*\().*?(\bwhere\b)
 */
public class TestRegex {

	private static void processInput(String regex, String input) {
		System.out.println("Regex: " + regex);
		System.out.println("Input: " + input);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(input);
		System.out.println(match.groupCount());
		if (match.find()) {
			for (int i = 1; i <= match.groupCount(); i++) {

				System.out.println(match.group(i) + ":" + match.start(i) + ":"
						+ match.end(i));

			}
		}

	}

	private static void testReTokenizer() {
		String pattern = "(select|from|where|ID|Document|Assembly|FOLDER|IN)";
		String query = "select * from dm_document where ";

		SimpleDQLTokenizer toks = new SimpleDQLTokenizer(query, true);
		while (toks.hasNext()) {
			QueryWord qw = (QueryWord) toks.next();
			System.out.println(qw.word + ":start=" + qw.start);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// processInput(args[0],args[1]);
		testReTokenizer();
	}

}
