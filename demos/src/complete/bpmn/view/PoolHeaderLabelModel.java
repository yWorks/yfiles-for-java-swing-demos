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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.labelmodels.ConstantLabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.DefaultLabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterProvider;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.Obfuscation;
import java.util.ArrayList;

/**
 * A label model for nodes using a {@link PoolNodeStyle} that position labels inside the
 * {@link ITable#getInsets() table insets}.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class PoolHeaderLabelModel implements ILabelModel, ILabelModelParameterProvider {

  /**
   * The {@link PoolHeaderLabelModel} singleton.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final PoolHeaderLabelModel INSTANCE = new PoolHeaderLabelModel();

  /**
   * A parameter instance using the north insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter NORTH = new PoolHeaderParameter((byte) 0);

  /**
   * A parameter instance using the east insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter EAST = new PoolHeaderParameter((byte) 1);

  /**
   * A parameter instance using the south insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter SOUTH = new PoolHeaderParameter((byte) 2);

  /**
   * A parameter instance using the west insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter WEST = new PoolHeaderParameter((byte) 3);

  static  {
    ArrayList<ILabelModelParameter>  params = new ArrayList<>(4);
    params.add(NORTH);
    params.add(EAST);
    params.add(SOUTH);
    params.add(WEST);
    PARAMETERS = IEnumerable.create(params);
  }
  private static final IEnumerable<ILabelModelParameter> PARAMETERS;


  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public <TLookup> TLookup lookup( Class<TLookup> type ) {
    if (type == ILabelModelParameterProvider.class) {
      return (TLookup)this;
    }
    if (type == ILabelModelParameterFinder.class) {
      return (TLookup)DefaultLabelModelParameterFinder.INSTANCE;
    }
    if (type == ILabelCandidateDescriptorProvider.class) {
      return (TLookup)ConstantLabelCandidateDescriptorProvider.INTERNAL_DESCRIPTOR_PROVIDER;
    }
    return null;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IOrientedRectangle getGeometry( ILabel label, ILabelModelParameter parameter ) {
    PoolHeaderParameter php = (parameter instanceof PoolHeaderParameter) ? (PoolHeaderParameter)parameter : null;
    INode owner = (INode)label.getOwner();
    if (php == null || owner == null) {
      return null;
    }

    ITable table = owner.lookup(ITable.class);
    InsetsD insets = table != null && !InsetsD.equals(table.getInsets(), InsetsD.EMPTY) ? table.getInsets() : new InsetsD(0);

    OrientedRectangle orientedRectangle = new OrientedRectangle();
    orientedRectangle.resize(label.getPreferredSize());
    IRectangle layout = owner.getLayout().toRectD();
    switch (php.getSide()) {
      case 0: // North
        orientedRectangle.setUpVector(0, -1);
        orientedRectangle.setCenter(new PointD(layout.getX() + layout.getWidth() / 2, layout.getY() + insets.top / 2));
        break;
      case 1: // East
        orientedRectangle.setUpVector(1, 0);
        orientedRectangle.setCenter(new PointD(layout.getMaxX() - insets.right / 2, layout.getY() + layout.getHeight() / 2));
        break;
      case 2: // South
        orientedRectangle.setUpVector(0, -1);
        orientedRectangle.setCenter(new PointD(layout.getX() + layout.getWidth() / 2, layout.getMaxY() - insets.bottom / 2));
        break;
      case 3: // West
      default:
        orientedRectangle.setUpVector(-1, 0);
        orientedRectangle.setCenter(new PointD(layout.getX() + insets.left / 2, layout.getY() + layout.getHeight() / 2));
        break;
    }

    return orientedRectangle;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter createDefaultParameter() {
    return WEST;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILookup getContext( ILabel label, ILabelModelParameter parameter ) {
    return ILookup.EMPTY;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IEnumerable<ILabelModelParameter> getParameters( ILabel label, ILabelModel model ) {
    return PARAMETERS;
  }

  @GraphML(singletonContainers = {PoolHeaderLabelModel.class})
  static class PoolHeaderParameter implements ILabelModelParameter {
    private final byte side;

    public final byte getSide() {
      return side;
    }

    public PoolHeaderParameter( byte side ) {
      this.side = side;
    }

    public final PoolHeaderParameter clone() {
      return this;
    }

    public final ILabelModel getModel() {
      return INSTANCE;
    }

    public final boolean supports( ILabel label ) {
      return label.getOwner().lookup(ITable.class) != null;
    }

  }

}
