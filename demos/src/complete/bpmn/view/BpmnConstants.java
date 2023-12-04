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

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.view.Colors;
import java.awt.Color;
import java.awt.Paint;

@GraphML(valueSerializer = BpmnConstantConverter.class)
public final class BpmnConstants {
  private BpmnConstants() {
  }

  public static final double DOUBLE_LINE_OFFSET = 2;

  public static final double CHOREOGRAPHY_CORNER_RADIUS = 6;

  public static final double GROUP_NODE_CORNER_RADIUS = 3;

  /**
   * The namespace URI for yFiles bpmn extensions to graphml.
   * <p>
   * This field has the constant value {@code http://www.yworks.com/xml/yfiles-bpmn/java/3.0}
   * </p>
   */
  public static final String YFILES_BPMN_NS = "http://www.yworks.com/xml/yfiles-bpmn/java/3.0";

  /**
   * The namespace URI for the older yFiles BPMN extensions to GraphML.
   * <p>
   * This is the version of the styles without changeable colors.
   * </p>
   */
  public static final String YFILES_BPMN_LEGACY_NS = "http://www.yworks.com/xml/yfiles-bpmn/java/2.0";

  /**
   * The default namespace prefix for {@link #YFILES_BPMN_NS}.
   * <p>
   * This field has the constant value {@code "bpmn"}
   * </p>
   */
  public static final String YFILES_BPMN_PREFIX = "bpmn";

  // Shared constants that apply to several different items
  public static final Paint DEFAULT_BACKGROUND = new Color( 250, 250, 250, 255);

  public static final Paint DEFAULT_ICON_COLOR = Colors.BLACK;

  public static final Paint DEFAULT_EVENT_OUTLINE = null; 

  // null triggers fallback to characteristic-specific colors

  public static final Paint DEFAULT_MESSAGE_OUTLINE = Colors.BLACK;

  public static final Paint DEFAULT_INITIATING_COLOR = Colors.WHITE;

  public static final Paint DEFAULT_RECEIVING_COLOR = Colors.GRAY;

  // Activity
  public static final double ACTIVITY_CORNER_RADIUS = 6;

  public static final Paint ACTIVITY_DEFAULT_BACKGROUND = DEFAULT_BACKGROUND;

  public static final Paint ACTIVITY_DEFAULT_OUTLINE = Colors.DARK_BLUE;

  // Gateway
  public static final Paint GATEWAY_DEFAULT_BACKGROUND = DEFAULT_BACKGROUND;

  public static final Paint GATEWAY_DEFAULT_OUTLINE = Colors.DARK_ORANGE;

  // Annotation
  public static final Paint ANNOTATION_DEFAULT_BACKGROUND = DEFAULT_BACKGROUND;

  public static final Paint ANNOTATION_DEFAULT_OUTLINE = Colors.BLACK;

  // Edges
  public static final Paint EDGE_DEFAULT_COLOR = Colors.BLACK;

  public static final Paint EDGE_DEFAULT_INNER_COLOR = Colors.WHITE;

  // Choreography
  public static final Paint CHOREOGRAPHY_DEFAULT_BACKGROUND = DEFAULT_BACKGROUND;

  public static final Paint CHOREOGRAPHY_DEFAULT_OUTLINE = Colors.DARK_GREEN;

  public static final Paint CHOREOGRAPHY_DEFAULT_ICON_COLOR = Colors.BLACK;

  public static final Paint CHOREOGRAPHY_DEFAULT_MESSAGE_OUTLINE = DEFAULT_MESSAGE_OUTLINE;

  public static final Paint CHOREOGRAPHY_DEFAULT_INITIATING_COLOR = DEFAULT_INITIATING_COLOR;

  public static final Paint CHOREOGRAPHY_DEFAULT_RESPONSE_COLOR = DEFAULT_RECEIVING_COLOR;

  // Conversation
  public static final Paint CONVERSATION_DEFAULT_OUTLINE = Colors.DARK_GREEN;

