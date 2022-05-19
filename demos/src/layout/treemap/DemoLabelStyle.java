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
package layout.treemap;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

/**
 * Visualizes text for interior node labels.
 * <p>
 * Automatically determines a suitable font size to ensure that label text fits
 * into the bounds of the label's owner node.
 * </p><p>
 * Only meant to be used for labels whose layout parameter is
 * {@link com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel#CENTER}.
 * </p>
 */
public class DemoLabelStyle extends AbstractLabelStyle {
  static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 4);
  static final InsetsD INSETS = new InsetsD(4);

  /**
   * Creates text visualizations for the given (node) label.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, ILabel label ) {
    DemoLabelVisual visual = new DemoLabelVisual();
    visual.updateContent(label.getText(), getPreferredSize(label));
    visual.updateVisual(context, getBounds(context, label));
    return visual;
  }

  /**
   * Updates text visualizations for the given (node) label.
   */
  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, ILabel label ) {
    if (oldVisual instanceof DemoLabelVisual) {
      DemoLabelVisual visual = (DemoLabelVisual) oldVisual;
      visual.updateContent(label.getText(), getPreferredSize(label));
      visual.updateVisual(context, getBounds(context, label));
      return visual;
    }
    return createVisual(context, label);
  }

  /**
   * Determines the preferred size for the given (node) label.
   * @return the width and height of the given label's owner node.
   */
  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    ILabelOwner owner = label.getOwner();
    if (owner instanceof INode) {
      IRectangle nl = ((INode) owner).getLayout();
      return new SizeD(nl.getWidth(), nl.getHeight());
    } else {
      return SizeD.ZERO;
    }
  }


  /**
   * Handles the automatic font size calculation.
   */
  private static final class DemoLabelVisual implements IVisual {
    final SimpleLabel dummy;
    SizeD size;
    boolean visible;
    IVisual visual;

    DemoLabelVisual() {
      DefaultLabelStyle dummyStyle = new DefaultLabelStyle();
      dummyStyle.setTextPaint(Color.WHITE);
      dummyStyle.setInsets(InsetsD.EMPTY);
      dummyStyle.setTextAlignment(TextAlignment.CENTER);
      dummyStyle.setTextClippingEnabled(false);
      dummyStyle.setTextWrapping(TextWrapping.NO_WRAP);
      dummyStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
      dummyStyle.setUsingFractionalFontMetricsEnabled(true);
      dummy = new SimpleLabel(new SimpleNode(), "", InteriorStretchLabelModel.CENTER);
      dummy.setStyle(dummyStyle);
      dummy.setPreferredSize(SizeD.ZERO);
      size = SizeD.ZERO;
    }

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      if (visual != null) {
        visual.paint(context, g);
      }
    }

    void updateVisual( IRenderContext context, RectD lblBnds ) {
      ((SimpleNode) dummy.getOwner()).setLayout(lblBnds);

      if (SizeD.ZERO.equals(dummy.getPreferredSize())) {
        visual = null;
      } else {
        ILabelStyle style = dummy.getStyle();
        visual = style.getRenderer().getVisualCreator(dummy, style).updateVisual(context, visual);
      }
    }

    void updateContent( String text, SizeD size ) {
      if (Objects.equals(dummy.getText(), text) && Objects.equals(this.size, size)) {
        return;
      }

      dummy.setText(text);
      this.size = size;

      configure(text, size);
    }

    /**
     * Calculates a font size which ensures that the display size of the given
     * text does not exceed the given size.
     */
    private void configure( String text, SizeD size ) {
      DefaultLabelStyle style = (DefaultLabelStyle) dummy.getStyle();
      style.setFont(DEFAULT_FONT);

      double prefW = size.width - INSETS.left - INSETS.right;
      double prefH = size.height - INSETS.top - INSETS.bottom;

      String[] lines = text.split("\n");
      SizeD minSize = preferredSize(dummy);
      if (prefW < minSize.getWidth() || prefH < minSize.getHeight()) {
        // the text does not fit into the given size
        // do not display any text in this case
        dummy.setPreferredSize(SizeD.ZERO);
      } else {
        int ub = 128;
        int lb =   0;

        for (int diff = 2 * (ub - lb); diff > 1; diff = ub - lb) {
          int tmp = diff / 2 + lb;
          style.setFont(style.getFont().deriveFont((float) tmp));
          SizeD tmpSize = preferredSize(dummy);
          if (prefW < tmpSize.getWidth() || prefH < tmpSize.getHeight()) {
            ub = tmp;
          } else {
            lb = tmp;
          }
        }
      }
    }

    private static SizeD preferredSize( SimpleLabel label ) {
      ILabelStyle style = label.getStyle();
      label.setPreferredSize(style.getRenderer().getPreferredSize(label, style));
      return label.getPreferredSize();
    }
  }
}
