/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.imageexport;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.GraphViewerInputMode;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Use the export capabilities of the yFiles components, namely export to the bitmap formats PNG, JPEG, GIF
 * and BMP as well as drawing arbitrary objects onto the GraphComponent and interact with them via<code>
 * InputModes</code>.
 */
public class ImageExportDemo extends AbstractImageExportDemo {

  // displays the preview image for the export
  private CanvasComponent previewComponent;

  // image options
  private ImageFormat format = ImageFormat.JPG;
  private float quality = 1f;
  private boolean transparent = true;


  /**
   * Add the preview component to the tabbed pane
   */
  protected void configureTabbedPane(JTabbedPane tabbedPane) {
    super.configureTabbedPane(tabbedPane);
    // add a previewComponent on the second tab
    tabbedPane.addTab("Export Preview", previewComponent = new GraphComponent());
    previewComponent.setBackground(Color.GRAY);
    previewComponent.setInputMode(new GraphViewerInputMode());
  }

  /**
   * Adds options to the toolbar to configure the pixel image export.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(new JLabel(" Format: "));
    JCheckBox transparencyCheckBox = createCheckBox("Transparent", "Paint the background transparent?", transparent,
        e -> {
          transparent = ((JCheckBox) e.getSource()).isSelected();
          updatePreview();
        });
    JTextField compressionQualityTextField = createCompressionQualityTextField();
    toolBar.add(createFormatComboBox(transparencyCheckBox, compressionQualityTextField));
    toolBar.add(new JLabel(" Compression Quality: "));
    toolBar.add(compressionQualityTextField);
    toolBar.add(transparencyCheckBox);
  }

  /**
   * Creates a {@link javax.swing.JComboBox} to select an image format.
   * @param transparencyCheckBox enable if the selected image format supports transparency
   * @param compressionQualityTextField enable if the selected image format supports compression quality
   */
  private JComboBox<ImageFormat> createFormatComboBox(JCheckBox transparencyCheckBox, JTextField compressionQualityTextField) {
    JComboBox<ImageFormat> comboBox = new JComboBox<>(ImageFormat.values());
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Select the format to save the image as");
    comboBox.addActionListener(e -> {
      format = (ImageFormat) comboBox.getSelectedItem();
      transparencyCheckBox.setEnabled(format.supportsTransparency());
      compressionQualityTextField.setEnabled(format.isQualityAdjustable());
      updatePreview();
    });
    comboBox.setSelectedItem(format);
    return comboBox;
  }

