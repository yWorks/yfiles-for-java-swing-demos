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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterProvider;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.IEnumerable;

import java.util.ArrayList;

/**
 * An {@link ILabelModel} decorator for node labels, that wraps another label model and considers
 * the {@link RotatableNodeStyleDecorator#getAngle()} rotation angle of the label owner when
 * a {@link RotatableNodeStyleDecorator} is used.
 * <p>
 * Therefore this will make the labels rotate with the node's rotation.
 * </p>
 */
public class RotatableNodeLabelModelDecorator implements ILabelModel, IMarkupExtensionConverter {

  /**
   * Determines if the {@link RotatableNodeStyleDecorator#getAngle()} rotation of the label owner
   * should be considered.
   */
  private boolean nodeRotationEnabled;

  public ILabelModel wrapped;

  /**
   * Initialize a new instance using {@link FreeNodeLabelModel#INSTANCE} as {@link #wrapped} label model.
   */
  public RotatableNodeLabelModelDecorator() {
    this(FreeNodeLabelModel.INSTANCE);
  }

  /**
   * Creates a new instance for the passed model.
   * @param wrapped The label model to wrap.
   */
  RotatableNodeLabelModelDecorator(ILabelModel wrapped) {
    this.wrapped = wrapped;
    nodeRotationEnabled = true;
  }

  /**
   * Provides custom implementations of {@link ILabelModelParameterProvider} and
   * {@link ILabelModelParameterFinder} which both consider the rotation.
   */
  @Override
  public <TLookup> TLookup lookup(Class<TLookup> type) {
    if(type == ILabelModelParameterProvider.class) {
      ILabelModelParameterProvider provider = wrapped.lookup(ILabelModelParameterProvider.class);

      if(provider != null) {
        //provider not null therefore does the wrapper implement ILabelModelParameterProvider
        return (TLookup)new RotatedNodeLabelModelParameterProvider(provider);
      }
    }

    if (type == ILabelModelParameterFinder.class) {
      ILabelModelParameterFinder finder = wrapped.lookup(ILabelModelParameterFinder.class);
      if(finder != null) {
        //provider is null if wrapped doesn't implement ILabelModelParameterFinder
        return (TLookup)new RotatedNodeLabelModelParameterFinder(finder);
      }
    }

    return null;
  }

  /**
   * Returns the current geometry of the given label.
   */
  public IOrientedRectangle getGeometry(ILabel label, ILabelModelParameter parameter) {
    RotatableNodeStyleDecorator styleWrapper = getNodeStyleWrapper(label);
    ILabelModelParameter wrappedParameter = getWrappedParameter(parameter);
    IOrientedRectangle orientedRectangle = wrappedParameter.getModel().getGeometry(label, wrappedParameter);
    INode node = (INode) label.getOwner();

    if(!nodeRotationEnabled || node == null || styleWrapper == null || styleWrapper.getAngle() == 0){
      return orientedRectangle;
    }

    PointD rotatedCenter = styleWrapper.getRotatedPoint(orientedRectangle.getCenter(), node, true);
    CachingOrientedRectangle rotatedLayout = styleWrapper.getRotatedLayout(node);

    OrientedRectangle rectangle = new OrientedRectangle(orientedRectangle);
    rectangle.setAngle(rectangle.getAngle() + rotatedLayout.getAngle());
    rectangle.setCenter(rotatedCenter);
    return rectangle;
  }

  /**
   * Creates a wrapped instance of the wrapped label model's default parameter.
   */
  @Override
  public ILabelModelParameter createDefaultParameter() {
    return new RotatableNodeLabelModelDecoratorParameter(wrapped.createDefaultParameter(), this);
  }

  /**
   * Creates a new parameter wrapping.
   * The label model of the wrapping should be the same as {@link #wrapped}.
   * @param wrapped The parameter to wrap
   * @return A parameter wrapping
   */
  ILabelModelParameter createWrappingParameter(ILabelModelParameter wrapped) {
    return new RotatableNodeLabelModelDecoratorParameter(wrapped, this);
  }

