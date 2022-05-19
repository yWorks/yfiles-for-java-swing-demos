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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyleRenderer;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.GraphMLMemberVisibility;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IEditLabelHelper;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import com.yworks.yfiles.view.input.LabelEditingEventArgs;
import com.yworks.yfiles.view.input.NodeSizeConstraintProvider;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Choreography according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ChoreographyNodeStyle extends BpmnNodeStyle {
  private static final ShapeNodeStyle SNS;

  static {
    final ShapeNodeStyleRenderer renderer = new ShapeNodeStyleRenderer();
    renderer.setRoundRectArcRadius(BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS);
    final ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle(renderer);
    shapeNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    shapeNodeStyle.setPen(Pen.getBlack());
    shapeNodeStyle.setPaint(null);
    SNS = shapeNodeStyle;
  }

  private IIcon topInitiatingMessageIcon;

  private IIcon bottomResponseMessageIcon;

  private IIcon bottomInitiatingMessageIcon;

  private IIcon topResponseMessageIcon;

  private IIcon taskBandBackgroundIcon;

  private IIcon multiInstanceIcon;

  private IIcon messageLineIcon;

  private IIcon initiatingMessageIcon;

  private IIcon responseMessageIcon;

  private static final int MESSAGE_DISTANCE = 15;

  private ChoreographyType type;

  /**
   * Gets the choreography type of this style.
   * @return The Type.
   * @see #setType(ChoreographyType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChoreographyType.class, stringValue = "TASK")
  public final ChoreographyType getType() {
    return type;
  }

  /**
   * Sets the choreography type of this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChoreographyType.class, stringValue = "TASK")
  public final void setType( ChoreographyType value ) {
    if (type != value || outlineIcon == null) {
      incrementModCount();
      type = value;
      updateOutlineIcon();
    }
  }

  private LoopCharacteristic loopCharacteristic;

  /**
   * Gets the loop characteristic of this style.
   * @return The LoopCharacteristic.
   * @see #setLoopCharacteristic(LoopCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LoopCharacteristic.class, stringValue = "NONE")
  public final LoopCharacteristic getLoopCharacteristic() {
    return loopCharacteristic;
  }

  /**
   * Sets the loop characteristic of this style.
   * @param value The LoopCharacteristic to set.
   * @see #getLoopCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LoopCharacteristic.class, stringValue = "NONE")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    if (loopCharacteristic != value) {
      incrementModCount();
      loopCharacteristic = value;
      updateLoopIcon();
    }
  }

  private SubState subState;

  /**
   * Gets the sub state of this style.
   * @return The SubState.
   * @see #setSubState(SubState)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubState.class, stringValue = "NONE")
  public final SubState getSubState() {
    return subState;
  }

  /**
   * Sets the sub state of this style.
   * @param value The SubState to set.
   * @see #getSubState()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubState.class, stringValue = "NONE")
  public final void setSubState( SubState value ) {
    if (subState != value) {
      incrementModCount();
      subState = value;
      updateTaskBandIcon();
    }
  }

  private boolean initiatingMessage;

  /**
   * Gets whether the initiating message icon is displayed.
   * @return The InitiatingMessage.
   * @see #setInitiatingMessage(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isInitiatingMessage() {
    return initiatingMessage;
  }

  /**
   * Sets whether the initiating message icon is displayed.
   * @param value The InitiatingMessage to set.
   * @see #isInitiatingMessage()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setInitiatingMessage( boolean value ) {
    if (initiatingMessage != value) {
      incrementModCount();
      initiatingMessage = value;
    }
  }

  private boolean responseMessage;

  /**
   * Gets whether the response message icon is displayed.
   * @return The ResponseMessage.
   * @see #setResponseMessage(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isResponseMessage() {
    return responseMessage;
  }

  /**
   * Sets whether the response message icon is displayed.
   * @param value The ResponseMessage to set.
   * @see #isResponseMessage()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setResponseMessage( boolean value ) {
    if (responseMessage != value) {
      incrementModCount();
      responseMessage = value;
    }
  }

  private boolean initiatingAtTop = true;

  /**
   * Gets whether the initiating message icon or the response message icon is displayed on top of the node while the other
   * one is at the bottom side.
   * <p>
   * Whether the initiating and response message icons are displayed at all depends on {@link #isInitiatingMessage() InitiatingMessage}
   * and
   * {@link #isResponseMessage() ResponseMessage}. This property only determines which one is displayed on which side of
   * the node.
   * </p>
   * @return The InitiatingAtTop.
   * @see #setInitiatingAtTop(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isInitiatingAtTop() {
    return initiatingAtTop;
  }

  /**
   * Sets whether the initiating message icon or the response message icon is displayed on top of the node while the other
   * one is at the bottom side.
   * <p>
   * Whether the initiating and response message icons are displayed at all depends on {@link #isInitiatingMessage() InitiatingMessage}
   * and
   * {@link #isResponseMessage() ResponseMessage}. This property only determines which one is displayed on which side of
   * the node.
   * </p>
   * @param value The InitiatingAtTop to set.
   * @see #isInitiatingAtTop()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setInitiatingAtTop( boolean value ) {
    if (initiatingAtTop != value) {
      initiatingAtTop = value;
      if (isInitiatingMessage() || isResponseMessage()) {
        incrementModCount();
      }
    }
  }

  private ParticipantList topParticipants = new ParticipantList();

  /**
   * Gets the list of {@link Participant}s at the top of the node, ordered from top to bottom.
   * @return The TopParticipants.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(visibility = GraphMLMemberVisibility.CONTENT)
  public final Collection<Participant> getTopParticipants() {
    return topParticipants;
  }

  private ParticipantList bottomParticipants = new ParticipantList();

  /**
   * Gets the list of {@link Participant}s at the bottom of the node, ordered from bottom to top.
   * @return The BottomParticipants.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(visibility = GraphMLMemberVisibility.CONTENT)
  public final Collection<Participant> getBottomParticipants() {
    return bottomParticipants;
  }

  private Paint background = BpmnConstants.CHOREOGRAPHY_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the choreography.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the choreography.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
      updateTaskBandIcon();
    }
  }

  private Paint outline = BpmnConstants.CHOREOGRAPHY_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the choreography.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the choreography.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
      updateOutlineIcon();
    }
  }

  private Paint iconColor = BpmnConstants.CHOREOGRAPHY_DEFAULT_ICON_COLOR;

  /**
   * Gets the primary color for icons and markers.
   * @return The IconColor.
   * @see #setIconColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultIconColor", classValue = BpmnConstants.class)
  public final Paint getIconColor() {
    return iconColor;
  }

  /**
   * Sets the primary color for icons and markers.
   * @param value The IconColor to set.
   * @see #getIconColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultIconColor", classValue = BpmnConstants.class)
  public final void setIconColor( Paint value ) {
    if (iconColor != value) {
      setModCount(getModCount() + 1);
      iconColor = value;
      updateMultiInstanceIcon();
      updateLoopIcon();
      updateTaskBandIcon();
    }
  }

  private Paint initiatingColor = BpmnConstants.CHOREOGRAPHY_DEFAULT_INITIATING_COLOR;

  /**
   * Gets the color for initiating participants and messages.
   * @return The InitiatingColor.
   * @see #setInitiatingColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultInitiatingColor", classValue = BpmnConstants.class)
  public final Paint getInitiatingColor() {
    return initiatingColor;
  }

  /**
   * Sets the color for initiating participants and messages.
   * @param value The InitiatingColor to set.
   * @see #getInitiatingColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultInitiatingColor", classValue = BpmnConstants.class)
  public final void setInitiatingColor( Paint value ) {
    if (initiatingColor != value) {
      setModCount(getModCount() + 1);
      initiatingColor = value;
    }
  }

  private Paint responseColor = BpmnConstants.CHOREOGRAPHY_DEFAULT_RESPONSE_COLOR;

  /**
   * Gets the primary color for responding participants and messages.
   * @return The ResponseColor.
   * @see #setResponseColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultResponseColor", classValue = BpmnConstants.class)
  public final Paint getResponseColor() {
    return responseColor;
  }

  /**
   * Sets the primary color for responding participants and messages.
   * @param value The ResponseColor to set.
   * @see #getResponseColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultResponseColor", classValue = BpmnConstants.class)
  public final void setResponseColor( Paint value ) {
    if (responseColor != value) {
      setModCount(getModCount() + 1);
      responseColor = value;
    }
  }

  private Paint messageOutline;

  Pen messagePen;

  private Pen messageLinePen;

  /**
   * Gets the outline color for messages.
   * <p>
   * This also influences the color of the line to the message.
   * </p>
   * @return The MessageOutline.
   * @see #setMessageOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultMessageOutline", classValue = BpmnConstants.class)
  public final Paint getMessageOutline() {
    return messageOutline;
  }

  /**
   * Sets the outline color for messages.
   * <p>
   * This also influences the color of the line to the message.
   * </p>
   * @param value The MessageOutline to set.
   * @see #getMessageOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyDefaultMessageOutline", classValue = BpmnConstants.class)
  public final void setMessageOutline( Paint value ) {
    if (messageOutline != value) {
      setModCount(getModCount() + 1);
      messageOutline = value;
      messagePen = (Pen)new Pen(messageOutline, 1);
      Pen pen = new Pen(messageOutline, 1);
      pen.setDashStyle(DashStyle.getDot());
      pen.setEndCap(BasicStroke.CAP_ROUND);
      messageLinePen = (Pen)pen;
      updateMessageLineIcon();
      updateInitiatingMessageIcon();
      updateResponseMessageIcon();
    }
  }

  /**
   * Gets the insets for the task name band of the given item.
   * <p>
   * These insets are extended by the sizes of the participant bands on top and bottom side and returned via an {@link INodeInsetsProvider}
   * if such an instance is queried through the
   * {@link com.yworks.yfiles.graph.styles.INodeStyleRenderer#getContext(INode, com.yworks.yfiles.graph.styles.INodeStyle) context lookup}.
   * </p>
   * @return The Insets.
   * @see INodeInsetsProvider
   * @see #setInsets(InsetsD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "5", classValue = InsetsD.class)
  public final InsetsD getInsets() {
    return insets;
  }

  /**
   * Sets the insets for the task name band of the given item.
   * <p>
   * These insets are extended by the sizes of the participant bands on top and bottom side and returned via an {@link INodeInsetsProvider}
   * if such an instance is queried through the
   * {@link com.yworks.yfiles.graph.styles.INodeStyleRenderer#getContext(INode, com.yworks.yfiles.graph.styles.INodeStyle) context lookup}.
   * </p>
   * @param value The Insets to set.
   * @see INodeInsetsProvider
   * @see #getInsets()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "5", classValue = InsetsD.class)
  public final void setInsets( InsetsD value ) {
    insets = value;
  }

  private boolean isShowTopMessage() {
    return (isInitiatingMessage() && isInitiatingAtTop()) || (isResponseMessage() && !isInitiatingAtTop());
  }

  private boolean isShowBottomMessage() {
    return (isInitiatingMessage() && !isInitiatingAtTop()) || (isResponseMessage() && isInitiatingAtTop());
  }

  private IIcon outlineIcon;

  private IIcon loopIcon;

  private InsetsD insets = new InsetsD(5);

  /**
   * Creates a new instance.
   */
  public ChoreographyNodeStyle() {
    setType(ChoreographyType.TASK);
    setMessageOutline(BpmnConstants.CHOREOGRAPHY_DEFAULT_MESSAGE_OUTLINE);
    setMinimumSize(new SizeD(30, 30));
    setLoopCharacteristic(LoopCharacteristic.NONE);
    setSubState(SubState.NONE);
  }

  private void updateOutlineIcon() {
    outlineIcon = IconFactory.createChoreography(type, getOutline());
    if (type == ChoreographyType.CALL) {
      outlineIcon = new PlacedIcon(outlineIcon, BpmnConstants.THICK_LINE_PLACEMENT, SizeD.EMPTY);
    }
  }

  private void updateTaskBandIcon() {
    taskBandBackgroundIcon = IconFactory.createChoreographyTaskBand(getBackground());
  }

  private void updateMessageLineIcon() {
    messageLineIcon = IconFactory.createLine(messageLinePen, 0.5, 0, 0.5, 1);
  }

  private void updateInitiatingMessageIcon() {
    initiatingMessageIcon = IconFactory.createMessage(messagePen, getInitiatingColor(), false);
    updateMessageLineIcon();
    updateTopInitiatingMessageIcon();
    updateBottomInitiatingMessageIcon();
  }

  private void updateTopInitiatingMessageIcon() {
    topInitiatingMessageIcon = IconFactory.createCombinedIcon(Arrays.asList(new IIcon[]{IconFactory.createPlacedIcon(messageLineIcon, ExteriorLabelModel.NORTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)), IconFactory.createPlacedIcon(initiatingMessageIcon, BpmnConstants.CHOREOGRAPHY_TOP_MESSAGE_PLACEMENT, BpmnConstants.MESSAGE_SIZE)}));
  }

  private void updateBottomInitiatingMessageIcon() {
    bottomInitiatingMessageIcon = IconFactory.createCombinedIcon(Arrays.asList(new IIcon[]{IconFactory.createPlacedIcon(messageLineIcon, ExteriorLabelModel.SOUTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)), IconFactory.createPlacedIcon(initiatingMessageIcon, BpmnConstants.CHOREOGRAPHY_BOTTOM_MESSAGE_PLACEMENT, BpmnConstants.MESSAGE_SIZE)}));
  }

  private void updateResponseMessageIcon() {
    responseMessageIcon = IconFactory.createMessage(messagePen, getResponseColor(), false);
    updateMessageLineIcon();
    updateTopResponseMessageIcon();
    updateBottomResponseMessageIcon();
  }

  private void updateTopResponseMessageIcon() {
    topResponseMessageIcon = IconFactory.createCombinedIcon(Arrays.asList(new IIcon[]{IconFactory.createPlacedIcon(messageLineIcon, ExteriorLabelModel.NORTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)), IconFactory.createPlacedIcon(responseMessageIcon, BpmnConstants.CHOREOGRAPHY_TOP_MESSAGE_PLACEMENT, BpmnConstants.MESSAGE_SIZE)}));
  }

  private void updateBottomResponseMessageIcon() {
    bottomResponseMessageIcon = IconFactory.createCombinedIcon(Arrays.asList(new IIcon[]{IconFactory.createPlacedIcon(messageLineIcon, ExteriorLabelModel.SOUTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)), IconFactory.createPlacedIcon(responseMessageIcon, BpmnConstants.CHOREOGRAPHY_BOTTOM_MESSAGE_PLACEMENT, BpmnConstants.MESSAGE_SIZE)}));
  }

  private void updateMultiInstanceIcon() {
    multiInstanceIcon = IconFactory.createPlacedIcon(IconFactory.createLoopCharacteristic(LoopCharacteristic.PARALLEL, getIconColor()), BpmnConstants.CHOREOGRAPHY_MARKER_PLACEMENT, BpmnConstants.MARKER_SIZE);
  }

  private void updateLoopIcon() {
    loopIcon = IconFactory.createLoopCharacteristic(getLoopCharacteristic(), getIconColor());
  }


  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected IVisual createVisual( IRenderContext context, INode node ) {
    RectD bounds = node.getLayout().toRectD();
    ChoreographyContainer container = new ChoreographyContainer();

    // task band
    TaskBandContainer taskBandContainer = new TaskBandContainer();
    IIcon bandIcon = createTaskBandIcon(node);
    bandIcon.setBounds(getRelativeTaskNameBandBounds(node));
    taskBandContainer.add(bandIcon.createVisual(context));
    taskBandContainer.setIcon(bandIcon);
    container.getChildren().add(taskBandContainer);

    ArrayList<IIcon> tpi = new ArrayList<>();
    // top participants
    double topOffset = 0;
    boolean first = true;
    for (Participant participant : topParticipants) {
      IIcon participantIcon = createParticipantIcon(participant, true, first);
      tpi.add(participantIcon);
      double height = participant.getSize();
      participantIcon.setBounds(new RectD(0, topOffset, bounds.width, height));
      container.add(participantIcon.createVisual(context));
      topOffset += height;
      first = false;
    }

    ArrayList<IIcon> bpi = new ArrayList<>();
    // bottom participants
    double bottomOffset = bounds.height;
    first = true;
    for (Participant participant : bottomParticipants) {
      IIcon participantIcon = createParticipantIcon(participant, false, first);
      bpi.add(participantIcon);
      double height = participant.getSize();
      bottomOffset -= height;
      participantIcon.setBounds(new RectD(0, bottomOffset, bounds.width, height));
      container.add(participantIcon.createVisual(context));
      first = false;
    }

    // outline
    outlineIcon.setBounds(new RectD(PointD.ORIGIN, bounds.getSize()));
    container.add(outlineIcon.createVisual(context));

    // messages
    if (isInitiatingMessage()) {
      updateInitiatingMessageIcon();
      IIcon initiatingMessageIcon = isInitiatingAtTop() ? topInitiatingMessageIcon : bottomInitiatingMessageIcon;
      initiatingMessageIcon.setBounds(new RectD(0, 0, bounds.width, bounds.height));
      container.add(initiatingMessageIcon.createVisual(context));
    }
    if (isResponseMessage()) {
      updateResponseMessageIcon();
      IIcon responseMessageIcon = isInitiatingAtTop() ? bottomResponseMessageIcon : topResponseMessageIcon;
      responseMessageIcon.setBounds(new RectD(0, 0, bounds.width, bounds.height));
      container.add(responseMessageIcon.createVisual(context));
    }

    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));

    container.setModCount(getModCount() + topParticipants.getModCount() + bottomParticipants.getModCount());
    container.setBounds(bounds);
    container.setBottomParticipantIcons(bpi);
    container.setTopParticipantIcons(tpi);
    return container;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, INode node ) {
    ChoreographyContainer container = (oldVisual instanceof ChoreographyContainer) ? (ChoreographyContainer)oldVisual : null;

    int currentModCount = getModCount() + topParticipants.getModCount() + bottomParticipants.getModCount();
    if (container == null || container.getModCount() != currentModCount) {
      return createVisual(context, node);
    }

    RectD newBounds = node.getLayout().toRectD();

    if (RectD.equals(container.getBounds(), newBounds)) {
      return container;
    }

    if (container.getBounds().width != newBounds.width || container.getBounds().height != newBounds.height) {
      // update icon bounds
      int childIndex = 0;
      IVisual v = container.getChildren().get(childIndex++);
      // task band
      TaskBandContainer taskBandContainer = (v instanceof TaskBandContainer) ? (TaskBandContainer)v : null;
      IIcon taskBandIcon = taskBandContainer != null?taskBandContainer.getIcon():null;
      RectD taskBandBounds = getRelativeTaskNameBandBounds(node);

      if (taskBandIcon != null && taskBandContainer.getChildren().size() == 1) {
        taskBandIcon.setBounds(taskBandBounds);
        updateChildVisual(context, taskBandContainer, 0, taskBandIcon);
      }

      // top participants
      double topOffset = 0;
      for (int i = 0; i < topParticipants.size(); i++) {
        Participant participant = topParticipants.get(i);
        IIcon participantIcon = container.getTopParticipantIcons().get(i);
        double height = participant.getSize();
        participantIcon.setBounds(new RectD(0, topOffset, newBounds.width, height));
        updateChildVisual(context, container, childIndex++, participantIcon);
        topOffset += height;
      }

      // bottom participants
      double bottomOffset = newBounds.height;
      for (int i = 0; i < bottomParticipants.size(); i++) {
        Participant participant = bottomParticipants.get(i);
        IIcon participantIcon = container.getBottomParticipantIcons().get(i);
        double height = participant.getSize();
        bottomOffset -= height;
        participantIcon.setBounds(new RectD(0, bottomOffset, newBounds.width, height));
        updateChildVisual(context, container, childIndex++, participantIcon);
      }

      // outline
      outlineIcon.setBounds(new RectD(PointD.ORIGIN, newBounds.getSize()));
      updateChildVisual(context, container, childIndex++, outlineIcon);

      // messages
      if (isInitiatingMessage()) {
        IIcon initiatingMessageIcon = isInitiatingAtTop() ? topInitiatingMessageIcon : bottomInitiatingMessageIcon;
        initiatingMessageIcon.setBounds(new RectD(0, 0, newBounds.width, newBounds.height));
        updateChildVisual(context, container, childIndex++, initiatingMessageIcon);
      }
      if (isResponseMessage()) {
        IIcon responseMessageIcon = isInitiatingAtTop() ? bottomResponseMessageIcon : topResponseMessageIcon;
        responseMessageIcon.setBounds(new RectD(0, 0, newBounds.width, newBounds.height));
        updateChildVisual(context, container, childIndex++, responseMessageIcon);
      }
    }

    container.setModCount(getModCount() + topParticipants.getModCount() + bottomParticipants.getModCount());
    container.setBounds(newBounds);
    container.getTransform().setToTranslation(newBounds.getX(), newBounds.getY());
    return container;
  }

  private IIcon createTaskBandIcon( INode node ) {
    if (taskBandBackgroundIcon == null) {
      updateTaskBandIcon();
    }
    IIcon subStateIcon = null;
    if (getSubState() != SubState.NONE) {
      subStateIcon = getSubState() == SubState.DYNAMIC ? IconFactory.createDynamicSubState(node, getIconColor()) : IconFactory.createStaticSubState(getSubState(), getIconColor());
    }

    IIcon markerIcon = null;
    if (loopIcon != null && subStateIcon != null) {
      markerIcon = IconFactory.createLineUpIcon(Arrays.asList(loopIcon, subStateIcon), BpmnConstants.MARKER_SIZE, 5);
    } else if (loopIcon != null) {
      markerIcon = loopIcon;
    } else if (subStateIcon != null) {
      markerIcon = subStateIcon;
    }
    if (markerIcon != null) {
      IIcon placedMarkers = IconFactory.createPlacedIcon(markerIcon, BpmnConstants.CHOREOGRAPHY_MARKER_PLACEMENT, BpmnConstants.MARKER_SIZE);
      return IconFactory.createCombinedIcon(Arrays.asList(taskBandBackgroundIcon, placedMarkers));
    } else {
      return taskBandBackgroundIcon;
    }
  }

  private IIcon createParticipantIcon( Participant participant, boolean top, boolean isFirst ) {
    boolean isInitializing = isFirst && (top ^ !isInitiatingAtTop());

    double radius = BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS;
    IIcon icon = IconFactory.createChoreographyParticipant(getOutline(), isInitializing ? getInitiatingColor() : getResponseColor(), top && isFirst ? radius : 0, !top && isFirst ? radius : 0);
    if (participant.isMultiInstance()) {
      if (multiInstanceIcon == null) {
        updateMultiInstanceIcon();
      }
      icon = IconFactory.createCombinedIcon(Arrays.asList(icon, multiInstanceIcon));
    }
    return icon;
  }

  private static void updateChildVisual( IRenderContext context, VisualGroup container, int index, IVisualCreator icon ) {
    IVisual oldPathVisual = container.getChildren().get(index);
    IVisual newPathVisual = icon.updateVisual(context, oldPathVisual);
    if (!oldPathVisual.equals(newPathVisual)) {
      newPathVisual = newPathVisual != null ? newPathVisual : new VisualGroup();
      container.getChildren().remove(oldPathVisual);
      container.getChildren().add(index, newPathVisual);
    }
  }


  /**
   * Returns the participant at the specified location.
   * @param node The node whose bounds shall be used.
   * @param location The location of the participant.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final Participant getParticipant( INode node, PointD location ) {
    RectD layout = node.getLayout().toRectD();
    if (!layout.contains(location)) {
      return null;
    }

    double relativeY = (PointD.subtract(location, layout.getTopLeft())).y;
    if (relativeY < topParticipants.getHeight()) {
      for (Participant participant : getTopParticipants()) {
        double size = participant.getSize();
        if (relativeY < size) {
          return participant;
        }
        relativeY -= size;
      }
    } else if (layout.getHeight() - bottomParticipants.getHeight() < relativeY) {
      double yFromBottom = layout.getHeight() - relativeY;
      for (Participant participant : getBottomParticipants()) {
        double size = participant.getSize();
        if (yFromBottom < size) {
          return participant;
        }
        yFromBottom -= size;
      }
    }

    return null;
  }

  /**
   * Returns the bounds of the specified participant band.
   * @param owner The node whose bounds shall be used.
   * @param index The index of the participant in its list.
   * @param top Whether the top of bottom list of participants shall be used.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final RectD getParticipantBandBounds( INode owner, int index, boolean top ) {
    RectD layout = owner.getLayout().toRectD();
    double width = layout.getWidth();
    if (top && index <= topParticipants.size()) {
      int i = 0;
      double yOffset = 0;
      for (Participant topParticipant : topParticipants) {
        if (index == i++) {
          return new RectD(layout.getX(), layout.getY() + yOffset, width, topParticipant.getSize());
        } else {
          yOffset += topParticipant.getSize();
        }
      }
    } else if (!top && index < bottomParticipants.size()) {
      int i = 0;
      double yOffset = layout.getHeight();
      for (Participant bottomParticipant : bottomParticipants) {
        yOffset -= bottomParticipant.getSize();
        if (index == i++) {
          return new RectD(layout.getX(), layout.getY() + yOffset, width, bottomParticipant.getSize());
        }
      }
    }
    return getTaskNameBandBounds(owner);
  }

  public final ILabelModelParameter getParticipantParameters( Participant participant ) {

    boolean top;
    int index;

    if (bottomParticipants.contains(participant)) {
      top = false;
      index = bottomParticipants.indexOf(participant);
    } else {
      top = true;
      index = topParticipants.indexOf(participant);
    }

    return ChoreographyLabelModel.INSTANCE.createParticipantParameter(top, index);
  }

  /**
   * Returns the bounds of the task name band.
   * @param owner The node whose bounds shall be used.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final RectD getTaskNameBandBounds( INode owner ) {
    return getRelativeTaskNameBandBounds(owner).getTranslated(owner.getLayout().getTopLeft());
  }

  private RectD getRelativeTaskNameBandBounds( INode owner ) {
    double topHeight = topParticipants.getHeight();
    return new RectD(0, topHeight, owner.getLayout().getWidth(), Math.max(0, owner.getLayout().getHeight() - topHeight - bottomParticipants.getHeight()));
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    SNS.getRenderer().getShapeGeometry(node, SNS);
    GeneralPath tmp = SNS.getRenderer().getOutline();
    GeneralPath path = tmp != null ? tmp : new GeneralPath(16);

    RectD layout = node.getLayout().toRectD();

    if (isShowTopMessage()) {
      SizeD topBoxSize = BpmnConstants.MESSAGE_SIZE;
      double cx = layout.getCenter().x;
      double topBoxMaxY = layout.getY() - MESSAGE_DISTANCE;
      path.moveTo(cx - topBoxSize.width / 2, layout.getY());
      path.lineTo(cx - topBoxSize.width / 2, topBoxMaxY);
      path.lineTo(cx - topBoxSize.width / 2, topBoxMaxY - topBoxSize.height);
      path.lineTo(cx + topBoxSize.width / 2, topBoxMaxY - topBoxSize.height);
      path.lineTo(cx + topBoxSize.width / 2, topBoxMaxY);
      path.lineTo(cx - topBoxSize.width / 2, topBoxMaxY);
      path.close();
    }

    if (isShowBottomMessage()) {
      SizeD bottomBoxSize = BpmnConstants.MESSAGE_SIZE;
      double cx = layout.getCenter().x;
      double bottomBoxY = layout.getMaxY() + MESSAGE_DISTANCE;
      path.moveTo(cx - bottomBoxSize.width / 2, layout.getMaxY());
      path.lineTo(cx - bottomBoxSize.width / 2, bottomBoxY);
      path.lineTo(cx - bottomBoxSize.width / 2, bottomBoxY + bottomBoxSize.height);
      path.lineTo(cx + bottomBoxSize.width / 2, bottomBoxY + bottomBoxSize.height);
      path.lineTo(cx + bottomBoxSize.width / 2, bottomBoxY);
      path.lineTo(cx - bottomBoxSize.width / 2, bottomBoxY);
      path.close();
    }

    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    if (SNS.getRenderer().getHitTestable(node, SNS).isHit(context, location)) {
      return true;
    }
    RectD layout = node.getLayout().toRectD();
    if (isShowTopMessage()) {
      double cx = layout.getCenter().x;
      SizeD topBoxSize = BpmnConstants.MESSAGE_SIZE;
      RectD messageRect = new RectD(new PointD(cx - topBoxSize.width / 2, layout.getY() - MESSAGE_DISTANCE - topBoxSize.height), topBoxSize);
      if (messageRect.contains(location, context.getHitTestRadius())) {
        return true;
      }
      if (Math.abs(location.x - cx) < context.getHitTestRadius() && layout.getY() - MESSAGE_DISTANCE - context.getHitTestRadius() < location.y && location.y < layout.getY() + context.getHitTestRadius()) {
        return true;
      }
    }

    if (isShowBottomMessage()) {
      SizeD bottomBoxSize = BpmnConstants.MESSAGE_SIZE;
      double cx = layout.getCenter().x;
      RectD messageRect = new RectD(new PointD(cx - bottomBoxSize.width / 2, layout.getMaxY() + MESSAGE_DISTANCE), bottomBoxSize);
      if (messageRect.contains(location, context.getHitTestRadius())) {
        return true;
      }
      if (Math.abs(location.x - cx) < context.getHitTestRadius() && layout.getMaxY() - context.getHitTestRadius() < location.y && location.y < layout.getMaxY() + MESSAGE_DISTANCE + context.getHitTestRadius()) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected RectD getBounds( ICanvasContext context, INode node ) {
    RectD bounds = node.getLayout().toRectD();
    if (isShowTopMessage()) {
      bounds = bounds.getEnlarged(InsetsD.fromLTRB(0, MESSAGE_DISTANCE + BpmnConstants.MESSAGE_SIZE.height, 0, 0));
    }
    if (isShowBottomMessage()) {
      bounds = bounds.getEnlarged(InsetsD.fromLTRB(0, 0, 0, MESSAGE_DISTANCE + BpmnConstants.MESSAGE_SIZE.height));
    }

    return bounds;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Object lookup( INode node, Class type ) {
    if (type == INodeSizeConstraintProvider.class) {
      double minWidth = Math.max(0, getMinimumSize().width);
      double minHeight = Math.max(0, getMinimumSize().height) + topParticipants.getHeight() + bottomParticipants.getHeight();
      return new NodeSizeConstraintProvider(new SizeD(minWidth, minHeight), SizeD.INFINITE, (IRectangle)null);
    } else if (type == INodeInsetsProvider.class) {
      return new ChoreographyInsetsProvider(this);
    } else if (type == IEditLabelHelper.class) {
      return new ChoreographyEditLabelHelper(node);
    } else if (type == IClickListener.class) {
      IIcon bandIcon = createTaskBandIcon(node);
      if (bandIcon != null) {
        bandIcon.setBounds(getTaskNameBandBounds(node));
        return getDelegatingClickListener(bandIcon);
      }
    }
    return super.lookup(node, type);
  }

  @Override
  public ChoreographyNodeStyle clone() {
    ChoreographyNodeStyle newInstance = (ChoreographyNodeStyle)super.clone();

    newInstance.topParticipants = new ParticipantList();
    for (Participant participant : getTopParticipants()) {
      newInstance.topParticipants.add(participant.clone());
    }
    newInstance.bottomParticipants = new ParticipantList();
    for (Participant participant : getBottomParticipants()) {
      newInstance.bottomParticipants.add(participant.clone());
    }
    return newInstance;
  }

  public static class ParticipantList extends ArrayList<Participant> {
    public final int getModCount() {
      return modCount + getParticipantModCount();
    }

    public final double getHeight() {
      double height = 0;
      for (Participant participant : this) {
        height += participant.getSize();
      }
      return height;
    }

    private int getParticipantModCount() {
      int participantCount = 0;
      for (Participant participant : this) {
        participantCount += participant.getModCount();
      }
      return participantCount;
    }
  }

  /**
   * Uses the style insets extended by the size of the participant bands.
   */
  private static final class ChoreographyInsetsProvider implements INodeInsetsProvider {
    private final ChoreographyNodeStyle style;

    ChoreographyInsetsProvider( ChoreographyNodeStyle style ) {
      this.style = style;
    }

    public final InsetsD getInsets( INode node ) {
      double topInsets = ((ChoreographyNodeStyle.ParticipantList)style.getTopParticipants()).getHeight();
      double bottomInsets = ((ChoreographyNodeStyle.ParticipantList)style.getBottomParticipants()).getHeight();

      bottomInsets += (style.getLoopCharacteristic() != LoopCharacteristic.NONE || style.getSubState() != SubState.NONE)
          ? BpmnConstants.MARKER_SIZE.height + ((InteriorLabelModel)BpmnConstants.CHOREOGRAPHY_MARKER_PLACEMENT.getModel()).getInsets().bottom : 0;

      return InsetsD.fromLTRB(style.getInsets().left, style.getInsets().top + topInsets, style.getInsets().right, style.getInsets().bottom + bottomInsets);
    }

  }

  private static final class ChoreographyEditLabelHelper implements IEditLabelHelper {
    private final INode node;

    public ChoreographyEditLabelHelper( INode node ) {
      this.node = node;
    }

    public final void onLabelEditing( LabelEditingEventArgs args ) {
      if (node.getLabels().size() == 0) {
        onLabelAdding(args);
        return;
      }
      args.setLabel(node.getLabels().getItem(0));
      args.setHandled(true);
    }

    public final void onLabelAdding( LabelEditingEventArgs args ) {
      ILabelModelParameter parameter = ChoreographyLabelModel.INSTANCE.findNextParameter(node);
      ILabelStyle labelStyle;
      if (parameter == ChoreographyLabelModel.NORTH_MESSAGE || parameter == ChoreographyLabelModel.SOUTH_MESSAGE) {
        labelStyle = new ChoreographyMessageLabelStyle();
      } else {
        labelStyle = ((GraphComponent)args.getContext().getCanvasComponent()).getGraph().getNodeDefaults().getLabelDefaults().getStyle();
      }
      if (parameter == null) {
        parameter = ExteriorLabelModel.WEST;
      }

      args.setLayoutParameter(parameter);
      args.setOwner(node);
      args.setStyle(labelStyle);
      args.setHandled(true);
    }

  }

  private static class ChoreographyContainer extends VisualGroup {
    public void setBounds(RectD bounds) {
      this.bounds = bounds;
    }

    private RectD bounds;

    public final RectD getBounds() {
      return bounds;
    }

    private int modCount;

    public final int getModCount() {
      return modCount;
    }

    private List<IIcon> topParticipantIcons;

    public final List<IIcon> getTopParticipantIcons() {
      return this.topParticipantIcons;
    }

    public final void setTopParticipantIcons( List<IIcon> value ) {
      this.topParticipantIcons = value;
    }

    private List<IIcon> bottomParticipantIcons;

    public final List<IIcon> getBottomParticipantIcons() {
      return this.bottomParticipantIcons;
    }

    public final void setBottomParticipantIcons( List<IIcon> value ) {
      this.bottomParticipantIcons = value;
    }

    public void setModCount(int modCount) {
      this.modCount = modCount;
    }
  }

  private static class TaskBandContainer extends VisualGroup {
    private IIcon icon;

    public IIcon getIcon() {
      return icon;
    }

    public void setIcon(IIcon icon) {
      this.icon = icon;
    }
  }

}
