package org.jglrxavpok.lang;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * Need to be redone, pretty old :/
 */ 
public final class IO
{
	
	/**
	 * @param is
	 * @param os
	 * @param bufferLength
	 * @throws IOException
	 */
	public static OutputStream copy(InputStream is, OutputStream os, int bufferLength) throws IOException
	{
		int i = 0;
		byte[] buffer = new byte[bufferLength];
		while((i = is.read(buffer, 0, buffer.length)) != -1)
		{
			os.write(buffer,0,i);
		}
		return os;
	}
	
	public static OutputStream copy(InputStream in, String output) throws FileNotFoundException, IOException
	{
		return copy(in, new BufferedOutputStream(new FileOutputStream(output)), 65565);
	}
	
	public static void copy(InputStream in, OutputStream output) throws FileNotFoundException, IOException
	{
		copy(in, output, 65565);
	}
	
	public static String readString(InputStream in, String charset) throws UnsupportedEncodingException, IOException
	{
		return new String(read(in), charset);
	}
	
	public static byte[] read(InputStream in) throws IOException
	{
			byte[] buffer = new byte[65565];
			ByteArrayOutputStream ous = new ByteArrayOutputStream(buffer.length);
			int i = 0;
			while((i = in.read(buffer, 0, buffer.length)) != -1)
			{
				ous.write(buffer, 0, i);
			}
			ous.close();
			return ous.toByteArray();
	}
	
}
