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
package viewer.largegraphs.styles.fast;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailLabelStyle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A faster label style.
 * <p>
 * Unlike {@link LevelOfDetailLabelStyle} this style doesn't keep full fidelity of the visualization. Instead it
 * approximates the label's appearance by drawing just a broken line where words are. The intention is to not render
 * text at zoom levels where it wouldn't be legible at all. However, using this style for very small zoom levels is
 * still not recommended. Using a {@link com.yworks.yfiles.graph.styles.VoidLabelStyle} that uses a {@link
 * FastEdgeStyle} below a certain point is much more reasonable.
 * </p>
 */
public class FastLabelStyle extends AbstractLabelStyle {

  // region Properties

  private boolean autoFlip;

  /**
   * Gets a value indicating whether the label automatically flips depending on the orientation so that it stays upright
   * even when rotated upside-down.
   */
  public boolean isAutoFlip() {
    return autoFlip;
  }

  /**
   * Sets a value indicating whether the label automatically flips depending on the orientation so that it stays upright
   * even when rotated upside-down.
   */
  public void setAutoFlip(boolean autoFlip) {
    this.autoFlip = autoFlip;
  }

  private Color backgroundColor;

  /**
   * Gets the color to paint the label's background with.
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Sets the color to paint the label's background with.
   */
  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  private Pen pen;

  /**
   * Gets the foreground color.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the foreground color.
   */
  public void setPen(Pen pen) {
    this.pen = pen;
  }

  // endregion

  /**
   * Initializes a new instance of the FastLabelStyle class with the given auto-flip setting.
   *
   * @param autoFlip Whether to flip the label automatically depending on its orientation.
   */
  public FastLabelStyle(boolean autoFlip) {
    this.setAutoFlip(autoFlip);
    setBackgroundColor(null);
    setPen(new Pen(Color.BLACK, 5));
  }

  /**
   * Initializes a new instance of the FastLabelStyle class with the given auto-flip setting and background color.
   *  @param autoFlip Whether to flip the label automatically depending on its orientation.
   * @param backgroundColor The background color of the label.
   */
  public FastLabelStyle(boolean autoFlip, Color backgroundColor) {
    this.setAutoFlip(autoFlip);
    this.backgroundColor = backgroundColor;
    setPen(new Pen(Color.BLACK, 5));  }

  // region Style

  @Override
  protected IVisual createVisual(IRenderContext context, ILabel label) {
    OrientedRectangle layout = new OrientedRectangle(label.getLayout());
    return createLabelVisual(layout, label.getText());
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, ILabel label) {
    LabelVisual labelVisual = oldVisual instanceof LabelVisual ? ((LabelVisual) oldVisual) : null;
    if (labelVisual == null) {
      return createVisual(context, label);
    }

    IOrientedRectangle layout = label.getLayout();
    IOrientedRectangle oldLayout = labelVisual.layout;

    // Did the layout change? In that case we have to re-create the canvas
    if (!layout.equals(oldLayout)) {
      labelVisual = createLabelVisual(new OrientedRectangle(layout), label.getText());
    }
    return labelVisual;
  }

  @Override
  protected SizeD getPreferredSize(ILabel label) {
    return label.getLayout().toSizeD();
  }

  // endregion

  // region Helper methods

  /**
   * Creates a custom visual that renders the background and an approximation of the label text.
   * <p>
   * The label's visualization is an approximation of its text where each word is represented by a line with a length
   * proportional to the word's length. In the vast majority of cases this approximates the position of spaces in the
   * line accurately enough that the switch from a text label to a fast label is almost imperceptible.
   * </p>
   *
   * @param layout The label's layout.
   * @param text   The label text.
   * @return A Canvas containing the label's path.
   */
  private LabelVisual createLabelVisual(IOrientedRectangle layout, String text) {

    // This part could be optimized a bit by not allocating new objects and thus reduce pressure on the garbage
    // collector. However, in practice it made not much of an impact.
    String[] lines = split(text, '\r', '\n');
    int longestLineLength = 0;
    for (String line : lines) {
      longestLineLength = Math.max(longestLineLength, line.length());
    }
    double sizePerLetter = layout.getWidth() / longestLineLength;

    return new LabelVisual(layout, isAutoFlip(), getBackgroundColor(), getPen(), lines, sizePerLetter);
  }


  public static String[] split(final String s, final char... chars) {
    final StringTokenizer tokenizer = new StringTokenizer(s, new String(chars), false);
    List<String> result = new ArrayList<String>();
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken();
      if (token.length() != 0) {
        result.add(token);
      }
    }
    return result.toArray(new String[0]);
  }

  private static class LabelVisual implements IVisual {

    private static final Path2D.Double path = new Path2D.Double();
    private static final Line2D.Double line2D = new Line2D.Double();

    private IOrientedRectangle layout;
    private boolean autoFlip;
    private Color backgroundColor;
    private Pen pen;

    String[] lines;
    double sizePerLetter;

    public LabelVisual(IOrientedRectangle layout, boolean autoFlip, Color backgroundColor, Pen pen,
                       String[] lines, double sizePerLetter) {
      this.layout = layout;
      this.autoFlip = autoFlip;
      this.backgroundColor = backgroundColor;
      this.pen = pen;
      this.lines = lines;
      this.sizePerLetter = sizePerLetter;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D ctx) {
      PointD origin = layout.getAnchorLocation();
      PointD up = layout.getUp();
      PointD right = new PointD(-up.getY(), up.getX());

      if (autoFlip && up.getY() > 0) {
        origin = new PointD(
            origin.getX() + up.getX() * layout.getHeight() + right.getX() * layout.getWidth(),
            origin.getY() + up.getY() * layout.getHeight() + right.getY() * layout.getWidth());
        up = new PointD(-up.getX(), -up.getY());
        right = new PointD(-right.getX(), -right.getY());
      }

      PointD upperLeft = new PointD(
          origin.getX() + up.getX() * layout.getHeight(),
          origin.getY() + up.getY() * layout.getHeight());


      // Fill -- only drawn when a brush was set
      if (backgroundColor != null) {
        ctx.setBackground(Color.ORANGE);

        path.reset();
        path.moveTo(origin.getX(), origin.getY());
        path.lineTo(origin.getX() + right.getX() * layout.getWidth(), origin.getY() + right.getY() * layout.getWidth());
        path.lineTo(
            origin.getX() + right.getX() * layout.getWidth() + up.getX() * layout.getHeight(),
            origin.getY() + right.getY() * layout.getWidth() + up.getY() * layout.getHeight());
        path.lineTo(origin.getX() + up.getX() * layout.getHeight(), origin.getY() + up.getY() * layout.getHeight());
        path.closePath();

        ctx.setPaint(Colors.TRANSPARENT);
        ctx.fill(path);
      }

      pen.commit(ctx);

      // Lines
      for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        String[] words = split(line, ' ');
        double factor = layout.getHeight() / lines.length * (i + 0.6);
        double currentPointX = upperLeft.getX() - up.getX() * factor;
        double currentPointY = upperLeft.getY() - up.getY() * factor;
        // Words
        for (String word : words) {
          double wordLength = word.length() * sizePerLetter;
          double targetPointX = currentPointX + right.getX() * wordLength;
          double targetPointY = currentPointY + right.getY() * wordLength;

          line2D.setLine(currentPointX, currentPointY, targetPointX, targetPointY);
          ctx.draw(line2D);

          currentPointX = targetPointX + right.getX() * sizePerLetter;
          currentPointY = targetPointY + right.getY() * sizePerLetter;
        }
      }
    }
  }

}
