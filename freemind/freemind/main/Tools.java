/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2001 Joerg Mueller <joergmueller@bigfoot.com> See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
/* $Id: Tools.java,v 1.17.18.7 2006-02-21 20:49:42 christianfoltin Exp $ */

package freemind.main;

//maybe move this class to another package like tools or something...

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Tools {

    //public static final Set executableExtensions = new HashSet ({ "exe",
    // "com", "vbs" });

    //The Java programming language provides a shortcut syntax for creating and
    // initializing an array. Here's an example of this syntax:
    //boolean[] answers = { true, false, true, true, false };

    public static final Set executableExtensions = new HashSet(Arrays
            .asList(new String[] { "exe", "com", "vbs", "bat", "lnk" }));

    private static Set availableFontFamilyNames = null; // Keep set of platform

    // fonts

    public static boolean executableByExtension(String file) {
        return executableExtensions.contains(getExtension(file));
    }

    public static String colorToXml(Color col) {
        //	if (col == null) throw new IllegalArgumentException("Color was
        // null");
        if (col == null)
            return null;
        String red = Integer.toHexString(col.getRed());
        if (col.getRed() < 16)
            red = "0" + red;
        String green = Integer.toHexString(col.getGreen());
        if (col.getGreen() < 16)
            green = "0" + green;
        String blue = Integer.toHexString(col.getBlue());
        if (col.getBlue() < 16)
            blue = "0" + blue;
        return "#" + red + green + blue;
    }

    public static Color xmlToColor(String string) {
        if (string == null)
            return null;
        string = string.trim();
        if (string.length() == 7) {

            int red = Integer.parseInt(string.substring(1, 3), 16);
            int green = Integer.parseInt(string.substring(3, 5), 16);
            int blue = Integer.parseInt(string.substring(5, 7), 16);
            return new Color(red, green, blue);
        } else {
            throw new IllegalArgumentException("No xml color given by '"
                    + string + "'.");
        }
    }

    public static String PointToXml(Point col) {
        if (col == null)
            return null; //throw new IllegalArgumentException("Point was
        // null");
        Vector l = new Vector();
        l.add(Integer.toString(col.x));
        l.add(Integer.toString(col.y));
        return listToString((List) l);
    }

    public static Point xmlToPoint(String string) {
        if (string == null)
            return null;
        // fc, 3.11.2004: bug fix for alpha release of FM
        if (string.startsWith("java.awt.Point")) {
            string = string.replaceAll(
                    "java\\.awt\\.Point\\[x=([0-9]*),y=([0-9]*)\\]", "$1;$2");
        }
        List l = stringToList(string);
        ListIterator it = l.listIterator(0);
        if (l.size() != 2)
            throw new IllegalArgumentException(
                    "A point must consist of two numbers (and not: '" + string
                            + "').");
        int x = Integer.parseInt((String) it.next());
        int y = Integer.parseInt((String) it.next());
        return new Point(x, y);
    }

    public static String BooleanToXml(boolean col) {
        return (col) ? "true" : "false";
    }

    public static boolean xmlToBoolean(String string) {
    		if(string == null) 
    			return false;
        if(string.equals("true"))
            return true;
        return false;
    }

    /**
     * Converts a String in the format "value;value;value" to a List with the
     * values (as strings)
     */
    public static List stringToList(String string) {
        StringTokenizer tok = new StringTokenizer(string, ";");
        List list = new LinkedList();
        while (tok.hasMoreTokens()) {
            list.add(tok.nextToken());
        }
        return list;
    }

    public static String listToString(List list) {
        ListIterator it = list.listIterator(0);
        String str = new String();
        while (it.hasNext()) {
            str += it.next().toString() + ";";
        }
        return str;
    }

    /**
     * Replaces a ~ in a filename with the users home directory
     */
    public static String expandFileName(String file) {
        //replace ~ with the users home dir
        if (file.startsWith("~")) {
            file = System.getProperty("user.home") + file.substring(1);
        }
        return file;
    }

    public static Set getAvailableFontFamilyNames() {
        if (availableFontFamilyNames == null) {
            GraphicsEnvironment gEnv = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();
            String envFonts[] = gEnv.getAvailableFontFamilyNames();
            availableFontFamilyNames = new HashSet();
            for (int i = 0; i < envFonts.length; i++) {
                availableFontFamilyNames.add(envFonts[i]);
            }
            // Add this one explicitly, Java defaults to it if the font is not
            availableFontFamilyNames.add("dialog");
        }
        return availableFontFamilyNames;
    }

    public static Vector getAvailableFontFamilyNamesAsVector() {
        GraphicsEnvironment gEnv = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String envFonts[] = gEnv.getAvailableFontFamilyNames();
        Vector availableFontFamilyNames = new Vector();
        for (int i = 0; i < envFonts.length; i++) {
            availableFontFamilyNames.add(envFonts[i]);
        }
        return availableFontFamilyNames;
    }

    public static boolean isAvailableFontFamily(String fontFamilyName) {
        return getAvailableFontFamilyNames().contains(fontFamilyName);
    }

    /**
     * Returns the lowercase of the extension of a file. Example:
     * getExtension("fork.pork.MM") == "mm"
     */
    public static String getExtension(File f) {
        return getExtension(f.toString());
    }

    /**
     * Returns the lowercase of the extension of a file name. Example:
     * getExtension("fork.pork.MM") == "mm"
     */
    public static String getExtension(String s) {
        int i = s.lastIndexOf('.');
        return (i > 0 && i < s.length() - 1) ? s.substring(i + 1).toLowerCase()
                .trim() : "";
    }

    public static String removeExtension(String s) {
        int i = s.lastIndexOf('.');
        return (i > 0 && i < s.length() - 1) ? s.substring(0, i) : "";
    }

    public static String toXMLEscapedText(String text) {
        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

    public static String toXMLUnescapedText(String text) {
        return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll(
                "&quot;", "\"").replaceAll("&amp;", "&");
    }

    public static String toXMLEscapedTextWithNBSPizedSpaces(String text) {
        int len = text.length();
        StringBuffer result = new StringBuffer(len);
        char myChar;
        boolean previousSpace = false;
        boolean spaceOccured = false;
        for (int i = 0; i < len; ++i) {
            myChar = text.charAt(i);
            spaceOccured = false;
            switch (myChar) {
            case '&':
                result.append("&amp;");
                break;
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case ' ':
                spaceOccured = true;
                if (previousSpace) {
                    result.append("&nbsp;");
                } else {
                    result.append(" ");
                }
                break;
            default:
                result.append(myChar);
            }
            previousSpace = spaceOccured;
        }
        return result.toString();
    }

    public static boolean isAbsolutePath(String path) {
        // On Windows, we cannot just ask if the file name starts with file
        // separator.
        // If path contains ":" at the second position, then it is not relative,
        // I guess.
        // However, if it starts with separator, then it is absolute too.

        // Possible problems: Not tested on Macintosh, but should work.
        // Koh, 1.4.2004: Resolved problem: I tested on Mac OS X 10.3.3 and
        // worked.

        String osNameStart = System.getProperty("os.name").substring(0, 3);
        String fileSeparator = System.getProperty("file.separator");
        if (osNameStart.equals("Win")) {
            return ((path.length() > 1) && path.substring(1, 2).equals(":"))
                    || path.startsWith(fileSeparator);
        } else if (osNameStart.equals("Mac")) {
            //Koh:Panther (or Java 1.4.2) may change file path rule
            return path.startsWith(fileSeparator);
        } else {
            return path.startsWith(fileSeparator);
        }
    }

    /**
     * This is a correction of a method getFile of a class URL. Namely, on
     * Windows it returned file paths like /C: etc., which are not valid on
     * Windows. This correction is heuristic to a great extend. One of the
     * reasons is that file:// is basically no protocol at all, but rather
     * something every browser and every system uses slightly differently.
     */
    public static String urlGetFile(URL url) {
        String osNameStart = System.getProperty("os.name").substring(0, 3);
        if (osNameStart.equals("Win") && url.getProtocol().equals("file")) {
            String fileName = url.toString().replaceFirst("^file:", "")
                    .replace('/', '\\');
            return (fileName.indexOf(':') >= 0) ? fileName.replaceFirst(
                    "^\\\\*", "") : fileName;
        } // Network path
        else {
            return url.getFile();
        }
    }

    /**
     * This method converts an absolute url to an url relative to a given
     * base-url. The algorithm is somewhat chaotic, but it works (Maybe rewrite
     * it). Be careful, the method is ".mm"-specific. Something like this should
     * be included in the librarys, but I couldn't find it. You can create a new
     * absolute url with "new URL(URL context, URL relative)".
     */
    public static String toRelativeURL(URL base, URL target) {
        // Precondition: If URL is a path to folder, then it must end with '/'
        // character.
        if ((base.getProtocol().equals(target.getProtocol()))
                && (base.getHost().equals(target.getHost()))) {

            String baseString = base.getFile();
            String targetString = target.getFile();
            String result = "";

            //remove filename from URL
            baseString = baseString.substring(0,
                    baseString.lastIndexOf("/") + 1);

            //remove filename from URL
            targetString = targetString.substring(0, targetString
                    .lastIndexOf("/") + 1);

            StringTokenizer baseTokens = new StringTokenizer(baseString, "/");//Maybe
            // this
            // causes
            // problems
            // under
            // windows
            StringTokenizer targetTokens = new StringTokenizer(targetString,
                    "/");//Maybe this causes problems under windows

            String nextBaseToken = "", nextTargetToken = "";

            //Algorithm

            while (baseTokens.hasMoreTokens() && targetTokens.hasMoreTokens()) {
                nextBaseToken = baseTokens.nextToken();
                nextTargetToken = targetTokens.nextToken();
                if (!(nextBaseToken.equals(nextTargetToken))) {
                    while (true) {
                        result = result.concat("../");
                        if (!baseTokens.hasMoreTokens()) {
                            break;
                        }
                        nextBaseToken = baseTokens.nextToken();
                    }
                    while (true) {
                        result = result.concat(nextTargetToken + "/");
                        if (!targetTokens.hasMoreTokens()) {
                            break;
                        }
                        nextTargetToken = targetTokens.nextToken();
                    }
                    String temp = target.getFile();
                    result = result.concat(temp.substring(
                            temp.lastIndexOf("/") + 1, temp.length()));
                    return result;
                }
            }

            while (baseTokens.hasMoreTokens()) {
                result = result.concat("../");
                baseTokens.nextToken();
            }

            while (targetTokens.hasMoreTokens()) {
                nextTargetToken = targetTokens.nextToken();
                result = result.concat(nextTargetToken + "/");
            }

            String temp = target.getFile();
            result = result.concat(temp.substring(temp.lastIndexOf("/") + 1,
                    temp.length()));
            return result;
        }
        return target.toString();
    }

    public static boolean safeEquals(String string1, String string2) {
        return (string1 != null && string2 != null && string1.equals(string2))
                || (string1 == null && string2 == null);
    }

    public static boolean safeEquals(Object obj1, Object obj2) {
		return (obj1 != null && obj2 != null && obj1.equals(obj2))
				|| (obj1 == null && obj2 == null);
	}
    
    public static boolean safeEqualsIgnoreCase(String string1, String string2) {
        return (string1 != null && string2 != null && string1.toLowerCase()
                .equals(string2.toLowerCase()))
                || (string1 == null && string2 == null);
    }

    public static boolean safeEquals(Color color1, Color color2) {
        return (color1 != null && color2 != null && color1.equals(color2))
                || (color1 == null && color2 == null);
    }

    public static String firstLetterCapitalized(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        return text.substring(0, 1).toUpperCase()
                + text.substring(1, text.length());
    }

    public static void setHidden(File file, boolean hidden,
            boolean synchronously) {
        // According to Web articles, UNIX systems do not have attribute hidden
        // in general, rather, they consider files starting with . as hidden.
        String osNameStart = System.getProperty("os.name").substring(0, 3);
        if (osNameStart.equals("Win")) {
            try {
                Runtime.getRuntime().exec(
                        "attrib " + (hidden ? "+" : "-") + "H \""
                                + file.getAbsolutePath() + "\"");
                // Synchronize the effect, because it is asynchronous in
                // general.
                if (!synchronously) {
                    return;
                }
                int timeOut = 10;
                while (file.isHidden() != hidden && timeOut > 0) {
                    Thread.sleep(10/* miliseconds */);
                    timeOut--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Example: expandPlaceholders("Hello $1.","Dolly"); => "Hello Dolly."
     */
    public static String expandPlaceholders(String message, String s1) {
        String result = message;
        if (s1 != null) {
            s1 = s1.replaceAll("\\\\", "\\\\\\\\"); // Replace \ with \\
            result = result.replaceAll("\\$1", s1);
        }
        return result;
    }

    public static String expandPlaceholders(String message, String s1, String s2) {
        String result = message;
        if (s1 != null) {
            result = result.replaceAll("\\$1", s1);
        }
        if (s2 != null) {
            result = result.replaceAll("\\$2", s2);
        }
        return result;
    }

    public static String expandPlaceholders(String message, String s1,
            String s2, String s3) {
        String result = message;
        if (s1 != null) {
            result = result.replaceAll("\\$1", s1);
        }
        if (s2 != null) {
            result = result.replaceAll("\\$2", s2);
        }
        if (s3 != null) {
            result = result.replaceAll("\\$3", s3);
        }
        return result;
    }

    public static class IntHolder {
        private int value;

        public IntHolder() {
        }

        public IntHolder(int value) {
            this.value = value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public String toString() {
            return new String("IntHolder(") + value + ")";
        }
    }

    public static class BooleanHolder {
        private boolean value;

        public BooleanHolder() {
        }

        public void setValue(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }
    }

    public static class ObjectHolder {
        Object object;

        public ObjectHolder() {
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    public static class Pair {
        Object first;

        Object second;

        public Pair(Object first, Object second) {
            this.first = first;
            this.second = second;
        }

        public Object getFirst() {
            return first;
        }

        public Object getSecond() {
            return second;
        }
    }

    /** from: http://javaalmanac.com/egs/javax.crypto/PassKey.html */
	public static class TripleDesEncrypter {
	    private static final String SALT_PRESENT_INDICATOR = " ";
	    private static final int SALT_LENGTH=8;
	    
	    Cipher ecipher;
	
	    Cipher dcipher;
	
	    // 8-byte default Salt
	    byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
	            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };
	
	    // Iteration count
	    int iterationCount = 19;
	
		private final char[] passPhrase;
	
	    public TripleDesEncrypter(StringBuffer pPassPhrase) {
	    		passPhrase = new char[pPassPhrase.length()];
	    		pPassPhrase.getChars(0, passPhrase.length, passPhrase, 0);
	    }
	
	    /**
		 * @param mSalt
		 */
		private void init(byte[] mSalt) {
	        if(mSalt!=null) {
	        		this.salt = mSalt;
	        }
			if (ecipher==null) {
				try {
					// Create the key
					KeySpec keySpec = new PBEKeySpec(passPhrase,
							salt, iterationCount);
					SecretKey key = SecretKeyFactory.getInstance(
							"PBEWithMD5AndTripleDES").generateSecret(keySpec);
					ecipher = Cipher.getInstance(key.getAlgorithm());
					dcipher = Cipher.getInstance(key.getAlgorithm());
	
					// Prepare the parameter to the ciphers
					AlgorithmParameterSpec paramSpec = new PBEParameterSpec(
							salt, iterationCount);
	
					// Create the ciphers
					ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
					dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
				} catch (java.security.InvalidAlgorithmParameterException e) {
				} catch (java.security.spec.InvalidKeySpecException e) {
				} catch (javax.crypto.NoSuchPaddingException e) {
				} catch (java.security.NoSuchAlgorithmException e) {
				} catch (java.security.InvalidKeyException e) {
				}
			}
		}
	
		public String encrypt(String str) {
	        try {
	            // Encode the string into bytes using utf-8
	            byte[] utf8 = str.getBytes("UTF8");
	            // determine salt by random:
	            byte[] newSalt = new byte[SALT_LENGTH];
	            for (int i = 0; i < newSalt.length; i++) {
	                newSalt[i] = (byte)(Math.random()*256l-128l);
	            }
	
				init(newSalt);
	            // Encrypt
	            byte[] enc = ecipher.doFinal(utf8);
	
	            // Encode bytes to base64 to get a string
	            return Tools.toBase64(newSalt)
	                    + SALT_PRESENT_INDICATOR
	                    + Tools.toBase64(enc);
	        } catch (javax.crypto.BadPaddingException e) {
	        } catch (IllegalBlockSizeException e) {
	        } catch (UnsupportedEncodingException e) {
	        }
	        return null;
	    }
	
	
	    public String decrypt(String str) {
	        if(str == null) {
	            return null;
	        }
	        try {
	            byte[] salt = null;
	            // test if salt exists:
	            int indexOfSaltIndicator = str.indexOf(SALT_PRESENT_INDICATOR);
	            if(indexOfSaltIndicator>=0) {
	                String saltString = str.substring(0, indexOfSaltIndicator);
	                str = str.substring(indexOfSaltIndicator+1);
	                salt = Tools.fromBase64(saltString);
	            }
	            // Decode base64 to get bytes
	            byte[] dec = Tools.fromBase64(str);
				init(salt);
				               
				// Decrypt
	            byte[] utf8 = dcipher.doFinal(dec);
	
	            // Decode using utf-8
	            return new String(utf8, "UTF8");
	        } catch (javax.crypto.BadPaddingException e) {
	        } catch (IllegalBlockSizeException e) {
	        } catch (UnsupportedEncodingException e) {
	        }
	        return null;
	    }
	}

	/**
     * @param byteBuffer
     * @return
     */
    public static String toBase64(byte[] byteBuffer) {
        return new String(CommonsCodecBase64.encodeBase64(byteBuffer));
    }

    /**
     * @param base64String
     * @return
     * @throws IOException
     */
    public static byte[] fromBase64(String base64String)  {
        return CommonsCodecBase64.decodeBase64(base64String.getBytes());
    }

    public static String compress(String message) {
        byte[] input = uTF8StringToByteArray(message);
        //     Create the compressor with highest level of compression
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);

        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // You cannot use an array that's the same size as the orginal because
        // there is no guarantee that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
        }

        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
        return toBase64(compressedData);
    }

    public static String decompress(String compressedMessage) {
        byte[] compressedData = fromBase64(compressedMessage);
        //      Create the decompressor and give it the data to compress
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(
                compressedData.length);

        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
        }

        // Get the decompressed data
        byte[] decompressedData = bos.toByteArray();
        return byteArrayToUTF8String(decompressedData);
    }
    /**
     * @param compressedData
     * @return
     */
    public static String byteArrayToUTF8String(byte[] compressedData) {
        //     Decode using utf-8
        try {
            return new String(compressedData, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }
    /**
     * @param uncompressedData
     * @return
     */
    public static byte[] uTF8StringToByteArray(String uncompressedData) {
        //     Code using utf-8
        try {
            return uncompressedData.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }
    
	/** Extracts a long from xml. Only useful for dates.
     * @param xmlString
     * @return
     */
    public static Date xmlToDate(String xmlString) {
        try {
            return new Date(Long.valueOf(xmlString).longValue());
        } catch (Exception e) {
            return new Date(System.currentTimeMillis());
        }
    }

    
    public static String dateToString(Date date) {
    	 return Long.toString(date.getTime());
    }

	public static boolean safeEquals(BooleanHolder holder, BooleanHolder holder2) {
		return (holder == null && holder2 == null)
				|| (holder != null && holder2 != null && holder.getValue() == holder2
						.getValue());
	}


}