  /**
   * Provides a lookup context for the given combination of label and parameter.
   */
  @Override
  public ILookup getContext(ILabel label, ILabelModelParameter parameter) {
    ILabelModelParameter wrappedParameter = getWrappedParameter(parameter);
    return wrappedParameter.getModel().getContext(label, wrappedParameter);
  }

  /**
   * Returns the wrapped label model parameter.
   */
  private ILabelModelParameter getWrappedParameter(ILabelModelParameter parameter) {
    return ((RotatableNodeLabelModelDecoratorParameter) parameter).wrapped;
  }

  /**
   * Returns the wrapping style for nodes when {@link RotatableNodeStyleDecorator} is used, otherwise {@literal null}.
   */
  private RotatableNodeStyleDecorator getNodeStyleWrapper(ILabel label) {
    ILabelOwner lblOwner = label.getOwner();
    if (lblOwner instanceof  INode) {
      INodeStyle style = ((INode) lblOwner).getStyle();
      if (style instanceof RotatableNodeStyleDecorator) {
        return (RotatableNodeStyleDecorator) style;
      }
    }
    return null;
  }

  /**
   * Returns that this label model can be converted.
   */
  @Override
  public boolean canConvert(IWriteContext iWriteContext, Object o) {
    return true;
  }

  /**
   * Converts this label model using {@link RotatableNodeLabelModelDecoratorParameterExtension}.
   */
  @Override
  public MarkupExtension convert(IWriteContext context, Object o) {
    RotatableNodeLabelModelDecoratorExtension extension = new RotatableNodeLabelModelDecoratorExtension();
    extension.setWrapped(wrapped);
    extension.setNodeRotationEnabled(nodeRotationEnabled);
    return new RotatableNodeLabelModelDecoratorParameterExtension();
  }

  /**
   * Returns if the node's rotation is used for the label's rotation.
   */
  public boolean getNodeRotationEnabled() {
    return nodeRotationEnabled;
  }

  /**
   * Sets if the node's rotation is used for the label's rotation.
   */
  void setNodeRotationEnabled(boolean nodeRotationEnabled) {
    this.nodeRotationEnabled = nodeRotationEnabled;
  }

  /**
   * Returns the wrapping of the label model.
   */
  public ILabelModel getWrapped() {
    return wrapped;
  }

  /**
   * Sets the wrapping of the label model.
   */
  public void setWrapped(ILabelModel wrapped) {
    this.wrapped = wrapped;
  }

  /**
   * A ILabelModelParameter decorator for node labels using RotatableNodeLabelModelDecorator to
   * adjust the label rotation to the node rotation.
   */
  private static class RotatableNodeLabelModelDecoratorParameter implements ILabelModelParameter, IMarkupExtensionConverter {

    private ILabelModelParameter wrapped;

    private  ILabelModel model;

    /**
     * Initialize a new instance wrapping the given parameter.
     */
    public RotatableNodeLabelModelDecoratorParameter(ILabelModelParameter wrapped, ILabelModel model) {
      this.wrapped = wrapped;
      this.model = model;
    }

    /**
     * Returns a copy of this label model parameter.
     */
    @Override
    public Object clone() {
      return new RotatableNodeLabelModelDecoratorParameter(wrapped, model);
    }

    /**
     * Accepts node labels that are supported by the wrapped label model parameter.
     */
    @Override
    public boolean supports(ILabel label) {
      return label.getOwner() instanceof INode && wrapped.supports(label);
    }

    /**
     * Returns that this label model parameter can be converted.
     */
    @Override
    public boolean canConvert(IWriteContext iWriteContext, Object o) {
      return true;
    }

    /**
     * Converts this label model parameter using {@link RotatableNodeLabelModelDecoratorParameterExtension}.
     */
    @Override
    public MarkupExtension convert(IWriteContext iWriteContext, Object o) {
      RotatableNodeLabelModelDecoratorParameterExtension extension = new RotatableNodeLabelModelDecoratorParameterExtension();
      extension.setWrapped(wrapped);
      extension.setModel(model);
      return extension;
    }

