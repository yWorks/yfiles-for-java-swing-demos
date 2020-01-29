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
package complete.isometric;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

/**
 * Visualizes edge labels as isometric text blocks "standing" on their
 * respective edges.
 */
public class EdgeLabelStyle extends AbstractLabelStyle {
  /** Stores the font for rendering the label text. */
  private Font font;
  /** Stores the insets between label border and label text. */
  private InsetsD insets;
  /** Stores the color for rendering the label background. */
  private Color backgroundColor;
  /** Stores the color for rendering the label border. */
  private Color borderColor;
  /** Stores the color for rendering the label text. */
  private Color textColor;

  /**
   * Initializes a new edge label style.
   */
  public EdgeLabelStyle() {
    font = new Font("Dialog", Font.PLAIN, 12);
    insets = new InsetsD(3);
    backgroundColor = new Color(255, 255, 153);
    borderColor = Color.BLACK;
    textColor = Color.BLACK;
  }

  /**
   * Creates the visual representations for edge labels using this style.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, ILabel label ) {
    IsometricGeometry geometry = IsometricGeometry.get(label);
    double[] corners = IsometricTransformationSupport.calculateCorners(
            label.getLayout().getBounds(), geometry);

    boolean horizontal = geometry.isHorizontal();

    // the anchor point of the label in the isometric view
    double anchorX = corners[horizontal ? IsometricTransformationSupport.C0_X : IsometricTransformationSupport.C1_X];
    double anchorY = corners[horizontal ? IsometricTransformationSupport.C0_Y : IsometricTransformationSupport.C1_Y];

    LabelVisual visual = new LabelVisual();
    visual.anchorX = anchorX;
    visual.anchorY = anchorY;
    visual.backgroundColor = backgroundColor;
    visual.borderColor = borderColor;
    visual.font = font;
    visual.insets = insets;
    visual.mirrorShearY = !horizontal;
    visual.text = label.getText();
    visual.textColor = textColor;
    return visual;
  }

  /**
   * Calculates the preferred size of the given label in world coordinates.
   * @throws NullPointerException if no {@link IsometricGeometry geometry data}
   * is available for the given label.
   */
  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    IsometricGeometry geometry = IsometricGeometry.get(label);
    double[] corners = IsometricTransformationSupport.calculateCorners(
            label.getLayout().getBounds(), geometry);
    return IsometricTransformationSupport.calculateViewBounds(geometry, corners).toSizeD();
  }


  /**
   * Handles the actual label rendering in an isometric fashion.
   */
  private static final class LabelVisual extends AbstractLabelVisual {
    Color backgroundColor;
    Color borderColor;

    LabelVisual() {
      super(0, 1);
    }

    @Override
    void paintLabel( IRenderContext context, Graphics2D gfx ) {
      Graphics2D g = (Graphics2D) gfx.create();
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      FontMetrics fm = g.getFontMetrics();
      int descent = fm.getMaxDescent();

      // paint background and border
      if (backgroundColor != null || borderColor != null) {
        double labelWidth = ("".equals(text) ? 0 : fm.stringWidth(text)) + insets.left + insets.right;
        double labelHeight = fm.getMaxAscent() + descent + insets.top + insets.bottom;
        Rectangle2D background = new Rectangle2D.Double(anchorX, anchorY - labelHeight, labelWidth, labelHeight);
        g.setColor(backgroundColor);
        g.fill(background);
        g.setColor(borderColor);
        g.draw(background);
      }

      // paint the text
      if (textColor != null && !"".equals(text)) {
        g.setColor(textColor);
        g.setFont(font);
        g.drawString(text, (float) (anchorX + insets.left), (float) (anchorY - insets.bottom - descent));
      }
      g.dispose();
    }
  }
}
