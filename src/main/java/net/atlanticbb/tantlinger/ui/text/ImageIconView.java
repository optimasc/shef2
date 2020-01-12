package net.atlanticbb.tantlinger.ui.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.Segment;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.InlineView;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGSVGElement;

import com.optimasc.utils.SVGIcon;

/**
 * Represents an icon image view that is compatible with HTML 4.01 Strict /
 * XHTML 1.0 Strict attributes.
 * 
 * It supports the following attributes which are currently used:
 * <ul>
 * <li><code>alt</code> attribute used as tooltip and text to be presented.</li>
 * <li><code>width</code> attribute that supports both percentage and pixel
 * units.</li>
 * <li><code>height</code> attribute that supports pixel attributes
 * </ul>
 * 
 * When only width or height is set in pixels, the image is resized accordingly
 * to keep its aspect ratio.
 * 
 * It currently supports the following image formats:
 * <ul>
 * <li>image/png</li>
 * <li>image/jpeg</li>
 * <li>image/gif</li>
 * <li>text/svg+xml</li>
 * </ul>
 * 
 * @author Carl Eric Codère
 * 
 */
public class ImageIconView extends View
{
  /**
   * Property name for pending image icon
   */
  private static final String PENDING_IMAGE = "html.pendingImage";
  /**
   * Property name for missing image icon
   */
  private static final String MISSING_IMAGE = "html.missingImage";

  protected Icon icon;
  protected ImageIcon scaledImage;
  /** Expected image width in pixels after checking attributes. */
  protected int width;
  /** Expected image height in pixels after checking attributes. */
  protected int height;

  // Insets, obtained from the painter.
  protected short leftInset = 0;
  protected short rightInset = 0;
  protected short topInset = 0;
  protected short bottomInset = 0;
  protected View altView;
  protected org.apache.batik.gvt.GraphicsNode svgIcon = null; 

  private static final int DEFAULT_WIDTH = 38;
  private static final int DEFAULT_HEIGHT = 38;

  /**
   * ImageLabelView is used if the image can't be loaded, and the attribute
   * specified an alt attribute. It overriden a handle of methods as the text is
   * hardcoded and does not come from the document.
   */
  private class ImageLabelView extends InlineView
  {
    private Segment segment;
    private Color fg;

    ImageLabelView(Element e, String text)
    {
      super(e);
      reset(text);
    }

    public void reset(String text)
    {
      segment = new Segment(text.toCharArray(), 0, text.length());
    }

    public void paint(Graphics g, Shape a)
    {
      checkPainter();
      GlyphPainter painter = getGlyphPainter();

      if (painter != null)
      {
        g.setColor(getForeground());
        painter.paint(this, g, a, getStartOffset(), getEndOffset());
      }
    }

    public Segment getText(int p0, int p1)
    {
      if (p0 < 0 || p1 > segment.array.length)
      {
        throw new RuntimeException("ImageLabelView: Stale view");
      }
      segment.offset = p0;
      segment.count = p1 - p0;
      return segment;
    }

    public int getStartOffset()
    {
      return 0;
    }

    public int getEndOffset()
    {
      return segment.array.length;
    }

    public View breakView(int axis, int p0, float pos, float len)
    {
      // Don't allow a break
      return this;
    }

    public Color getForeground()
    {
      View parent;
      if (fg == null && (parent = getParent()) != null)
      {
        Document doc = getDocument();
        AttributeSet attr = parent.getAttributes();

        if (attr != null && (doc instanceof StyledDocument))
        {
          fg = ((StyledDocument) doc).getForeground(attr);
        }
      }
      return fg;
    }
  }

