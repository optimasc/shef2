package com.optimasc.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;


import org.apache.commons.codec.binary.Base64;

/** Represents an URL that contains a data: protocol as specified in RFC IETF
 *  2397.
 *  
 * <em>Note</em>: Currently only supports US-ASCII character sets when the
 * data is not base64 encoded. Implementation contains minimum implementation
 * to have it work with URL images.
 * 
 * To register and use the data protocol, do the following in your application:
 * <code>URL.setURLStreamHandlerFactory(new DataURLConnection.DataURLStreamHandlerFactory());</code>
 * 
 * @author Carl Eric Codere
 * 
 */
public class DataURLConnection extends URLConnection
{
  /** Contains the data contents record */
  protected DataInformation dataContents;
  
  /** Represents data URL information */
  public static class DataInformation
  {
    /** The MIME content type. */
    public String contentType;
    /** Data converted to bytes. */
    public byte[] data;

    public DataInformation(String contentType, byte[] data)
    {
      this.data = data;
      this.contentType = contentType;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
        return true;
      if (obj == null)
        return false;
      if ((obj instanceof DataInformation) == false)
        return false;
      if (Arrays.equals(data, ((DataInformation) obj).data) == false)
      {
        return false;
      }
      if (contentType.equals(((DataInformation) obj).contentType))
      {
        return false;
      }
      return true;
    }
  }

  public static class CustomURLStreamHandler extends URLStreamHandler
  {

    @Override
    protected URLConnection openConnection(URL url) throws IOException
    {
      return new DataURLConnection(url);
    }
  }

  public static class DataURLStreamHandlerFactory implements URLStreamHandlerFactory
  {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol)
    {
      if ("data".equals(protocol))
      {
        return new CustomURLStreamHandler();
      }

      return null;
    }
  }

  /**
   * Parses a data URI path information and sets up the values of the record
   * accordingly following IETF RFC 2397.
   * 
   * <em>Note</em>: Currently only supports US-ASCII character sets when the
   * data is not base64 encoded.
   * 
   * @param path The URL path.
   * @return The record information of type {@link #DataInformation}
   * @throws {@link java.lang.IllegalArgumentException} if the format is
   *         invalid.
   */
  public static DataInformation parseDataPath(String path)
  {
    boolean isBase64 = false;
    byte[] content;
    DataInformation dataContainer;
    int index;
    content = null;
    String mimeType = "text/plain;charset=US-ASCII";
    int commaSeparator = path.indexOf(',');
    if (commaSeparator == -1)
      throw new IllegalArgumentException("Missing data separator");
    String data = path.substring(commaSeparator + 1);
    String prefix = path.substring(0, commaSeparator);
    index = prefix.lastIndexOf(";base64");
    if (index == -1)
    {
      if (prefix.length() > 0)
      {
        mimeType = prefix;
      }
    } else
    {
      isBase64 = true;
      mimeType = prefix.substring(0, index);
    }
    /* Extract the data */
    if (isBase64 == true)
    {
      content = Base64.decodeBase64(data);
    } else
    {
      try
      {
        data = java.net.URLDecoder.decode(data, "US-ASCII");
        content = data.getBytes("US-ASCII");
      } catch (UnsupportedEncodingException e)
      {
        throw new IllegalArgumentException(e);
      }
    }
    dataContainer = new DataInformation(mimeType, content);
    return dataContainer;
  }

  protected DataURLConnection(URL url)
  {
    super(url);
    dataContents = parseDataPath(url.getPath());
  }

  @Override
  public void connect() throws IOException
  {
    connected = true;
  }

  @Override
  public int getContentLength()
  {
    return dataContents.data.length;
  }

  @Override
  public String getContentType()
  {
    return dataContents.contentType;
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return new ByteArrayInputStream(dataContents.data);
  }

}
