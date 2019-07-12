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
package complete.isometric;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Visualizes group node label texts as flat text lying in the bottom right
 * side of isometric node visualizations.
 * <p>
 * Note: This label style does not take label models into account.
 * </p>
 */
public class GroupLabelStyle extends AbstractLabelStyle {
  /** Used for text measuring. */
  private static final FontRenderContext DEFAULT_FRC =
          new FontRenderContext(new AffineTransform(), false, true);

  /** Stores the font for rendering the label text. */
  private Font font;
  /** Stores the distances between label border and node border. */
  private InsetsD insets;
  /** Stores the color for rendering the label text. */
  private Color textColor;

  /**
   * Initializes a new group label style.
   */
  public GroupLabelStyle() {
    font = new Font("Dialog", Font.PLAIN, 14);
    insets = new InsetsD(3);
    textColor = Color.BLACK;
  }

  /**
   * Creates the visual representations for group node labels using this style.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, ILabel label ) {
    // Calculate the corners of the node in the view space.
    INode group = (INode) label.getOwner();
    IsometricGeometry geometry = IsometricGeometry.get(group);
    double[] corners = IsometricTransformationSupport.calculateCorners(
            group.getLayout(), geometry);

    // the anchor point of the label in the isometric view
    double anchorX = corners[IsometricTransformationSupport.C3_X];
    double anchorY = corners[IsometricTransformationSupport.C3_Y];

    LabelVisual visual = new LabelVisual();
    visual.anchorX = anchorX;
    visual.anchorY = anchorY;
    visual.font = font;
    visual.insets = insets;
    visual.text = label.getText();
    visual.textColor = textColor;
    visual.width = geometry.getWidth();
    return visual;
  }

  /**
   * Calculates the preferred size of the label.
   */
  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    Rectangle2D bounds = font.getStringBounds(label.getText(), DEFAULT_FRC);
    return new SizeD(
            bounds.getWidth() + insets.left + insets.right,
            bounds.getHeight() + insets.top + insets.bottom);
  }


  /**
   * Handles the actual label rendering in an isometric fashion.
   */
  private static final class LabelVisual extends AbstractLabelVisual {
    double width;

    LabelVisual() {
      super(IsometricTransformationSupport.M_TO_VIEW_12, IsometricTransformationSupport.M_TO_VIEW_22);
    }

    @Override
    void paintLabel( final IRenderContext context, final Graphics2D gfx ) {
      // Draw the label text with the transformed graphics context.
      // It is placed on the bottom right side of the node.
      if (textColor != null && !"".equals(text)) {
        Graphics2D g = (Graphics2D) gfx.create();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setFont(this.font);
        g.setColor(textColor);
        FontMetrics fm = g.getFontMetrics();
        int descent = fm.getMaxDescent();
        g.drawString(
                text,
                (float) (anchorX + width - fm.stringWidth(text) - insets.right),
                (float) (anchorY - insets.bottom - descent));
        g.dispose();
      }
    }
  }
}
