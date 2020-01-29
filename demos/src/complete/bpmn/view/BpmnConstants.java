/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.Pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

public final class BpmnConstants {
  private BpmnConstants() {
  }

  public static final double DOUBLE_LINE_OFFSET = 2;

  public static final double ACTIVITY_CORNER_RADIUS = 6;

  public static final double CHOREOGRAPHY_CORNER_RADIUS = 6;

  public static final double GROUP_NODE_CORNER_RADIUS = 3;

  /**
   * The namespace URI for yFiles bpmn extensions to graphml.
   * <p>
   * This field has the constant value {@code http://www.yworks.com/xml/yfiles-bpmn/java/2.0}
   * </p>
   */
  public static final String YFILES_BPMN_NS = "http://www.yworks.com/xml/yfiles-bpmn/java/2.0";

  /**
   * The default namespace prefix for {@link #YFILES_BPMN_NS}.
   * <p>
   * This field has the constant value {@code "bpmn"}
   * </p>
   */
  public static final String YFILES_BPMN_PREFIX = "bpmn";

  static class Pens {
    public static final Pen TASK = Pen.getDarkBlue();
    public static final Pen CALL_ACTIVITY = new Pen(Colors.DARK_BLUE, 3);
    public static final Pen ACTIVITY_EVENT_SUB_PROCESS = createPen(Colors.DARK_BLUE, -1, BasicStroke.CAP_ROUND, DashStyle.getDot());
    public static final Pen ACTIVITY_TASK_ROUND = createPen(Colors.BLACK, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND, null);
    public static final Pen TASK_TYPE_SERVICE = new Pen(Colors.BLACK, 0.3);
    public static final Pen ANNOTATION = Pen.getBlack();

    public static final Pen CHOREOGRAPHY_TASK = Pen.getDarkGreen();
    public static final Pen CHOREOGRAPHY_CALL = new Pen(Colors.DARK_GREEN, 3);
    public static final Pen CHOREOGRAPHY_MESSAGE_LINK = createPen(Colors.BLACK, -1, BasicStroke.CAP_ROUND, DashStyle.getDot());
    public static final Pen CONVERSATION = Pen.getDarkGreen();
    public static final Pen CALLING_CONVERSATION = new Pen(Colors.DARK_GREEN, 3);
    public static final Pen DATA_OBJECT = Pen.getBlack();

    public static final Pen DATA_STORE = Pen.getBlack();

    public static final Pen EVENT_START = new Pen(new Color(0, 128, 0));
    public static final Pen EVENT_SUB_PROCESS_NON_INTERRUPTING = createPen(new Color(0, 128, 0), -1, -1, DashStyle.getDash());
    public static final Pen EVENT_INTERMEDIATE = Pen.getGoldenrod();
    public static final Pen EVENT_BOUNDARY_NON_INTERRUPTING = createPen(Colors.GOLDENROD, -1, -1, DashStyle.getDash());
    public static final Pen EVENT_END = new Pen(Colors.RED, 3);
    public static final Pen EVENT_TYPE;
    public static final Pen EVENT_TYPE_DETAIL = createPen(Colors.BLACK, -1, BasicStroke.CAP_ROUND, null);
    public static final Pen EVENT_TYPE_DETAIL_INVERTED = createPen(Colors.WHITE, -1, BasicStroke.CAP_ROUND, null);
    public static final Pen GATEWAY = Pen.getDarkOrange();
    public static final Pen GATEWAY_TYPE_INCLUSIVE = new Pen(Colors.BLACK, 3);
    public static final Pen GROUP_NODE = createPen(Colors.BLACK, -1, BasicStroke.CAP_ROUND, DashStyle.getDashDot());
    public static final Pen MESSAGE = Pen.getBlack();
    public static final Pen MESSAGE_INVERTED = Pen.getWhite();
    public static final Pen ARROW = createPen(Colors.BLACK, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND, null);
    public static final Pen ASSOCIATION_EDGE_STYLE = createPen(Colors.BLACK, -1, BasicStroke.CAP_ROUND, DashStyle.getDot());
    public static final Pen BPMN_EDGE_STYLE = Pen.getBlack();
    public static final Pen CONVERSATION_DOUBLE_LINE;
    public static final Pen CONVERSATION_CENTER_LINE = createPen(Colors.WHITE, BasicStroke.JOIN_ROUND, -1, null);
    public static final Pen MESSAGE_EDGE_STYLE = createPen(Colors.BLACK, -1, -1, DashStyle.getDash());

