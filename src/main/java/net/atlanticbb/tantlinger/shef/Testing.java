package net.atlanticbb.tantlinger.shef;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.codec.binary.Base64;

import com.optimasc.text.html.XHTMLWriter;
import com.optimasc.utils.DataURLConnection;
import com.optimasc.utils.DataURLConnection.DataInformation;

public class Testing
{
   public static final String paragraphs[] =
   {
     "This is sample text.",
     "This is bold/italic text.",
     "This is strikethrough text." 
   };
   
   public static final String htmlDocument = 
     "<html><head></head><body>" +
     "<p>This is sample text.</p> " +
     "<p><b><i>This is bold/italic text.</i></b><span style='text-decoration: line-through'>This is strikethrough text.</span></p>"+
     "</body></html>";
   
   
   protected void setStyleAttributes(MutableAttributeSet attrs)
   {
      
   }
   
   
   /** Tests the XHTML Writer */
   public static class CustomHTMLEditorKit extends HTMLEditorKit
   {

      @Override
      public void write(Writer out, Document doc, int offset, int len)
            throws IOException, BadLocationException
      {
         XHTMLWriter writer = new XHTMLWriter(out,doc);
         writer.write();         
      }
   }
   

   /** Tests the writer that using StyleConstants 
    * @throws BadLocationException 
    * @throws IOException */
   public static void testWriterStyleConstants() throws BadLocationException, IOException
   {
      StyledEditorKit kit = new CustomHTMLEditorKit();
      Document document = kit.createDefaultDocument();
      
      /* Inserts the string. */
      int offset = 0;
      int index = 0;
      
      /* 0 */
      
      document.insertString(offset,paragraphs[index], null);
      offset += paragraphs[index].length();
      index++;
      
      /* 1 */
      MutableAttributeSet set1 = new SimpleAttributeSet();
      StyleConstants.setBold(set1, true);
      StyleConstants.setItalic(set1, true);
      document.insertString(offset,paragraphs[index], set1);
      offset += paragraphs[index].length();
      index++;
      
      /* 2 */
      MutableAttributeSet set2 = new SimpleAttributeSet();
      StyleConstants.setStrikeThrough(set2, true);
      document.insertString(offset,paragraphs[index], set2);
      
      StringWriter out = new StringWriter();
      kit.write(out, document, 0, document.getLength());
      System.out.println(out.toString());
     
   }
   
   

   /** Tests the writer that using StyleConstants 
    * @throws BadLocationException 
    * @throws IOException */
   public static void testWriterHTMLTags() throws BadLocationException, IOException
   {
      StyledEditorKit kit = new CustomHTMLEditorKit();
//      StyledEditorKit kit = new HTMLEditorKit();
     
      Document document = kit.createDefaultDocument();
      MutableAttributeSet spanAttributes = null;
      
      /* Inserts the string. */
      int offset = 0;
      int index = 0;
      
      /* 0 */
      
      document.insertString(offset,paragraphs[index], null);
      offset += paragraphs[index].length();
      index++;
      
      /* 1 */
      MutableAttributeSet set1 = new SimpleAttributeSet();
      set1.addAttribute(StyleConstants.NameAttribute,HTML.Tag.CONTENT);
      set1.addAttribute(HTML.Tag.B, new SimpleAttributeSet());
      set1.addAttribute(HTML.Tag.I, new SimpleAttributeSet());
      document.insertString(offset,paragraphs[index], set1);
      offset += paragraphs[index].length();
      index++;
      
      /* 2 */
      MutableAttributeSet set2 = new SimpleAttributeSet();
      set2.addAttribute(StyleConstants.NameAttribute,HTML.Tag.CONTENT);
      spanAttributes = new SimpleAttributeSet();
      spanAttributes.addAttribute(CSS.Attribute.TEXT_DECORATION, "line-through");
      set2.addAttribute(HTML.Tag.SPAN, spanAttributes);
      document.insertString(offset,paragraphs[index], set2);

      StringWriter out = new StringWriter();
      kit.write(out, document, 0, document.getLength());
      System.out.println(out.toString());
      
      StringReader in = new StringReader(out.toString());
      kit.read(in, document, 0);
      ((AbstractDocument)document).dump(System.out);
     
   }  
   
   public static void testPreDefinedHTMLTags() throws BadLocationException, IOException
   {
     StyledEditorKit kit = new CustomHTMLEditorKit();
     StringReader in = new StringReader(htmlDocument);
     Document document = kit.createDefaultDocument();
     
     kit.read(in, document, 0);
     ((AbstractDocument)document).dump(System.out);
   }
   
   public static class CustomURLConnection extends URLConnection {

     protected CustomURLConnection(URL url) {
         super(url);
     }

     @Override
     public void connect() throws IOException {
         // Do your job here. As of now it merely prints "Connected!".
         System.out.println("Connected!");
     }

 }   
   
   
   
   
   
   public static String dataValues[] = 
   {
   "data:text/plain;charset=UTF-8;page=21,the%20data:1234,5678",
   "data:,",
   "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
   };

   public static final byte imagePNG[] =
  {(byte)0x89, (byte)0x50, (byte)0x4e, (byte)0x47, (byte)0x0d, (byte)0x0a, (byte)0x1a, (byte)0x0a, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0d, (byte)0x49, (byte)0x48, (byte)0x44, 
     (byte)0x52, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x8d, 
     (byte)0x6f, (byte)0x26, (byte)0xe5, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1c, (byte)0x49, (byte)0x44, (byte)0x41, (byte)0x54, (byte)0x08, (byte)0xd7, (byte)0x63, (byte)0xf8, 
     (byte)0xff, (byte)0xff, (byte)0x3f, (byte)0xc3, (byte)0x7f, (byte)0x06, (byte)0x20, (byte)0x05, (byte)0xc3, (byte)0x20, (byte)0x12, (byte)0x84, (byte)0xd0, (byte)0x31, (byte)0xf1, 
     (byte)0x82, (byte)0x58, (byte)0xcd, (byte)0x04, (byte)0x00, (byte)0x0e, (byte)0xf5, (byte)0x35, (byte)0xcb, (byte)0xd1, (byte)0x8e, (byte)0x0e, (byte)0x1f, (byte)0x00, (byte)0x00, 
     (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4e, (byte)0x44, (byte)0xae, (byte)0x42, (byte)0x60, (byte)0x82};
           
 
   public static DataInformation dataResults[] =
  {
    new DataInformation("text/plain;charset=UTF-8","the data:1234,5678".getBytes()), 
    new DataInformation("text/plain;charset=US-ASCII","".getBytes()),
    new DataInformation("image/png",imagePNG)
  };
   
   public static void testDataURL()
   {
     DataInformation data;
     for (int i=0; i < dataValues.length; i++)
     {
       data = DataURLConnection.parseDataPath(dataValues[i]);
       if (data.equals(dataResults[i])==false)
       {
         System.err.println("ERror!");
       }
     }
   }
   
   public static void main(String[] args) throws BadLocationException, IOException
   {
//      testWriterHTMLTags();
      // testWriterStyleConstants()
     //testPreDefinedHTMLTags();
     URL.setURLStreamHandlerFactory(new DataURLConnection.DataURLStreamHandlerFactory());
     URL url = new URL("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRUggg==");
     System.out.println(url.getProtocol());
     System.out.println(url.getHost());
     System.out.println(url.getPath());
     System.out.println(url.getFile());
     System.out.println(url.getUserInfo());
     testDataURL();
   }

}
