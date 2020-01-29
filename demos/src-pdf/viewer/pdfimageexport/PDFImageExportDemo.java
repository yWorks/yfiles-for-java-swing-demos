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
package viewer.pdfimageexport;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.RectangleIndicatorInstaller;
import com.yworks.yfiles.view.RectangleVisualTemplate;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.ps.EPSGraphics2D;
import viewer.imageexport.AbstractImageExportDemo;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Use the export capabilities of the yFiles components in combination with the FreeHEP Vectorgraphics framework to export to PDF, EPS or EMF files.
 * Draw arbitrary objects onto the GraphComponent and interact with them via <code>InputModes</code>.
 * <p>
 * The demo uses a patched version of the FreeHEP Vectorgraphics Toolkit available on the
 * <a href="http://www.yworks.com/resources/yfilesjava/demos-support/3.1/vectorgraphics.jar">yFiles website</a>.
 * </p>
 */
public class PDFImageExportDemo extends AbstractImageExportDemo {

  private boolean transparent = true;
  private ImageFormat format = ImageFormat.PDF;

  protected void initializeInputModes() {
    // The VectorGraphics toolkit doesn't support gradients or texture paints per default so we use a simpler
    // template for the default selection indication which is also used for our export rectangle

    // create a rectangle visual template with a dark gray pen
    RectangleVisualTemplate rectangleVisualTemplate = new RectangleVisualTemplate();
    rectangleVisualTemplate.setPen(new Pen(Colors.DARK_GRAY, 2));

    // set the template as client property for the SELECTION_TEMPLATE_KEY
    graphComponent.putClientProperty(RectangleIndicatorInstaller.SELECTION_TEMPLATE_KEY, rectangleVisualTemplate);

    super.initializeInputModes();
  }

  /**
   * Initializes the default node and edge style.
   * We override the method to set a simpler node style
   */
  protected void initializeGraphDefaults(IGraph graph) {
    super.initializeGraphDefaults(graph);

    // use a simpler node style without linear gradients as these are not supported by FreeHEP VectorGraphics
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setPaint(Colors.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graphComponent.setBackground(Colors.WHITE_SMOKE);
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
   * Adds a combo box to select the export format and a button to change the transparency of the output to the toolbar.
   */
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);

    toolBar.addSeparator();

    JCheckBox transparencyCheckBox = createCheckBox("Transparent", "Paint the background transparent?", transparent,
        e -> {
          transparent = ((JCheckBox) e.getSource()).isSelected();
          updatePreview();
        });
    toolBar.add(createFormatComboBox(transparencyCheckBox));
    toolBar.add(transparencyCheckBox);
  }

  /**
   * Creates a {@link javax.swing.JComboBox} to select an image format.
   */
  private JComboBox<ImageFormat> createFormatComboBox(JCheckBox transparencyCheckBox) {
    JComboBox<ImageFormat> comboBox = new JComboBox<>(ImageFormat.values());
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Select the format to save the image as");
    comboBox.addActionListener(e -> {
      format = (ImageFormat) comboBox.getSelectedItem();
      transparencyCheckBox.setEnabled(format.supportsTransparency());
      updatePreview();
    });
    comboBox.setSelectedItem(format);
    return comboBox;
  }

