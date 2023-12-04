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
package input.customlabelmodel;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterProvider;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.utils.IEnumerable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom implementation of {@link ILabelModel} that provides either continuous
 * or discrete label positions directly outside the node border.
 * In addition to the label model itself, two important support interfaces
 * {@link ILabelModelParameterFinder} and {@link ILabelModelParameterProvider}
 * are implemented.
 */
public class MyNodeLabelLocationModel implements ILabelModel, ILabelModelParameterProvider, ILabelModelParameterFinder {

  private int candidateCount;
  private double offset;

  /**
   * Initializes a new instance of <code>MyNodeLabelLocationModel</code> with eight discrete
   * label positions around the border and no distance to the node layout borders.
   */
  public MyNodeLabelLocationModel() {
    this.candidateCount = 8;
    this.offset = 0;
  }

  /**
   * Returns the number of discrete label positions around the border.
   * A value of 0 signifies that continuous label positions are used.
   */
  @DefaultValue(intValue = 8, valueType = DefaultValue.ValueType.INT_TYPE)
  public int getCandidateCount() {
    return this.candidateCount;
  }

  /**
   * Sets the number of discrete label positions around the border.
   * A value of 0 signifies that continuous label positions are used.
   */
  @DefaultValue(intValue = 8, valueType = DefaultValue.ValueType.INT_TYPE)
  public void setCandidateCount(int value) {
    this.candidateCount = value;
  }