  public ImageIconView(Element elem)
  {
    super(elem);
    URL imageURL = HTMLUtils.getImageURL(elem.getAttributes(),(HTMLDocument)getDocument());
    if (imageURL != null)
    {
      String mimeType = HTMLUtils.getImageMIME(imageURL);
      if (mimeType != null)
      {
        if (
             (mimeType.startsWith("image/png")) ||
             (mimeType.startsWith("image/jpeg")) ||
             (mimeType.startsWith("image/gif"))
            )
        {
          icon = new ImageIcon(imageURL);
        } else
        if (mimeType.startsWith("image/svg+xml"))
        {
          try
          {
            icon = new SVGIcon(imageURL);
          } catch (IOException e)
          {
            e.printStackTrace();
            icon = null;
          }
        }
      } else
      {
        icon = null;
      }
    }
    String text = getAltText();

    if (text != null)
    {
      altView = new ImageLabelView(getElement(), text);
    }
  }

  public String getAltText()
  {
    AttributeSet a = getElement().getAttributes();
    if (a.isDefined(HTML.Attribute.ALT))
    {
      return a.getAttribute(HTML.Attribute.ALT).toString();
    }
    return "";
  }


  /**
   * Returns the <code>ALT</code> attribute text for the image element.
   * 
   */
  @Override
  public String getToolTipText(float x, float y, Shape allocation)
  {
    return getAltText();
  }

  /**
   * Returns the icon to use while in the process of loading the image.
   */
  public Icon getLoadingImageIcon()
  {
    return (Icon) UIManager.getLookAndFeelDefaults().get(PENDING_IMAGE);
  }

  @Override
  public float getAlignment(int axis)
  {
    switch (axis)
    {
      case View.Y_AXIS:
        return 1.0f;
      default:
        return super.getAlignment(axis);
    }
  }

  @Override
  public float getPreferredSpan(int axis)
  {
    String strWidth = null;
    String strHeight = null;
    Icon iconToResize = null;
    Image scaled = null;
    int newWidth = 0;
    Dimension d;
    int i;

    /* Possible existing choices for icon */
    iconToResize = icon;
    if (icon==null)
    {
      iconToResize = (Icon) UIManager.getLookAndFeelDefaults().get(MISSING_IMAGE);
    } 
    width = iconToResize.getIconWidth();
    height = iconToResize.getIconHeight();

    /* Calculate aspect ratio */
    AttributeSet a = getElement().getAttributes();
    if (a.isDefined(HTML.Attribute.WIDTH))
    {
      strWidth = a.getAttribute(HTML.Attribute.WIDTH).toString();
      /* Is this a percentage width ? */
      if (strWidth.lastIndexOf("%") >= 0)
      {
        strWidth = strWidth.substring(0, strWidth.lastIndexOf("%"));
        /* Get percentage value */
        i = Math.max(0, Integer.parseInt(strWidth));
        Container c = getContainer();
        Insets insets = c.getInsets();
        int containerWidth = c.getWidth() - (insets.left + insets.right);
        newWidth = (containerWidth * i) / 100;
        d = getImageSizeKeepAspectRatio(iconToResize, newWidth);
        width = d.width;
        height = d.height;
      } else
      {
        newWidth = Math.max(0, Integer.parseInt(strWidth));
        d = getImageSizeKeepAspectRatio(iconToResize, newWidth);
        width = d.width;
        height = d.height;
      }
    }
    if (a.isDefined(HTML.Attribute.HEIGHT))
    {
      strHeight = a.getAttribute(HTML.Attribute.HEIGHT).toString();
      /* Is this a percentage width ? */
      if (strHeight.lastIndexOf("%") >= 0)
      {
        /* Ignored! */
      } else
      {
        height = Math.max(0, Integer.parseInt(strHeight));
      }

    }

    if ((width > 0) && (height > 0) && (icon != null))
    {
      /* Only rescale if necessary */
      if ((scaledImage == null)
          ||
          ((scaledImage != null) && ((scaledImage.getIconHeight() != height) || (scaledImage
              .getIconWidth() != width))))
      {
        /* Fire that the preferences have changed. */
        preferenceChanged(null, true, true);


        
        if (iconToResize instanceof SVGIcon)
        {
          scaled = ((SVGIcon)iconToResize).getImage2(width, height);
        } else
        {
          BufferedImage bi = new BufferedImage(
              iconToResize.getIconWidth(),
              iconToResize.getIconHeight(),
              BufferedImage.TYPE_INT_ARGB);
          Graphics2D g = bi.createGraphics();
          // paint the Icon to the BufferedImage.
          iconToResize.paintIcon(null, g, 0, 0);
          g.dispose();

          scaled = bi.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        }
        scaledImage = new ImageIcon(scaled);
      }
    }

    switch (axis)
    {
      case View.X_AXIS:
        return width;
      case View.Y_AXIS:
        return height;
    }
    return 0;
  }

