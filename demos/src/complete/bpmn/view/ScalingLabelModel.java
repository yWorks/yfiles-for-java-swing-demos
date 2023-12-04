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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graphml.DefaultValue;

class ScalingLabelModel implements ILabelModel {

  private static final InteriorStretchLabelModel STRETCH_MODEL;

  private static final ILabelModelParameter STRETCH_PARAMETER;

  private static final SimpleNode DUMMY_NODE;

  private static final SimpleLabel DUMMY_LABEL;


  private InsetsD insets = new InsetsD();

  /**
   * Gets the insets to use within the node's {@link INode#getLayout() Layout}.
   * @return The Insets.
   * @see #setInsets(InsetsD)
   */
  @DefaultValue(stringValue = "0", classValue = InsetsD.class)
  public InsetsD getInsets() {
    return this.insets;
  }

  /**
   * Sets the insets to use within the node's {@link INode#getLayout() Layout}.
   * @param value The Insets to set.
   * @see #getInsets()
   */
  @DefaultValue(stringValue = "0", classValue = InsetsD.class)
  public void setInsets( InsetsD value ) {
    this.insets = value;
  }

  public final <TLookup> TLookup lookup( Class<TLookup> type ) {
    return STRETCH_MODEL.lookup(type);
  }

  public final ILookup getContext( ILabel label, ILabelModelParameter parameter ) {
    return STRETCH_MODEL.getContext(label, parameter);
  }

  public final IOrientedRectangle getGeometry( ILabel label, ILabelModelParameter parameter ) {
    ScalingParameter scalingParameter = (ScalingParameter)parameter;
    if (!(label.getOwner() instanceof INode)) {
      return IOrientedRectangle.EMPTY;
    }

    RectD availableRect = ((INode)label.getOwner()).getLayout().toRectD();
    double horizontalInsets = getInsets().left + getInsets().right;
    double verticalInsets = getInsets().top + getInsets().bottom;

    // consider fix insets
    double x = availableRect.getMinX() + (availableRect.width > horizontalInsets ? getInsets().left : 0);
    double y = availableRect.getMinY() + (availableRect.height > verticalInsets ? getInsets().top : 0);
    double width = availableRect.width - (availableRect.width > horizontalInsets ? horizontalInsets : 0);
    double height = availableRect.height - (availableRect.height > verticalInsets ? verticalInsets : 0);

    // consider scaling insets
    x += scalingParameter.getScalingInsets().left * width;
    y += scalingParameter.getScalingInsets().top * height;
    width = width * (1 - scalingParameter.getScalingInsets().left - scalingParameter.getScalingInsets().right);
    height = height * (1 - scalingParameter.getScalingInsets().top - scalingParameter.getScalingInsets().bottom);

    if (scalingParameter.isKeepRatio()) {
      double fixRatio = scalingParameter.getRatio();
      double availableRatio = height > 0 && width > 0 ? width / height : 1;

      if (fixRatio > availableRatio) {
        // keep width
        double cy = y + height / 2;
        height *= availableRatio / fixRatio;
        y = cy - height / 2;
      } else {
        double cx = x + width / 2;
        width *= fixRatio / availableRatio;
        x = cx - width / 2;
      }
    }

    DUMMY_NODE.setLayout(new RectD(x, y, width, height));
    DUMMY_LABEL.setPreferredSize(label.getPreferredSize());
    return STRETCH_MODEL.getGeometry(DUMMY_LABEL, STRETCH_PARAMETER);
  }


  public final ILabelModelParameter createDefaultParameter() {
    ScalingParameter scalingParameter = new ScalingParameter();
    scalingParameter.setModel(this);
    scalingParameter.setScalingInsets(InsetsD.EMPTY);
    return scalingParameter;
  }

  public final ILabelModelParameter createScaledParameter( double scale ) {
    if (scale <= 0 || scale > 1) {
      throw new IllegalArgumentException("Argument '" + scale + "' not allowed. Valid values are in ]0; 1].");
    }
    ScalingParameter scalingParameter = new ScalingParameter();
    scalingParameter.setModel(this);
    scalingParameter.setScalingInsets(new InsetsD((1 - scale) / 2));
    return scalingParameter;
  }

