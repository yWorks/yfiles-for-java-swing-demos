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
package style.jcomponentstyle;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.styles.AbstractJComponentNodeStyle;
import com.yworks.yfiles.graph.styles.AbstractJComponentStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;

/**
 * Abstract node component that provides some factory methods as well as a callback to adjust the node size to
 * the component's preferred size.
 */
public abstract class ComponentNodeStyle extends AbstractJComponentNodeStyle {

  /**
   * The minimum width the node returns as preferred size.
   */
  private static final int MIN_WIDTH = 150;

  /**
   * Update the node size after the content of the text fields had been changed.
   */
  protected void updateNodeSize(IRenderContext context, INode node) {
    if (context != null) {
      // get the preferred size of the updated NodeJPanel
      SizeD newPreferredSize = getPreferredSize(context, node);

      // consider the minimum width for the panel
      double newWidth = Math.max(MIN_WIDTH, newPreferredSize.getWidth() + 10);
      double newHeight = newPreferredSize.getHeight();
      SizeD size = new SizeD(newWidth, newHeight);

      // set new bounds for the node keeping its center coordinates
      RectD newBounds = RectD.fromCenter(node.getLayout().getCenter(), size);
      ((GraphComponent) context.getCanvasComponent()).getGraph().setNodeLayout(node, newBounds);
    }
  }

