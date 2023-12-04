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
package input.customportmodel;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.GraphML;

/**
 * Custom implementation of {@link com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel} that provides five discrete port
 * locations, one at the node center and one at each side.
 */
public class MyNodePortLocationModel implements IPortLocationModel {
  private double inset;

  public MyNodePortLocationModel() {
    this(0.0d);
  }

  public MyNodePortLocationModel(double inset) {
    this.inset = inset;
  }

  /**
   * Gets the inset of the port location, i.e. the distance to the node layout borders. This is ignored for the {@link
   * PortLocation#CENTER} position.
   */
  @DefaultValue(doubleValue = 0.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public double getInset() {
    return inset;
  }

  /**
   * Sets the inset of the port location, i.e. the distance to the node layout borders. This is ignored for the {@link
   * PortLocation#CENTER} position.
   */
  @DefaultValue(doubleValue = 0.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public void setInset(double value) {
    inset = value;
  }

  public <TLookup> TLookup lookup(Class<TLookup> type) {
    return null;
  }

  /**
   * Determines the actual absolute world location of the port for the given parameter.
   * @param port      The port to determine the location for.
   * @param parameter The parameter to use.
   * @return The calculated location of the port.
   */
  public PointD getLocation(IPort port, IPortLocationModelParameter parameter) {
    if (parameter instanceof MyNodePortLocationModelParameter && port.getOwner() instanceof INode) {
      MyNodePortLocationModelParameter modelParameter = (MyNodePortLocationModelParameter) parameter;
      INode ownerNode = (INode) port.getOwner();
      //If we have an actual owner node and the parameter can be really used by this model,
      //we just calculate the correct location, based on the node's layout.
      IRectangle layout = ownerNode.getLayout();
      switch (modelParameter.getLocation()) {
        case CENTER:
          return layout.getCenter();
        case NORTH:
          return new PointD(layout.getX() + 0.5d * layout.getWidth(), layout.getY() + getInset());
        case SOUTH:
          return new PointD(layout.getX() + 0.5d * layout.getWidth(), layout.getMaxY() - getInset());
        case EAST:
          return new PointD(layout.getMaxX() - getInset(), layout.getY() + 0.5d * layout.getHeight());
        case WEST:
          return new PointD(layout.getX() + getInset(), layout.getY() + 0.5d * layout.getHeight());
        default:
          throw new IllegalArgumentException("Unknown model parameter.");
      }
    } else {
      //No owner node (e.g. an edge port), or parameter mismatch - return (0,0)
      return PointD.ORIGIN;
    }
  }

  /**
   * Factory method that creates a parameter for the given port that tries to match the provided location in absolute
   * world coordinates.
   * <p>
   * While you are free to return arbitrary implementations of {@link IPortLocationModelParameter}, you usually want to
   * use a specialized implementation that corresponds to your model. Here we return {@link MyNodePortLocationModelParameter}
   * instances. Note that for discrete port models, you'll want to use some discretization of the coordinate space. This
   * means that calculating the port location with {@link #getLocation} for the returned parameter does not necessarily
   * result in the coordinates of the {@code location} for which the parameter was created. Still, the given location should
   * coordinate subspace represented by the returned parameter (otherwise the behaviour of the model is very confusing).
   * </p>
   * @param portOwner The port owner that owns the port for which the parameter shall be created.
   * @param location  The location in the world coordinate system that should be matched as best as possible.
   * @return A new instance that can be used to describe the location of an {@link IPort} at the given {@code
   * portOwner}.
   */
  public IPortLocationModelParameter createParameter(IPortOwner portOwner, PointD location) {
    if (portOwner instanceof INode) {
      INode ownerNode = (INode) portOwner;
      //determine the distance of the specified location to the node layout center
      PointD delta = PointD.subtract(location, ownerNode.getLayout().getCenter());
      double minSize = Math.min(ownerNode.getLayout().getWidth(), ownerNode.getLayout().getHeight());
      if (delta.getVectorLength() < 0.25d * minSize) {
        //nearer to the center than to the border => map to center
        return createParameter(PortLocation.CENTER);
      } else {
        //map to a location on the side
        if (Math.abs(delta.getX()) > Math.abs(delta.getY())) {
          return createParameter(delta.getX() > 0 ? PortLocation.EAST : PortLocation.WEST);
        } else {
          return createParameter(delta.getY() > 0 ? PortLocation.SOUTH : PortLocation.NORTH);
        }
      }
    } else {
      //Just return  a fallback  - GetLocation will ignore this anyway if the owner is null or not a node.
      return createParameter(PortLocation.CENTER);
    }
  }

  public IPortLocationModelParameter createParameter(PortLocation location) {
    return new MyNodePortLocationModelParameter(this, location);
  }

  public ILookup getContext(IPort label, IPortLocationModelParameter parameter) {
    return ILookup.EMPTY;
  }

  /**
   * Custom {@link IPortLocationModelParameter} implementation for {@link MyNodePortLocationModel} instances.
   * This implementation just stores one of the symbolic {@link PortLocation} instances.
   */
  @GraphML(markupExtensionConverter = MyNodePortLocationModelParameterConverter.class)
  static class MyNodePortLocationModelParameter implements IPortLocationModelParameter {
    private final MyNodePortLocationModel owner;
    private final PortLocation location;

    public MyNodePortLocationModelParameter(MyNodePortLocationModel owner, PortLocation location) {
      this.owner = owner;
      this.location = location;
    }

    PortLocation getLocation() {
      return location;
    }

    public Object clone() {
      // we have no mutable state, so return this.
      return this;
    }

    /**
     * Returns a model instance where this parameter belongs to. This is usually a reference to the model instance that
     * has created this parameter.
     */
    public IPortLocationModel getModel() {
      return owner;
    }

    /**
     * Determines if this parameter instance may be used to describe ports for the given {@code owner}.
     * Our model/parameter implementation only makes sense when used for {@link INode}s.
     */
    public boolean supports(IPortOwner owner) {
      return owner instanceof INode;
    }
  }

}