  public final ILabelModelParameter createScaledParameter( double leftScale, double topScale, double rightScale, double bottomScale ) {
    if (leftScale < 0 || rightScale < 0 || topScale < 0 || bottomScale < 0) {
      throw new IllegalArgumentException("Negative Arguments are not allowed.");
    }
    if (leftScale + rightScale >= 1 || topScale + bottomScale >= 1) {
      throw new IllegalArgumentException("Arguments not allowed. The sum of left and right scale respectively top and bottom scale must be below 1.");
    }
    ScalingParameter scalingParameter = new ScalingParameter();
    scalingParameter.setModel(this);
    scalingParameter.setScalingInsets(InsetsD.fromLTRB(leftScale, topScale, rightScale, bottomScale));
    return scalingParameter;
  }

  public final ILabelModelParameter createScaledParameterWithRatio( double scale, double ratio ) {
    if (scale <= 0 || scale > 1) {
      throw new IllegalArgumentException("Argument '" + scale + "' not allowed. Valid values are in ]0; 1].");
    }
    if (ratio <= 0) {
      throw new IllegalArgumentException("Argument '" + ratio + "' not allowed. Ratio must be positive.");
    }
    ScalingParameter scalingParameter = new ScalingParameter();
    scalingParameter.setModel(this);
    scalingParameter.setScalingInsets(new InsetsD((1 - scale) / 2));
    scalingParameter.setKeepRatio(true);
    scalingParameter.setRatio(ratio);
    return scalingParameter;
  }

  public final ILabelModelParameter createScaledParameterWithRatio( double leftScale, double topScale, double rightScale, double bottomScale, double ratio ) {
    if (leftScale < 0 || rightScale < 0 || topScale < 0 || bottomScale < 0) {
      throw new IllegalArgumentException("Negative Arguments are not allowed.");
    }
    if (leftScale + rightScale >= 1 || topScale + bottomScale >= 1) {
      throw new IllegalArgumentException("Arguments not allowed. The sum of left and right scale respectively top and bottom scale must be below 1.");
    }
    if (ratio <= 0) {
      throw new IllegalArgumentException("Argument '" + ratio + "' not allowed. Ratio must be positive.");
    }
    ScalingParameter scalingParameter = new ScalingParameter();
    scalingParameter.setModel(this);
    scalingParameter.setScalingInsets(InsetsD.fromLTRB(leftScale, topScale, rightScale, bottomScale));
    scalingParameter.setKeepRatio(true);
    scalingParameter.setRatio(ratio);
    return scalingParameter;
  }



  private static class ScalingParameter implements ILabelModelParameter {
    private ILabelModel model;

    public final ILabelModel getModel() {
      return this.model;
    }

    public final void setModel( ILabelModel value ) {
      this.model = value;
    }

    private InsetsD scalingInsets = new InsetsD();

    public final InsetsD getScalingInsets() {
      return this.scalingInsets;
    }

    public final void setScalingInsets( InsetsD value ) {
      this.scalingInsets = value;
    }

    private boolean keepRatio;

    public final boolean isKeepRatio() {
      return this.keepRatio;
    }

    public final void setKeepRatio( boolean value ) {
      this.keepRatio = value;
    }

    private double ratio;

    public final double getRatio() {
      return this.ratio;
    }

    public final void setRatio( double value ) {
      this.ratio = value;
    }

    public final ScalingParameter clone() {
      ScalingParameter scalingParameter = new ScalingParameter();
      scalingParameter.setModel(getModel());
      scalingParameter.setScalingInsets(getScalingInsets());
      scalingParameter.setKeepRatio(isKeepRatio());
      return scalingParameter;
    }

    public final boolean supports( ILabel label ) {
      return label.getOwner() instanceof INode;
    }

  }

  static {
    STRETCH_MODEL = new InteriorStretchLabelModel();
    STRETCH_PARAMETER = STRETCH_MODEL.createParameter(InteriorStretchLabelModel.Position.CENTER);
    DUMMY_NODE = new SimpleNode();
    DUMMY_LABEL = new SimpleLabel(DUMMY_NODE, "", STRETCH_PARAMETER);
  }

}