  public static final Paint CONVERSATION_DEFAULT_BACKGROUND = DEFAULT_BACKGROUND;

  // Data object
  public static final Paint DATA_OBJECT_DEFAULT_BACKGROUND = Colors.WHITE;

  public static final Paint DATA_OBJECT_DEFAULT_OUTLINE = Colors.BLACK;

  // Data store
  public static final Paint DATA_STORE_DEFAULT_OUTLINE = Colors.BLACK;

  public static final Paint DATA_STORE_DEFAULT_BACKGROUND = Colors.WHITE;

  // Event
  public static final Paint DEFAULT_EVENT_BACKGROUND = DEFAULT_BACKGROUND;

  // Group
  public static final Paint GROUP_DEFAULT_BACKGROUND = null;

  public static final Paint GROUP_DEFAULT_OUTLINE = Colors.BLACK;

  // Messages
  public static final Paint DEFAULT_INITIATING_MESSAGE_COLOR = DEFAULT_INITIATING_COLOR;

  public static final Paint DEFAULT_RECEIVING_MESSAGE_COLOR = DEFAULT_RECEIVING_COLOR;

  // Pools
  public static final Paint DEFAULT_POOL_NODE_BACKGROUND = new Color(0xE0, 0xE0, 0xE0, 255);

  public static final Paint DEFAULT_POOL_NODE_EVEN_LEAF_BACKGROUND = new Color(196, 215, 237, 255);

  public static final Paint DEFAULT_POOL_NODE_EVEN_LEAF_INSET = new Color(0xE0, 0xE0, 0xE0, 255);

  public static final Paint DEFAULT_POOL_NODE_ODD_LEAF_BACKGROUND = new Color(171, 200, 226, 255);

  public static final Paint DEFAULT_POOL_NODE_ODD_LEAF_INSET = new Color(0xE0, 0xE0, 0xE0, 255);

  public static final Paint DEFAULT_POOL_NODE_PARENT_BACKGROUND = new Color(113, 146, 178, 255);

  public static final Paint DEFAULT_POOL_NODE_PARENT_INSET = new Color(0xE0, 0xE0, 0xE0, 255);

  // Placement constants for where parts of item visualizations should appear
  private static final InteriorLabelModel ILM2;

  private static final InteriorLabelModel ILM6;

  private static final InteriorStretchLabelModel ISLM_INSIDE_DOUBLE_LINE;

  private static final ExteriorLabelModel ELM15;

  private static final ScalingLabelModel SLM = new ScalingLabelModel();

  private static final ScalingLabelModel SLM3;

  public static final ILabelModelParameter TASK_TYPE_PLACEMENT;

  public static final ILabelModelParameter TASK_MARKER_PLACEMENT;

  public static final ILabelModelParameter CHOREOGRAPHY_MARKER_PLACEMENT;

  public static final ILabelModelParameter CHOREOGRAPHY_TOP_MESSAGE_PLACEMENT;

  public static final ILabelModelParameter CHOREOGRAPHY_BOTTOM_MESSAGE_PLACEMENT;

  private static final double RATIO_WIDTH_HEIGHT = 1 / Math.sin(Math.PI / 3.0);

  public static final ILabelModelParameter CONVERSATION_PLACEMENT = SLM.createScaledParameterWithRatio(1, RATIO_WIDTH_HEIGHT);

  public static final ILabelModelParameter CONVERSATION_MARKER_PLACEMENT;

  public static final ILabelModelParameter DATA_OBJECT_TYPE_PLACEMENT;

  public static final ILabelModelParameter DATA_OBJECT_MARKER_PLACEMENT;

  public static final ILabelModelParameter EVENT_PLACEMENT = SLM.createScaledParameterWithRatio(1, 1);

  public static final ILabelModelParameter EVENT_TYPE_PLACEMENT;

  public static final ILabelModelParameter GATEWAY_PLACEMENT = SLM.createScaledParameterWithRatio(1, 1);

  public static final ILabelModelParameter GATEWAY_TYPE_PLACEMENT = SLM.createScaledParameterWithRatio(0.6, 1);

