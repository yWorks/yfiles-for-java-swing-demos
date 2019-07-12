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
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 * Base class for displaying label text in an isometric view.
 */
abstract class AbstractLabelVisual implements IVisual {
  /** X-coordinate of the label's anchor point in the isometric view. */
  double anchorX;
  /** Y-coordinate of the label's anchor point in the isometric view. */
  double anchorY;

  boolean mirrorShearY;

  /** X-coordinate of the isometric transformation's shear element. */
  final double shearX;
  /** Y-coordinate of the isometric transformation's shear element. */
  final double shearY;
  /** X-coordinate of the isometric transformation's scale element. */
  final double scaleX;
  /** Y-coordinate of the isometric transformation's scale element. */
  final double scaleY;

  /** The insets between label border and label text. */
  InsetsD insets;

  /** The font for rendering the label text. */
  Font font;
  /** The label text to display. */
  String text;
  /** The color for rendering the label text. */
  Color textColor;

  AbstractLabelVisual( double shearX, double scaleY ) {
    mirrorShearY = false;

    scaleX = IsometricTransformationSupport.M_TO_VIEW_11;
    this.scaleY = scaleY;
    this.shearX = shearX;
    shearY = IsometricTransformationSupport.M_TO_VIEW_21;
  }

  @Override
  public void paint( IRenderContext context, Graphics2D gfx ) {
    Graphics2D g = (Graphics2D) gfx.create();

    g.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    // transformation to the right backside of the 3-dimensional label box
    g.translate(anchorX, anchorY);
    int sign = mirrorShearY ? -1 : 1;
    g.transform(new AffineTransform(
            scaleX,        // m00 - scaleX
            sign * shearY, // m10 - shearY
            shearX,        // m01 - shearX
            scaleY,        // m11 - scaleY
            0,             // m02 - translateX
            0));           // m12 - translateY
    g.translate(-anchorX, -anchorY);

    paintLabel(context, g);

    g.dispose();
  }

  /**
   * Paints the label.
   */
  abstract void paintLabel( IRenderContext context, Graphics2D g );
}