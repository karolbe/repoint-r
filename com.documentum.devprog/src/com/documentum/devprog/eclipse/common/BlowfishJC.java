/*
 * Created on Jan 29, 2007
 *
 * EMC Developer Network 
 */
package com.documentum.devprog.eclipse.common;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * Blowfish encrypt/decrypt algorithm wrappers around the Java Crypto API
 * implementation
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class BlowfishJC {

	private BlowfishJC() {
		// blank ctor
		// no need for a ctor
	}

	/**
	 * Encrypts the data using the provided key.
	 * 
	 * <br/>
	 * Note that when converting from bytes to String and vice versa the default
	 * platform encoding is used.
	 * 
	 * <br/>
	 * Algo Specs: Blowfish/ECB/PKCS5Padding
	 * 
	 * @param key
	 *            shared key
	 * @param data
	 *            data to be encrypted.
	 * @return encrypted data as a string.
	 * @throws Exception
	 */
	public static String encryptString(String key, String data)
			throws Exception {
		byte[] dataBytes = data.getBytes();
		return new String(encryptData(key, dataBytes));
	}

	/**
	 * Decrypts the encrypted data using the provided key.
	 * 
	 * 
	 * <br/>
	 * Note that when converting from bytes to String and vice versa the default
	 * platform encoding is used.
	 * 
	 * <br/>
	 * Algo Specs: Blowfish/ECB/PKCS5Padding
	 * 
	 * @param key
	 *            shared key
	 * @param encrString
	 *            Encrypted data
	 * @return Decrypted data
	 * @throws Exception
	 * 
	 * 
	 */
	public static String decryptString(String key, String encrString)
			throws Exception {
		byte[] encrData = encrString.getBytes();
		return new String(decryptData(key, encrData));
	}

	/**
	 * Encrypts a byte array using the specified key. <br/>
	 * Algo Specs: Blowfish/ECB/PKCS5Padding
	 * 
	 * @param key
	 *            encryption key
	 * @param dataBytes
	 *            bytes
	 * @return encrypted bytes
	 * @throws Exception
	 */
	public static byte[] encryptData(String key, byte[] dataBytes)
			throws Exception {

		byte[] keyBytes = key.getBytes();
		String algo = "Blowfish/ECB/PKCS5Padding";
		SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "Blowfish");

		Cipher bfCipher = Cipher.getInstance(algo);
		bfCipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encrData = bfCipher.doFinal(dataBytes);

		return encrData;
	}

	/**
	 * Decrypts an encrypted byte array.
	 * 
	 * <br/>
	 * Algo Specs: Blowfish/ECB/PKCS5Padding
	 * 
	 * @param key
	 *            shared key
	 * @param encrData
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptData(String key, byte[] encrData)
			throws Exception {

		byte[] keyBytes = key.getBytes();
		String algo = "Blowfish/ECB/PKCS5Padding";
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "Blowfish");

		Cipher bfCipher = Cipher.getInstance(algo);
		bfCipher.init(Cipher.DECRYPT_MODE, keySpec);

		byte[] decryptedData = bfCipher.doFinal(encrData);

		return decryptedData;

	}

	// ////////////////////////////////////////////////////////////
	// ////UTILITY METHODS////////////////////////////////////////
	// ///////////////////////////////////////////////////////////

	/**
	 * Converts a string of Hex characters into a corresponding byte array
	 * 
	 */
	public static byte[] convertHexToBytes(String hexString) {
		byte[] byteArr = new byte[hexString.length() / 2];
		for (int i = 0; i < byteArr.length; i++) {
			byteArr[i] = (byte) Integer.parseInt(
					hexString.substring(2 * i, 2 * i + 2), 16);
		}
		return byteArr;
	}

	/**
	 * Converts a string of bytes into the corresponding hex string.
	 * 
	 * @param val
	 * @return
	 */
	public static String convertBytesToHex(byte[] val) {
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		// A hex char corresponds to 4 bits. Hence a byte (8 bits)
		// corresponds to two hex chars.
		StringBuffer bufHexVal = new StringBuffer(val.length * 2);
		for (int i = 0; i < val.length; i++) {
			byte b = val[i];
			int hiNibble = ((b & 0xf0) >> 4);
			int loNibble = (b & 0x0f);
			bufHexVal.append(hexChars[hiNibble]).append(hexChars[loNibble]);
		}

		String hexString = bufHexVal.toString();
		// System.out.println(hexString);
		return hexString;
	}

}