  /**
   * Returned a scaled image.
   * 
   * @param srcImg
   *          Source image to scale.
   * @param w
   *          New with in pixels.
   * @param h
   *          New height in pixels.
   * @return
   */
  protected Image getScaledImage(Image srcImg, int w, int h)
  {
    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = resizedImg.createGraphics();

    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(srcImg, 0, 0, w, h, null);
    g2.dispose();

    return resizedImg;
  }

  @Override
  public void paint(Graphics g, Shape allocation)
  {
    Rectangle alloc = allocation.getBounds();
    Rectangle clip = g.getClipBounds();

    if (clip != null)
    {
      g.clipRect(alloc.x + leftInset, alloc.y + topInset,
          alloc.width - leftInset - rightInset,
          alloc.height - topInset - bottomInset);
    }

    if (scaledImage != null)
    {
      scaledImage.paintIcon(getContainer(), g, alloc.x, alloc.y);
    } else
    {
      Icon icon = (Icon) UIManager.getLookAndFeelDefaults().get(MISSING_IMAGE);
      if (icon != null)
      {
        icon.paintIcon(getContainer(), g, alloc.x + leftInset,
            alloc.y + topInset);
      }
      View view = altView;
      // Paint the view representing the alt text, if its non-null
      if (view != null)
      {
        Rectangle altRect = new Rectangle
            (alloc.x + leftInset + DEFAULT_WIDTH, alloc.y + topInset,
                alloc.width - leftInset - rightInset - DEFAULT_WIDTH,
                alloc.height - topInset - bottomInset);

        view.paint(g, altRect);

      }
    }

    if (clip != null)
    {
      // Reset clip.
      g.setClip(clip.x, clip.y, clip.width, clip.height);
    }
  }

  @Override
  public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException
  {
    int p0 = getStartOffset();
    int p1 = getEndOffset();
    if ((pos >= p0) && (pos <= p1))
    {
      Rectangle r = a.getBounds();
      if (pos == p1)
      {
        r.x += r.width;
      }
      r.width = 0;
      return r;
    }
    return null;
  }

  @Override
  public int viewToModel(float x, float y, Shape a, Bias[] biasReturn)
  {
    Rectangle alloc = (Rectangle) a;
    if (x < alloc.x + (alloc.width / 2))
    {
      biasReturn[0] = Position.Bias.Forward;
      return getStartOffset();
    }
    biasReturn[0] = Position.Bias.Backward;
    return getEndOffset();
  }

  /**
   * Calculate new size of image according to maximum limits keeping aspect
   * ratio.
   * 
   * @param image
   *          The image that will be resized.
   * @param newWidth
   *          Maximum width of image in pixels.
   * @param maxHeight
   *          Maximum height of image in pixels.
   * @return Dimension image size.
   */
  public Dimension getImageSizeKeepAspectRatio(Icon image, int newWidth)
  {
    Dimension d = new Dimension();
    float imageWidth = image.getIconWidth();
    float imageHeight = image.getIconHeight();
    d.setSize(newWidth, Math.floor((imageHeight / imageWidth) * newWidth));
    return d;
  }

  @Override
  public void preferenceChanged(View child, boolean width, boolean height)
  {
    super.preferenceChanged(child, width, height);
  }

  @Override
  public void setSize(float width, float height)
  {
    super.setSize(width, height);
  }

  @Override
  public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f)
  {
    super.changedUpdate(e, a, f);
    // Assume the worst.
    preferenceChanged(null, true, true);
  }

}