  /**
   * Returns the offset of the label location, i.e., the distance to the node layout borders.
   */
  @DefaultValue(doubleValue = 0.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public double getOffset() {
    return this.offset;
  }

  /**
   * Sets the offset of the label location, i.e., the distance to the node layout borders.
   */
  @DefaultValue(doubleValue = 0.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public void setOffset(double value) {
    this.offset = value;
  }

  /**
   * Returns instances of the support interfaces (which are actually the model instance itself)
   */
  public MyNodeLabelLocationModel lookup(Class type) {
    if ((type == ILabelModelParameterProvider.class) && (this.candidateCount > 0)) {
      // if we request an ILabelModelParameterProvider AND we use discrete label candidates, we return the label model
      // itself, otherwise, null is returned, which means that continuous label positions are supported
      return this;
    }
    if (type == ILabelModelParameterFinder.class) {
      // if we request a ILabelModelParameterProvider, we return the label model itself, so we can always retrieve a
      // matching parameter for a given actual position
      return this;
    }
    return null;
  }

  /**
   * Calculates for the given parameter the actual geometry of the specified label in absolute world coordinates.
   * The actual position is calculated from the {@link MyNodeCustomNodeLabelModelParameter#getRatio()} specified in the
   * parameter as the counterclockwise angle on the label owner's circumference. Note that we also rotate the label
   * layout itself accordingly.
   */
  public IOrientedRectangle getGeometry(ILabel label, ILabelModelParameter parameter) {
    if (parameter instanceof MyNodeCustomNodeLabelModelParameter && label.getOwner() instanceof INode) {
      MyNodeCustomNodeLabelModelParameter modelParameter = (MyNodeCustomNodeLabelModelParameter) parameter;
      INode ownerNode = (INode) label.getOwner();

      // if we have a matching parameter and a node as owner, calculate the angle for the label position
      // and the matching rotation of the label layout box itself
      PointD center = ownerNode.getLayout().getCenter();
      double radius = Math.max(ownerNode.getLayout().getWidth(), ownerNode.getLayout().getHeight()) * 0.5;
      double ratio = modelParameter.ratio;
      double angle = ratio * Math.PI * 2;
      double x = Math.sin(angle);
      double y = Math.cos(angle);
      PointD up = new PointD(-y, x);

      OrientedRectangle result = new OrientedRectangle();
      result.setUpVector(up);
      result.resize(label.getPreferredSize());

      double multiplicity = offset + radius + label.getPreferredSize().getHeight() * 0.5;
      result.setCenter(PointD.add(center, new PointD(up.getX() * multiplicity, up.getY() * multiplicity)));

      return result;
    }
    return IOrientedRectangle.EMPTY;
  }

  /**
   * Creates the default parameter for this model. Here it is located at 1/4 around the node's circumference.
   */
  public MyNodeCustomNodeLabelModelParameter createDefaultParameter() {
    return this.createParameter(0.25);
  }

  /**
   * Factory method that creates a parameter for a given rotation angle.
   */
  MyNodeCustomNodeLabelModelParameter createParameter(double ratio) {
    return new MyNodeCustomNodeLabelModelParameter(this, ratio);
  }

  /**
   * Provides a lookup context for the given combination of label and parameter.
   */
  public ILookup getContext(ILabel label, ILabelModelParameter parameter) {
    return ILookup.EMPTY;
  }

  /**
   * Returns an enumerator over a set of possible {@link ILabelModelParameter}
   * instances that can be used for the given label and model.
   */
  @Override
  public IEnumerable<ILabelModelParameter> getParameters(ILabel label, ILabelModel model) {
    List<ILabelModelParameter> parameters = new ArrayList<>(this.candidateCount);
    for (int i = 0; i < this.candidateCount; i++) {
      parameters.add(new MyNodeCustomNodeLabelModelParameter(this, (double) i / (double) this.candidateCount));
    }
    return IEnumerable.create(parameters);
  }

  /**
   * Tries to find a parameter that best matches the given layout for the provided label instance.
   * By default, this method is only called when <b>no discrete</b> candidates are specified (i.e. here for
   * {@link #candidateCount} = 0. This implementation just calculates the rotation angle for the center of
   * layout and creates a parameter for exactly this angle which createParameter.
   */
  @Override
  public MyNodeCustomNodeLabelModelParameter findBestParameter(ILabel label, ILabelModel model, IOrientedRectangle layout) {
    if (model instanceof MyNodeLabelLocationModel && label.getOwner() instanceof INode) {
      MyNodeLabelLocationModel labelModel = (MyNodeLabelLocationModel) model;
      INode node = (INode) label.getOwner();

      PointD direction = PointD.subtract(layout.getCenter(), node.getLayout().getCenter()).getNormalized();
      double ratio = Math.atan2(direction.getY(), -direction.getX()) / (Math.PI * 2);
      return labelModel.createParameter(ratio);
    }
    return findBestParameter(label, model, layout);
  }

  /**
   * Custom implementation of {@link ILabelModelParameter} that is tailored to match
   * {@link MyNodeLabelLocationModel} instances.
   */
  @GraphML(markupExtensionConverter = MyNodeLabelLocationModelParameterConverter.class)
  static class MyNodeCustomNodeLabelModelParameter implements ILabelModelParameter {
    private final MyNodeLabelLocationModel owner;
    private final double ratio;

    MyNodeCustomNodeLabelModelParameter(MyNodeLabelLocationModel owner, double ratio) {
      this.owner = owner;
      this.ratio = ratio;
    }

    /**
     * Returns the ratio for the given label model parameter. The ratio corresponds to the counterclockwise angle on
     * the label owner's circumference.
     */
    double getRatio() {
      return this.ratio;
    }

    /**
     * Creates a clone of this object.
     */
    public Object clone() {
      // we have no mutable state, so return this.
      return this;
    }

    /**
     * Returns the model instance to which this parameter belongs.
     * This is usually a reference to the model instance that has created this parameter.
     */
    @Override
    public MyNodeLabelLocationModel getModel() {
      return this.owner;
    }

    /**
     * Predicate that checks if this parameter instance may be used with the given label.
     * Our model/parameter implementation only makes sense when used for {@link INode}s.
     */
    @Override
    public boolean supports(ILabel label) {
      return label.getOwner() instanceof INode;
    }
  }
}
