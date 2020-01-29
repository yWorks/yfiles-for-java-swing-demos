/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for Java (Swing) functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for Java (Swing) version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for Java (Swing) powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for Java (Swing)
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package style.svgnodestyle;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.GraphMLSharingPolicy;
import com.yworks.yfiles.graphml.XamlAttributeWritePolicy;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.svg.SVGDocument;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;

/**
 * Displays SVG documents when representing nodes.
 */
public class SVGNodeStyle extends AbstractNodeStyle {
  /**
   * Handles low-level rendering of SVG documents.
   */
  GraphicsNode painter;
  /**
   * Stores the textual representation of the displayed SVG document.
   */
  String definition;
  /**
   * Determines whether or not double-buffering should be used.
   * With double-buffering enabled, SVG documents are rendered into a temporary
   * bitmap. The bitmap is rendered on-screen whenever the corresponding
   * graph component requests a repaint. Rendering the bitmap is usually
   * substantially faster than rendering the original SVG document.
   */
  boolean doubleBuffering;

  /**
   * Initializes a new <code>SVGNodeStyle</code> with double-buffering enabled.
   */
  public SVGNodeStyle() {
    doubleBuffering = true;
  }

  /**
   * Clones this style instance.
   */
  public SVGNodeStyle clone() {
    return (SVGNodeStyle) super.clone();
  }

  /**
   * Creates a visual representation for the given node that displays this
   * style instance's associated SVG document.
   */
  protected IVisual createVisual(IRenderContext context, INode node) {
    if (isDoubleBufferingEnabled()) {
      return createImageVisual(context, node);
    } else {
      return createSvgVisual(context, node);
    }
  }

