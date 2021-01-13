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
package tutorial02_CustomStyles.step11_CustomLabelStyle;

import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IOrientedRectangle;
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

/////////////// This class is new in this sample ///////////////

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.ILabelStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractLabelStyle} as its base class.
 * The font for the label text can be set. The label text is drawn with black letters inside a blue rounded rectangle.
 */
public class MySimpleLabelStyle extends AbstractLabelStyle {

  private Font font;

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

    return new LabelVisual(label, getFont(), layoutTransform);
  }

  /**
   * Calculates the preferred size for the given label if this style is used for rendering.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    return new SizeD(80, 15);
  }

  /**
   * A {@link IVisual} that paints a label with text. Note that we paint the label at the
   * origin and move and rotate the graphics context to the current location and orientation of the label.
   */
  private static class LabelVisual implements IVisual {
    // color to fill the background of the label with
    private static final Color FILL_COLOR = new Color(155, 226, 255);

    // the label to show
    private ILabel label;
    // the font to draw the text of the label with
    private Font font;

    // the transformation that is used to arrange the label
    private AffineTransform transform;

    LabelVisual(ILabel label, Font font, AffineTransform transform) {
      this.label = label;
      this.font = font;
      this.transform = transform;
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
        IOrientedRectangle labelLayout = label.getLayout();
        RoundRectangle2D.Double background = new RoundRectangle2D.Double(0, 0, labelLayout.getWidth(),
            labelLayout.getHeight(), labelLayout.getWidth() / 10, labelLayout.getHeight() / 10);
        gfx.setPaint(FILL_COLOR);
        gfx.fill(background);
        Pen.getSkyBlue().commit(gfx);
        gfx.draw(background);

        // draw the text of the label in the center of the label's bounds
        String labelText = label.getText();
        if (!labelText.isEmpty()) {
          // calculate the bounds of the text with the given font
          FontRenderContext frc = new FontRenderContext(font.getTransform(), true, true);
          TextLayout textLayout = new TextLayout(labelText, font, frc);
          Rectangle2D textBounds = textLayout.getBounds();
          // draw the text of the label
          gfx.setColor(Color.BLACK);
          gfx.setFont(font);
          gfx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
          gfx.drawString(labelText, (float) ((labelLayout.getWidth() - textBounds.getWidth()) * 0.5),
              (float) ((labelLayout.getHeight() + textLayout.getAscent() - textLayout.getDescent()) * 0.5));
        }
      } finally {
        // after all is done, dispose the copy
        gfx.dispose();
      }
    }
  }
}