  protected void saveToFile(String filename) {
    // append the correct file extension if it is missing
    if (!filename.endsWith("." + format.extension)) {
      filename += "." + format.extension;
    }

    try (FileOutputStream stream = new FileOutputStream(filename)) {
      export(stream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void export(OutputStream os) {
    final ImageFormat currentFormat = format;
    final boolean currentTransparent = currentFormat.supportsTransparency() && transparent;

    final GraphComponent canvas = getExportingGraphComponent();
    final ContextConfigurator cnfg = createContextConfigurator();
    final Dimension size = new Dimension(cnfg.getViewWidth(), cnfg.getViewHeight());

    // create and initialize the VectorGraphics
    final VectorGraphics gfx;
    switch(currentFormat) {
      case EPS:
        gfx= createEpsGraphics(os, size);
        break;
      case EMF:
        gfx= createEmfGraphics(os, size);
        break;
      default:
        gfx = createPdfGraphics(os, size);
        break;
    }

    gfx.startExport();

    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    final Graphics2D graphics = (Graphics2D) gfx.create();
    try {
      // fill background
      Paint fill = currentTransparent ? Colors.TRANSPARENT : canvas.getBackground();
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

    gfx.endExport();
  }

  private EMFGraphics2D createEmfGraphics(OutputStream os, Dimension size) {
    EMFGraphics2D gfx = new EMFGraphics2D(os, size);
    gfx.setDeviceIndependent(true);
    return gfx;
  }

  private PDFGraphics2D createPdfGraphics(OutputStream os, Dimension size) {
    // create export properties
    Properties properties = new Properties();
    properties.putAll(PDFGraphics2D.getDefaultProperties());
    properties.setProperty(PDFGraphics2D.PAGE_SIZE, PDFGraphics2D.CUSTOM_PAGE_SIZE);
    properties.setProperty(PDFGraphics2D.CUSTOM_PAGE_SIZE, size.width + ", " + size.height);
    UserProperties.setProperty(properties, PDFGraphics2D.PAGE_MARGINS, new Insets(0, 0, 0, 0));
    UserProperties.setProperty(properties, PDFGraphics2D.FIT_TO_PAGE, false);

    PDFGraphics2D gfx = new PDFGraphics2D(os, size);
    gfx.setProperties(properties);
    return gfx;
  }

  private EPSGraphics2D createEpsGraphics( OutputStream os, Dimension size ) {
    // create export properties
    Properties properties = new Properties();
    properties.putAll(EPSGraphics2D.getDefaultProperties());
    properties.setProperty(EPSGraphics2D.PAGE_SIZE, EPSGraphics2D.CUSTOM_PAGE_SIZE);
    properties.setProperty(EPSGraphics2D.CUSTOM_PAGE_SIZE, size.width + ", " + size.height);
    UserProperties.setProperty(properties, EPSGraphics2D.PAGE_MARGINS, new Insets(0, 0, 0, 0));
    UserProperties.setProperty(properties, EPSGraphics2D.FIT_TO_PAGE, false);

    EPSGraphics2D gfx = new EPSGraphics2D(os, size);
    gfx.setProperties(properties);
    return gfx;
  }

  /**
   * Returns a file filter for the current file format.
   */
  protected FileFilter getFileFilter() {
    return format.fileFilter();
  }

  /**
   * An enum for well-known image formats.
   */
  private enum ImageFormat {
    PDF("PDF Files", true, "pdf"),
    EPS("EPS Files", false, "eps"),
    EMF("EMF Files", true, "emf");

    private String description;
    private boolean transparency;
    private String extension;
    private FileFilter fileFilter;

    /**
     * Initializes a new <code>ImageFormat</code> instance for a single image format.
     * @param description a human-readable description of the image format.
     */
    ImageFormat(String description, boolean transparency, String extension) {
      this.description = description;
      this.transparency = transparency;
      this.extension = extension;
      this.fileFilter = createFileFilter();
    }

    /**
     * Creates a {@link javax.swing.filechooser.FileFilter} for the image format.
     */
    private FileFilter createFileFilter() {
      return new FileFilter() {
        public String getDescription() {
          return description;
        }
        public boolean accept(File file) {
          return file.isDirectory() || file.getName().toLowerCase().endsWith("." + extension);
        }
      };
    }

    /**
     * Returns the canonical file name extension for the image format represented by this filter.
     */
    String canonicalExtension() {
      return extension;
    }

    /**
     * Returns a {@link javax.swing.filechooser.FileFilter} for the image format.
     */
    FileFilter fileFilter() {
      return fileFilter;
    }

    /**
     * Determines whether or not the file format supports transparency.
     */
    boolean supportsTransparency() {
      return transparency;
    }

    @Override
    public String toString() {
      return canonicalExtension().toUpperCase();
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new PDFImageExportDemo().start("PDF Image Export Demo - yFiles for Java (Swing)");
    });
  }
}