    /**
     * Returns the label model.
     */
    @Override
    public ILabelModel getModel() {
      return this.model;
    }

    /**
     * Sets the label model.
     */
    private void setModel(ILabelModel model){
      this.model = model;
    }

    /**
     * Returns the wrapped label model parameter.
     */
    public ILabelModelParameter getWrapped() {
      return wrapped;
    }

    /**
     * Sets the wrapped label model parameter.
     */
    private void setWrapped(ILabelModelParameter wrapped) {
      this.wrapped = wrapped;
    }
  }

  /**
   * Provides candidate parameters for rotated label models.
   */
  private static class RotatedNodeLabelModelParameterProvider implements ILabelModelParameterProvider {

    private final ILabelModelParameterProvider WRAPPED_PROVIDER;


    /**
     * Initialize a new instance using the given parameter provider.
     */
    RotatedNodeLabelModelParameterProvider(ILabelModelParameterProvider WRAPPED_PROVIDER) {
      this.WRAPPED_PROVIDER = WRAPPED_PROVIDER;
    }

    /**
     * Returns a set of possible wrapped {@link ILabelModelParameter} instances.
     */
    @Override
    public IEnumerable<ILabelModelParameter> getParameters(ILabel label, ILabelModel labelModel) {
      RotatableNodeLabelModelDecorator wrappedModel = (RotatableNodeLabelModelDecorator) labelModel;
      IEnumerable<ILabelModelParameter> parameters = WRAPPED_PROVIDER.getParameters(label, wrappedModel.getWrapped());

      ArrayList<ILabelModelParameter> result = new ArrayList<>();
      for (ILabelModelParameter parameter : parameters) {
        result.add(wrappedModel.createWrappingParameter(parameter));
      }

      return IEnumerable.create(result);
    }
  }

  /**
   * Finds the best {@link ILabelModelParameter} to approximate a specific rotated layout.
   */
  private static class RotatedNodeLabelModelParameterFinder implements  ILabelModelParameterFinder{

    private final ILabelModelParameterFinder wrappedFinder;

    /**
     * Initialize a new instance using the given parameter.
     */
    RotatedNodeLabelModelParameterFinder(ILabelModelParameterFinder finder) {
      this.wrappedFinder = finder;
    }

    /**
     * Finds the label model parameter that describes the given label layout best.
     * Sometimes the layout cannot be met exactly, in this case the nearest location is used.
     */
    @Override
    public ILabelModelParameter findBestParameter(ILabel label, ILabelModel model, IOrientedRectangle labelLayout) {
      //assuming model is always instanceof RotatableNodeLabelModelDecorator
      RotatableNodeLabelModelDecorator wrapperModel = (RotatableNodeLabelModelDecorator) model;
      RotatableNodeStyleDecorator styleWrapper = wrapperModel.getNodeStyleWrapper(label);

      if (!wrapperModel.nodeRotationEnabled || styleWrapper == null || styleWrapper.getAngle() == 0) {
        return wrapperModel.createWrappingParameter(
                wrappedFinder.findBestParameter(label, wrapperModel.wrapped, labelLayout));
      }

      INode node = (INode) label.getOwner();
      PointD rotatedCenter = styleWrapper.getRotatedPoint(labelLayout.getCenter(), node, false);
      CachingOrientedRectangle rotatedLayout = styleWrapper.getRotatedLayout(node);

      OrientedRectangle rectangle = new OrientedRectangle(labelLayout);
      rectangle.setAngle(rectangle.getAngle() - rotatedLayout.getAngle());
      rectangle.setCenter(rotatedCenter);

      return wrapperModel.createWrappingParameter(
              wrappedFinder.findBestParameter(label, wrapperModel.wrapped, rectangle));
    }
  }
}
