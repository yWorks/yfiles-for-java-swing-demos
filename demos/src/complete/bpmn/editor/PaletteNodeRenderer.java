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
package complete.bpmn.editor;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.PixelImageExporter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;

/**
 * Paints {@link INode} instances in a
 * {@link JList}.
 */
class PaletteNodeRenderer implements ListCellRenderer<INode> {
  // renders the list cell
  private DefaultListCellRenderer renderer;
  // holds an icon for each node
  private WeakHashMap<INode, NodeIcon> node2icon;

  PaletteNodeRenderer() {
    renderer = new DefaultListCellRenderer();
    node2icon = new WeakHashMap<>();
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends INode> list, INode node, int index, boolean isSelected, boolean cellHasFocus) {
    // we use a label as component that renders the list cell and sets the icon that paints the given node
    JLabel label = (JLabel) renderer.getListCellRendererComponent(list, node, index, isSelected, cellHasFocus);
    label.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setIcon(getIcon(node));
    label.setText(null);
    return label;
  }

  /**
   * Returns an {@link Icon} painting the given node.
   */
  private Icon getIcon(INode node) {
    NodeIcon icon = node2icon.get(node);
    if (icon == null) {
      icon = new NodeIcon(node);
      node2icon.put(node, icon);
    }
    return icon;
  }


  /**
   * An {@link Icon} that paints an {@link INode}.
   */
  private static class NodeIcon implements Icon {
    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 70;
    final BufferedImage image;

    NodeIcon(INode node) {
      // create a GraphComponent instance and add a copy of the given node with its labels
      GraphComponent graphComponent = new GraphComponent();
      RectD newLayout = new RectD(PointD.ORIGIN, node.getLayout().toSizeD());
      INode newNode = graphComponent.getGraph().createNode(newLayout, node.getStyle(), node.getTag());
      node.getLabels().forEach(label ->
          graphComponent.getGraph().addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag()));
      // create an image of the node with its labels
      graphComponent.updateContentRect();
      PixelImageExporter pixelImageExporter = new PixelImageExporter(graphComponent.getContentRect().getEnlarged(2));
      pixelImageExporter.setTransparencyEnabled(true);
      double scale1 = Math.min(1, pixelImageExporter.getConfiguration().calculateScaleForWidth(MAX_WIDTH));
      double scale2 = Math.min(1, pixelImageExporter.getConfiguration().calculateScaleForHeight(MAX_HEIGHT));
      pixelImageExporter.getConfiguration().setScale(Math.min(scale1, scale2));
      image =  pixelImageExporter.exportToBitmap(graphComponent);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.drawImage(image, x, y, null);
    }

    @Override
    public int getIconWidth() {
      return image.getWidth();
    }

    @Override
    public int getIconHeight() {
      return image.getHeight();
    }
  }
}
