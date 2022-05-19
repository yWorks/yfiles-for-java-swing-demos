/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package layout.organicsubstructures;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;

/**
 * Provides utility methods for getting and setting the type of node.
 */
class NodeTypeSupport {
  private static final Color[] FILL_COLORS = newFillColors();
  private static final Color[] LINE_COLORS = newLinecolors();

  private NodeTypeSupport() {
  }

  /**
   * Returns the type of the given node.
   */
  static Integer getNodeType( INode node ) {
    final Integer type = (Integer) node.getTag();
    return type == null ? Integer.valueOf(0) : type;
  }

  /**
   * Sets the type of the given node.
   */
  static void setNodeType( INode node, Integer type ) {
    node.setTag(type == null ? Integer.valueOf(0) : type);
  }

  /**
   * Creates a new node style for a node with the given type.
   */
  static INodeStyle newNodeStyle( Integer type ) {
    int idx = type == null ? 0 : type.intValue();
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setPaint(NodeTypeSupport.getFillColor(idx));
    nodeStyle.setPen(new Pen(NodeTypeSupport.getLineColor(idx), 2));
    nodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    return nodeStyle;
  }

  /**
   * Returns the fill color for nodes with the given type.
   */
  static Color getFillColor( int type ) {
    return getColorImpl(FILL_COLORS, type);
  }

  /**
   * Returns the line color for nodes with the given type.
   */
  static Color getLineColor( int type ) {
    return getColorImpl(LINE_COLORS, type);
  }

  private static Color getColorImpl( Color[] colors, int type ) {
    if (type < 0 || colors.length <= type) {
      throw new IllegalArgumentException("Unsupported type: " + type);
    }
    return colors[type];
  }

  private static Color[] newFillColors() {
    return new Color[] {
      new Color(23, 190, 187),
      new Color(255, 201, 20),
      new Color(255, 108, 0),
      new Color(98, 27, 0),
      new Color(118, 176, 65),
      new Color(17, 29, 74),
      new Color(11, 113, 137),
      new Color(171, 35, 70),
    };
  }

  private static Color[] newLinecolors() {
    return new Color[] {
      new Color(64, 114, 113),
      new Color(153, 137, 83),
      new Color(102, 43, 0),
      new Color(59, 42, 33),
      new Color(5, 138, 72),
      new Color(27, 31, 44),
      new Color(44, 75, 82),
      new Color(103, 62, 73),
    };
  }
}
