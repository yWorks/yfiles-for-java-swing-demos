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

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ConstantLabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.DefaultLabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterProvider;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.labelmodels.SandwichLabelModel;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.Obfuscation;
import java.util.ArrayList;

/**
 * A label model for nodes using a {@link ChoreographyNodeStyle} that position labels on the participant or task name
 * bands.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ChoreographyLabelModel implements ILabelModel, ILabelModelParameterProvider {

  /**
   * The {@link ChoreographyLabelModel} singleton.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ChoreographyLabelModel INSTANCE;

  /**
   * A singleton for labels placed centered on the task name band.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter TASK_NAME_BAND;

  /**
   * A singleton for message labels placed north of the node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter NORTH_MESSAGE;

  /**
   * A singleton for message labels placed south of the node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ILabelModelParameter SOUTH_MESSAGE;

  private static final InteriorStretchLabelModel INTERIOR_STRETCH_MODEL;

  private static final SimpleNode DUMMY_NODE = new SimpleNode();

  private static final SimpleLabel DUMMY_LABEL = new SimpleLabel(DUMMY_NODE, "", InteriorStretchLabelModel.CENTER);


  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IOrientedRectangle getGeometry( ILabel label, ILabelModelParameter parameter ) {
    if (parameter instanceof ChoreographyParameter && label.getOwner() instanceof INode
        && ((INode)label.getOwner()).getStyle() instanceof ChoreographyNodeStyle) {
      return ((ChoreographyParameter)parameter).getGeometry(label);
    } else if (label.getOwner() instanceof INode) {
      IRectangle layout = ((INode)label.getOwner()).getLayout();
      return new OrientedRectangle(layout.getX(), layout.getY() + layout.getHeight(), layout.getWidth(), layout.getHeight(), 0, -1);
    }
    return IOrientedRectangle.EMPTY;
  }

  /**
   * Returns {@link #TASK_NAME_BAND} as default parameter.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter createDefaultParameter() {
    return TASK_NAME_BAND;
  }

  /**
   * Creates the parameter for the participant at the given position.
   * @param top Whether the index refers to {@link ChoreographyNodeStyle#getTopParticipants() TopParticipants} or
   * {@link ChoreographyNodeStyle#getBottomParticipants() BottomParticipants}.
   * @param index The index of the participant band the label shall be placed in.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter createParticipantParameter( boolean top, int index ) {
    return new ParticipantParameter(top, index);
  }

  /**
   * Determines, if these two parameters are equal.
   */
  public static final boolean areEqual( ILabelModelParameter parameter1, ILabelModelParameter parameter2 ) {
    if (parameter1 instanceof ParticipantParameter && parameter2 instanceof ParticipantParameter) {
      if (((ParticipantParameter)parameter1).index == ((ParticipantParameter)parameter2).index && ((ParticipantParameter)parameter1).top == ((ParticipantParameter)parameter2).top) {
        return true;
      }
    }

    if (parameter1 instanceof TaskNameBandParameter && parameter2 instanceof TaskNameBandParameter) {
      return true;
    }

    if (parameter1 instanceof MessageParameter && parameter2 instanceof MessageParameter) {
      if (((MessageParameter)parameter1).isNorth() == ((MessageParameter)parameter2).isNorth()) {
        return true;
      }
    }

    return false;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILookup getContext( ILabel label, ILabelModelParameter parameter ) {
    return InteriorLabelModel.CENTER.getModel().getContext(label, parameter);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final <TLookup> TLookup lookup( Class<TLookup> type ) {
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
  public final IEnumerable<ILabelModelParameter> getParameters( ILabel label, ILabelModel model ) {
    ILabelOwner owner = label.getOwner();
    if(!(owner instanceof INode)) {
      return IEnumerable.EMPTY;
    }
    ArrayList<ILabelModelParameter> parameters = new ArrayList<>();
    INode node = (INode)owner;
    if (node.getStyle() instanceof ChoreographyNodeStyle) {
      ChoreographyNodeStyle nodeStyle = (ChoreographyNodeStyle)node.getStyle();
      for (int i = 0; i < nodeStyle.getTopParticipants().size(); i++) {
        parameters.add(createParticipantParameter(true, i));
      }
      parameters.add(TASK_NAME_BAND);
      for (int i = 0; i < nodeStyle.getBottomParticipants().size(); i++) {
        parameters.add(createParticipantParameter(false, i));
      }
      parameters.add(NORTH_MESSAGE);
      parameters.add(SOUTH_MESSAGE);
    }

    return IEnumerable.create(parameters);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter findNextParameter( INode node ) {
    INodeStyle style = node.getStyle();
    ChoreographyNodeStyle nodeStyle = style instanceof ChoreographyNodeStyle ? (ChoreographyNodeStyle) style : null;
    if (nodeStyle != null) {
      int taskNameBandCount = 1;
      int topParticipantCount = nodeStyle.getTopParticipants().size();
      int bottomParticipantCount = nodeStyle.getBottomParticipants().size();
      int messageCount = 2;

      boolean[] parameterTaken = new boolean[taskNameBandCount + topParticipantCount + bottomParticipantCount + messageCount];

      // check which label positions are already taken
      for (ILabel label : node.getLabels()) {
        ILabelModelParameter param = label.getLayoutParameter();
        ChoreographyParameter parameter = param instanceof ChoreographyParameter ? (ChoreographyParameter)param : null;
        if (parameter != null) {
          int index = 0;
          if (!(parameter instanceof TaskNameBandParameter)) {
            index++;

            if (parameter instanceof ParticipantParameter) {
              ParticipantParameter pp = (ParticipantParameter)parameter;
              if (!pp.top) {
                index += topParticipantCount;
              }
              index += pp.index;
            } else {
              index += topParticipantCount + bottomParticipantCount;
              if (!((MessageParameter)parameter).isNorth()) {
                index++;
              }
            }
          }
          parameterTaken[index] = true;
        }
      }

      // get first label position that isn't taken already
      for (int i = 0; i < parameterTaken.length; i++) {
        if (!parameterTaken[i]) {
          if (i < taskNameBandCount) {
            return TASK_NAME_BAND;
          }
          i -= taskNameBandCount;
          if (i < topParticipantCount) {
            return createParticipantParameter(true, i);
          }
          i -= topParticipantCount;
          if (i < bottomParticipantCount) {
            return createParticipantParameter(false, i);
          }
          i -= bottomParticipantCount;
          return i == 0 ? NORTH_MESSAGE : SOUTH_MESSAGE;
        }
      }
    }
    return null;
  }


  private abstract static class ChoreographyParameter implements ILabelModelParameter {
    public final ILabelModel getModel() {
      return INSTANCE;
    }

    public abstract IOrientedRectangle getGeometry( ILabel label );

    public final boolean supports( ILabel label ) {
      return label.getOwner() instanceof INode;
    }

    @Override
    public ChoreographyParameter clone() {
      try {
        return (ChoreographyParameter) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
      }
    }
  }

  @GraphML(markupExtensionConverter = ParticipantParameterConverter.class)
  private static class ParticipantParameter extends ChoreographyParameter {
    private static final InteriorLabelModel ILM;
    private static final ILabelModelParameter PLACEMENT;

    static {
      ILM = new InteriorLabelModel();
      ILM.setInsets(new InsetsD(3));
      PLACEMENT = ILM.createParameter(InteriorLabelModel.Position.NORTH);
    }
    final int index;

    final boolean top;

    public ParticipantParameter( boolean top, int index ) {
      this.top = top;
      this.index = index;
    }

    @Override
    public IOrientedRectangle getGeometry( ILabel label ) {
      if (!(label.getOwner() instanceof INode)) {
        return IOrientedRectangle.EMPTY;
      }
      INode node = (INode)label.getOwner();
      if (!(node.getStyle() instanceof ChoreographyNodeStyle)) {
        return IOrientedRectangle.EMPTY;
      }
      ChoreographyNodeStyle style = (ChoreographyNodeStyle)node.getStyle();
      DUMMY_NODE.setLayout(style.getParticipantBandBounds(node, index, top));
      DUMMY_LABEL.setPreferredSize(label.getPreferredSize());
      return ILM.getGeometry(DUMMY_LABEL, PLACEMENT);
    }

    @Override
    public ParticipantParameter clone() {
      return (ParticipantParameter)this;
    }

  }

  public static final class ParticipantParameterConverter implements IMarkupExtensionConverter {
    public final boolean canConvert( IWriteContext context, Object value ) {
      return value instanceof ChoreographyLabelModel.ParticipantParameter;
    }

    public final MarkupExtension convert( IWriteContext context, Object value ) {
      ChoreographyLabelModel.ParticipantParameter participantParameter = (ChoreographyLabelModel.ParticipantParameter)value;
      ParticipantLabelModelParameterExtension participantLabelModelParameterExtension = new ParticipantLabelModelParameterExtension();
      participantLabelModelParameterExtension.setIndex(participantParameter.index);
      participantLabelModelParameterExtension.setTop(participantParameter.top);
      return participantLabelModelParameterExtension;
    }

  }

  @GraphML(singletonContainers = {ChoreographyLabelModel.class})
  private static final class TaskNameBandParameter extends ChoreographyParameter {
    @Override
    public IOrientedRectangle getGeometry( ILabel label ) {
      if (!(label.getOwner() instanceof INode)) {
        return IOrientedRectangle.EMPTY;
      }
      INode node = (INode)label.getOwner();
      if (!(node.getStyle() instanceof ChoreographyNodeStyle)) {
        return IOrientedRectangle.EMPTY;
      }
      ChoreographyNodeStyle style = (ChoreographyNodeStyle)node.getStyle();
      RectD bandBounds = style.getTaskNameBandBounds(node);
      DUMMY_NODE.setLayout(bandBounds);
      DUMMY_LABEL.setPreferredSize(label.getPreferredSize());
      return INTERIOR_STRETCH_MODEL.getGeometry(DUMMY_LABEL, InteriorStretchLabelModel.CENTER);
    }

    @Override
    public TaskNameBandParameter clone() {
      return (TaskNameBandParameter)this;
    }

  }

  @GraphML(singletonContainers = {ChoreographyLabelModel.class})
  private static final class MessageParameter extends ChoreographyParameter implements Cloneable {
    private static final ILabelModelParameter NORTH_PARAMETER;

    private static final ILabelModelParameter SOUTH_PARAMETER;

    private boolean north;

    public final boolean isNorth() {
      return this.north;
    }

    public final void setNorth( boolean value ) {
      this.north = value;
    }

    @Override
    public IOrientedRectangle getGeometry( ILabel label ) {
      ILabelModelParameter parameter = isNorth() ? NORTH_PARAMETER : SOUTH_PARAMETER;
      return parameter.getModel().getGeometry(label, parameter);
    }

    @Override
    public MessageParameter clone() {
      return (MessageParameter)super.clone();
    }

    static {
      SandwichLabelModel slm = new SandwichLabelModel();
      slm.setYOffset(32);
      NORTH_PARAMETER = slm.createNorthParameter();
      SOUTH_PARAMETER = slm.createSouthParameter();
    }

  }

  static {
    INSTANCE = new ChoreographyLabelModel();
    TASK_NAME_BAND = new TaskNameBandParameter();
    MessageParameter messageParameter = new MessageParameter();
    messageParameter.setNorth(true);
    NORTH_MESSAGE = messageParameter;
    MessageParameter messageParameter2 = new MessageParameter();
    messageParameter2.setNorth(false);
    SOUTH_MESSAGE = messageParameter2;

    InteriorStretchLabelModel labelModel = new InteriorStretchLabelModel();
    labelModel.setInsets(new InsetsD(3, 3, 3, 3));
    INTERIOR_STRETCH_MODEL = labelModel;
  }

}
