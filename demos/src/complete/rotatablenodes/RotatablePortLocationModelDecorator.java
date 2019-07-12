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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;


/**
 * Port location model decorator that automatically provides the location in the rotated coordinates of the owner.
 */
public class RotatablePortLocationModelDecorator implements IPortLocationModel, IMarkupExtensionConverter {

  private static final double EPS = Math.toRadians(0.001d);

  /**
   * Default instance.
   */
  public static final RotatablePortLocationModelDecorator INSTANCE = new RotatablePortLocationModelDecorator();

  private IPortLocationModel wrapped = new FreeNodePortLocationModel();


  /**
   * Delegates to the wrapped location model's lookup.
   */
  @Override
  public <TLookup> TLookup lookup(Class<TLookup> type) {
    return getWrapped().lookup(type);
  }

  /**
   * Recalculates the coordinates provided by parameter.
   *
   * This has only an effect when parameter is created by this model
   * and the owner of port has a {@link RotatableNodeStyleDecorator}.
   */
  @Override
  public PointD getLocation(IPort port, IPortLocationModelParameter parameter) {
    IPortLocationModelParameter param = ((RotatablePortLocationModelDecoratorParameter) parameter).getWrapped();
    PointD coreLocation = getWrapped().getLocation(port, param);

    //port owner not an INode
    if (!(port.getOwner() instanceof INode)){
      return coreLocation;
    }

    INode ownerNode = ((INode) port.getOwner());

    double angle = getAngle(ownerNode);

    if (Math.abs(angle) < EPS) {
      return coreLocation;
    }

    PointD center = ownerNode.getLayout().getCenter();
    Matrix2D rotation = new Matrix2D();
    rotation.rotate(-angle, center);

    return rotation.transform(coreLocation.toPointD());
  }

  /**
   * Creates a parameter that matches the location.
   *
   * This implementation undoes the rotation by the portOwner, creates a parameter
   * for this location in {@link #getWrapped()} and wraps this into a model specific parameter.
   * @param location the actual coordinates.
   */
  @Override
  public IPortLocationModelParameter createParameter(IPortOwner portOwner, PointD location) {

    if (portOwner instanceof INode) {
      INode ownerNode = (INode) portOwner;
      double angle = getAngle(ownerNode);
      if(Math.abs(angle) >= EPS) {
        //undo the rotation by the ownerNode so that we can create a core parameter for the unrotated layout
        PointD center = ownerNode.getLayout().getCenter();
        Matrix2D rotation = new Matrix2D();
        rotation.rotate(angle, center);

        location = rotation.transform(location.toPointD());
      }
    }

    return new RotatablePortLocationModelDecoratorParameter(getWrapped().createParameter(portOwner, location), this);
  }

  /**
   * Wraps a given parameter so it can be automatically rotated.
   * @param coreParameter is assumed to provide coordinates for a unrotated owner.
   */
  IPortLocationModelParameter createWrappingParameter(IPortLocationModelParameter coreParameter) {
    return new RotatablePortLocationModelDecoratorParameter(coreParameter, this);
  }

  /**
   * Returns the lookup of the wrapped location model.
   */
  @Override
  public ILookup getContext(IPort port, IPortLocationModelParameter parameter) {
    return getWrapped().getContext(port, parameter);
  }

  /**
   * Returns that this port location model can be converted.
   */
  @Override
  public boolean canConvert(IWriteContext context,Object value) {
    return true;
  }

  /**
   * Converts this port location model using {@link RotatablePortLocationModelDecoratorExtension}.
   */
  @Override
  public MarkupExtension convert(IWriteContext context, Object value) {
    RotatablePortLocationModelDecoratorExtension extension = new RotatablePortLocationModelDecoratorExtension();
    extension.setWrapped(getWrapped());
    return extension;
  }

  /**
   * Returns the current angle of the given rotated node in radians.
   */
  private static double getAngle(INode ownerNode) {
    INodeStyle style = ownerNode.getStyle();
    return style instanceof RotatableNodeStyleDecorator ? ((RotatableNodeStyleDecorator) style).getAngle() : 0;
  }

  public IPortLocationModel getWrapped() {
    return wrapped;
  }

  public void setWrapped(IPortLocationModel wrapped) {
    this.wrapped = wrapped;
  }

  /**
   * An {@link IPortLocationModelParameter} decorator for ports using {@link RotatablePortLocationModelDecorator}
   * to adjust the port location to the node rotation.
   */
  private static class RotatablePortLocationModelDecoratorParameter implements IPortLocationModelParameter, IMarkupExtensionConverter {

    private final IPortLocationModelParameter wrapped;
    private final RotatablePortLocationModelDecorator model;

    /**
     * Initialize a new instance.
     */
    public RotatablePortLocationModelDecoratorParameter(IPortLocationModelParameter wrapped, RotatablePortLocationModelDecorator model) {
      this.wrapped = wrapped;
      this.model = model;
    }

    /**
     * Creates a copy of this location model parameter.
     */
    @Override
    public Object clone() {
      return new RotatablePortLocationModelDecoratorParameter(((IPortLocationModelParameter) wrapped.clone()), model);
    }

    /**
     * Accepts all port owners that are supported by the wrapped parameter.
     */
    public boolean supports(IPortOwner portOwner) {
      return wrapped.supports(portOwner);
    }

    /**
     * Returns that this port location model parameter can be converted.
     */
    @Override
    public boolean canConvert(IWriteContext context, Object value) {
      return true;
    }

    /**
     * Converts this port location model parameter using {@link RotatablePortLocationModelDecoratorParameterExtension}.
     */
    @Override
    public MarkupExtension convert(IWriteContext iWriteContext, Object o) {
      RotatablePortLocationModelDecoratorParameterExtension extension = new RotatablePortLocationModelDecoratorParameterExtension();
      extension.setModel(model == INSTANCE ? null : model);
      extension.setWrapped(wrapped);
      return extension;
    }

    public IPortLocationModel getModel() {
      return model;
    }

    public IPortLocationModelParameter getWrapped() {
      return wrapped;
    }
  }
}