  /**
   * Creates a JPanel using a GridBagLayout as well as a transparent background.
   */
  protected static JPanel createGridBagPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(Colors.TRANSPARENT);
    return panel;
  }

  /**
   * Configures formatted text fields for displaying text in zoomed in or zoomed out states.
   */
  protected static JFormattedTextField configureFormattedField(JFormattedTextField jftf, Font font, int horizontalAlignment) {

    // configure the text field's document for bidirectional text
    // this is done to leverage a little known side effect of bidirectional
    // text rendering: text components render bidirectional text with floating
    // point precision
    // floating point precision text rendering is important for proper
    // zooming of text components
    jftf.getDocument().putProperty("i18n", Boolean.TRUE);

    jftf.setBackground(Colors.TRANSPARENT);
    jftf.setFont(font);
    jftf.setHorizontalAlignment(horizontalAlignment);
    return jftf;
  }

  /**
   * Creates a LinearGradientPaint with vertical orientation using the specified colors.
   */
  protected static LinearGradientPaint createLinearGradient(final Color c1, final Color c2) {
    return new LinearGradientPaint(0, 0, 0, 80, new float[]{0, 0.3f, 1}, new Color[] {c1, c1, c2});
  }

  /**
   * A customized JPanel that uses a linear gradient as background paint.
   * <p>
   * The panel contains several child components that are used by subclasses to display
   * the business data encapsulated in {@link style.jcomponentstyle.Customer} and
   * {@link style.jcomponentstyle.Product} instances associated to nodes.
   * </p><p>
   * The panel listens for client property changes that are associated to property keys
   * {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle#STYLE_TAG_KEY},
   * {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle#IS_SELECTED_KEY}, and
   * {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle#IS_FOCUSED_KEY}.
   * The <code>STYLE_TAG_KEY</code> and <code>IS_SELECTED_KEY</code> keys
   * determine the panel's background gradient and border style while the
   * <code>IS_FOCUSED_KEY</code> key determines the background color of one of the child components.
   * </p>
   */
  static class NodeJPanel extends JPanel {

    /**
     * The key that can be used with {@link #addPropertyChangeListener(String, java.beans.PropertyChangeListener) addPropertyChangeListener}
     * to be notified about the changes of the 'id' property.
     */
    static final String ID = "node.id";

    /**
     * The key that can be used with {@link #addPropertyChangeListener(String, java.beans.PropertyChangeListener) addPropertyChangeListener}
     * to be notified about the changes of the 'header' property.
     */
    static final String HEADER = "node.header";

    /**
     * Initialize the borders used for selected and unselected node states.
     */
    static final Border selectedBorder = BorderFactory.createStrokeBorder(
        new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5), Color.BLACK);
    static final Border unselectedBorder = BorderFactory.createStrokeBorder(
        new BasicStroke(2), Colors.TRANSPARENT);

    /**
     * Initialize the fonts used for the child components
     */
    static final Font plainFont;
    static final Font smallPlainFont;

    static {
      Font font = new JLabel().getFont();
      plainFont = font.deriveFont(Font.PLAIN, 16);
      smallPlainFont = font.deriveFont(Font.PLAIN, 12);
    }

    /**
     * The background paints initialized using the {@link #getStyleTag() style tag} of the node.
     */
    private Paint unselectedBackground;
    private Paint selectedBackground;

    /**
     * The background paint actually used for rendering.
     */
    private Paint currentBackground;

    /**
     * The child components presenting the business data.
     */
    protected JFormattedTextField headerTextField;
    protected JFormattedTextField idTextField;

    /**
     * Creates a new NodeJPanel that initializes its child components using a GridBagLayout and adds child property
     * listener to style the background, border and the text fields.
     */
    public NodeJPanel() {
      super(new GridBagLayout());

      initializeChildren();

      addChildPropertyListener();
    }

    /**
     * Initializes the child components and adds them to the panel.
     */
    protected void initializeChildren() {

      // Header:
      // JFormattedTextField is used because of its property change event based value notification
      // the header text field displays the customer or product name
      headerTextField = configureFormattedField(new JFormattedTextField(), plainFont, SwingConstants.CENTER);
      headerTextField.addPropertyChangeListener("value", evt ->
          firePropertyChange(HEADER, evt.getOldValue(), headerTextField.getText()));

      // ID:
      // As the id description and id text field shall be in the same line, we use an idPanel to position them.
      JPanel idPanel = createGridBagPanel();
      JLabel idDescriptionLabel = new JLabel("Id:");
      idDescriptionLabel.setFont(smallPlainFont);

      // the id text field is an IntegerTextField that uses a NumberFormatter for Integer
      idTextField = configureFormattedField(new IntegerTextField(), smallPlainFont, SwingConstants.LEFT);
      idTextField.addPropertyChangeListener("value", evt -> {
        try {
          firePropertyChange(ID, evt.getOldValue(), Integer.valueOf(idTextField.getText()));
        } catch (NumberFormatException e) {
          firePropertyChange(ID, evt.getOldValue(), null);
        }
      });

      // add the components using a GridBagLayout
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.ipadx = 5;
      constraints.anchor = GridBagConstraints.WEST;
      constraints.weightx = 0;
      idPanel.add(idDescriptionLabel, constraints);

      constraints.gridx = 1;
      constraints.weightx = 1;
      idPanel.add(idTextField, constraints);

      constraints.gridx = 0;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.anchor = GridBagConstraints.CENTER;
      constraints.insets = new Insets(5, 3, 3, 3);
      this.add(headerTextField, constraints);

      constraints.gridy = 1;
      constraints.anchor = GridBagConstraints.WEST;
      constraints.fill = GridBagConstraints.NONE;
      constraints.insets = new Insets(3, 8, 3, 3);
      this.add(idPanel, constraints);
    }

    /**
     * Adds property change listeners listening for changes of different client properties.
     */
    protected void addChildPropertyListener() {

      // the border and background depends on the selection state of the node
      addPropertyChangeListener(AbstractJComponentStyle.IS_SELECTED_KEY,
          evt -> {
            boolean isSelected = (Boolean) evt.getNewValue();
            currentBackground = isSelected ? selectedBackground : unselectedBackground;
            this.setBorder(isSelected ? selectedBorder : unselectedBorder);
          });

      // the background paints are initialized using the style tag of the node which is set as
      // client property using the AbstractJComponentStyle.STYLE_TAG_KEY tag
      addPropertyChangeListener(AbstractJComponentStyle.STYLE_TAG_KEY,
          evt -> {
            if (evt.getNewValue() instanceof BackgroundPaints) {
              BackgroundPaints backgroundPaints = (BackgroundPaints) evt.getNewValue();
              unselectedBackground = backgroundPaints.getUnselectedBackground();
              selectedBackground = backgroundPaints.getSelectedBackground();
            }

            // per default use the unselected background paint
            currentBackground = unselectedBackground;
          });

      // the focus state of the node determines the background color of the header text field
      addPropertyChangeListener(AbstractJComponentStyle.IS_FOCUSED_KEY,
          evt -> {
            boolean isFocused = (Boolean) evt.getNewValue();
            headerTextField.setBackground(isFocused ? Colors.WHITE : Colors.TRANSPARENT);
          });
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      Paint oldPaint = g2d.getPaint();
      g2d.setPaint(currentBackground);
      g2d.fillRect(0, 0, getWidth(), getHeight());
      g2d.setPaint(oldPaint);
    }
  }

  /**
   * A text field using a formatter for Integer.class.
   * Its caret position handling is modified to keep the caret position when loosing and gaining focus.
   */
  static class IntegerTextField extends JFormattedTextField {
    public IntegerTextField() {
      super();
      setFormatter(createIntegerFormatter());
    }

    // last caret position before loosing the focus
    int lastCaretPosition;

    // flag indicating if a focus event of type FocusEvent.FOCUS_GAINED is processed
    boolean processingFocusGainedEvent;

    @Override
    protected void processFocusEvent(FocusEvent e) {
      if (e.getID() == FocusEvent.FOCUS_GAINED) {
        processingFocusGainedEvent = true;
      } else {
        // focus is lost, so remember the caret position
        lastCaretPosition = getCaretPosition();
      }
      super.processFocusEvent(e);
      if (e.getID() == FocusEvent.FOCUS_GAINED) {
        processingFocusGainedEvent = false;
      }
    }

    @Override
    public void setCaretPosition(int position) {
      // While processing focus gained events, the NumberFormatter is reinstalled and resets the caret position to 0.
      // We want to keep the caret position in this case, so we use the position stored
      // while processing the last focus lost event
      super.setCaretPosition(processingFocusGainedEvent ? lastCaretPosition : position);
    }

    private NumberFormatter createIntegerFormatter() {
      NumberFormat numberFormat = NumberFormat.getInstance();
      if (numberFormat instanceof DecimalFormat) {
        ((DecimalFormat) numberFormat).setGroupingSize(100);
      }
      final NumberFormatter intFormatter = new NumberFormatter(numberFormat);
      intFormatter.setValueClass(Integer.class);
      intFormatter.setAllowsInvalid(false);
      return intFormatter;
    }
  }
}
