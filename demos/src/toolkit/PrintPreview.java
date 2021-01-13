/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package toolkit;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.CanvasPrintable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * A print preview panel that displays what a CanvasPrintable would print with the given settings.
 * Can be used to edit the PageFormat for printing and to issue the printing command.
 */
public class PrintPreview {

  /*
    Swing specific stuff
   */

  /**
   * The content pane for the preview panel.
   */
  private JPanel contentPane;

  /**
   * The widget that displays the preview for the printed pages.
   */
  private PagePanel pagePanel;

  /**
   * The JScrollPane which contains the pagePanel.
   */
  private JScrollPane pageScrollPane;

  /**
   * The JComboBox in the toolbar of the preview panel in which different zoom levels can be selected for the pagePanel.
   */
  private JComboBox<String> zoomBox;

  /*
    Printing specific stuff
   */

  /**
   * The PrinterJob for this printing process.
   */
  private PrinterJob printerJob;

  /**
   * The yFiles library class CanvasPrintable that actually does the printing.
   */
  private CanvasPrintable canvasPrintable;

  /**
   * The PageFormat to use currently for all pages.
   */
  private PageFormat pageFormat;

  /**
   * Creates a new instance of a PrintPreview.
   * @param canvasPrintable printable to be printed
   **/
  public PrintPreview(CanvasPrintable canvasPrintable) {
    this.printerJob = PrinterJob.getPrinterJob();
    this.pageFormat = new PageFormat();
    this.canvasPrintable = canvasPrintable;

    // setup the PrinterJob to use the correct printable, page format and to query the correct number of pages.
    configurePrinterJob();

    addContentPane();
  }

  /**
   * Configures the PrinterJob to use our printable and page format.
   */
  private void configurePrinterJob() {
    this.printerJob.setPageable(new Pageable() {
      @Override
      public int getNumberOfPages() {
        // query the number of pages from the CanvasPrintable with the current set PageFormat
        return canvasPrintable.pageCount(PrintPreview.this.getPageFormat());
      }

      @Override
      public PageFormat getPageFormat(final int pageIndex) throws IndexOutOfBoundsException {
        // delegate to the pageFormat property of the print preview
        return PrintPreview.this.getPageFormat();
      }

      @Override
      public Printable getPrintable(final int pageIndex) throws IndexOutOfBoundsException {
        return canvasPrintable;
      }
    });
  }

  /**
   * Returns the JPanel for this preview.
   */
  public JPanel getContentPane() {
    return contentPane;
  }

  /**
   * Initializes the content pane and builds the toolbar as well as
   * the paper panel that is the actual preview of the printed papers.
   */
  private void addContentPane() {
    // our main container is a common JPanel with a border layout.
    contentPane = new JPanel(new BorderLayout());

    // creates and configures the toolbar with buttons to change settings, zoom in and out and print.
    addToolbar();

    // create the preview that displays the papers that would be printed.
    addPaperPanel();
  }

