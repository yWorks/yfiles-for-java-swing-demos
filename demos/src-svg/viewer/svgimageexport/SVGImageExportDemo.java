/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.svgimageexport;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ContextConfigurator;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import viewer.imageexport.AbstractImageExportDemo;

import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Use the export capabilities of the yFiles components in combination with the Batik framework to export to SVG files.
 * Draw arbitrary objects onto the GraphComponent and interact with them via <code>InputModes</code>.
 * <p>
 * The demo uses a patched version of the Batik SVG library available on the
 * <a href="https://www.yworks.com/resources/yfilesjava/demos-support/3.6/batik.jar">yFiles website</a>.
 * </p>
 */
public class SVGImageExportDemo extends AbstractImageExportDemo {

  private boolean transparent = true;
  private JSVGCanvas previewCanvas = new InteractiveSVGCanvas();

  /**
   * Adds the preview canvas to the tabbed pane.
   */
  protected void configureTabbedPane(JTabbedPane tabbedPane) {
    super.configureTabbedPane(tabbedPane);

    // add a JSVGCanvas as SVG preview on the second tab
    tabbedPane.addTab("Preview", new JSVGScrollPane(previewCanvas));
    previewCanvas.setBackground(Color.GRAY);
  }

  /**
   * Adds a button to the toolbar to change the transparency of the SVG output.
   */
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    JCheckBox transparencyCheckBox = createCheckBox("Transparent", "Paint the background transparent?", transparent,
        e -> {
          transparent = ((JCheckBox) e.getSource()).isSelected();
          updatePreview();
        });
    toolBar.add(transparencyCheckBox);
  }

  /**
   * Enlarges the initial export rectangle to prevent it from being "tight".
   * Otherwise, bottom and right borders of nodes might be cut off when
   * exporting with the initial state of this demo.
   */
  protected void initializeGraph() {
    super.initializeGraph();

    exportRect.reshape(
            exportRect.getX() -1,
            exportRect.getY() -1,
            exportRect.getWidth() +5,
            exportRect.getHeight() +5);
  }

  /**
   * Updates the export preview if the preview tab is selected.
   */
  protected void updatePreview() {
    if (tabbedPane.getSelectedIndex() == 0) {
      // if the current tab isn't the export preview, there is no need to update the preview
      // because when we change to the export preview later this will trigger an update anyway
      return;
    }

    // export the canvas content to an SVG Element and update the previewCanvas
    Element svgRoot = exportToSVGElement();
    previewCanvas.setSVGDocument((SVGDocument) svgRoot.getOwnerDocument());
  }

  protected void saveToFile(String filename) {
    // append the correct file extension if it is missing
    if (!filename.endsWith(".svg")) {
      filename += ".svg";
    }

    // export to an SVG element
    Element svgRoot = exportToSVGElement();
    DocumentFragment svgDocumentFragment = svgRoot.getOwnerDocument().createDocumentFragment();
    svgDocumentFragment.appendChild(svgRoot);

    // write the SVG Document into the specified file
    try (FileOutputStream stream = new FileOutputStream(filename)) {
      OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
      writeDocument(svgDocumentFragment, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Element exportToSVGElement() {
    // Create a SVG document.
    DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);

    // Create a converter for this document.
    SVGGraphics2D svgGraphics2D = new SVGGraphics2D(doc);

    // paint the content of the exporting graph component to the Graphics object
    CanvasComponent canvas = getExportingGraphComponent();
    ((Graphics2D) svgGraphics2D).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    final ContextConfigurator cnfg = createContextConfigurator();
    final Graphics2D graphics = (Graphics2D) ((Graphics2D) svgGraphics2D).create();
    try {
      // fill background
      Paint fill = transparent ? Colors.TRANSPARENT : canvas.getBackground();
      if (fill != null) {
        final Paint oldPaint = graphics.getPaint();
        graphics.setPaint(fill);
        graphics.fill(new Rectangle2D.Double(0, 0, cnfg.getViewWidth(), cnfg.getViewHeight()));
        graphics.setPaint(oldPaint);
      }

      // configure the Graphics transform
      final InsetsD margins = cnfg.getMargins();
      graphics.translate(margins.getLeft(), margins.getTop());
      IRenderContext paintContext = cnfg.createRenderContext(canvas);
      graphics.transform(paintContext.getToWorldTransform());

      // set the graphics clip
      final RectD clip = paintContext.getClip();
      if (clip != null) {
        graphics.clip(new Rectangle2D.Double(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight()));
      }

      // export the canvas content
      canvas.exportContent(paintContext).paint(paintContext, graphics);
    } finally {
      graphics.dispose();
    }

    svgGraphics2D.dispose();
    Element svgRoot = svgGraphics2D.getRoot(doc.getDocumentElement());
    svgRoot.setAttributeNS(
      "http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
    svgRoot.setAttribute("width", "" + cnfg.getViewWidth());
    svgRoot.setAttribute("height", "" + cnfg.getViewHeight());
    return svgRoot;
  }

  private void writeDocument(DocumentFragment svgDocument, Writer writer) throws IOException {
    try {
      // Prepare the DOM document for writing
      Source source = new DOMSource(svgDocument);
      Result result = new StreamResult(writer);

      // Write the DOM document to the file
      TransformerFactory tf = TransformerFactory.newInstance();
      try {
        tf.setAttribute("indent-number", 2);
      } catch (IllegalArgumentException iaex) {
        iaex.printStackTrace();
      }
      Transformer xformer = tf.newTransformer();
      xformer.setOutputProperty(OutputKeys.INDENT, "yes");
      xformer.transform(source, result);
    } catch (TransformerException e) {
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Returns a file filter for SVG files.
   */
  protected FileFilter getFileFilter() {
    return SVGFileFilter.INSTANCE;
  }

  /**
   * A file filter for SVG files which accepts directories or file names ending with ".svg".
   */
  private static class SVGFileFilter extends FileFilter {

    public static SVGFileFilter INSTANCE = new SVGFileFilter();

    public boolean accept(File file) {
      return file.isDirectory() || file.getName().toLowerCase().endsWith(".svg");
    }

    public String getDescription() {
      return "SVG Files";
    }
  }

  /**
   * An interactive SVG Canvas that supports panning and zooming.
   */
  private static class InteractiveSVGCanvas extends JSVGCanvas {
    InteractiveSVGCanvas() {
      setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

      setEnableImageZoomInteractor(false);
      setEnablePanInteractor(false);
      setEnableResetTransformInteractor(false);
      setEnableRotateInteractor(false);
      setEnableZoomInteractor(false);

      panInteractor = new AbstractPanInteractor() {
        public boolean startInteraction(InputEvent e) {
          return e.getID() == MouseEvent.MOUSE_PRESSED &&
                 (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
        }
      };

      setEnablePanInteractor(true);

      // support mouse wheel zooming
      addMouseWheelListener(new MouseWheelListener() {
        public void mouseWheelMoved(final MouseWheelEvent e) {
          double scale = 1 - e.getUnitsToScroll() / 10.0;
          double tx = -e.getX() * (scale - 1);
          double ty = -e.getY() * (scale - 1);
          AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
          at.scale(scale, scale);
          updateCanvasTransform(at);
          e.consume();
        }
      });
    }

    void updateCanvasTransform(final AffineTransform at) {
      UpdateManager updateManager = getUpdateManager();
      if (updateManager != null) {
        updateManager.getUpdateRunnableQueue().invokeLater(() -> {
          at.concatenate(getRenderingTransform());
          setRenderingTransform(at);
        });
      }
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SVGImageExportDemo().start("SVG Image Export Demo - yFiles for Java (Swing)");
    });
  }
}
