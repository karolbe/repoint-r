/*
 * Created on Sep 27, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.rcpapp;

import java.io.File;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TestClasspathMod {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// String cp = System.getProperty("java.class.path");
		testAbsolutePath();
	}

	private static void testAbsolutePath() {
		File fl = new File("");
		System.out.println(fl.getAbsolutePath());

	}

}
