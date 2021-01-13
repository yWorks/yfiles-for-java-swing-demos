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

import com.yworks.yfiles.graph.styles.AbstractJComponentStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.INode;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Paint;

/**
 * Component node style that displays the business data of a Product.
 */
public class ProductNodeStyle extends ComponentNodeStyle {

  private static final BackgroundPaints BACKGROUND_PAINTS;

  /**
   * Initialize the backgrounds used for selected and unselected node states.
   */
  static {
    Color color1 = new Color(255, 221, 51);
    Color color2 = new Color(255, 119, 51);
    Paint selectedBackground = createLinearGradient(color1, color2);
    Paint unselectedBackground = createLinearGradient(color1, color2.brighter());
    BACKGROUND_PAINTS = new BackgroundPaints(unselectedBackground, selectedBackground);
  }

  public ProductNodeStyle() {
    this.setStyleTag(BACKGROUND_PAINTS);
  }

  /**
   * Creates the component representing a node.
   * <p>
   * This implementation returns a customized JPanel that contains several JLabels showing the node's business data.
   * </p>
   * @param ctx The context for which the component should be created.
   * @param node The node that will be rendered.
   */
  @Override
  public JComponent createComponent(final IRenderContext ctx, final INode node) {

    // create the customized JPanel that uses a gradient paint as background.
    final ProductJPanel panel = new ProductJPanel();

    // react on edits of the header text field
    panel.addPropertyChangeListener(NodeJPanel.HEADER, evt -> {
      final String newName = (String) evt.getNewValue();
      // write the edited name back to the business data
      getProduct(node).setName(newName);
      // update the node bounds
      updateNodeSize(ctx, node);
    });

    // react on edits of the id field
    panel.addPropertyChangeListener(NodeJPanel.ID, evt -> {
      Object newValue = evt.getNewValue();
      if (newValue instanceof Number) {
        // write the edited id back to the business data
        getProduct(node).setId(((Number) newValue).intValue());
        // update the node bounds
        updateNodeSize(ctx, node);
      }
    });

    // react on edits of the inStock check box
    panel.addPropertyChangeListener("node.inStock", evt -> {
      boolean newValue = (Boolean) evt.getNewValue();
      // write the edited inStock value back to the business data
      getProduct(node).setInStock(newValue);
    });

    return panel;
  }

  /**
   * Returns the tag of the specified node casted to a Product
   */
  private static Product getProduct(INode node) {
    return (Product) node.getTag();
  }

  /**
   * A ComponentNodeStyle.NodeJPanel customized to visualize the business data of a Product.
   * <p>
   * The panel supports automatic update of the displayed business data by
   * listening for client property changes that are associated to property key
   * {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle#USER_TAG_KEY}.
   * </p>
   */
  static class ProductJPanel extends ComponentNodeStyle.NodeJPanel {

    private JCheckBox inStockCheckBox;

    @Override
    protected void initializeChildren() {
      super.initializeChildren();

      // additional info whether the product is in stock
      JPanel inStockPanel = createGridBagPanel();

      JLabel inStockDescription = new JLabel();
      inStockDescription.setFont(smallPlainFont);
      inStockDescription.setText("In Stock:");

      inStockCheckBox = new JCheckBox();
      inStockCheckBox.setBackground(Colors.TRANSPARENT);
      inStockCheckBox.addActionListener(e -> {
        boolean newValue = inStockCheckBox.isSelected();
        firePropertyChange("node.inStock", !newValue, newValue);
      });

      // add the components using a GridBagLayout
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.ipadx = 5;
      constraints.anchor = GridBagConstraints.WEST;
      constraints.weightx = 0;
      inStockPanel.add(inStockDescription, constraints);

      constraints.gridx = 1;
      constraints.weightx = 1;
      inStockPanel.add(inStockCheckBox, constraints);

      constraints.gridx = 0;
      constraints.gridy = 2;
      constraints.insets = new Insets(3, 8, 8, 3);
      this.add(inStockPanel, constraints);
    }

    @Override
    protected void addChildPropertyListener() {
      super.addChildPropertyListener();

      // set business data values for headerTextField, idTextField and inStockCheckBox
      // whenever the user tag of the node is set
      addPropertyChangeListener(AbstractJComponentStyle.USER_TAG_KEY, evt -> {
        Product product = (Product) evt.getNewValue();
        headerTextField.setText(product.getName());
        idTextField.setText(String.valueOf(product.getId()));
        inStockCheckBox.setSelected(product.getInStock());
      });
    }
  }
}