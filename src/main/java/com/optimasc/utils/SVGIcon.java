package com.optimasc.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGIcon implements Icon
{
  protected GraphicsNode svgRoot = null;
  protected SVGDocument document;
  protected BridgeContext bridgeContext;
  
  public SVGIcon(URL url) throws IOException
  {
    
    String parser = XMLResourceDescriptor.getXMLParserClassName();
    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser, true);
    document = f.createSVGDocument(url.toString(),url.openStream());
    document.getRootElement().setAttributeNS(null, "overflow", "visible");
    org.apache.batik.bridge.UserAgent userAgent = new org.apache.batik.bridge.UserAgentAdapter();
    org.apache.batik.bridge.DocumentLoader loader = new org.apache.batik.bridge.DocumentLoader(userAgent);
    bridgeContext = new org.apache.batik.bridge.BridgeContext(userAgent, loader);
    bridgeContext.setDynamicState(org.apache.batik.bridge.BridgeContext.DYNAMIC);
    org.apache.batik.bridge.GVTBuilder builder = new org.apache.batik.bridge.GVTBuilder();
    svgRoot = builder.build(bridgeContext, document);    
  }

  /**
   * Method to paint the icon using Graphics2D. Note that the scaling factors
   * have nothing to do with the zoom operation, the scaling factors set the
   * size your icon relative to the other objects on your canvas.
   * 
   * @param g
   *          the graphics context used for drawing
   * 
   * @param svgRoot
   *          the graphics node object that contains the SVG icon information
   * 
   * @param x
   *          the X coordinate of the top left corner of the icon
   * 
   * @param y
   *          the Y coordinate of the top left corner of the icon
   * 
   * @param scaleX
   *          the X scaling to be applied to the icon before drawing
   * 
   * @param scaleY
   *          the Y scaling to be applied to the icon before drawing
   */
  private void paintSvgIcon(
      java.awt.Graphics2D g,
      int x, int y,
      double scaleX, double scaleY
      )
  {
    java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform(scaleX, 0.0, 0.0,
        scaleY, x, y);
    svgRoot.setTransform(transform);
    svgRoot.paint(g);
  }

  public int getIconHeight()
  {
    return (int) svgRoot.getPrimitiveBounds().getHeight();
  }

  public int getIconWidth()
  {
    return (int) svgRoot.getPrimitiveBounds().getWidth();
  }
  
  public SVGDocument getDocument()
  {
    return document;
  }

  public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y)
  {
    paintSvgIcon((java.awt.Graphics2D) g, x, y, 1, 1);
  }
  
  public Image getImage2(int width, int height) 
  {
    TranscoderInput input = new TranscoderInput(document);
    
    final BufferedImage[] imagePointer = new BufferedImage[1];
    
    ImageTranscoder t = new ImageTranscoder() {

      @Override
      public BufferedImage createImage(int w, int h) {
          return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      }


      @Override
      public void writeImage(BufferedImage image, TranscoderOutput out) 
          throws TranscoderException
      {
        imagePointer[0] = image;
      }
  };
     try
    {
       
       // Check without these hints, real width/height of input image, something
       // seems to be cut.
      t.addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(width));
      t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, new Float(height));
      t.transcode(input, null);
    } catch (TranscoderException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
     return imagePointer[0];
  }
  
  /**
   * Renders and returns the svg based image.
   *
   * @param width desired width, if it is less than or equal to 0 aspect
   * ratio is preserved and the size is determined by height.
   * @param height desired height, if it is less than or equal to 0 aspect
   * ratio is preserved and the size is determined by width.
   * @return image of the rendered svg.'
   * TODO: modify to also give a image that preserves aspects but matches
   *       width or height individually.
   */
  public Image getImage(int width, int height) {
      /* Adjusts the scale of the transformation below, if either width or
       * height is less than or equal to 0 the aspect ratio is preserved. */
      SVGSVGElement elt = document.getRootElement();
 /*     elt.setAttributeNS(null, "width", ""+width);
      elt.setAttributeNS(null, "height", ""+height);*/
      Rectangle2D bounds = svgRoot.getPrimitiveBounds();
      double scaleX, scaleY;
      if(width <= 0){
          scaleX = scaleY = height/bounds.getHeight();
          width = (int) (scaleX * bounds.getWidth());
      }else if(height <= 0){
          scaleX = scaleY = width/bounds.getWidth();
          height = (int) (scaleY * bounds.getHeight());
      }else{
          scaleX = width/bounds.getWidth();
          scaleY = height/bounds.getHeight();
      }
     
      // Paint svg into image buffer
      BufferedImage bufferedImage = new BufferedImage(width,
              height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

      // For a smooth graphic with no jagged edges or rastorized look.
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
              RenderingHints.VALUE_INTERPOLATION_BILINEAR);
     
      g2d.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING,
              RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);

      // Scale image to desired size
      AffineTransform usr2dev =
              new AffineTransform(scaleX, 0.0, 0.0, scaleY, 0.0, 0.0);
      g2d.transform(usr2dev);
      svgRoot.paint(g2d);

      // Cleanup and return image
      g2d.dispose();
      return bufferedImage;
  }   

}