  /**
   * Creates the toolbar that contains buttons to control the page panel and
   * to change settings like the PageFormat. Also contains the print button.
   */
  private void addToolbar() {
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));

    // an action that brings up the page dialog of the PrinterJob to adjust the page format
    toolbar.add(createButton("Adjust the page format", "Page Format...", e -> queryPageFormat()));

    // an action that issues the actual printing
    toolbar.add(createButton("Print with the current settings", "Print...", e -> print()));

    // an action to zoom into the preview panel
    toolbar.add(createButton("Zoom in", "Zoom In", e -> pagePanel.setZoom(pagePanel.getZoom() * 2)));

    // appropriately, an action to zoom out of the preview panel
    toolbar.add(createButton("Zoom out", "Zoom Out", e -> pagePanel.setZoom(pagePanel.getZoom() / 2)));

    // the combobox that contains various zoom levels and two special levels that fit the current displayed area.
    this.zoomBox = new JComboBox<>(new String[]{ "Fit", "Fit Width", "400%","300%","200%","100%","90%","80%","70%","60%","50%","25%","10%"});
    this.zoomBox.setMaximumSize(this.zoomBox.getPreferredSize());
    this.zoomBox.setToolTipText("Select a zoom level");
    toolbar.add(this.zoomBox);

    contentPane.add(toolbar, BorderLayout.NORTH);
  }

  /**
   * Creates a {@link javax.swing.JButton specific button}.
   * @param tooltip the tooltip to display for the button.
   * @param text    the text to print on the button.
   * @param action  the action to execute when the button is selected.
   */
  private JButton createButton(String tooltip, String text, ActionListener action) {
    JButton button = new JButton();
    button.setToolTipText(tooltip);
    button.setText(text);
    button.addActionListener(action);
    return button;
  }

  /**
   * Creates and configures the preview panel that displays the papers to be printed.
   */
  private void addPaperPanel() {
    JPanel paperPanel = new JPanel(new BorderLayout());
    paperPanel.setBorder(BorderFactory.createEtchedBorder());

    this.pagePanel = new PagePanel(this.canvasPrintable, this.pageFormat);
    // put the page panel into a scrollpane, it implements Scrollable
    this.pageScrollPane = new JScrollPane(this.pagePanel);
    paperPanel.add(pageScrollPane, BorderLayout.CENTER);

    this.pageScrollPane.setPreferredSize(new Dimension(400, 700));

    // wire up the combobox that contains the various zoom levels. when a zoom level is selected the page panel is updated.
    this.zoomBox.addActionListener(e -> onZoomChanged());

    this.contentPane.add(paperPanel, BorderLayout.CENTER);
  }

  /**
   * Called when an item in the combobox that contains various zoom levels is selected.
   */
  public void onZoomChanged() {
    if (zoomBox.getSelectedIndex() == 0) {
      // fit zoom option
      zoomToFit();
    } else if (zoomBox.getSelectedIndex() == 1) {
      // fit width zoom option
      zoomToFitWidth();
    } else {
      // parse the zoom of the string and update the page panel
      String text = zoomBox.getSelectedItem().toString().trim();
      text = text.substring(0, text.indexOf('%')).trim();
      double zoomPercent = Double.parseDouble(text);
      pagePanel.setZoom(zoomPercent / 100.0d);
    }
  }

  /**
   * Updates the content of the preview panel with the given CanvasComponent and area to print.
   */
  public void update(CanvasComponent componentToPrint, RectD printRectangle) {

    // update the printable with the new settings
    this.canvasPrintable.setCanvas(componentToPrint);
    this.canvasPrintable.setPrintRectangle(printRectangle);
    this.canvasPrintable.setScale(componentToPrint.getZoom());
    this.canvasPrintable.reset();

    // notify the page panel that something has has changed
    updatePagePanel();
  }

  /**
   * Updates the content of the preview panel with the given CanvasComponent and world points that shall be included
   * in the print bounds.
   */
  public void update(CanvasComponent componentToPrint, Iterable<PointD> printPoints) {

    // update the printable with the new settings
    this.canvasPrintable.setCanvas(componentToPrint);
    this.canvasPrintable.setPrintPoints(printPoints);
    this.canvasPrintable.setScale(componentToPrint.getZoom());
    this.canvasPrintable.reset();

    // notify the page panel that something has has changed
    updatePagePanel();
  }

  /**
   * Updates the page panel to the current values of the PageFormat, zoom and CanvasPrintable.
   */
  private void updatePagePanel() {
    this.pagePanel.updatePrintInfo();
    this.pagePanel.adjust();
  }

  /**
   * Returns the currently used {@link java.awt.print.PageFormat}
   */
  public PageFormat getPageFormat() {
    return this.pageFormat;
  }

  /**
   * Sets the new {@link java.awt.print.PageFormat} to be used in the Preview
   */
  public void setPageFormat(PageFormat newFormat) {
    if (newFormat != null && newFormat != this.pageFormat) {
      this.pageFormat = newFormat;
      this.pagePanel.setPageFormat(pageFormat);
      if (canvasPrintable.isPageMarkPrintingEnabled()) {
        canvasPrintable.setContentMargins(newPrintMarksMargins(pageFormat));
      }
      onPageFormatChanged();
    }
  }

  /**
   * Called when the page format was changed to update the preview with the new values.
   * This default implementation calls {@link #update(CanvasComponent, Iterable)}
   * with the values already present in the <code>canvasPrintable</code>
   * and repaints the content pane.
   */
  public void onPageFormatChanged() {
    // notify the page panel that something has has changed
    update(this.canvasPrintable.getCanvas(), this.canvasPrintable.getPrintPoints());
    this.contentPane.repaint();
  }

  /**
   * Returns whether or not page marks will be printed.
   * @return <code>true</code> if page marks will be printed and
   * <code>false</code> otherwise.
   */
  public boolean isPageMarkPrintingEnabled() {
    return canvasPrintable.isPageMarkPrintingEnabled();
  }

  /**
   * Specifies whether or not page marks have to be printed.
   * This method also updates the printable's content margins to reserve
   * space for print marks.
   * @param enabled if <code>true</code> page marks will be printed.
   */
  public void setPageMarkPrintingEnabled(boolean enabled) {
    if (enabled) {
      canvasPrintable.setPageMarkPrintingEnabled(true);
      canvasPrintable.setContentMargins(newPrintMarksMargins(getPageFormat()));
    } else {
      canvasPrintable.setPageMarkPrintingEnabled(false);
      canvasPrintable.setContentMargins(new InsetsD(0));
    }
  }

  /**
   * Zooms the document to make it fit the preview panel.
   */
  public void zoomToFit() {
    int width = this.pageScrollPane.getViewport().getWidth() - 3;
    int height = this.pageScrollPane.getViewport().getHeight() - 3;
    this.pagePanel.zoomToFit(width, height);
  }

  /**
   * Zooms the document to make its width fit the preview panel.
   */
  public void zoomToFitWidth() {
    int width = this.pageScrollPane.getViewport().getWidth() - 20;
    this.pagePanel.zoomToFitWidth(width);
  }

  /**
   * Pops up the page dialog of the PrinterJob to query a new PageFormat.
   */
  public void queryPageFormat() {
    PageFormat newPageFormat = printerJob.pageDialog(pageFormat);
    setPageFormat(newPageFormat);
  }

  /**
   * Starts the printing.
   */
  public void print() {
    if (printerJob.printDialog()) {
      try {
        printerJob.print();
      } catch (PrinterAbortException e) {
        // don't show an error because this exception is typically thrown when the user canceled printing
      } catch (PrinterException pe) {
        JOptionPane.showMessageDialog(contentPane,
            "Printing failed." + (pe.getMessage() != null ? " Reason: " + pe.getMessage() : ""),
            "Printing Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Creates insets that extend the margins of the given page format by half an
   * inch.
   */
  private static InsetsD newPrintMarksMargins(PageFormat page) {
    double top = page.getImageableY();
    double left = page.getImageableX();
    double bottom = page.getHeight() - top - page.getImageableHeight();
    double right = page.getWidth() - left - page.getImageableWidth();

    double markSize = 36; // Java print API works at 72 dpi
    return new InsetsD(top + markSize, left + markSize, bottom + markSize, right + markSize);
  }
}