  /**
   * Creates a {@link javax.swing.JTextField} to the compression quality of the encoding.
   */
  private JTextField createCompressionQualityTextField() {
    JTextField textField = new JTextField(Float.toString(quality));
    textField.setToolTipText("Enter the compression quality [0,1]");
    textField.setEnabled(format.isQualityAdjustable());
    textField.setMaximumSize(new Dimension(50, (int) textField.getPreferredSize().getHeight()));
    textField.addActionListener(e -> {
      updateCompressionQuality(textField);
      updatePreview();
    });
    // also call the update method when the focus was lost
    textField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        updateCompressionQuality(textField);
        updatePreview();
      }
    });
    return textField;
  }

  /**
   * Updates the setting for the compression quality with the values provided by the textfield.
   * @param textField the text field that yields the value.
   */
  private void updateCompressionQuality(JTextField textField) {
    try {
      float textFieldValue = Float.parseFloat(textField.getText());
      quality = Math.max(0, Math.min(1, textFieldValue));
    } catch (NumberFormatException e) {
      // couldn't convert the value from the text field - ignore
    }
  }

  /**
   * Update the export preview it the preview tab is selected.
   */
  protected void updatePreview() {
    if (tabbedPane.getSelectedIndex() == 0) {
      // if the current tab isn't the export preview, there is no need to update the preview
      // because when we change to the export preview later this will trigger an update anyway
      return;
    }
    // clear the preview
    ICanvasObjectGroup rootGroup = previewComponent.getRootGroup();
    while (rootGroup.getFirst() != null) {
      rootGroup.getFirst().remove();
    }

    // export the graph to an image and show it in the preview panel
    Image image = exportToImage();
    IVisual imageVisual = (context, gfx) -> gfx.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
    rootGroup.addChild(imageVisual, ICanvasObjectDescriptor.VISUAL);

    previewComponent.setZoom(1);
    previewComponent.setViewPoint(PointD.ORIGIN);
    previewComponent.repaint();
  }

  /**
   * Exports the graph or part of it to an image using the settings provided by the option panel.
   */
  private Image exportToImage() {
    // the component to export from
    GraphComponent component = getExportingGraphComponent();

    // create an exporter that exports the given region
    PixelImageExporter exporter = getPixelImageExporter();

    // export the image using the PixelImageExporter to a stream and
    // read it again to obtain the image that truly represents the output image
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    exportComponentToStream(component, exporter, output);
    byte[] data = output.toByteArray();
    ByteArrayInputStream input = new ByteArrayInputStream(data);

    BufferedImage image = null;
    try {
      image = ImageIO.read(input);
      output.close();
      input.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return image;
  }

  protected void saveToFile(String filename) {
    // append the correct file extension if it is missing
    boolean hasExtension = false;
    for (int i = 0; i < format.extensions.length; i++) {
      String extension = format.extensions[i];
      if (filename.endsWith(extension)) {
        hasExtension = true;
        break;
      }
    }
    if (!hasExtension) {
      filename += "." + format.canonicalExtension();
    }

    // the component to export from
    GraphComponent component = getExportingGraphComponent();

    // create an exporter that exports the given region
    PixelImageExporter exporter = getPixelImageExporter();

    try {
      FileOutputStream stream = new FileOutputStream(filename);
      exportComponentToStream(component, exporter, stream);
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Exports the image to the given stream using the <code>export</code> method of the {@link PixelImageExporter}.
   * @param component the component to export, see {@link #getExportingGraphComponent()}
   * @param exporter the {@link PixelImageExporter} that actually exports the data, see {@link #getPixelImageExporter()}
   * @param stream the stream to export to. Note that this method does not close the stream afterwards.
   */
  private void exportComponentToStream(GraphComponent component, PixelImageExporter exporter, OutputStream stream) {
    if (format.isQualityAdjustable()) {
      // adjust the compression quality for jpg format
      ImageWriter writer = ImageIO.getImageWritersByFormatName(format.canonicalExtension()).next();
      ImageWriteParam param = writer.getDefaultWriteParam();
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(quality);
      try  {
        exporter.export(component, stream, writer, param);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        writer.dispose();
      }
    } else {
      try  {
        exporter.export(component, stream, format.canonicalExtension());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Creates an exporter that exports the given region.
   */
  private PixelImageExporter getPixelImageExporter() {
    // create an exporter with the settings of the option panel
    ContextConfigurator configurator = createContextConfigurator();

    PixelImageExporter exporter = new PixelImageExporter(configurator);
    // check if the format is transparent PNG
    if (format.supportsTransparency() && transparent) {
      exporter.setBackgroundFill(Colors.TRANSPARENT);
      exporter.setUsingCanvasComponentBackgroundColorEnabled(false);
      exporter.setTransparencyEnabled(transparent);
    } else {
      exporter.setUsingCanvasComponentBackgroundColorEnabled(true);
      exporter.setTransparencyEnabled(false);
    }
    return exporter;
  }

  protected FileFilter getFileFilter() {
    return format.fileFilter();
  }

  /**
   * An enum for well-known image formats.
   */
  private enum ImageFormat {
    JPG("JPEG Files", false, true, "jpg", "jpeg", "jpe"),
    GIF("GIF Files", false, false, "gif"),
    PNG("PNG Files", true, false, "png"),
    BMP("Bitmap Files", false, false, "bmp");

    private String description;
    private boolean transparency;
    private boolean qualityAdjustable;
    private String[] extensions;
    private FileFilter fileFilter;

    /**
     * Initializes a new <code>ImageFormat</code> instance for a single image format.
     * @param description       a human-readable description of the image format.
     * @param transparency      whether or not the file format supports transparency
     * @param qualityAdjustable whether or not the quality of the encoding is adjustable
     * @param extensions        the file name extensions used by the image format. The first given extension should be
     *                          the canonical file name extension
     */
    ImageFormat(String description, boolean transparency, boolean qualityAdjustable, String... extensions) {
      this.description = description;
      this.transparency = transparency;
      this.qualityAdjustable = qualityAdjustable;
      this.extensions = extensions;
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
          return file.isDirectory() || accept(file.getName().toLowerCase());
        }
        private boolean accept(String fileName) {
          for (String extension : extensions) {
            if (fileName.endsWith("." + extension)) {
              return true;
            }
          }
          return false;
        }
      };
    }

    /**
     * Returns the canonical file name extension for the image format represented by this filter.
     */
    String canonicalExtension() {
      return extensions[0];
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

    /**
     * Determines whether or not the quality of the encoding is adjustable.
     */
    boolean isQualityAdjustable() {
      return qualityAdjustable;
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
      new ImageExportDemo().start();
    });
  }
}