  /**
   * Updates the given visual for the given node to display the appropriately
   * scaled representation of this style instance's associated SVG document.
   */
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (isDoubleBufferingEnabled()) {
      return updateImageVisual(context, oldVisual, node);
    } else {
      return updateSvgVisual(context, oldVisual, node);
    }
  }

  /**
   * Returns the textual representation of the SVG document associated to this
   * style instance.
   */
  @GraphML(shareable = GraphMLSharingPolicy.ALWAYS, writeAsAttribute = XamlAttributeWritePolicy.ALWAYS)
  public String getDefinition() {
    return definition;
  }

  /**
   * Sets the textual representation of the SVG document associated to this
   * style instance.
   * Additionally, this method builds the low-level renderer for the SVG
   * document. This is a potentially expensive operation.
   */
  @GraphML(shareable = GraphMLSharingPolicy.ALWAYS, writeAsAttribute = XamlAttributeWritePolicy.ALWAYS)
  public void setDefinition(String definition) {
    String oldDefinition = this.definition;
    if (definition == null
        ? null != oldDefinition
        : !definition.equals(oldDefinition)) {
      this.definition = definition;

      painter = newGraphicsNode(null, definition);
    }
  }

  /**
   * Returns the low-level renderer for the SVG document associated to this
   * style instance.
   */
  GraphicsNode getPainter() {
    return painter;
  }

  /**
   * Returns the preferred size for the SVG document associated to this
   * style instance.
   */
  public SizeD getPreferredSize() {
    final GraphicsNode painter = getPainter();
    if (painter == null) {
      return new SizeD(1, 1);
    } else {
      Rectangle2D bounds = painter.getBounds();
      return new SizeD(bounds.getWidth(), bounds.getHeight());
    }
  }

  /**
   * Returns whether or not double-buffering is enabled.
   */
  public boolean isDoubleBufferingEnabled() {
    return doubleBuffering;
  }

  /**
   * Sets whether or not double-buffering is enabled.
   */
  public void setDoubleBufferingEnabled(boolean enabled) {
    doubleBuffering = enabled;
  }


  /**
   * Creates an image visual displaying this style instance's associated SVG
   * document.
   * This visual is used if {@link #isDoubleBufferingEnabled() double-buffering}
   * is enabled.
   */
  IVisual createImageVisual(IRenderContext context, INode node) {
    GraphicsNode painter = getPainter();
    if (painter == null) {
      return new ImageVisual(null, new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), node.getLayout());
    }

    IRectangle nl = node.getLayout();
    double zoom = context.getZoom();
    // determine the actual on-screen size of the given node
    // this is necessary to be able to render the image visual in view
    // coordinates (otherwise the bitmap would have to be scaled which would
    // result in both a performance hit and a loss in quality) 
    double width = nl.getWidth() * zoom;
    double height = nl.getHeight() * zoom;

    int w = Math.max(1, (int) Math.ceil(width));
    int h = Math.max(1, (int) Math.ceil(height));
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

    if (width > 0 && height > 0) {
      // render the style's SVG document onto the bitmap once
      Graphics2D g = image.createGraphics();
      g.setRenderingHint(
              RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(
              RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      Rectangle2D bounds = painter.getBounds();
      g.scale(width / bounds.getWidth(), height / bounds.getHeight());
      g.translate(-bounds.getX(), -bounds.getY());
      painter.paint(g);
      g.dispose();
    }

    return new ImageVisual(painter, image, nl);
  }

  /**
   * Updates an image visual if {@link #isDoubleBufferingEnabled()
   * double-buffering} is enabled.
   */
  IVisual updateImageVisual(IRenderContext context, IVisual oldVisual, INode node) {
    // determine if the given visual may be re-used
    if (oldVisual instanceof ImageVisual) {
      ImageVisual oldImageVisual = (ImageVisual) oldVisual;
      // check if the visual displays the correct SVG document
      if (getPainter() == oldImageVisual.getPainter()) {
        IRectangle nl = node.getLayout();
        double nw = nl.getWidth();
        double nh = nl.getHeight();
        IRectangle vl = oldImageVisual.getLayout();
        // check if the node size stayed the same
        if (nw == vl.getWidth() && nh == vl.getHeight()) {
          double zoom = context.getZoom();
          int w = (int) Math.ceil(nw * zoom);
          int h = (int) Math.ceil(nh * zoom);
          Image image = oldImageVisual.getImage();
          // check if the zoom level stayed the same
          if (image.getWidth(null) == w && image.getHeight(null) == h) {
            oldImageVisual.setLayout(nl);
            return oldVisual;
          }
        }
      }
    }

    // the given visual cannot be re-used, create a new one
    return createImageVisual(context, node);
  }

  /**
   * Creates an vector graphics visual displaying this style instance's
   * associated SVG document.
   * This visual is used if {@link #isDoubleBufferingEnabled() double-buffering}
   * is disabled.
   */
  IVisual createSvgVisual(IRenderContext context, INode node) {
    return new VectorVisual(getPainter(), node.getLayout());
  }

  /**
   * Updates an vector graphics visual if {@link #isDoubleBufferingEnabled()
   * double-buffering} is disabled.
   */
  IVisual updateSvgVisual(IRenderContext context, IVisual oldVisual, INode node) {
    // determine if the given visual may be re-used
    if (oldVisual instanceof VectorVisual) {
      // check if the visual displays the correct SVG document
      if (getPainter() == ((VectorVisual) oldVisual).getPainter()) {
        ((VectorVisual) oldVisual).setLayout(node.getLayout());
        return oldVisual;
      }
    }

    // the given visual cannot be re-used, create a new one
    return createSvgVisual(context, node);
  }

  /**
   * Builds the low-level renderer for the given SVG document.
   * This is a potentially expensive operation.
   */
  static GraphicsNode newGraphicsNode(String uri, String document) {
    try {
      UserAgentAdapter agent = new UserAgentAdapter();

      SVGDocumentFactory factory = new SAXSVGDocumentFactory(agent.getXMLParserClassName());
      SVGDocument doc = factory.createSVGDocument(uri, new StringReader(document));

      GVTBuilder builder = new GVTBuilder();
      return builder.build(new BridgeContext(agent), doc);
    } catch (IOException e) {
      return null;
    }
  }


  /**
   * Provides error visualization, geometric bounds, and low-level renderer
   * for visuals displaying SVG documents.
   */
  abstract static class AbstractSVGVisual implements IVisual {
    /**
     * Handles low-level rendering of SVG documents.
     */
    final GraphicsNode painter;
    /**
     * Geometric bounds of the displayed SVG document. 
     */
    IRectangle layout;

    /**
     * Initializes a new visual instance with the given painter and the given
     * bounds.
     */
    AbstractSVGVisual(GraphicsNode painter, IRectangle layout) {
      this.painter = painter;
      this.layout = layout;
    }

    /**
     * Paints a red, framed cross meant to signal that the SVG document could
     * not be properly visualized.
     */
    protected void paintError(IRenderContext context, Graphics2D g) {
      IRectangle nl = getLayout();
      if (nl != null) {
        double x = nl.getX();
        double y = nl.getY();
        double w = nl.getWidth();
        double h = nl.getHeight();
        Line2D.Double line = new Line2D.Double();

        Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(1));
        g.draw(new Rectangle2D.Double(x, y, w, h));
        line.setLine(x, y, x + w, y + h);
        g.draw(line);
        line.setLine(x + w, y, x, y + h);
        g.draw(line);

        g.setStroke(oldStroke);
        g.setPaint(oldPaint);
      }
    }

    /**
     * Returns the low-level renderer for the SVG document.
     */
    public GraphicsNode getPainter() {
      return painter;
    }

    /**
     * Returns the geometric bounds of the displayed SVG document. 
     */
    public IRectangle getLayout() {
      return layout;
    }

    /**
     * Sets the geometric bounds of the displayed SVG document. 
     */
    public void setLayout(IRectangle layout) {
      this.layout = layout;
    }
  }

  /**
   * Displays a bitmap representation of a SVG document.
   * This visual is used for best rendering performance.
   */
  public static class ImageVisual extends AbstractSVGVisual {
    /**
     * The bitmap representation of the SVG document.
     */
    final Image image;

    /**
     * Initializes a new <code>ImageVisual</code> instance for the given
     * low-level renderer, the given bitmap representation, and the given
     * geometric bounds.
     * For image visuals, the low-level renderer is only used to determine
     * whether or not a given visual displays the same SVG document as the
     * {@link SVGNodeStyle} instance updating the visual.
     */
    public ImageVisual(GraphicsNode painter, Image image, IRectangle layout ) {
      super(painter, layout);
      this.image = image;
    }

    /**
     * Paints the visual's image upon the given graphics context.
     */
    public void paint(IRenderContext context, Graphics2D g) {
      if (painter == null) {
        // something is wrong - display an error marker
        paintError(context, g);
      } else {
        IRectangle nl = getLayout();
        if (nl != null) {
          double sf = 1.0 / context.getZoom();
          AffineTransform oldTransform = g.getTransform();
          g.translate(nl.getX(), nl.getY());
          g.scale(sf, sf);
          g.drawImage(image, 0, 0, null);
          g.setTransform(oldTransform);
        }
      }
    }

    /**
     * Returns the bitmap representation of the SVG document.
     */
    public Image getImage() {
      return image;
    }
  }

  /**
   * Displays a SVG document using low-level rendering support.
   */
  public static class VectorVisual extends AbstractSVGVisual {
    /**
     * The visual bounds of the SVG document.
     */
    final Rectangle2D bounds;

    /**
     * Initializes a new <code>VectorVisual</code> instance for the given
     * low-level renderer and the given geometric bounds.
     * For vector visuals, the low-level renderer is called each and every
     * time the corresponding graph component requests a repaint.
     */
    public VectorVisual(GraphicsNode painter, IRectangle layout) {
      super(painter, layout);
      bounds = painter == null ? null: painter.getBounds();
    }

    /**
     * Calls upon the visual's low-level renderer to paint the SVG document
     * onto the given graphics context.
     */
    public void paint(IRenderContext context, Graphics2D g) {
      if (painter == null) {
        // something is wrong - display an error marker
        paintError(context, g);
      } else {
        IRectangle nl = getLayout();
        if (nl != null) {
          double x = nl.getX();
          double y = nl.getY();
          double width = nl.getWidth();
          double height = nl.getHeight();

          if (width > 0 && height > 0) {
            AffineTransform oldTransform = g.getTransform();

            g.translate(x, y);
            g.scale(width / bounds.getWidth(), height / bounds.getHeight());
            g.translate(-bounds.getX(), -bounds.getY());

            painter.paint(g);
            g.setTransform(oldTransform);
          }
        }
      }
    }
  }
}