    private Pens() {
    }

    static {
      EVENT_TYPE =  new Pen();
      EVENT_TYPE.setPaint(Colors.BLACK);
      EVENT_TYPE.setMiterLimit(3);

      CONVERSATION_DOUBLE_LINE = new Pen();
      CONVERSATION_DOUBLE_LINE.setPaint(Colors.BLACK);
      CONVERSATION_DOUBLE_LINE.setThickness(3);
      CONVERSATION_DOUBLE_LINE.setLineJoin(BasicStroke.JOIN_ROUND);
    }

    private static Pen createPen(Color color, int join, int cap, DashStyle dash) {
      Pen pen = new Pen();
      pen.setPaint(color);
      if(cap != -1) {
        pen.setEndCap(cap);
    }
      if(join != -1) {
        pen.setLineJoin(join);
    }
      if(dash != null) {
        pen.setDashStyle(dash);
    }
      return pen;
    }
  }

  static class Paints {
    public static final Paint ACTIVITY = new Color(250, 250, 250);

    public static final Paint ACTIVITY_TASK_LIGHT = Colors.LIGHT_GRAY;

    public static final Paint ACTIVITY_TASK_DARK = Colors.GRAY;

    public static final Paint ANNOTATION = new Color(250, 250, 250);

    public static final Paint CONVERSATION = new Color(250, 250, 250);

    public static final Paint CHOREOGRAPHY_TASK_BAND = new Color(250, 250, 250);

    // BPMN says, this should be white, but looks better this way
    public static final Paint CHOREOGRAPHY_INITIALIZING_PARTICIPANT = Colors.LIGHT_GRAY;

    // BPMN says, this should be a light fill, but looks better this way
    public static final Paint CHOREOGRAPHY_RECEIVING_PARTICIPANT = Colors.GRAY;

    public static final Paint DATA_OBJECT = Colors.WHITE;

    public static final Paint DATA_STORE = Colors.WHITE;

    public static final Paint EVENT = new Color(250, 250, 250);

    public static final Paint EVENT_TYPE_CATCHING = Colors.TRANSPARENT;

    public static final Paint EVENT_TYPE_THROWING = Colors.BLACK;

    public static final Paint GATEWAY = new Color(250, 250, 250);

    public static final Paint GROUP_NODE = null;

    public static final Paint MESSAGE = Colors.WHITE;

    // BPMN says, this should be white, but looks better this way
    public static final Paint INITIATING_MESSAGE = Colors.LIGHT_GRAY;

    // BPMN says, this should be a light fill, but looks better this way
    public static final Paint RECEIVING_MESSAGE = Colors.GRAY;

    public static final Paint MESSAGE_INVERTED = Colors.BLACK;

    public static final Paint POOL_NODE_BACKGROUND = new Color(0xE0, 0xE0, 0xE0);

    public static final Paint POOL_NODE_EVEN_LEAF_BACKGROUND = new Color(196, 215, 237);

    public static final Paint POOL_NODE_EVEN_LEAF_INSET = new Color(0xE0, 0xE0, 0xE0);

    public static final Paint POOL_NODE_ODD_LEAF_BACKGROUND = new Color(171, 200, 226);

    public static final Paint POOL_NODE_ODD_LEAF_INSET = new Color(0xE0, 0xE0, 0xE0);

    public static final Paint POOL_NODE_PARENT_BACKGROUND = new Color(113, 146, 178);

    public static final Paint POOL_NODE_PARENT_INSET = new Color(0xE0, 0xE0, 0xE0);

    private Paints() {
    }
  }