  public static final ILabelModelParameter EVENT_TYPE_MESSAGE_PLACEMENT = SLM.createScaledParameterWithRatio(0.8, 1.4);

  public static final ILabelModelParameter ACTIVITY_TASK_TYPE_MESSAGE_PLACEMENT = SLM.createScaledParameterWithRatio(1, 1.4);

  public static final ILabelModelParameter DOUBLE_LINE_PLACEMENT;

  public static final ILabelModelParameter THICK_LINE_PLACEMENT;

  public static final ILabelModelParameter INSIDE_DOUBLE_LINE_PLACEMENT;

  public static final ILabelModelParameter POOL_NODE_MARKER_PLACEMENT;

  // Default sizes for different items
  public static final SizeD MARKER_SIZE = new SizeD(10, 10);

  public static final SizeD TASK_TYPE_SIZE = new SizeD(15, 15);

  public static final SizeD MESSAGE_SIZE = new SizeD(20, 14);

  public static final double CONVERSATION_WIDTH_HEIGHT_RATIO = Math.sin(Math.PI / 3.0);

  public static final SizeD CONVERSATION_SIZE = new SizeD(20, 20 * CONVERSATION_WIDTH_HEIGHT_RATIO);

  public static final SizeD DATA_OBJECT_TYPE_SIZE = new SizeD(10, 8);

  public static final SizeD EVENT_PORT_SIZE = new SizeD(20, 20);

  static {
    ILM2 = new InteriorLabelModel();
    ILM2.setInsets(new InsetsD(2));

    ILM6 = new InteriorLabelModel();
    ILM6.setInsets(new InsetsD(6));

    ISLM_INSIDE_DOUBLE_LINE = new InteriorStretchLabelModel();
    ISLM_INSIDE_DOUBLE_LINE.setInsets(new InsetsD(2 * DOUBLE_LINE_OFFSET + 1));

    ELM15 = new ExteriorLabelModel();
    ELM15.setInsets(new InsetsD(15));

    SLM3 = new ScalingLabelModel();
    SLM3.setInsets(new InsetsD(3));

    TASK_TYPE_PLACEMENT = ILM6.createParameter(InteriorLabelModel.Position.NORTH_WEST);
    TASK_MARKER_PLACEMENT = ISLM_INSIDE_DOUBLE_LINE.createParameter(InteriorStretchLabelModel.Position.SOUTH);
    CHOREOGRAPHY_MARKER_PLACEMENT = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
    CHOREOGRAPHY_TOP_MESSAGE_PLACEMENT = ELM15.createParameter(ExteriorLabelModel.Position.NORTH);
    CHOREOGRAPHY_BOTTOM_MESSAGE_PLACEMENT = ELM15.createParameter(ExteriorLabelModel.Position.SOUTH);
    CONVERSATION_MARKER_PLACEMENT= ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
    DATA_OBJECT_TYPE_PLACEMENT = ILM2.createParameter(InteriorLabelModel.Position.NORTH_WEST);
    DATA_OBJECT_MARKER_PLACEMENT = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
    EVENT_TYPE_PLACEMENT = SLM3.createScaledParameterWithRatio(0.9, 1);

    InteriorStretchLabelModel islm = new InteriorStretchLabelModel();
    islm.setInsets(new InsetsD(DOUBLE_LINE_OFFSET));
    DOUBLE_LINE_PLACEMENT = islm.createParameter(InteriorStretchLabelModel.Position.CENTER);

    InteriorStretchLabelModel islm2 = new InteriorStretchLabelModel();
    islm2.setInsets(new InsetsD(DOUBLE_LINE_OFFSET / 2));
    THICK_LINE_PLACEMENT = islm2.createParameter(InteriorStretchLabelModel.Position.CENTER);
    INSIDE_DOUBLE_LINE_PLACEMENT = ISLM_INSIDE_DOUBLE_LINE.createParameter(InteriorStretchLabelModel.Position.CENTER);
    POOL_NODE_MARKER_PLACEMENT = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
  }

}
