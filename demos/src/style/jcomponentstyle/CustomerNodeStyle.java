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
package style.jcomponentstyle;

import com.yworks.yfiles.graph.styles.AbstractJComponentStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.INode;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Paint;

/**
 * Component node style that displays the business data of a Customer.
 */
public class CustomerNodeStyle extends ComponentNodeStyle {

  private static final BackgroundPaints BACKGROUND_PAINTS;

  /**
   * Initialize the backgrounds used for selected and unselected node states.
   */
  static {
    Color color1 = new Color(204, 255, 255);
    Color color2 = new Color(36, 154, 231);
    Paint selectedBackground = createLinearGradient(color1, color2);
    Paint unselectedBackground = createLinearGradient(color1, color2.brighter());
    BACKGROUND_PAINTS = new BackgroundPaints(unselectedBackground, selectedBackground);
  }

  public CustomerNodeStyle() {
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
    final CustomerJPanel panel = new CustomerJPanel();

    // react on edits of the header text field
    panel.addPropertyChangeListener(NodeJPanel.HEADER, evt -> {
      final String newName = (String) evt.getNewValue();
      // write the edited name back to the business data
      getCustomer(node).setName(newName);
      // update the node bounds
      updateNodeSize(ctx, node);
    });

    // react on edits of the id field
    panel.addPropertyChangeListener(NodeJPanel.ID, evt -> {
      Object newValue = evt.getNewValue();
      if (newValue instanceof Number) {
        // write the edited id back to the business data
        getCustomer(node).setId(((Number) newValue).intValue());
        // update the node bounds
        updateNodeSize(ctx, node);
      }
    });

    // react on edits of the location field
    panel.addPropertyChangeListener("node.location", evt -> {
      final String newLocation = (String) evt.getNewValue();
      // write the edited location back to the business data
      getCustomer(node).setLocation(newLocation);
      // update the node bounds
      updateNodeSize(ctx, node);
    });

    return panel;
  }

  /**
   * Returns the tag of the specified node casted to a Customer
   */
  private static Customer getCustomer(INode node) {
    return (Customer) node.getTag();
  }

  /**
   * A ComponentNodeStyle.NodeJPanel customized to visualize the business data of a Customer.
   * <p>
   * The panel supports automatic update of the displayed business data by
   * listening for client property changes that are associated to property key
   * {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle#USER_TAG_KEY}.
   * </p>
   */
  static class CustomerJPanel extends ComponentNodeStyle.NodeJPanel {

    private JFormattedTextField locationTextField;

    @Override
    protected void initializeChildren() {
      super.initializeChildren();

      // location:
      JPanel locationPanel = createGridBagPanel();

      JLabel locationDescription = new JLabel();
      locationDescription.setFont(smallPlainFont);
      locationDescription.setText("Location:");

      // JFormattedTextField is used because of its property change event based value notification
      locationTextField = configureFormattedField(new JFormattedTextField(), smallPlainFont, SwingConstants.LEFT);
      locationTextField.addPropertyChangeListener("value",
          evt -> firePropertyChange("node.location", evt.getOldValue(), locationTextField.getText()));

      // add the components using a GridBagLayout
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.ipadx = 5;
      constraints.anchor = GridBagConstraints.WEST;
      constraints.weightx = 0;
      locationPanel.add(locationDescription, constraints);

      constraints.gridx = 1;
      constraints.weightx = 1;
      locationPanel.add(locationTextField, constraints);

      constraints.gridx = 0;
      constraints.gridy = 2;
      constraints.weightx = 1;
      constraints.insets = new Insets(3, 8, 8, 3);
      this.add(locationPanel, constraints);
    }

    @Override
    protected void addChildPropertyListener() {
      super.addChildPropertyListener();

      // set business data values for headerTextField, idTextField and locationTextField
      // whenever the user tag of the node is set
      addPropertyChangeListener(AbstractJComponentStyle.USER_TAG_KEY, evt -> {
        Customer customer = (Customer) evt.getNewValue();
        headerTextField.setText(customer.getName());
        idTextField.setText(String.valueOf(customer.getId()));
        locationTextField.setText(customer.getLocation());
      });
    }
  }
}