  static class Placements {
    private static final InteriorLabelModel ILM2;
    private static final InteriorLabelModel ILM6;
    private static final InteriorStretchLabelModel ISLM_INSIDE_DOUBLE_LINE;
    private static final ExteriorLabelModel ELM15;
    private static final ScalingLabelModel SLM;
    private static final ScalingLabelModel SLM3;
    public static final ILabelModelParameter TASK_TYPE;
    public static final ILabelModelParameter TASK_MARKER;
    public static final ILabelModelParameter CHOREOGRAPHY_MARKER;
    public static final ILabelModelParameter CHOREOGRAPHY_TOP_MESSAGE;
    public static final ILabelModelParameter CHOREOGRAPHY_BOTTOM_MESSAGE;
    private static final double RATIO_WIDTH_HEIGHT;
    public static final ILabelModelParameter CONVERSATION;
    public static final ILabelModelParameter CONVERSATION_MARKER;
    public static final ILabelModelParameter DATA_OBJECT_TYPE;
    public static final ILabelModelParameter DATA_OBJECT_MARKER;
    public static final ILabelModelParameter EVENT;
    public static final ILabelModelParameter EVENT_TYPE;
    public static final ILabelModelParameter GATEWAY;
    public static final ILabelModelParameter GATEWAY_TYPE;
    public static final ILabelModelParameter EVENT_TYPE_MESSAGE;
    public static final ILabelModelParameter ACTIVITY_TASK_TYPE_MESSAGE;
    public static final ILabelModelParameter DOUBLE_LINE;
    public static final ILabelModelParameter THICK_LINE;
    public static final ILabelModelParameter INSIDE_DOUBLE_LINE;
    public static final ILabelModelParameter POOL_NODE_MARKER;

    private Placements() {
    }

    static {
      RATIO_WIDTH_HEIGHT = 1 / Math.sin(Math.PI / 3.0);
      SLM = new ScalingLabelModel();

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

      InteriorStretchLabelModel ism1 = new InteriorStretchLabelModel();
      ism1.setInsets(new InsetsD(DOUBLE_LINE_OFFSET));
      DOUBLE_LINE = ism1.createParameter(InteriorStretchLabelModel.Position.CENTER);

      InteriorStretchLabelModel ism2 = new InteriorStretchLabelModel();
      ism2.setInsets(new InsetsD(DOUBLE_LINE_OFFSET / 2));
      THICK_LINE = ism1.createParameter(InteriorStretchLabelModel.Position.CENTER);

      TASK_TYPE = ILM6.createParameter(InteriorLabelModel.Position.NORTH_WEST);
      TASK_MARKER = ISLM_INSIDE_DOUBLE_LINE.createParameter(InteriorStretchLabelModel.Position.SOUTH);
      CHOREOGRAPHY_MARKER = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
      CHOREOGRAPHY_TOP_MESSAGE = ELM15.createParameter(ExteriorLabelModel.Position.NORTH);
      CHOREOGRAPHY_BOTTOM_MESSAGE = ELM15.createParameter(ExteriorLabelModel.Position.SOUTH);
      INSIDE_DOUBLE_LINE = ISLM_INSIDE_DOUBLE_LINE.createParameter(InteriorStretchLabelModel.Position.CENTER);
      POOL_NODE_MARKER = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
      CONVERSATION_MARKER = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
      DATA_OBJECT_TYPE = ILM2.createParameter(InteriorLabelModel.Position.NORTH_WEST);
      DATA_OBJECT_MARKER = ILM2.createParameter(InteriorLabelModel.Position.SOUTH);
      EVENT_TYPE = SLM3.createScaledParameterWithRatio(0.9, 1);
      GATEWAY = SLM.createScaledParameterWithRatio(1, 1);
      GATEWAY_TYPE = SLM.createScaledParameterWithRatio(0.6, 1);
      EVENT_TYPE_MESSAGE = SLM.createScaledParameterWithRatio(0.8, 1.4);
      ACTIVITY_TASK_TYPE_MESSAGE = SLM.createScaledParameterWithRatio(1, 1.4);
      EVENT = SLM.createScaledParameterWithRatio(1, 1);
      CONVERSATION = SLM.createScaledParameterWithRatio(1, RATIO_WIDTH_HEIGHT);
    }
    }


  static class Sizes {
    public static final SizeD MARKER = new SizeD(10, 10);

    public static final SizeD TASK_TYPE = new SizeD(15, 15);

    public static final SizeD MESSAGE = new SizeD(20, 14);

    public static final double CONVERSATION_WIDTH_HEIGHT_RATIO = Math.sin(Math.PI / 3.0);

    public static final SizeD CONVERSATION = new SizeD(20, 20 * CONVERSATION_WIDTH_HEIGHT_RATIO);

    public static final SizeD DATA_OBJECT_TYPE = new SizeD(10, 8);

    public static final SizeD EVENT_PORT = new SizeD(20, 20);

    private Sizes() {
    }
  }
}
