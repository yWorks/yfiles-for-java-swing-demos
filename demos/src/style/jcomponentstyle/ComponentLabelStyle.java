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
package style.jcomponentstyle;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.styles.AbstractJComponentLabelStyle;
import com.yworks.yfiles.graph.styles.AbstractJComponentStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.ILabel;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import java.awt.Stroke;
import java.beans.PropertyChangeListener;

/**
 * Label component that displays the business data of a relation.
 */
public class ComponentLabelStyle extends AbstractJComponentLabelStyle {

  /**
   * Creates the component representing a label.
   * <p>
   * This implementation returns a customized JPanel that contains several JLabels showing the relation's business data.
   * </p>
   * @param ctx The context for which the component should be created.
   * @param label The label that will be rendered.
   */
  @Override
  public JComponent createComponent(final IRenderContext ctx, final ILabel label) {
    // create the customized JPanel that uses a rounded rectangle as shape and a gradient paint as background.
    final LabelJPanel panel = new LabelJPanel();

    // create the child JLabels that show information about the relation's business data
    JLabel labelText = new JLabel();
    JLabel customerName = new JLabel();
    JLabel arrow = new JLabel("->");
    JLabel productName = new JLabel();

    Font font = labelText.getFont();
    Font big = font.deriveFont(Font.PLAIN, 12);
    Font small = font.deriveFont(Font.PLAIN, 6);

    labelText.setFont(big);
    customerName.setFont(small);
    arrow.setFont(small);
    productName.setFont(small);

    // define a change listener that updates the label texts and the preferred side based on the relation's business data
    PropertyChangeListener updateLabels = (evt) -> {
      Relation relation = (Relation) label.getTag();
      if (relation != null) {
        customerName.setText(relation.getCustomer().getName());
        productName.setText(relation.getProduct().getName());
        labelText.setText(relation.toString());

        // adjust the label size
        if (ctx != null && ((GraphComponent) ctx.getCanvasComponent()).getGraph().contains(label)) {
          ((GraphComponent) ctx.getCanvasComponent()).getGraph().adjustLabelPreferredSize(label);
        }
      }
    };

    // register the change listener to update the label when the relation's business data changes
    panel.addPropertyChangeListener(AbstractJComponentStyle.USER_TAG_KEY,
        evt -> {
          // initialize the label texts
          updateLabels.propertyChange(evt);

          // listen to changes of the names and ids of the relation's customer and product
          final Relation relation = (Relation) evt.getNewValue();
          relation.getCustomer().addPropertyChangeListener(Customer.NAME, updateLabels);
          relation.getCustomer().addPropertyChangeListener(Customer.ID, updateLabels);
          relation.getProduct().addPropertyChangeListener(Product.NAME, updateLabels);
          relation.getProduct().addPropertyChangeListener(Product.ID, updateLabels);
        });

    // when the label gets selected, a black border is used
    panel.addPropertyChangeListener(AbstractJComponentStyle.IS_SELECTED_KEY,
        evt -> {
          boolean isSelected = (Boolean) evt.getNewValue();
          panel.setBorderColor(isSelected ? Colors.BLACK : Colors.TRANSPARENT);
        });

    // add child components to the panel
    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.SOUTH;
    constraints.gridwidth = 3;
    constraints.weightx = 1;
    constraints.weighty = 1;
    constraints.ipadx = 5;
    constraints.insets = new Insets(5, 5, 1, 5);
    panel.add(labelText, constraints);

    constraints.anchor = GridBagConstraints.NORTHEAST;
    constraints.gridwidth = 1;
    constraints.gridy = 1;
    constraints.weightx = 0.5;
    constraints.insets = new Insets(0, 5, 5, 0);
    panel.add(customerName, constraints);

    constraints.anchor = GridBagConstraints.NORTH;
    constraints.gridx = 1;
    constraints.weightx = 0;
    constraints.insets = new Insets(0, 0, 5, 0);
    panel.add(arrow, constraints);

    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.gridx = 2;
    constraints.weightx = 0.5;
    constraints.insets = new Insets(0, 0, 5, 5);
    panel.add(productName, constraints);

    return panel;
  }

  /**
   * A customized JPanel that uses a rounded rectangle as shape and a linear gradient as background paint.
   */
  private static class LabelJPanel extends JPanel {

    private static final LinearGradientPaint BACKGROUND_PAINT = new LinearGradientPaint(
        0, 0, 0, 30,
        new float[]{0, 1},
        new Color[]{new Color(255, 255, 187), new Color(255, 238, 119)});

    private static final BasicStroke BORDER_STROKE = new BasicStroke(2);

    private Color borderColor = Colors.TRANSPARENT;

    public LabelJPanel() {
      super(new GridBagLayout());
      // don't paint a rectangular background
      this.setBackground(Colors.TRANSPARENT);
      this.setAlignmentX(0);
    }

    /**
     * Sets the color of the panel's border.
     */
    public void setBorderColor(Color borderColor) {
      this.borderColor = borderColor;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;

      Paint oldPaint = g2d.getPaint();
      Stroke oldStroke = g2d.getStroke();

      g2d.setPaint(BACKGROUND_PAINT);
      g2d.fillRoundRect(1, 1, getWidth() -1, getHeight() -1, 20, 20);
      g2d.setColor(borderColor);
      g2d.setStroke(BORDER_STROKE);
      g2d.drawRoundRect(1, 1, getWidth()-1, getHeight() -1, 20, 20);

      g2d.setStroke(oldStroke);
      g2d.setPaint(oldPaint);
    }
  }
}
