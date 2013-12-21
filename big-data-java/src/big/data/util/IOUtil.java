package big.data.util;

/*
 * METHODS OF THIS CLASS HAVE BEEN RUTHLESSLY CANNABALIZED FROM
 * THE PROCESSING PROJECT. SEE COPYRIGHT NOTICE BELOW.
 */

/*
Part of the Processing project - http://processing.org

Copyright (c) 2012-13 The Processing Foundation
Copyright (c) 2004-12 Ben Fry and Casey Reas
Copyright (c) 2001-04 Massachusetts Institute of Technology

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IOUtil {

	//////////////////////////////////////////////////////////////

	// STRINGS

	
	// .nah.
	
	public static String repeat(char c, int count) {
		char[] spaces = new char[count];
		Arrays.fill(spaces, c);
		return new String(spaces);
	}
	
	
	

	/**
	 * ( begin auto-generated from trim.xml )
	 *
	 * Removes whitespace characters from the beginning and end of a String. In
	 * addition to standard whitespace characters such as space, carriage
	 * return, and tab, this function also removes the Unicode "nbsp" character.
	 *
	 * ( end auto-generated )
	 * @webref data:string_functions
	 * @param str any string
	 * @see PApplet#split(String, String)
	 * @see PApplet#join(String[], char)
	 */
	static public String trim(String str) {
		return str.replace('\u00A0', ' ').trim();
	}


	/**
	 * @param array a String array
	 */
	static public String[] trim(String[] array) {
		String[] outgoing = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				outgoing[i] = array[i].replace('\u00A0', ' ').trim();
			}
		}
		return outgoing;
	}


	/**
	 * ( begin auto-generated from join.xml )
	 *
	 * Combines an array of Strings into one String, each separated by the
	 * character(s) used for the <b>separator</b> parameter. To join arrays of
	 * ints or floats, it's necessary to first convert them to strings using
	 * <b>nf()</b> or <b>nfs()</b>.
	 *
	 * ( end auto-generated )
	 * @webref data:string_functions
	 * @param list array of Strings
	 * @param separator char or String to be placed between each item
	 * @see PApplet#split(String, String)
	 * @see PApplet#trim(String)
	 * @see PApplet#nf(float, int, int)
	 * @see PApplet#nfs(float, int, int)
	 */
	static public String join(String[] list, char separator) {
		return join(list, String.valueOf(separator));
	}


	static public String join(String[] list, String separator) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.length; i++) {
			if (i != 0) buffer.append(separator);
			buffer.append(list[i]);
		}
		return buffer.toString();
	}


	//////////////////////////////////////////////////////////////

	// READERS AND WRITERS


	/**
	 * ( begin auto-generated from createReader.xml )
	 *
	 * Creates a <b>BufferedReader</b> object that can be used to read files
	 * line-by-line as individual <b>String</b> objects. This is the complement
	 * to the <b>createWriter()</b> function.
	 * <br/> <br/>
	 * Starting with Processing release 0134, all files loaded and saved by the
	 * Processing API use UTF-8 encoding. In previous releases, the default
	 * encoding for your platform was used, which causes problems when files
	 * are moved to other platforms.
	 *
	 * ( end auto-generated )
	 * @webref input:files
	 * @param filename name of the file to be opened
	 * @see BufferedReader
	 * @see PApplet#createWriter(String)
	 * @see PrintWriter
	 */
	public static BufferedReader createReader(String filename) {
		try {
			InputStream is = createInput(filename);
			if (is == null) {
				System.err.println(filename + " does not exist or could not be read");
				return null;
			}
			return createReader(is);

		} catch (Exception e) {
			if (filename == null) {
				System.err.println("Filename passed to reader() was null");
			} else {
				System.err.println("Couldn't create a reader for " + filename);
			}
		}
		return null;
	}


	/**
	 * @nowebref
	 */
	static public BufferedReader createReader(File file) {
		try {
			InputStream is = new FileInputStream(file);
			if (file.getName().toLowerCase().endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}
			return createReader(is);

		} catch (Exception e) {
			if (file == null) {
				throw new RuntimeException("File passed to createReader() was null");
			} else {
				e.printStackTrace();
				throw new RuntimeException("Couldn't create a reader for " +
						file.getAbsolutePath());
			}
		}
		//return null;
	}


	/**
	 * @nowebref
	 * I want to read lines from a stream. If I have to type the
	 * following lines any more I'm gonna send Sun my medical bills.
	 */
	static public BufferedReader createReader(InputStream input) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(input, "UTF-8");
		} catch (UnsupportedEncodingException e) { }  // not gonna happen
		return new BufferedReader(isr);
	}


	/**
	 * ( begin auto-generated from createWriter.xml )
	 *
	 * Creates a new file in the sketch folder, and a <b>PrintWriter</b> object
	 * to write to it. For the file to be made correctly, it should be flushed
	 * and must be closed with its <b>flush()</b> and <b>close()</b> methods
	 * (see above example).
	 * <br/> <br/>
	 * Starting with Processing release 0134, all files loaded and saved by the
	 * Processing API use UTF-8 encoding. In previous releases, the default
	 * encoding for your platform was used, which causes problems when files
	 * are moved to other platforms.
	 *
	 * ( end auto-generated )
	 *
	 * @webref output:files
	 * @param filename name of the file to be created
	 * @see PrintWriter
	 * @see PApplet#createReader
	 * @see BufferedReader
	 */
	public PrintWriter createWriter(String filename) {
		return createWriter(saveFile(filename));
	}

	/**
	 * @nowebref
	 * I want to print lines to a file. I have RSI from typing these
	 * eight lines of code so many times.
	 */
	static public PrintWriter createWriter(File file) {
		try {
			createPath(file);  // make sure in-between folders exist
			OutputStream output = new FileOutputStream(file);
			if (file.getName().toLowerCase().endsWith(".gz")) {
				output = new GZIPOutputStream(output);
			}
			return createWriter(output);

		} catch (Exception e) {
			if (file == null) {
				throw new RuntimeException("File passed to createWriter() was null");
			} else {
				e.printStackTrace();
				throw new RuntimeException("Couldn't create a writer for " +
						file.getAbsolutePath());
			}
		}
		//return null;
	}

	/**
	 * @nowebref
	 * I want to print lines to a file. Why am I always explaining myself?
	 * It's the JavaSoft API engineers who need to explain themselves.
	 */
	static public PrintWriter createWriter(OutputStream output) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(output, 8192);
			OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
			return new PrintWriter(osw);
		} catch (UnsupportedEncodingException e) { }  // not gonna happen
		return null;
	}



	//////////////////////////////////////////////////////////////

	// FILE INPUT


	/**
	 * ( begin auto-generated from createInput.xml )
	 *
	 * This is a function for advanced programmers to open a Java InputStream.
	 * It's useful if you want to use the facilities provided by PApplet to
	 * easily open files from the data folder or from a URL, but want an
	 * InputStream object so that you can use other parts of Java to take more
	 * control of how the stream is read.<br />
	 * <br />
	 * The filename passed in can be:<br />
	 * - A URL, for instance <b>openStream("http://processing.org/")</b><br />
	 * - A file in the sketch's <b>data</b> folder<br />
	 * - The full path to a file to be opened locally (when running as an
	 * application)<br />
	 * <br />
	 * If the requested item doesn't exist, null is returned. If not online,
	 * this will also check to see if the user is asking for a file whose name
	 * isn't properly capitalized. If capitalization is different, an error
	 * will be printed to the console. This helps prevent issues that appear
	 * when a sketch is exported to the web, where case sensitivity matters, as
	 * opposed to running from inside the Processing Development Environment on
	 * Windows or Mac OS, where case sensitivity is preserved but ignored.<br />
	 * <br />
	 * If the file ends with <b>.gz</b>, the stream will automatically be gzip
	 * decompressed. If you don't want the automatic decompression, use the
	 * related function <b>createInputRaw()</b>.
	 * <br />
	 * In earlier releases, this function was called <b>openStream()</b>.<br />
	 * <br />
	 *
	 * ( end auto-generated )
	 *
	 * <h3>Advanced</h3>
	 * Simplified method to open a Java InputStream.
	 * <p>
	 * This method is useful if you want to use the facilities provided
	 * by PApplet to easily open things from the data folder or from a URL,
	 * but want an InputStream object so that you can use other Java
	 * methods to take more control of how the stream is read.
	 * <p>
	 * If the requested item doesn't exist, null is returned.
	 * (Prior to 0096, die() would be called, killing the applet)
	 * <p>
	 * For 0096+, the "data" folder is exported intact with subfolders,
	 * and openStream() properly handles subdirectories from the data folder
	 * <p>
	 * If not online, this will also check to see if the user is asking
	 * for a file whose name isn't properly capitalized. This helps prevent
	 * issues when a sketch is exported to the web, where case sensitivity
	 * matters, as opposed to Windows and the Mac OS default where
	 * case sensitivity is preserved but ignored.
	 * <p>
	 * It is strongly recommended that libraries use this method to open
	 * data files, so that the loading sequence is handled in the same way
	 * as functions like loadBytes(), loadImage(), etc.
	 * <p>
	 * The filename passed in can be:
	 * <UL>
	 * <LI>A URL, for instance openStream("http://processing.org/");
	 * <LI>A file in the sketch's data folder
	 * <LI>Another file to be opened locally (when running as an application)
	 * </UL>
	 *
	 * @webref input:files
	 * @param filename the name of the file to use as input
	 * @see PApplet#createOutput(String)
	 * @see PApplet#selectOutput(String)
	 * @see PApplet#selectInput(String)
	 *
	 */
	public static InputStream createInput(String filename) {
		InputStream input = createInputRaw(filename);
		if (input != null) {
			if (filename.toLowerCase().endsWith(".gz")) {
				try {
					return new GZIPInputStream(input);
				} catch (IOException e) {
					//e.printStackTrace();
					return null;
				}
			} else if (filename.toLowerCase().endsWith(".zip")) {
				try {
					ZipInputStream zin = new ZipInputStream(input);
					ZipEntry ze = zin.getNextEntry();
					System.err.println("Using " + ze.getName() + " from zip source");
					return zin;	
				} catch (IOException e) {
					//e.printStackTrace();
					return null;
				}
			}
		}
		return input;
	}


	/**
	 * Call openStream() without automatic gzip decompression.
	 */
	public static InputStream createInputRaw(String filename) {
		InputStream stream = null;

		if (filename == null) return null;

		if (filename.length() == 0) {
			// an error will be called by the parent function
			//System.err.println("The filename passed to openStream() was empty.");
			return null;
		}

		// safe to check for this as a url first. this will prevent online
		// access logs from being spammed with GET /sketchfolder/http://blahblah
		if (filename.contains(":")) {  // at least smells like URL
			try {
				URL url = new URL(filename);
				stream = url.openStream();
				return stream;

			} catch (MalformedURLException mfue) {
				// not a url, that's fine

			} catch (FileNotFoundException fnfe) {
				// Java 1.5 likes to throw this when URL not available. (fix for 0119)
				// http://dev.processing.org/bugs/show_bug.cgi?id=403

			} catch (IOException e) {
				// changed for 0117, shouldn't be throwing exception
				//e.printStackTrace();
				//System.err.println("Error downloading from URL " + filename);
				return null;
				//throw new RuntimeException("Error downloading from URL " + filename);
			}
		}

		// Moved this earlier than the getResourceAsStream() checks, because
		// calling getResourceAsStream() on a directory lists its contents.
		// http://dev.processing.org/bugs/show_bug.cgi?id=716
		try {
			File file = new File(filename);

			if (file.isDirectory()) {
				return null;
			}
			if (file.exists()) {
				try {
					// handle case sensitivity check
					String filePath = file.getCanonicalPath();
					String filenameActual = new File(filePath).getName();
					// make sure there isn't a subfolder prepended to the name
					String filenameShort = new File(filename).getName();
					// if the actual filename is the same, but capitalized
					// differently, warn the user.
					//if (filenameActual.equalsIgnoreCase(filenameShort) &&
					//!filenameActual.equals(filenameShort)) {
					if (!filenameActual.equals(filenameShort)) {
						throw new RuntimeException("This file is named " +
								filenameActual + " not " +
								filename + ". Rename the file " +
								"or change your code.");
					}
				} catch (IOException e) { }
			}

			// if this file is ok, may as well just load it
			stream = new FileInputStream(file);
			if (stream != null) return stream;

			// have to break these out because a general Exception might
			// catch the RuntimeException being thrown above
		} catch (IOException ioe) {
		} catch (SecurityException se) { }

		return null;
	}

	/**
	 * @nowebref
	 */
	static public InputStream createInput(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File passed to createInput() was null");
		}
		try {
			InputStream input = new FileInputStream(file);
			if (file.getName().toLowerCase().endsWith(".gz")) {
				return new GZIPInputStream(input);
			}
			return input;

		} catch (IOException e) {
			System.err.println("Could not createInput() for " + file);
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * ( begin auto-generated from loadBytes.xml )
	 *
	 * Reads the contents of a file or url and places it in a byte array. If a
	 * file is specified, it must be located in the sketch's "data"
	 * directory/folder.<br />
	 * <br />
	 * The filename parameter can also be a URL to a file found online. For
	 * security reasons, a Processing sketch found online can only download
	 * files from the same server from which it came. Getting around this
	 * restriction requires a <a
	 * href="http://wiki.processing.org/w/Sign_an_Applet">signed applet</a>.
	 *
	 * ( end auto-generated )
	 * @webref input:files
	 * @param filename name of a file in the data folder or a URL.
	 * @see PApplet#loadStrings(String)
	 * @see PApplet#saveStrings(String, String[])
	 * @see PApplet#saveBytes(String, byte[])
	 *
	 */
	public byte[] loadBytes(String filename) {
		InputStream is = createInput(filename);
		if (is != null) {
			byte[] outgoing = loadBytes(is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();  // shouldn't happen
			}
			return outgoing;
		}

		System.err.println("The file \"" + filename + "\" " +
				"is missing or inaccessible, make sure " +
				"the URL is valid or that the file has been " +
				"added to your sketch and is readable.");
		return null;
	}

	/**
	 * @nowebref
	 */
	static public byte[] loadBytes(InputStream input) {
		try {
			BufferedInputStream bis = new BufferedInputStream(input);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int c = bis.read();
			while (c != -1) {
				out.write(c);
				c = bis.read();
			}
			return out.toByteArray();

		} catch (IOException e) {
			// .nah. e.printStackTrace();
			//throw new RuntimeException("Couldn't load bytes from stream");
		}
		return null;
	}

	/**
	 * @nowebref
	 */
	static public byte[] loadBytes(File file) {
		InputStream is = createInput(file);
		return loadBytes(is);
	}

	/**
	 * @nowebref
	 */
	static public String[] loadStrings(File file) {
		InputStream is = createInput(file);
		if (is != null) {
			String[] outgoing = loadStrings(is);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return outgoing;
		}
		return null;
	}

	/**
	 * ( begin auto-generated from loadStrings.xml )
	 *
	 * Reads the contents of a file or url and creates a String array of its
	 * individual lines. If a file is specified, it must be located in the
	 * sketch's "data" directory/folder.<br />
	 * <br />
	 * The filename parameter can also be a URL to a file found online. For
	 * security reasons, a Processing sketch found online can only download
	 * files from the same server from which it came. Getting around this
	 * restriction requires a <a
	 * href="http://wiki.processing.org/w/Sign_an_Applet">signed applet</a>.
	 * <br />
	 * If the file is not available or an error occurs, <b>null</b> will be
	 * returned and an error message will be printed to the console. The error
	 * message does not halt the program, however the null value may cause a
	 * NullPointerException if your code does not check whether the value
	 * returned is null.
	 * <br/> <br/>
	 * Starting with Processing release 0134, all files loaded and saved by the
	 * Processing API use UTF-8 encoding. In previous releases, the default
	 * encoding for your platform was used, which causes problems when files
	 * are moved to other platforms.
	 *
	 * ( end auto-generated )
	 *
	 * <h3>Advanced</h3>
	 * Load data from a file and shove it into a String array.
	 * <p>
	 * Exceptions are handled internally, when an error, occurs, an
	 * exception is printed to the console and 'null' is returned,
	 * but the program continues running. This is a tradeoff between
	 * 1) showing the user that there was a problem but 2) not requiring
	 * that all i/o code is contained in try/catch blocks, for the sake
	 * of new users (or people who are just trying to get things done
	 * in a "scripting" fashion. If you want to handle exceptions,
	 * use Java methods for I/O.
	 *
	 * @webref input:files
	 * @param filename name of the file or url to load
	 * @see PApplet#loadBytes(String)
	 * @see PApplet#saveStrings(String, String[])
	 * @see PApplet#saveBytes(String, byte[])
	 */
	public static String[] loadStrings(String filename) {
		InputStream is = createInput(filename);
		if (is != null) return loadStrings(is);

		System.err.println("The file \"" + filename + "\" " +
				"is missing or inaccessible, make sure " +
				"the URL is valid or that the file has been " +
				"added to your sketch and is readable.");
		return null;
	}

	/**
	 * @nowebref
	 */
	static public String[] loadStrings(InputStream input) {
		try {
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(input, "UTF-8"));
			return loadStrings(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	static public String[] loadStrings(BufferedReader reader) {
		try {
			String lines[] = new String[100];
			int lineCount = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (lineCount == lines.length) {
					String temp[] = new String[lineCount << 1];
					System.arraycopy(lines, 0, temp, 0, lineCount);
					lines = temp;
				}
				lines[lineCount++] = line;
			}
			reader.close();

			if (lineCount == lines.length) {
				return lines;
			}

			// resize array to appropriate amount for these lines
			String output[] = new String[lineCount];
			System.arraycopy(lines, 0, output, 0, lineCount);
			return output;

		} catch (IOException e) {
			e.printStackTrace();
			//throw new RuntimeException("Error inside loadStrings()");
		}
		return null;
	}



	//////////////////////////////////////////////////////////////

	// FILE OUTPUT


	/**
	 * ( begin auto-generated from createOutput.xml )
	 *
	 * Similar to <b>createInput()</b>, this creates a Java <b>OutputStream</b>
	 * for a given filename or path. The file will be created in the sketch
	 * folder, or in the same folder as an exported application.
	 * <br /><br />
	 * If the path does not exist, intermediate folders will be created. If an
	 * exception occurs, it will be printed to the console, and <b>null</b>
	 * will be returned.
	 * <br /><br />
	 * This function is a convenience over the Java approach that requires you
	 * to 1) create a FileOutputStream object, 2) determine the exact file
	 * location, and 3) handle exceptions. Exceptions are handled internally by
	 * the function, which is more appropriate for "sketch" projects.
	 * <br /><br />
	 * If the output filename ends with <b>.gz</b>, the output will be
	 * automatically GZIP compressed as it is written.
	 *
	 * ( end auto-generated )
	 * @webref output:files
	 * @param filename name of the file to open
	 * @see PApplet#createInput(String)
	 * @see PApplet#selectOutput()
	 */
	public OutputStream createOutput(String filename) {
		return createOutput(saveFile(filename));
	}

	/**
	 * @nowebref
	 */
	static public OutputStream createOutput(File file) {
		try {
			createPath(file);  // make sure the path exists
			FileOutputStream fos = new FileOutputStream(file);
			if (file.getName().toLowerCase().endsWith(".gz")) {
				return new GZIPOutputStream(fos);
			}
			return fos;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * ( begin auto-generated from saveStream.xml )
	 *
	 * Save the contents of a stream to a file in the sketch folder. This is
	 * basically <b>saveBytes(blah, loadBytes())</b>, but done more efficiently
	 * (and with less confusing syntax).<br />
	 * <br />
	 * When using the <b>targetFile</b> parameter, it writes to a <b>File</b>
	 * object for greater control over the file location. (Note that unlike
	 * some other functions, this will not automatically compress or uncompress
	 * gzip files.)
	 *
	 * ( end auto-generated )
	 *
	 * @webref output:files
	 * @param target name of the file to write to
	 * @param source location to read from (a filename, path, or URL)
	 * @see PApplet#createOutput(String)
	 */
	public boolean saveStream(String target, String source) {
		return saveStream(saveFile(target), source);
	}

	/**
	 * Identical to the other saveStream(), but writes to a File
	 * object, for greater control over the file location.
	 * <p/>
	 * Note that unlike other api methods, this will not automatically
	 * compress or uncompress gzip files.
	 */
	public boolean saveStream(File target, String source) {
		return saveStream(target, createInputRaw(source));
	}

	/**
	 * @nowebref
	 */
	public boolean saveStream(String target, InputStream source) {
		return saveStream(saveFile(target), source);
	}

	/**
	 * @nowebref
	 */
	static public boolean saveStream(File target, InputStream source) {
		File tempFile = null;
		try {
			File parentDir = target.getParentFile();
			// make sure that this path actually exists before writing
			createPath(target);
			tempFile = File.createTempFile(target.getName(), null, parentDir);
			FileOutputStream targetStream = new FileOutputStream(tempFile);

			saveStream(targetStream, source);
			targetStream.close();
			targetStream = null;

			if (target.exists()) {
				if (!target.delete()) {
					System.err.println("Could not replace " +
							target.getAbsolutePath() + ".");
				}
			}
			if (!tempFile.renameTo(target)) {
				System.err.println("Could not rename temporary file " +
						tempFile.getAbsolutePath());
				return false;
			}
			return true;

		} catch (IOException e) {
			if (tempFile != null) {
				tempFile.delete();
			}
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @nowebref
	 */
	static public void saveStream(OutputStream target,
			InputStream source) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(source, 16384);
		BufferedOutputStream bos = new BufferedOutputStream(target);

		byte[] buffer = new byte[8192];
		int bytesRead;
		while ((bytesRead = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}

		bos.flush();
	}


	/**
	 * ( begin auto-generated from saveBytes.xml )
	 *
	 * Opposite of <b>loadBytes()</b>, will write an entire array of bytes to a
	 * file. The data is saved in binary format. This file is saved to the
	 * sketch's folder, which is opened by selecting "Show sketch folder" from
	 * the "Sketch" menu.<br />
	 * <br />
	 * It is not possible to use saveXxxxx() functions inside a web browser
	 * unless the sketch is <a
	 * href="http://wiki.processing.org/w/Sign_an_Applet">signed applet</A>. To
	 * save a file back to a server, see the <a
	 * href="http://wiki.processing.org/w/Saving_files_to_a_web-server">save to
	 * web</A> code snippet on the Processing Wiki.
	 *
	 * ( end auto-generated )
	 *
	 * @webref output:files
	 * @param filename name of the file to write to
	 * @param data array of bytes to be written
	 * @see PApplet#loadStrings(String)
	 * @see PApplet#loadBytes(String)
	 * @see PApplet#saveStrings(String, String[])
	 */
	public static void saveBytes(String filename, byte[] data) {
		saveBytes(saveFile(filename), data);
	}


	/**
	 * @nowebref
	 * Saves bytes to a specific File location specified by the user.
	 */
	static public void saveBytes(File file, byte[] data) {
		File tempFile = null;
		try {
			File parentDir = file.getParentFile();
			tempFile = File.createTempFile(file.getName(), null, parentDir);

			OutputStream output = createOutput(tempFile);
			saveBytes(output, data);
			output.close();
			output = null;

			if (file.exists()) {
				if (!file.delete()) {
					System.err.println("Could not replace " + file.getAbsolutePath());
				}
			}

			if (!tempFile.renameTo(file)) {
				System.err.println("Could not rename temporary file " +
						tempFile.getAbsolutePath());
			}

		} catch (IOException e) {
			System.err.println("error saving bytes to " + file);
			if (tempFile != null) {
				tempFile.delete();
			}
			e.printStackTrace();
		}
	}


	/**
	 * @nowebref
	 * Spews a buffer of bytes to an OutputStream.
	 */
	static public void saveBytes(OutputStream output, byte[] data) {
		try {
			output.write(data);
			output.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//

	/**
	 * ( begin auto-generated from saveStrings.xml )
	 *
	 * Writes an array of strings to a file, one line per string. This file is
	 * saved to the sketch's folder, which is opened by selecting "Show sketch
	 * folder" from the "Sketch" menu.<br />
	 * <br />
	 * It is not possible to use saveXxxxx() functions inside a web browser
	 * unless the sketch is <a
	 * href="http://wiki.processing.org/w/Sign_an_Applet">signed applet</A>. To
	 * save a file back to a server, see the <a
	 * href="http://wiki.processing.org/w/Saving_files_to_a_web-server">save to
	 * web</A> code snippet on the Processing Wiki.<br/>
	 * <br/ >
	 * Starting with Processing 1.0, all files loaded and saved by the
	 * Processing API use UTF-8 encoding. In previous releases, the default
	 * encoding for your platform was used, which causes problems when files
	 * are moved to other platforms.
	 *
	 * ( end auto-generated )
	 * @webref output:files
	 * @param filename filename for output
	 * @param data string array to be written
	 * @see PApplet#loadStrings(String)
	 * @see PApplet#loadBytes(String)
	 * @see PApplet#saveBytes(String, byte[])
	 */
	public static void saveStrings(String filename, String data[]) {
		saveStrings(saveFile(filename), data);
	}


	/**
	 * @nowebref
	 */
	static public void saveStrings(File file, String data[]) {
		saveStrings(createOutput(file), data);
	}


	/**
	 * @nowebref
	 */
	static public void saveStrings(OutputStream output, String[] data) {
		PrintWriter writer = createWriter(output);
		for (int i = 0; i < data.length; i++) {
			writer.println(data[i]);
		}
		writer.flush();
		writer.close();
	}


	/**
	 * Returns a path inside the applet folder to save to. Like sketchPath(),
	 * but creates any in-between folders so that things save properly.
	 * <p/>
	 * All saveXxxx() functions use the path to the sketch folder, rather than
	 * its data folder. Once exported, the data folder will be found inside the
	 * jar file of the exported application or applet. In this case, it's not
	 * possible to save data into the jar file, because it will often be running
	 * from a server, or marked in-use if running from a local file system.
	 * With this in mind, saving to the data path doesn't make sense anyway.
	 * If you know you're running locally, and want to save to the data folder,
	 * use <TT>saveXxxx("data/blah.dat")</TT>.
	 */
	public static String savePath(String where) {
		if (where == null) return null;
		createPath(where);
		return where;
	}


	/**
	 * Identical to savePath(), but returns a File object.
	 */
	public static File saveFile(String where) {
		return new File(savePath(where));
	}

	/**
	 * Takes a path and creates any in-between folders if they don't
	 * already exist. Useful when trying to save to a subfolder that
	 * may not actually exist.
	 */
	static public void createPath(String path) {
		createPath(new File(path));
	}


	static public void createPath(File file) {
		try {
			String parent = file.getParent();
			if (parent != null) {
				File unit = new File(parent);
				if (!unit.exists()) unit.mkdirs();
			}
		} catch (SecurityException se) {
			System.err.println("You don't have permissions to create " +
					file.getAbsolutePath());
		}
	}


	//////////////////////////////////////////////////////////////

	// DATA I/O

	/**
	 * @webref input:files
	 * @param filename name of a file in the data folder or a URL.
	 * @see XML
	 * @see PApplet#parseXML(String)
	 * @see PApplet#saveXML(XML, String)
	 * @see PApplet#loadBytes(String)
	 * @see PApplet#loadStrings(String)
	 * @see PApplet#loadTable(String)
	 */
	public static XML loadXML(String filename) {
		return loadXML(filename, null);
	}


	// version that uses 'options' though there are currently no supported options
	/**
	 * @nowebref
	 */
	public static XML loadXML(String filename, String options) {
		try {
			return new XML(createReader(filename), options);
			//	      return new XML(createInput(filename), options);
		} catch (Exception e) {
			//e.printStackTrace();
			//System.err.println("Warning: " + e.getMessage());
			return null;
		}
	}


	/**
	 * @webref input:files
	 * @brief Converts String content to an XML object
	 * @param data the content to be parsed as XML
	 * @return an XML object, or null
	 * @see XML
	 * @see PApplet#loadXML(String)
	 * @see PApplet#saveXML(XML, String)
	 */
	public static XML parseXML(String xmlString) {
		return parseXML(xmlString, null);
	}


	public static XML parseXML(String xmlString, String options) {
		try {
			return XML.parse(xmlString, options);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * @webref output:files
	 * @param xml the XML object to save to disk
	 * @param filename name of the file to write to
	 * @see XML
	 * @see PApplet#loadXML(String)
	 * @see PApplet#parseXML(String)
	 */
	public static boolean saveXML(XML xml, String filename) {
		return saveXML(xml, filename, null);
	}


	public static boolean saveXML(XML xml, String filename, String options) {
		return xml.save(saveFile(filename), options);
	}















	/**
	 * ( begin auto-generated from split.xml )
	 *
	 * The split() function breaks a string into pieces using a character or
	 * string as the divider. The <b>delim</b> parameter specifies the
	 * character or characters that mark the boundaries between each piece. A
	 * String[] array is returned that contains each of the pieces.
	 * <br/> <br/>
	 * If the result is a set of numbers, you can convert the String[] array to
	 * to a float[] or int[] array using the datatype conversion functions
	 * <b>int()</b> and <b>float()</b> (see example above).
	 * <br/> <br/>
	 * The <b>splitTokens()</b> function works in a similar fashion, except
	 * that it splits using a range of characters instead of a specific
	 * character or sequence.
	 * <!-- /><br />
	 * This function uses regular expressions to determine how the <b>delim</b>
	 * parameter divides the <b>str</b> parameter. Therefore, if you use
	 * characters such parentheses and brackets that are used with regular
	 * expressions as a part of the <b>delim</b> parameter, you'll need to put
	 * two blackslashes (\\\\) in front of the character (see example above).
	 * You can read more about <a
	 * href="http://en.wikipedia.org/wiki/Regular_expression">regular
	 * expressions</a> and <a
	 * href="http://en.wikipedia.org/wiki/Escape_character">escape
	 * characters</a> on Wikipedia.
	 * -->
	 *
	 * ( end auto-generated )
	 * @webref data:string_functions
	 * @usage web_application
	 * @param value the String to be split
	 * @param delim the character or String used to separate the data
	 */
	static public String[] split(String value, char delim) {
		// do this so that the exception occurs inside the user's
		// program, rather than appearing to be a bug inside split()
		if (value == null) return null;
		//return split(what, String.valueOf(delim));  // huh

		char chars[] = value.toCharArray();
		int splitCount = 0; //1;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == delim) splitCount++;
		}
		// make sure that there is something in the input string
		//if (chars.length > 0) {
		// if the last char is a delimeter, get rid of it..
		//if (chars[chars.length-1] == delim) splitCount--;
		// on second thought, i don't agree with this, will disable
		//}
		if (splitCount == 0) {
			String splits[] = new String[1];
			splits[0] = new String(value);
			return splits;
		}
		//int pieceCount = splitCount + 1;
		String splits[] = new String[splitCount + 1];
		int splitIndex = 0;
		int startIndex = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == delim) {
				splits[splitIndex++] =
						new String(chars, startIndex, i-startIndex);
				startIndex = i + 1;
			}
		}
		//if (startIndex != chars.length) {
		splits[splitIndex] =
				new String(chars, startIndex, chars.length-startIndex);
		//}
		return splits;
	}


	static public String[] split(String value, String delim) {
		ArrayList<String> items = new ArrayList<String>();
		int index;
		int offset = 0;
		while ((index = value.indexOf(delim, offset)) != -1) {
			items.add(value.substring(offset, index));
			offset = index + delim.length();
		}
		items.add(value.substring(offset));
		String[] outgoing = new String[items.size()];
		items.toArray(outgoing);
		return outgoing;
	}


	static public boolean[] subset(boolean list[], int start) {
		return subset(list, start, list.length - start);
	}

	/**
	 * ( begin auto-generated from subset.xml )
	 *
	 * Extracts an array of elements from an existing array. The <b>array</b>
	 * parameter defines the array from which the elements will be copied and
	 * the <b>offset</b> and <b>length</b> parameters determine which elements
	 * to extract. If no <b>length</b> is given, elements will be extracted
	 * from the <b>offset</b> to the end of the array. When specifying the
	 * <b>offset</b> remember the first array element is 0. This function does
	 * not change the source array.
	 * <br/> <br/>
	 * When using an array of objects, the data returned from the function must
	 * be cast to the object array's data type. For example: <em>SomeClass[]
	 * items = (SomeClass[]) subset(originalArray, 0, 4)</em>.
	 *
	 * ( end auto-generated )
	 * @webref data:array_functions
	 * @param list array to extract from
	 * @param start position to begin
	 * @param count number of values to extract
	 * @see PApplet#splice(boolean[], boolean, int)
	 */
	static public boolean[] subset(boolean list[], int start, int count) {
		boolean output[] = new boolean[count];
		System.arraycopy(list, start, output, 0, count);
		return output;
	}

	static public byte[] subset(byte list[], int start) {
		return subset(list, start, list.length - start);
	}

	static public byte[] subset(byte list[], int start, int count) {
		byte output[] = new byte[count];
		System.arraycopy(list, start, output, 0, count);
		return output;
	}


	static public char[] subset(char list[], int start) {
		return subset(list, start, list.length - start);
	}

	static public char[] subset(char list[], int start, int count) {
		char output[] = new char[count];
		System.arraycopy(list, start, output, 0, count);
		return output;
	}

	static public int[] subset(int list[], int start) {
		return subset(list, start, list.length - start);
	}

	static public int[] subset(int list[], int start, int count) {
		int output[] = new int[count];
		System.arraycopy(list, start, output, 0, count);
		return output;
	}

	static public float[] subset(float list[], int start) {
		return subset(list, start, list.length - start);
	}

	static public float[] subset(float list[], int start, int count) {
		float output[] = new float[count];
		System.arraycopy(list, start, output, 0, count);
		return output;
	}


	static public String[] subset(String list[], int start) {
		return subset(list, start, list.length - start);
	}

	static public String[] subset(String list[], int start, int count) {
		String output[] = new String[count];
		System.arraycopy(list, start, output, 0, count);
		return output;
	}


	static public Object subset(Object list, int start) {
		int length = Array.getLength(list);
		return subset(list, start, length - start);
	}

	static public Object subset(Object list, int start, int count) {
		Class<?> type = list.getClass().getComponentType();
		Object outgoing = Array.newInstance(type, count);
		System.arraycopy(list, start, outgoing, 0, count);
		return outgoing;
	}

	/**
	 * ( begin auto-generated from concat.xml )
	 *
	 * Concatenates two arrays. For example, concatenating the array { 1, 2, 3
	 * } and the array { 4, 5, 6 } yields { 1, 2, 3, 4, 5, 6 }. Both parameters
	 * must be arrays of the same datatype.
	 * <br/> <br/>
	 * When using an array of objects, the data returned from the function must
	 * be cast to the object array's data type. For example: <em>SomeClass[]
	 * items = (SomeClass[]) concat(array1, array2)</em>.
	 *
	 * ( end auto-generated )
	 * @webref data:array_functions
	 * @param a first array to concatenate
	 * @param b second array to concatenate
	 * @see PApplet#splice(boolean[], boolean, int)
	 * @see PApplet#arrayCopy(Object, int, Object, int, int)
	 */
	static public boolean[] concat(boolean a[], boolean b[]) {
		boolean c[] = new boolean[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static public byte[] concat(byte a[], byte b[]) {
		byte c[] = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static public char[] concat(char a[], char b[]) {
		char c[] = new char[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static public int[] concat(int a[], int b[]) {
		int c[] = new int[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static public float[] concat(float a[], float b[]) {
		float c[] = new float[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static public String[] concat(String a[], String b[]) {
		String c[] = new String[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	static public Object concat(Object a, Object b) {
		Class<?> type = a.getClass().getComponentType();
		int alength = Array.getLength(a);
		int blength = Array.getLength(b);
		Object outgoing = Array.newInstance(type, alength + blength);
		System.arraycopy(a, 0, outgoing, 0, alength);
		System.arraycopy(b, 0, outgoing, alength, blength);
		return outgoing;
	}

	//////////////////////////////////////////////////////////////

	// CASTING FUNCTIONS, INSERTED BY PREPROC


	/**
	 * Convert a char to a boolean. 'T', 't', and '1' will become the
	 * boolean value true, while 'F', 'f', or '0' will become false.
	 */
	/*
	  static final public boolean parseBoolean(char what) {
	    return ((what == 't') || (what == 'T') || (what == '1'));
	  }
	 */

	/**
	 * <p>Convert an integer to a boolean. Because of how Java handles upgrading
	 * numbers, this will also cover byte and char (as they will upgrade to
	 * an int without any sort of explicit cast).</p>
	 * <p>The preprocessor will convert boolean(what) to parseBoolean(what).</p>
	 * @return false if 0, true if any other number
	 */
	static final public boolean parseBoolean(int what) {
		return (what != 0);
	}

	/*
	  // removed because this makes no useful sense
	  static final public boolean parseBoolean(float what) {
	    return (what != 0);
	  }
	 */

	/**
	 * Convert the string "true" or "false" to a boolean.
	 * @return true if 'what' is "true" or "TRUE", false otherwise
	 */
	static final public boolean parseBoolean(String what) {
		return new Boolean(what).booleanValue();
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/*
	  // removed, no need to introduce strange syntax from other languages
	  static final public boolean[] parseBoolean(char what[]) {
	    boolean outgoing[] = new boolean[what.length];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] =
	        ((what[i] == 't') || (what[i] == 'T') || (what[i] == '1'));
	    }
	    return outgoing;
	  }
	 */

	/**
	 * Convert a byte array to a boolean array. Each element will be
	 * evaluated identical to the integer case, where a byte equal
	 * to zero will return false, and any other value will return true.
	 * @return array of boolean elements
	 */
	/*
	  static final public boolean[] parseBoolean(byte what[]) {
	    boolean outgoing[] = new boolean[what.length];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] = (what[i] != 0);
	    }
	    return outgoing;
	  }
	 */

	/**
	 * Convert an int array to a boolean array. An int equal
	 * to zero will return false, and any other value will return true.
	 * @return array of boolean elements
	 */
	static final public boolean[] parseBoolean(int what[]) {
		boolean outgoing[] = new boolean[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = (what[i] != 0);
		}
		return outgoing;
	}

	/*
	  // removed, not necessary... if necessary, convert to int array first
	  static final public boolean[] parseBoolean(float what[]) {
	    boolean outgoing[] = new boolean[what.length];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] = (what[i] != 0);
	    }
	    return outgoing;
	  }
	 */

	static final public boolean[] parseBoolean(String what[]) {
		boolean outgoing[] = new boolean[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = new Boolean(what[i]).booleanValue();
		}
		return outgoing;
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	static final public byte parseByte(boolean what) {
		return what ? (byte)1 : 0;
	}

	static final public byte parseByte(char what) {
		return (byte) what;
	}

	static final public byte parseByte(int what) {
		return (byte) what;
	}

	static final public byte parseByte(float what) {
		return (byte) what;
	}

	/*
	  // nixed, no precedent
	  static final public byte[] parseByte(String what) {  // note: array[]
	    return what.getBytes();
	  }
	 */

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	static final public byte[] parseByte(boolean what[]) {
		byte outgoing[] = new byte[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = what[i] ? (byte)1 : 0;
		}
		return outgoing;
	}

	static final public byte[] parseByte(char what[]) {
		byte outgoing[] = new byte[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = (byte) what[i];
		}
		return outgoing;
	}

	static final public byte[] parseByte(int what[]) {
		byte outgoing[] = new byte[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = (byte) what[i];
		}
		return outgoing;
	}

	static final public byte[] parseByte(float what[]) {
		byte outgoing[] = new byte[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = (byte) what[i];
		}
		return outgoing;
	}

	/*
	  static final public byte[][] parseByte(String what[]) {  // note: array[][]
	    byte outgoing[][] = new byte[what.length][];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] = what[i].getBytes();
	    }
	    return outgoing;
	  }
	 */

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/*
	  static final public char parseChar(boolean what) {  // 0/1 or T/F ?
	    return what ? 't' : 'f';
	  }
	 */

	static final public char parseChar(byte what) {
		return (char) (what & 0xff);
	}

	static final public char parseChar(int what) {
		return (char) what;
	}

	/*
	  static final public char parseChar(float what) {  // nonsensical
	    return (char) what;
	  }

	  static final public char[] parseChar(String what) {  // note: array[]
	    return what.toCharArray();
	  }
	 */

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/*
	  static final public char[] parseChar(boolean what[]) {  // 0/1 or T/F ?
	    char outgoing[] = new char[what.length];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] = what[i] ? 't' : 'f';
	    }
	    return outgoing;
	  }
	 */

	static final public char[] parseChar(byte what[]) {
		char outgoing[] = new char[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = (char) (what[i] & 0xff);
		}
		return outgoing;
	}

	static final public char[] parseChar(int what[]) {
		char outgoing[] = new char[what.length];
		for (int i = 0; i < what.length; i++) {
			outgoing[i] = (char) what[i];
		}
		return outgoing;
	}

	/*
	  static final public char[] parseChar(float what[]) {  // nonsensical
	    char outgoing[] = new char[what.length];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] = (char) what[i];
	    }
	    return outgoing;
	  }

	  static final public char[][] parseChar(String what[]) {  // note: array[][]
	    char outgoing[][] = new char[what.length][];
	    for (int i = 0; i < what.length; i++) {
	      outgoing[i] = what[i].toCharArray();
	    }
	    return outgoing;
	  }
	 */

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	static final public int parseInt(boolean what) {
		return what ? 1 : 0;
	}

	/**
	 * Note that parseInt() will un-sign a signed byte value.
	 */
	static final public int parseInt(byte what) {
		return what & 0xff;
	}

	/**
	 * Note that parseInt('5') is unlike String in the sense that it
	 * won't return 5, but the ascii value. This is because ((int) someChar)
	 * returns the ascii value, and parseInt() is just longhand for the cast.
	 */
	static final public int parseInt(char what) {
		return what;
	}

	/**
	 * Same as floor(), or an (int) cast.
	 */
	static final public int parseInt(float what) {
		return (int) what;
	}

	/**
	 * Parse a String into an int value. Returns 0 if the value is bad.
	 */
	static final public int parseInt(String what) {
		return parseInt(what, 0);
	}

	/**
	 * Parse a String to an int, and provide an alternate value that
	 * should be used when the number is invalid.
	 */
	static final public int parseInt(String what, int otherwise) {
		try {
			int offset = what.indexOf('.');
			if (offset == -1) {
				return Integer.parseInt(what);
			} else {
				return Integer.parseInt(what.substring(0, offset));
			}
		} catch (NumberFormatException e) { }
		return otherwise;
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	static final public int[] parseInt(boolean what[]) {
		int list[] = new int[what.length];
		for (int i = 0; i < what.length; i++) {
			list[i] = what[i] ? 1 : 0;
		}
		return list;
	}

	static final public int[] parseInt(byte what[]) {  // note this unsigns
		int list[] = new int[what.length];
		for (int i = 0; i < what.length; i++) {
			list[i] = (what[i] & 0xff);
		}
		return list;
	}

	static final public int[] parseInt(char what[]) {
		int list[] = new int[what.length];
		for (int i = 0; i < what.length; i++) {
			list[i] = what[i];
		}
		return list;
	}

	static public int[] parseInt(float what[]) {
		int inties[] = new int[what.length];
		for (int i = 0; i < what.length; i++) {
			inties[i] = (int)what[i];
		}
		return inties;
	}

	/**
	 * Make an array of int elements from an array of String objects.
	 * If the String can't be parsed as a number, it will be set to zero.
	 *
	 * String s[] = { "1", "300", "44" };
	 * int numbers[] = parseInt(s);
	 *
	 * numbers will contain { 1, 300, 44 }
	 */
	static public int[] parseInt(String what[]) {
		return parseInt(what, 0);
	}

	/**
	 * Make an array of int elements from an array of String objects.
	 * If the String can't be parsed as a number, its entry in the
	 * array will be set to the value of the "missing" parameter.
	 *
	 * String s[] = { "1", "300", "apple", "44" };
	 * int numbers[] = parseInt(s, 9999);
	 *
	 * numbers will contain { 1, 300, 9999, 44 }
	 */
	static public int[] parseInt(String what[], int missing) {
		int output[] = new int[what.length];
		for (int i = 0; i < what.length; i++) {
			try {
				output[i] = Integer.parseInt(what[i]);
			} catch (NumberFormatException e) {
				output[i] = missing;
			}
		}
		return output;
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/*
	  static final public float parseFloat(boolean what) {
	    return what ? 1 : 0;
	  }
	 */

	/**
	 * Convert an int to a float value. Also handles bytes because of
	 * Java's rules for upgrading values.
	 */
	static final public float parseFloat(int what) {  // also handles byte
		return what;
	}

	static final public float parseFloat(String what) {
		return parseFloat(what, Float.NaN);
	}

	static final public float parseFloat(String what, float otherwise) {
		try {
			return new Float(what).floatValue();
		} catch (NumberFormatException e) { }

		return otherwise;
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	/*
	  static final public float[] parseFloat(boolean what[]) {
	    float floaties[] = new float[what.length];
	    for (int i = 0; i < what.length; i++) {
	      floaties[i] = what[i] ? 1 : 0;
	    }
	    return floaties;
	  }

	  static final public float[] parseFloat(char what[]) {
	    float floaties[] = new float[what.length];
	    for (int i = 0; i < what.length; i++) {
	      floaties[i] = (char) what[i];
	    }
	    return floaties;
	  }
	 */

	static final public float[] parseByte(byte what[]) {
		float floaties[] = new float[what.length];
		for (int i = 0; i < what.length; i++) {
			floaties[i] = what[i];
		}
		return floaties;
	}

	static final public float[] parseFloat(int what[]) {
		float floaties[] = new float[what.length];
		for (int i = 0; i < what.length; i++) {
			floaties[i] = what[i];
		}
		return floaties;
	}

	static final public float[] parseFloat(String what[]) {
		return parseFloat(what, Float.NaN);
	}

	static final public float[] parseFloat(String what[], float missing) {
		float output[] = new float[what.length];
		for (int i = 0; i < what.length; i++) {
			try {
				output[i] = new Float(what[i]).floatValue();
			} catch (NumberFormatException e) {
				output[i] = missing;
			}
		}
		return output;
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	static final public String str(boolean x) {
		return String.valueOf(x);
	}

	static final public String str(byte x) {
		return String.valueOf(x);
	}

	static final public String str(char x) {
		return String.valueOf(x);
	}

	static final public String str(int x) {
		return String.valueOf(x);
	}

	static final public String str(float x) {
		return String.valueOf(x);
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

	static final public String[] str(boolean x[]) {
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
		return s;
	}

	static final public String[] str(byte x[]) {
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
		return s;
	}

	static final public String[] str(char x[]) {
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
		return s;
	}

	static final public String[] str(int x[]) {
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
		return s;
	}

	static final public String[] str(float x[]) {
		String s[] = new String[x.length];
		for (int i = 0; i < x.length; i++) s[i] = String.valueOf(x[i]);
		return s;
	}



}
