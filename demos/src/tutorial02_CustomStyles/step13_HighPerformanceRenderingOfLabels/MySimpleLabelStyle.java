/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step13_HighPerformanceRenderingOfLabels;

import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.ILabelStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractLabelStyle} as its base class.
 * The font for the label text can be set. The label text is drawn with black letters inside a blue rounded rectangle.
 */
public class MySimpleLabelStyle extends AbstractLabelStyle {
  private static final int HORIZONTAL_INSET = 4;
  private static final int VERTICAL_INSET = 2;

  private Font font;

  //////////////// New in this sample ////////////////
  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, ILabel)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual group, ILabel label) {
    LabelVisual visual = (LabelVisual) group;
    visual.update(label.getLayout(), label.getText(), getFont(), createLayoutTransform(context, label.getLayout(), true));
    return group;
  }
  ////////////////////////////////////////////////////

  /**
   * Initializes a new <code>MySimpleLabelStyle</code> instance and sets a default font for the label.
   */
  public MySimpleLabelStyle() {
    font = new Font("Dialog", Font.PLAIN, 8);
  }

  /**
   * Gets the font used for rendering the label text.
   */
  public Font getFont() {
    return font;
  }

  /**
   * Sets the font used for rendering the label text.
   */
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * Creates the visual for a label to be drawn.
   */
  @Override
  protected IVisual createVisual(IRenderContext context, ILabel label) {

    // we need to arrange the label according to its layout. We use the dedicated method
    // AbstractLabelStyle#createLayoutTransform for this, which creates an AffineTransformation
    // that can be used to arrange an element according to a given IOrientedRectangle.
    AffineTransform layoutTransform = createLayoutTransform(context, label.getLayout(), true);

    LabelVisual visual = new LabelVisual();
    visual.update(label.getLayout(), label.getText(), getFont(), layoutTransform);
    return visual;
  }

  /**
   * Calculates the preferred size for the given label if this style is used for rendering.
   * The size is calculated from the label's text.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    // return size of the text plus some inset space
    String text = label.getText();

    if (!text.isEmpty()) {
      // calculate the bounds of the text with the given font
      FontRenderContext frc = new FontRenderContext(font.getTransform(), true, true);
      TextLayout textLayout = new TextLayout(text, font, frc);
      double textWidth = textLayout.getBounds().getWidth();
      double maxTextHeight = textLayout.getAscent() + textLayout.getDescent();

      // then use the desired size - plus rounding and insets
      return new SizeD(
          Math.ceil(0.5d + textWidth) + 2 * HORIZONTAL_INSET,
          Math.ceil(0.5d + maxTextHeight) + 2 * VERTICAL_INSET);
    } else {
      return new SizeD(2 * HORIZONTAL_INSET, 2 * VERTICAL_INSET + font.getSize());
    }
  }

  /**
   * A {@link IVisual} that paints a label with text. Note that we paint the label at the origin
   * and move and rotate the graphics context to the current location and orientation of the label. We store the
   * background shape, the text, the position and the size of the label as instance variables.
   * The {@link #update(IOrientedRectangle, String, Font, AffineTransform)} method checks
   * whether these values have been changed. If so, the instance variables are updated.
   * The {@link #paint(IRenderContext, Graphics2D)} method uses the instance variables to paint the label
   * instead of creating new instances for each run.
   */
  private static class LabelVisual implements IVisual {
    // color to fill the background of the label with
    private static final Color FILL_COLOR = new Color(155, 226, 255);

    // the font to render the text with
    private Font font;

    // the transformation that is used to arrange the label
    private AffineTransform transform;

    //////////////// New in this sample ////////////////
    // the shape to draw the background of the label with
    private RoundRectangle2D background;
    // the position of the text within the label
    private PointD textPosition;

    // the size and position of the label
    private IOrientedRectangle labelLayout;
    // the text the label shows
    private String labelText;

    LabelVisual() {
      background = new RoundRectangle2D.Double();
      textPosition = PointD.ORIGIN;
      transform = new AffineTransform();
    }

    /**
     * Checks if the properties of the label have been changed. If so, updates all items needed to paint the label.
     * @param layout    the location and size of the label.
     * @param text      the text the label shows
     * @param font      the font to render the text with
     * @param transform the transform that arranges of the label
     */
    public void update(IOrientedRectangle layout, String text, Font font,  AffineTransform transform) {
      if (!equals(layout, this.labelLayout) || !text.equals(this.labelText) || !font.equals(this.font) || !this.transform.equals(transform)) {
        this.labelLayout = layout;
        this.labelText = text;
        this.font = font;
        this.transform = transform;

        // update background shape
        background.setRoundRect(0, 0, labelLayout.getWidth(), labelLayout.getHeight(), labelLayout.getWidth() / 10,
            labelLayout.getHeight() / 10);

        // calculate the position of the text
        if (!text.isEmpty()) {
          // calculate the bounds of the text with the given font
          FontRenderContext frc = new FontRenderContext(font.getTransform(), true, true);
          TextLayout textLayout = new TextLayout(text, font, frc);
          Rectangle2D textBounds = textLayout.getBounds();

          // update text position
          textPosition = new PointD(
              (labelLayout.getWidth() - textBounds.getWidth()) * 0.5,
              (labelLayout.getHeight() + textLayout.getAscent() - textLayout.getDescent()) * 0.5);
        }
      }
    }

    /**
     * Checks two IOrientedRectangles for extensional equality.
     */
    private boolean equals(IOrientedRectangle orientedRectangle1, IOrientedRectangle orientedRectangle2) {
      if (orientedRectangle1 == orientedRectangle2) {
        return true;
      }
      return orientedRectangle1 != null &&  orientedRectangle2 != null
          && orientedRectangle1.getAnchorX() == orientedRectangle2.getAnchorX()
          && orientedRectangle1.getAnchorY() == orientedRectangle2.getAnchorY()
          && orientedRectangle1.getWidth() == orientedRectangle2.getWidth()
          && orientedRectangle1.getHeight() == orientedRectangle2.getHeight()
          && orientedRectangle1.getUpX() == orientedRectangle2.getUpX()
          && orientedRectangle1.getUpY() == orientedRectangle2.getUpY();
    }
    
    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, we work on a copy of the graphic context.
      Graphics2D gfx = (Graphics2D) g.create();
      try {
        // move and rotate the graphics context to the current location and orientation of the label
        gfx.transform(transform);

        // paint the background of the label
        gfx.setPaint(FILL_COLOR);
        gfx.fill(background);
        Pen.getSkyBlue().commit(gfx);
        gfx.draw(background);

        // draw the text of the label in the center of the label's bounds
        if (!labelText.isEmpty()) {
          gfx.setColor(Color.BLACK);
          gfx.setFont(font);
          gfx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
          gfx.drawString(labelText, (float) textPosition.getX(), (float) textPosition.getY());
        }

      } finally {
        // after all is done, dispose the copy
        gfx.dispose();
      }
    }
    ////////////////////////////////////////////////////
  }
}
