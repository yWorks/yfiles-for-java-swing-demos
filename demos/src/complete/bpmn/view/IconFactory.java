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

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.Pen;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Factory class providing icons according to the BPMN.
 */
class IconFactory {
  private static final IconBuilder BUILDER = new IconBuilder();

  private static final double RADIUS_TO_CORNER_OFFSET = Math.sqrt((1.5 - Math.sqrt(2)));

  private IconFactory() {
  }

  public static final IIcon createPlacedIcon( IIcon icon, ILabelModelParameter placement, SizeD innerSize ) {
    return new PlacedIcon(icon, placement, innerSize);
  }

  public static final IIcon createCombinedIcon( List<IIcon> icons ) {
    return BUILDER.combineIcons(icons);
  }

  public static final IIcon createLineUpIcon( List<IIcon> icons, SizeD innerIconSize, double gap ) {
    return BUILDER.createLineUpIcon(icons, innerIconSize, gap);
  }

  private static final HashMap<ActivityType, IIcon> ACTIVITY_ICONS = new HashMap<>();

  public static final IIcon createActivity( ActivityType type ) {
    if (!ACTIVITY_ICONS.containsKey(type)) {
      IIcon result = null;
      BUILDER.setPaint(BpmnConstants.Paints.ACTIVITY);

      switch (type) {
        case CALL_ACTIVITY:
          BUILDER.setPen(BpmnConstants.Pens.CALL_ACTIVITY);
          result = BUILDER.createRectIcon(BpmnConstants.ACTIVITY_CORNER_RADIUS);
          break;
        case TRANSACTION:
          BUILDER.setPen(BpmnConstants.Pens.TASK);
          ArrayList<IIcon> icons = new ArrayList<>(2);
          icons.add(BUILDER.createRectIcon(BpmnConstants.ACTIVITY_CORNER_RADIUS));

          BUILDER.setPen(BpmnConstants.Pens.TASK);
          BUILDER.setPaint(BpmnConstants.Paints.ACTIVITY);
          IIcon rectIcon = BUILDER.createRectIcon(BpmnConstants.ACTIVITY_CORNER_RADIUS - BpmnConstants.DOUBLE_LINE_OFFSET);
          icons.add(createPlacedIcon(rectIcon, BpmnConstants.Placements.DOUBLE_LINE, SizeD.EMPTY));
          result = BUILDER.combineIcons(icons);
          break;
        case EVENT_SUB_PROCESS:
          BUILDER.setPen(BpmnConstants.Pens.ACTIVITY_EVENT_SUB_PROCESS);
          result = BUILDER.createRectIcon(BpmnConstants.ACTIVITY_CORNER_RADIUS);
          break;
        default: // Task, SubProcess
          BUILDER.setPen(BpmnConstants.Pens.TASK);
          result = BUILDER.createRectIcon(BpmnConstants.ACTIVITY_CORNER_RADIUS);
          break;
      }
      ACTIVITY_ICONS.put(type, result);
      return result;
    }
    return ACTIVITY_ICONS.get(type);
  }

  private static final HashMap<TaskType, IIcon> TASK_ICONS = new HashMap<>();

  public static final IIcon createActivityTaskType( TaskType type ) {
    if (!TASK_ICONS.containsKey(type)) {
      IIcon result = null;
      ArrayList<IIcon> icons = null;
      switch (type) {
        case SEND:
          result = createPlacedIcon(createMessage(BpmnConstants.Pens.MESSAGE_INVERTED, BpmnConstants.Paints.MESSAGE_INVERTED), BpmnConstants.Placements.ACTIVITY_TASK_TYPE_MESSAGE, SizeD.EMPTY);
          break;
        case RECEIVE:
          result = createPlacedIcon(createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.MESSAGE), BpmnConstants.Placements.ACTIVITY_TASK_TYPE_MESSAGE, SizeD.EMPTY);
          break;
        case USER:
          BUILDER.setPen(BpmnConstants.Pens.ACTIVITY_TASK_ROUND);
          BUILDER.setPaint(BpmnConstants.Paints.ACTIVITY_TASK_LIGHT);

          // body + head
          icons = new ArrayList<>(3);
          BUILDER.moveTo(1, 1);
          BUILDER.lineTo(0, 1);
          BUILDER.lineTo(0, 0.701);
          BUILDER.quadTo(0.13, 0.5, 0.316, 0.443);
          BUILDER.lineTo(0.5 + 0.224 * Math.cos(3.0 / 4.0 * Math.PI), 0.224 + 0.224 * Math.sin(3.0 / 4.0 * Math.PI));
          BUILDER.arcTo(0.224, 0.5, 0.224, 3.0 / 4.0 * Math.PI, 9.0 / 4.0 * Math.PI);
          BUILDER.lineTo(0.684f, 0.443);
          BUILDER.quadTo(0.87, 0.5, 1, 0.701);
          BUILDER.close();
          icons.add(BUILDER.getPathIcon());

          // hair
          BUILDER.setPen(BpmnConstants.Pens.ACTIVITY_TASK_ROUND);
          BUILDER.setPaint(Colors.BLACK);
          BUILDER.moveTo(0.287, 0.229);
          BUILDER.cubicTo(0.48, 0.053, 0.52, 0.253, 0.713, 0.137);
          BUILDER.arcTo(0.224, 0.5, 0.224, 31.0 / 16.0 * Math.PI, Math.PI);
          BUILDER.close();
          icons.add(BUILDER.getPathIcon());

          BUILDER.setPen(BpmnConstants.Pens.ACTIVITY_TASK_ROUND);

          // arms
          BUILDER.moveTo(0.19, 1);
          BUILDER.lineTo(0.19, 0.816);
          BUILDER.moveTo(0.810, 1);
          BUILDER.lineTo(0.810, 0.816);

          // collar
          BUILDER.moveTo(0.316, 0.443);
          BUILDER.cubicTo(0.3, 0.672, 0.7, 0.672, 0.684, 0.443);
          icons.add(BUILDER.getPathIcon());

          result = BUILDER.combineIcons(icons);
          break;
        case MANUAL:
          BUILDER.setPen(BpmnConstants.Pens.ACTIVITY_TASK_ROUND);
          BUILDER.moveTo(0, 0.286);
          BUILDER.quadTo(0.037, 0.175, 0.147, 0.143);

          // thumb
          BUILDER.lineTo(0.584, 0.143);
          BUILDER.quadTo(0.602, 0.225, 0.451, 0.286);
          BUILDER.lineTo(0.265, 0.286);

          // index finger
          BUILDER.lineTo(0.95, 0.286);
          BUILDER.quadTo(1, 0.358, 0.95, 0.429);
          BUILDER.lineTo(0.472, 0.429);

          // middle finger
          BUILDER.lineTo(0.915, 0.429);
          BUILDER.quadTo(0.965, 0.5, 0.915, 0.571);
          BUILDER.lineTo(0.531, 0.571);

          // ring finger
          BUILDER.lineTo(0.879, 0.571);
          BUILDER.quadTo(0.929, 0.642, 0.879, 0.714);
          BUILDER.lineTo(0.502, 0.714);

          // pinkie 
          BUILDER.lineTo(0.796, 0.714);
          BUILDER.quadTo(0.847, 0.786, 0.796, 0.857);
          BUILDER.lineTo(0.088, 0.857);

          BUILDER.quadTo(0.022, 0.833, 0, 0.759);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case BUSINESS_RULE:
          final float headHeight = 0.192f;
          final float rowHeight = 0.304f;
          final float column1Width = 0.264f;

          icons = new ArrayList<>(3);
          BUILDER.setPaint(BpmnConstants.Paints.ACTIVITY_TASK_DARK);

          // outline
          BUILDER.moveTo(0, 0.1);
          BUILDER.lineTo(1, 0.1);
          BUILDER.lineTo(1, 0.9);
          BUILDER.lineTo(0, 0.9);
          BUILDER.close();
          icons.add(BUILDER.getPathIcon());

          // rows outline
          BUILDER.setPaint(BpmnConstants.Paints.ACTIVITY_TASK_LIGHT);
          BUILDER.moveTo(0, 0.1 + headHeight);
          BUILDER.lineTo(1, 0.1 + headHeight);
          BUILDER.lineTo(1, 0.9);
          BUILDER.lineTo(0, 0.9);
          BUILDER.close();
          icons.add(BUILDER.getPathIcon());

          // line between second and third row
          BUILDER.moveTo(0, 0.1 + headHeight + rowHeight);
          BUILDER.lineTo(1f, 0.1 + headHeight + rowHeight);

          // line between first and second column
          BUILDER.moveTo(column1Width, 0.1 + headHeight);
          BUILDER.lineTo(column1Width, 0.9);
          icons.add(BUILDER.getPathIcon());

          result = BUILDER.combineIcons(icons);
          break;
        case SERVICE:
          icons = new ArrayList<>();
          // top gear
          icons.add(createGear(0.4, 0.4, 0.4, BpmnConstants.Pens.TASK_TYPE_SERVICE, BpmnConstants.Paints.ACTIVITY_TASK_DARK));
          icons.add(createGear(0.16, 0.4, 0.4, BpmnConstants.Pens.TASK_TYPE_SERVICE, BpmnConstants.Paints.ACTIVITY_TASK_LIGHT));

          // bottom gear
          icons.add(createGear(0.4, 0.6, 0.6, BpmnConstants.Pens.TASK_TYPE_SERVICE, BpmnConstants.Paints.ACTIVITY_TASK_DARK));
          icons.add(createGear(0.16, 0.6, 0.6, BpmnConstants.Pens.TASK_TYPE_SERVICE, BpmnConstants.Paints.ACTIVITY_TASK_LIGHT));

          result = BUILDER.combineIcons(icons);
          break;
        case SCRIPT:
          BUILDER.setPen(BpmnConstants.Pens.ACTIVITY_TASK_ROUND);

          // outline
          final double size = 0.5;
          final double curveEndX = 0.235;
          final double curveEndY = size;
          final double curveCenterX = curveEndX + (size - curveEndX) * 0.5;
          final double curveDeltaX = 0.5;
          final double curveDeltaY = size * 0.5;

          BUILDER.moveTo(0.5 + size, 0.5 - size);
          BUILDER.cubicTo(0.5 + curveCenterX - curveDeltaX, 0.5 - curveDeltaY, 0.5 + curveCenterX + curveDeltaX, 0.5 + curveDeltaY, 0.5 + curveEndX, 0.5 + curveEndY);
          BUILDER.lineTo(0.5 - size, 0.5 + size);
          BUILDER.cubicTo(0.5 - curveCenterX + curveDeltaX, 0.5 + curveDeltaY, 0.5 - curveCenterX - curveDeltaX, 0.5 - curveDeltaY, 0.5 - curveEndX, 0.5 - curveEndY);
          BUILDER.close();

          // inner lines
          final double deltaY2 = size * 0.2f;
          final double deltaX1 = 0.045f;
          final double deltaX2 = 0.085f;
          final double length = 0.3f * (size + curveEndX);

          BUILDER.moveTo(0.5 - length - deltaX2, 0.5 - 3f * deltaY2);
          BUILDER.lineTo(0.5 + length - deltaX2, 0.5 - 3f * deltaY2);
          BUILDER.moveTo(0.5 - length - deltaX1, 0.5 - 1f * deltaY2);
          BUILDER.lineTo(0.5 + length - deltaX1, 0.5 - 1f * deltaY2);
          BUILDER.moveTo(0.5 - length + deltaX1, 0.5 + 1f * deltaY2);
          BUILDER.lineTo(0.5 + length + deltaX1, 0.5 + 1f * deltaY2);
          BUILDER.moveTo(0.5 - length + deltaX2, 0.5 + 3f * deltaY2);
          BUILDER.lineTo(0.5 + length + deltaX2, 0.5 + 3f * deltaY2);
          result = BUILDER.getPathIcon();
          break;
        case EVENT_TRIGGERED:
        default:
          break;
      }
      TASK_ICONS.put(type, result);
      return result;
    }
    return TASK_ICONS.get(type);
  }

  private static IIcon createGear( double radius, double centerX, double centerY, Pen pen, Paint paint ) {
    BUILDER.setPen(pen);
    BUILDER.setPaint(paint);
    double smallR = 0.7 * radius;

    double angle = -2 * Math.PI / 48;
    BUILDER.moveTo(centerX + radius * Math.cos(angle), centerY + radius * Math.sin(angle));
    for (int i = 0; i < 8; i++) {
      BUILDER.arcTo(radius, centerX, centerY, angle, angle + 4 * Math.PI / 48);
      BUILDER.lineTo(centerX + smallR * Math.cos(angle + 5 * Math.PI / 48), centerY + smallR * Math.sin(angle + 5 * Math.PI / 48));
      BUILDER.arcTo(smallR, centerX, centerY, angle + 5 * Math.PI / 48, angle + 11 * Math.PI / 48);
      BUILDER.lineTo(centerX + radius * Math.cos(angle + 12 * Math.PI / 48), centerY + radius * Math.sin(angle + 12 * Math.PI / 48));
      angle += Math.PI / 4;
    }

    BUILDER.close();
    return BUILDER.getPathIcon();

  }

  private static final HashMap<LoopCharacteristic, IIcon> LOOP_TYPES = new HashMap<>(4);

  public static final IIcon createLoopCharacteristic( LoopCharacteristic loopCharacteristic ) {
    if (!LOOP_TYPES.containsKey(loopCharacteristic)) {
      IIcon result = null;
      switch (loopCharacteristic) {
        case LOOP:
          final double fromAngle = 0.65 * Math.PI;
          final double toAngle = 2.4 * Math.PI;

          double x = 0.5 + 0.5 * Math.cos(fromAngle);
          double y = 0.5 + 0.5 * Math.sin(fromAngle);
          BUILDER.moveTo(x, y);
          BUILDER.arcTo(0.5, 0.5, 0.5, fromAngle, toAngle);
          BUILDER.moveTo(x - 0.25, y + 0.05);
          BUILDER.lineTo(x, y);
          BUILDER.lineTo(x, y - 0.3);

          result = BUILDER.getPathIcon();
          break;
        case PARALLEL:
          BUILDER.setPaint(Colors.BLACK);

          for (double xOffset = 0; xOffset < 1; xOffset += 0.4) {
            BUILDER.moveTo(xOffset, 0);
            BUILDER.lineTo(xOffset + 0.2, 0);
            BUILDER.lineTo(xOffset + 0.2, 1);
            BUILDER.lineTo(xOffset, 1);
            BUILDER.close();
          }
          result = BUILDER.getPathIcon();
          break;
        case SEQUENTIAL:
          BUILDER.setPaint(Colors.BLACK);

          for (double yOffset = 0; yOffset < 1; yOffset += 0.4) {
            BUILDER.moveTo(0, yOffset);
            BUILDER.lineTo(0, yOffset + 0.2);
            BUILDER.lineTo(1, yOffset + 0.2);
            BUILDER.lineTo(1, yOffset);
            BUILDER.close();
          }
          result = BUILDER.getPathIcon();
          break;
        case NONE:
        default:
          break;
      }
      LOOP_TYPES.put(loopCharacteristic, result);
      return result;
    }
    return LOOP_TYPES.get(loopCharacteristic);
  }

  private static IIcon adHoc;

  public static final IIcon createAdHoc() {
    if (adHoc == null) {
      BUILDER.setPaint(Colors.BLACK);

      final double fromAngle1 = 5.0 / 4.0 * Math.PI;
      final double toAngle1 = 7.0 / 4.0 * Math.PI;
      final double fromAngle2 = 1.0 / 4.0 * Math.PI;
      final double toAngle2 = 3.0 / 4.0 * Math.PI;

      double smallR = 0.25 / (1 - Math.sqrt(1.5 - Math.sqrt(2)));
      double co = smallR * RADIUS_TO_CORNER_OFFSET;
      double dy = 0.1;

      double c1x = smallR - co;
      double c1y = 0.35 + smallR;
      double x1 = c1x + smallR * Math.cos(fromAngle1);
      double y1 = c1y + smallR * Math.sin(fromAngle1);

      double c2x = c1x + 2 * smallR - 2 * co;
      double c2y = c1y - 2 * smallR + 2 * co;

      double x2 = c2x + smallR * Math.cos(fromAngle2);
      double y2 = c2y + smallR * Math.sin(fromAngle2);
      BUILDER.moveTo(x1, y1 + dy);
      BUILDER.lineTo(x1, y1);
      BUILDER.arcTo(smallR, c1x, c1y, fromAngle1, toAngle1);
      BUILDER.arcTo(smallR, c2x, c2y, toAngle2, fromAngle2);
      BUILDER.lineTo(x2, y2 + dy);
      BUILDER.arcTo(smallR, c2x, c2y + dy, fromAngle2, toAngle2);
      BUILDER.arcTo(smallR, c1x, c1y + dy, toAngle1, fromAngle1);
      BUILDER.close();
      adHoc = BUILDER.getPathIcon();
    }
    return adHoc;
  }

  private static IIcon comparison;

  private static IIcon filledComparison;

  public static final IIcon createCompensation( boolean filled ) {
    if ((!filled && comparison == null) || (filled && filledComparison == null)) {
      BUILDER.setPaint(filled ? BpmnConstants.Paints.EVENT_TYPE_THROWING : BpmnConstants.Paints.EVENT_TYPE_CATCHING);
      double sqrt3inv = 1 / Math.sqrt(3);
      double halfSqurt3 = sqrt3inv / 2;
      double xOff = 0.5 / (2 * sqrt3inv);
      BUILDER.moveTo(0, 0.5);
      BUILDER.lineTo(xOff, 0.5 - halfSqurt3);
      BUILDER.lineTo(xOff, 0.5);
      BUILDER.lineTo(2 * xOff, 0.5 - halfSqurt3);
      BUILDER.lineTo(2 * xOff, 0.5 + halfSqurt3);
      BUILDER.lineTo(xOff, 0.5);
      BUILDER.lineTo(xOff, 0.5 + halfSqurt3);
      BUILDER.close();
      if (filled) {
        filledComparison = BUILDER.getPathIcon();
      } else {
        comparison = BUILDER.getPathIcon();
      }
    }
    return filled ? filledComparison : comparison;
  }

  private static final HashMap<SubState, IIcon> SUB_STATES = new HashMap<>(3);

  public static final IIcon createStaticSubState( SubState subState ) {
    if (!SUB_STATES.containsKey(subState)) {
      IIcon result = null;
      switch (subState) {
        case EXPANDED:
          ArrayList<IIcon> icons = new ArrayList<>();
          icons.add(BUILDER.createRectIcon(0));
          BUILDER.moveTo(0.2, 0.5);
          BUILDER.lineTo(0.8, 0.5);
          icons.add(BUILDER.getPathIcon());
          result = BUILDER.combineIcons(icons);
          break;
        case COLLAPSED:
          ArrayList<IIcon> icons2 = new ArrayList<>();
          icons2.add(BUILDER.createRectIcon(0));
          BUILDER.moveTo(0.2, 0.5);
          BUILDER.lineTo(0.8, 0.5);
          BUILDER.moveTo(0.5, 0.2);
          BUILDER.lineTo(0.5, 0.8);
          icons2.add(BUILDER.getPathIcon());
          result = BUILDER.combineIcons(icons2);
          break;
        case NONE:
        default:
          break;
      }
      SUB_STATES.put(subState, result);
      return result;
    }
    return SUB_STATES.get(subState);
  }

  public static final IIcon createDynamicSubState( INode node ) {
    return new CollapseButtonIcon(node);
  }

  private static IIcon gateway;

  public static final IIcon createGateway() {
    if (gateway == null) {
      BUILDER.setPen(BpmnConstants.Pens.GATEWAY);
      BUILDER.setPaint(BpmnConstants.Paints.GATEWAY);
      BUILDER.moveTo(0.5, 0);
      BUILDER.lineTo(1, 0.5);
      BUILDER.lineTo(0.5, 1);
      BUILDER.lineTo(0, 0.5);
      BUILDER.close();
      gateway = BUILDER.getPathIcon();
    }
    return gateway;
  }

  private static final HashMap<GatewayType, IIcon> GATEWAY_TYPES = new HashMap<>(8);

  public static final IIcon createGatewayType( GatewayType type ) {
    if (!GATEWAY_TYPES.containsKey(type)) {
      IIcon result = null;
      ArrayList<IIcon> icons;
      switch (type) {
        case EXCLUSIVE_WITHOUT_MARKER:
          break;
        case EXCLUSIVE_WITH_MARKER:
          BUILDER.setPaint(Colors.BLACK);
          double cornerOffY = 0.5 - 0.5 * Math.sin(Math.PI / 4);
          double cornerOffX = cornerOffY + 0.1;
          double xOff = 0.06;

          double x1 = cornerOffX;
          double x2 = cornerOffX + 2 * xOff;

          double y1 = cornerOffY;
          double y2 = 0.5 - (0.5 * xOff - cornerOffY * xOff) / (0.5 - cornerOffX - xOff);

          BUILDER.moveTo(x1, y1);
          BUILDER.lineTo(x2, y1);
          BUILDER.lineTo(0.5, y2);
          BUILDER.lineTo(1 - x2, y1);
          BUILDER.lineTo(1 - x1, y1);
          BUILDER.lineTo(0.5 + xOff, 0.5);
          BUILDER.lineTo(1 - x1, 1 - y1);
          BUILDER.lineTo(1 - x2, 1 - y1);
          BUILDER.lineTo(0.5, 1 - y2);
          BUILDER.lineTo(x2, 1 - y1);
          BUILDER.lineTo(x1, 1 - y1);
          BUILDER.lineTo(0.5 - xOff, 0.5);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case INCLUSIVE:
          BUILDER.setPen(BpmnConstants.Pens.GATEWAY_TYPE_INCLUSIVE);
          result = createPlacedIcon(BUILDER.createEllipseIcon(), BpmnConstants.Placements.THICK_LINE, SizeD.EMPTY);
          break;
        case EVENT_BASED:
        case EXCLUSIVE_EVENT_BASED:
          icons = new ArrayList<>(3);
          icons.add(BUILDER.createEllipseIcon());

          if (type == GatewayType.EVENT_BASED) {
            IIcon innerCircleIcon = BUILDER.createEllipseIcon();
            icons.add(createPlacedIcon(innerCircleIcon, BpmnConstants.Placements.DOUBLE_LINE, SizeD.EMPTY));
          }

          PointD[] polygon = createPolygon(5, 0.5, 0);
          BUILDER.moveTo(polygon[0].x, polygon[0].y);
          for (int i = 1; i < 5; i++) {
            BUILDER.lineTo(polygon[i].x, polygon[i].y);
          }
          BUILDER.close();
          IIcon innerIcon = BUILDER.getPathIcon();
          icons.add(createPlacedIcon(innerIcon, BpmnConstants.Placements.INSIDE_DOUBLE_LINE, SizeD.EMPTY));
          result = BUILDER.combineIcons(icons);
          break;
        case PARALLEL:
          result = createPlusIcon(0.8, Pen.getBlack(), Colors.BLACK);
          break;
        case PARALLEL_EVENT_BASED:
          icons = new ArrayList<>(2);
          icons.add(BUILDER.createEllipseIcon());
          icons.add(createPlusIcon(0.6, Pen.getBlack(), null));
          result = BUILDER.combineIcons(icons);
          break;
        case COMPLEX:
          BUILDER.setPaint(Colors.BLACK);
          PointD[] outer = createPolygon(24, 0.5, Math.PI / 24);
          double width = Math.sqrt(0.5 - (0.5 * Math.cos(Math.PI / 12)));
          double rInner = width * Math.sqrt((1 + Math.sqrt(2) / 2));
          PointD[] inner = createPolygon(8, rInner, Math.PI / 8);

          BUILDER.moveTo(outer[0].x, outer[0].y);
          for (int i = 0; i < 8; i++) {
            BUILDER.lineTo(outer[3 * i].x, outer[3 * i].y);
            BUILDER.lineTo(inner[i].x, inner[i].y);
            BUILDER.lineTo(outer[3 * i + 2].x, outer[3 * i + 2].y);
          }
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        default:
          break;
      }
      GATEWAY_TYPES.put(type, result);
      return result;
    }
    return GATEWAY_TYPES.get(type);
  }

  private static final HashMap<EventCharacteristic, IIcon> EVENT_CHARACTERISTICS = new HashMap<>(8);

  public static final IIcon createEvent( EventCharacteristic characteristic ) {
    if (!EVENT_CHARACTERISTICS.containsKey(characteristic)) {
      IIcon result = null;
      Pen pen = null;
      switch (characteristic) {
        case START:
        case SUB_PROCESS_INTERRUPTING:
          pen = BpmnConstants.Pens.EVENT_START;
          break;
        case SUB_PROCESS_NON_INTERRUPTING:
          pen = BpmnConstants.Pens.EVENT_SUB_PROCESS_NON_INTERRUPTING;
          break;
        case CATCHING:
        case BOUNDARY_INTERRUPTING:
        case THROWING:
          pen = BpmnConstants.Pens.EVENT_INTERMEDIATE;
          break;
        case BOUNDARY_NON_INTERRUPTING:
          pen = BpmnConstants.Pens.EVENT_BOUNDARY_NON_INTERRUPTING;
          break;
        case END:
          pen = BpmnConstants.Pens.EVENT_END;
          break;
      }

      BUILDER.setPen(pen);
      BUILDER.setPaint(BpmnConstants.Paints.EVENT);
      IIcon ellipseIcon = BUILDER.createEllipseIcon();

      switch (characteristic) {
        case CATCHING:
        case BOUNDARY_INTERRUPTING:
        case BOUNDARY_NON_INTERRUPTING:
        case THROWING:
          ArrayList<IIcon> icons = new ArrayList<>();
          icons.add(ellipseIcon);

          BUILDER.setPen(pen);
          BUILDER.setPaint(BpmnConstants.Paints.EVENT);
          IIcon innerEllipseIcon = BUILDER.createEllipseIcon();
          icons.add(createPlacedIcon(innerEllipseIcon, BpmnConstants.Placements.DOUBLE_LINE, SizeD.EMPTY));
          result = createCombinedIcon(icons);
          break;
        default:
          result = ellipseIcon;
          break;
      }
      EVENT_CHARACTERISTICS.put(characteristic, result);
      return result;
    }
    return EVENT_CHARACTERISTICS.get(characteristic);
  }

  private static final HashMap<EventTypeWithFill, IIcon> EVENT_TYPES = new HashMap<>(26);

  public static final IIcon createEventType( EventType type, boolean filled ) {
    EventTypeWithFill eventTypeWithFill = new EventTypeWithFill(type, filled);
    if (!EVENT_TYPES.containsKey(eventTypeWithFill)) {
      IIcon result = null;
      BUILDER.setPen(BpmnConstants.Pens.EVENT_TYPE);
      BUILDER.setPaint(filled ? BpmnConstants.Paints.EVENT_TYPE_THROWING : BpmnConstants.Paints.EVENT_TYPE_CATCHING);

      ArrayList<IIcon> icons;
      switch (type) {
        case PLAIN:
          BUILDER.clear();
          break;
        case MESSAGE:
          Pen messagePen = filled ? BpmnConstants.Pens.MESSAGE_INVERTED : BpmnConstants.Pens.MESSAGE;
          Paint messagePaint = filled ? BpmnConstants.Paints.MESSAGE_INVERTED : BpmnConstants.Paints.MESSAGE;
          IIcon combinedIcons = createMessage(messagePen, messagePaint);
          result = createPlacedIcon(combinedIcons, BpmnConstants.Placements.EVENT_TYPE_MESSAGE, SizeD.EMPTY);
          break;
        case TIMER:
          icons = new ArrayList<>();
          icons.add(BUILDER.createEllipseIcon());
          BUILDER.setPen(filled ? BpmnConstants.Pens.EVENT_TYPE_DETAIL_INVERTED : BpmnConstants.Pens.EVENT_TYPE_DETAIL);
          PointD[] outerPoints = createPolygon(12, 0.5, 0);
          PointD[] innerPoints = createPolygon(12, 0.4, 0);
          for (int i = 0; i < 12; i++) {
            BUILDER.moveTo(outerPoints[i].x, outerPoints[i].y);
            BUILDER.lineTo(innerPoints[i].x, innerPoints[i].y);
          }
          BUILDER.moveTo(0.5, 0.5);
          BUILDER.lineTo(0.8, 0.5);
          BUILDER.moveTo(0.5, 0.5);
          BUILDER.lineTo(0.6, 0.1);
          icons.add(BUILDER.getPathIcon());
          result = createCombinedIcon(icons);
          break;
        case ESCALATION:
          double cornerOnCircle = 0.5 - 0.5 * RADIUS_TO_CORNER_OFFSET;
          BUILDER.moveTo(0.5, 0);
          BUILDER.lineTo(0.5 + cornerOnCircle, 0.5 + cornerOnCircle);
          BUILDER.lineTo(0.5, 0.5);
          BUILDER.lineTo(0.5 - cornerOnCircle, 0.5 + cornerOnCircle);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case CONDITIONAL:
          icons = new ArrayList<>();
          BUILDER.moveTo(0.217, 0.147);
          BUILDER.lineTo(0.783, 0.147);
          BUILDER.lineTo(0.783, 0.853);
          BUILDER.lineTo(0.217, 0.853);
          BUILDER.close();
          icons.add(BUILDER.getPathIcon());

          BUILDER.setPen(filled ? BpmnConstants.Pens.EVENT_TYPE_DETAIL_INVERTED : BpmnConstants.Pens.EVENT_TYPE_DETAIL);
          for (int i = 0; i < 4; i++) {
            double y = 0.235 + i * 0.177;
            BUILDER.moveTo(0.274, y);
            BUILDER.lineTo(0.726, y);
          }
          icons.add(BUILDER.getPathIcon());
          result = BUILDER.combineIcons(icons);
          break;
        case LINK:
          BUILDER.moveTo(0.1, 0.38);
          BUILDER.lineTo(0.5, 0.38);
          BUILDER.lineTo(0.5, 0.1);
          BUILDER.lineTo(0.9, 0.5);
          BUILDER.lineTo(0.5, 0.9);
          BUILDER.lineTo(0.5, 0.62);
          BUILDER.lineTo(0.1, 0.62);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case ERROR:
          final float x1 = 0.354f;
          final float x2 = 0.084f;
          final float x3 = 0.115f;
          final float y1 = 0.354f;
          final float y2 = 0.049f;
          final float y3 = 0.260f;

          BUILDER.moveTo(0.5 + x1, 0.5 - y1);
          BUILDER.lineTo(0.5 + x2, 0.5 + y2);
          BUILDER.lineTo(0.5 - x3, 0.5 - y3);
          BUILDER.lineTo(0.5 - x1, 0.5 + y1);
          BUILDER.lineTo(0.5 - x2, 0.5 - y2);
          BUILDER.lineTo(0.5 + x3, 0.5 + y3);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case CANCEL:
          double bigD = 0.5 - 0.5 * RADIUS_TO_CORNER_OFFSET;
          final double smallD = 0.05;
          BUILDER.moveTo(0.5 - bigD - smallD, 0.5 - bigD + smallD);
          BUILDER.lineTo(0.5 - bigD + smallD, 0.5 - bigD - smallD);
          BUILDER.lineTo(0.5, 0.5 - 2 * smallD);
          BUILDER.lineTo(0.5 + bigD - smallD, 0.5 - bigD - smallD);
          BUILDER.lineTo(0.5 + bigD + smallD, 0.5 - bigD + smallD);
          BUILDER.lineTo(0.5 + 2 * smallD, 0.5);
          BUILDER.lineTo(0.5 + bigD + smallD, 0.5 + bigD - smallD);
          BUILDER.lineTo(0.5 + bigD - smallD, 0.5 + bigD + smallD);
          BUILDER.lineTo(0.5, 0.5 + 2 * smallD);
          BUILDER.lineTo(0.5 - bigD + smallD, 0.5 + bigD + smallD);
          BUILDER.lineTo(0.5 - bigD - smallD, 0.5 + bigD - smallD);
          BUILDER.lineTo(0.5 - 2 * smallD, 0.5);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case COMPENSATION:
          result = createCompensation(filled);
          BUILDER.clear();
          break;
        case SIGNAL:
          PointD[] triangle = createPolygon(3, 0.5, 0);
          BUILDER.moveTo(triangle[0].x, triangle[0].y);
          BUILDER.lineTo(triangle[1].x, triangle[1].y);
          BUILDER.lineTo(triangle[2].x, triangle[2].y);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case MULTIPLE:
          PointD[] pentagram = createPolygon(5, 0.5, 0);
          BUILDER.moveTo(pentagram[0].x, pentagram[0].y);
          BUILDER.lineTo(pentagram[1].x, pentagram[1].y);
          BUILDER.lineTo(pentagram[2].x, pentagram[2].y);
          BUILDER.lineTo(pentagram[3].x, pentagram[3].y);
          BUILDER.lineTo(pentagram[4].x, pentagram[4].y);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case PARALLEL_MULTIPLE:
          Paint paint = filled ? BpmnConstants.Paints.EVENT_TYPE_THROWING : BpmnConstants.Paints.EVENT_TYPE_CATCHING;
          result = createPlusIcon(1.0, BpmnConstants.Pens.EVENT_TYPE, paint);
          break;
        case TERMINATE:
          result = BUILDER.createEllipseIcon();
          break;
        default:
          BUILDER.clear();
          break;
      }
      EVENT_TYPES.put(eventTypeWithFill, result);
      return result;
    }
    return EVENT_TYPES.get(eventTypeWithFill);
    }

  private static class EventTypeWithFill {
    private final EventType type;

    private final boolean filled;

    public EventTypeWithFill( EventType type, boolean filled ) {
      this.type = type;
      this.filled = filled;
    }

    @Override
    public boolean equals( Object other ) {
      if (!(other instanceof EventTypeWithFill)) {
        return false;
      }
      return ((EventTypeWithFill)other).type == type && ((EventTypeWithFill)other).filled == filled;
    }

    @Override
    public int hashCode() {
      {
        return (type.hashCode() * 397) ^ Boolean.valueOf(filled).hashCode();
      }
    }

  }

  private static final HashMap<PenAndPaint, IIcon> MESSAGES = new HashMap<>();

  public static final IIcon createMessage( Pen pen, Paint paint ) {
    PenAndPaint penAndPaint = new PenAndPaint(pen, paint);
    if (!MESSAGES.containsKey(penAndPaint)) {
      IIcon result = null;

      ArrayList<IIcon> icons = new ArrayList<>();

      BUILDER.setPen(pen);
      BUILDER.setPaint(paint);
      BUILDER.moveTo(0, 0);
      BUILDER.lineTo(1, 0);
      BUILDER.lineTo(1, 1);
      BUILDER.lineTo(0, 1);
      BUILDER.close();
      icons.add(BUILDER.getPathIcon());

      BUILDER.setPen(pen);
      BUILDER.moveTo(0, 0);
      BUILDER.lineTo(0.5, 0.5);
      BUILDER.lineTo(1, 0);
      icons.add(BUILDER.getPathIcon());
      result = BUILDER.combineIcons(icons);
      MESSAGES.put(penAndPaint, result);
      return result;
    }
    return MESSAGES.get(penAndPaint);
    }

  private static class PenAndPaint {
    private final Pen pen;

    private final Paint paint;

    public PenAndPaint( Pen pen, Paint paint ) {
      this.pen = pen;
      this.paint = paint;
    }

    public final boolean equals( PenAndPaint other ) {
      return other.pen == pen && other.paint == paint;
    }

    @Override
    public boolean equals( Object other ) {
      if (!(other instanceof PenAndPaint)) {
        return false;
      }
      return ((PenAndPaint)other).pen == pen && ((PenAndPaint)other).paint == paint;
    }

    @Override
    public int hashCode() {
      {
        return (pen.hashCode() * 397) ^ paint.hashCode();
      }
    }

    public PenAndPaint clone() {
      try {
        return (PenAndPaint)super.clone();
      }catch (CloneNotSupportedException e) {
        throw new InternalError();
      }
    }

  }

  private static final HashMap<PlusData, IIcon> PLUS_ICONS = new HashMap<>();

  private static IIcon createPlusIcon( double size, Pen pen, Paint paint ) {
    PlusData plusData = new PlusData(size, pen, paint);
    if (!PLUS_ICONS.containsKey(plusData)) {
      IIcon result = null;
      BUILDER.setPen(pen);
      BUILDER.setPaint(paint);
      double d = 0.1 * size;
      double dOff = Math.sqrt(0.25 * size * size - d * d);
      BUILDER.moveTo(0.5 - dOff, 0.5 - d);
      BUILDER.lineTo(0.5 - d, 0.5 - d);
      BUILDER.lineTo(0.5 - d, 0.5 - dOff);
      BUILDER.lineTo(0.5 + d, 0.5 - dOff);
      BUILDER.lineTo(0.5 + d, 0.5 - d);
      BUILDER.lineTo(0.5 + dOff, 0.5 - d);
      BUILDER.lineTo(0.5 + dOff, 0.5 + d);
      BUILDER.lineTo(0.5 + d, 0.5 + d);
      BUILDER.lineTo(0.5 + d, 0.5 + dOff);
      BUILDER.lineTo(0.5 - d, 0.5 + dOff);
      BUILDER.lineTo(0.5 - d, 0.5 + d);
      BUILDER.lineTo(0.5 - dOff, 0.5 + d);
      BUILDER.close();
      result = BUILDER.getPathIcon();
      PLUS_ICONS.put(plusData, result);
      return result;
    }
    return PLUS_ICONS.get(plusData);
  }

  private static class PlusData {
    private final double size;

    private final Pen pen;

    private final Paint paint;

    public PlusData( double size, Pen pen, Paint paint ) {
      this.size = size;
      this.pen = pen;
      this.paint = paint;
    }

    public final boolean equals( PlusData other ) {
      return other.size == size && other.pen == pen && other.paint == paint;
    }

    @Override
    public boolean equals( Object other ) {
      if (!(other instanceof PlusData)) {
        return false;
      }
      PlusData otherData = (PlusData)other;
      return otherData.size == size && otherData.pen == pen && otherData.paint == paint;
    }

    @Override
    public int hashCode() {
      {
        int brushHC = paint != null ? paint.hashCode() : 1;
        return (((Double.valueOf(size).hashCode() * 397) ^ pen.hashCode()) * 397) ^ brushHC;
      }
    }

    public PlusData clone() {
      try {
        return (PlusData)super.clone();
      }catch (CloneNotSupportedException e) {
        throw new InternalError();
      }
    }

  }

  private static IIcon choreographyTask;

  private static IIcon choreographyCall;

  public static final IIcon createChoreography( ChoreographyType type ) {
    if (type == ChoreographyType.TASK) {
      if (choreographyTask == null) {
        BUILDER.setPen(BpmnConstants.Pens.CHOREOGRAPHY_TASK);
        choreographyTask = BUILDER.createRectIcon(BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS, BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS, BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS, BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS);
      }
      return choreographyTask;
    } else {
      if (choreographyCall == null) {
        BUILDER.setPen(BpmnConstants.Pens.CHOREOGRAPHY_CALL);
        choreographyCall = BUILDER.createRectIcon(BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS, BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS, BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS, BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS);
      }
      return choreographyCall;
    }
  }

  private static final HashMap<ParticipantBandType, IIcon> PARTICIPANT_BANDS = new HashMap<>();

  public static final IIcon createChoreographyParticipant( boolean sending, double topRadius, double bottomRadius ) {
    ParticipantBandType participantBandType = new ParticipantBandType(sending, topRadius, bottomRadius);
    if (!PARTICIPANT_BANDS.containsKey(participantBandType)) {
      IIcon result = null;
      BUILDER.setPen(BpmnConstants.Pens.CHOREOGRAPHY_TASK);
      BUILDER.setPaint(sending ? BpmnConstants.Paints.CHOREOGRAPHY_INITIALIZING_PARTICIPANT : BpmnConstants.Paints.CHOREOGRAPHY_RECEIVING_PARTICIPANT);
      result = BUILDER.createRectIcon(topRadius, topRadius, bottomRadius, bottomRadius);
      PARTICIPANT_BANDS.put(participantBandType, result);
      return result;
    }
    return PARTICIPANT_BANDS.get(participantBandType);
  }

  private static final class ParticipantBandType {

    private final boolean sending;

    private final double topRadius;

    private final double bottomRadius;

    public ParticipantBandType( boolean sending, double topRadius, double bottomRadius ) {
      this.sending = sending;
      this.topRadius = topRadius;
      this.bottomRadius = bottomRadius;
    }

    public final boolean equals( ParticipantBandType other ) {
      return other.sending == sending && other.topRadius == topRadius && other.bottomRadius == bottomRadius;
    }

    @Override
    public boolean equals( Object obj ) {
      if (obj.getClass() != ParticipantBandType.class) {
        return false;
      }
      return equals((ParticipantBandType)obj);
    }

    @Override
    public int hashCode() {
      {
        return (Boolean.valueOf(sending).hashCode() * 397) ^ Double.valueOf(topRadius).hashCode() * 397 ^ Double.valueOf(bottomRadius).hashCode();
      }
    }

    public ParticipantBandType clone() {
      try {
        return (ParticipantBandType)super.clone();
      }catch (CloneNotSupportedException e) {
        throw new InternalError();
      }
    }

  }

  private static IIcon taskBand;

  public static final IIcon createChoreographyTaskBand() {
    if (taskBand == null) {
      BUILDER.setPen(null);
      BUILDER.setPaint(BpmnConstants.Paints.CHOREOGRAPHY_TASK_BAND);
      taskBand = BUILDER.createRectIcon(0);
    }
    return taskBand;
  }

  private static final HashMap<ConversationType, IIcon> CONVERSATIONS = new HashMap<ConversationType, IIcon>(4);

  public static final IIcon createConversation( ConversationType type ) {
    if (!CONVERSATIONS.containsKey(type)) {
      IIcon result = null;
      switch (type) {
        case CONVERSATION:
        case SUB_CONVERSATION:
          BUILDER.setPen(BpmnConstants.Pens.CONVERSATION);
          break;
        case CALLING_GLOBAL_CONVERSATION:
        case CALLING_COLLABORATION:
          BUILDER.setPen(BpmnConstants.Pens.CALLING_CONVERSATION);
          break;
      }
      BUILDER.setPaint(BpmnConstants.Paints.CONVERSATION);

      BUILDER.moveTo(0, 0.5);
      BUILDER.lineTo(0.25, 0);
      BUILDER.lineTo(0.75, 0);
      BUILDER.lineTo(1, 0.5);
      BUILDER.lineTo(0.75, 1);
      BUILDER.lineTo(0.25, 1);
      BUILDER.close();
      result = BUILDER.getPathIcon();
      CONVERSATIONS.put(type, result);
      return result;
    }
    return CONVERSATIONS.get(type);
  }

  private static IIcon conversationSubState;

  public static final IIcon createConversationMarker( ConversationType type ) {
    switch (type) {
      case SUB_CONVERSATION:
      case CALLING_COLLABORATION:
        return conversationSubState != null ? conversationSubState : (conversationSubState = createStaticSubState(SubState.COLLAPSED));
      default:
        return null;
    }
  }

  private static IIcon dataObject;

  public static final IIcon createDataObject() {
    return dataObject != null ? dataObject : (dataObject = createDataObjectIcon());
  }

  private static DataObjectIcon createDataObjectIcon() {
    DataObjectIcon icon = new DataObjectIcon();
    icon.setPen(BpmnConstants.Pens.DATA_OBJECT);
    icon.setPaint(BpmnConstants.Paints.DATA_OBJECT);
    return icon;
  }

  private static IIcon dataObjectInputType;

  private static IIcon dataObjectOutputType;

  public static final IIcon createDataObjectType( DataObjectType type ) {
    switch (type) {
      case INPUT:
        return dataObjectInputType != null ? dataObjectInputType : (dataObjectInputType = createEventType(EventType.LINK, false));
      case OUTPUT:
        return dataObjectOutputType != null ? dataObjectOutputType : (dataObjectOutputType = createEventType(EventType.LINK, true));
      case NONE:
      default:
        return null;
    }
  }

  private static IIcon leftAnnotation;

  private static IIcon rightAnnotation;

  public static final IIcon createAnnotation( boolean left ) {
    if (left) {
      if (leftAnnotation == null) {
        ArrayList<IIcon> icons = new ArrayList<>();
        BUILDER.setPen(null);
        BUILDER.setPaint(BpmnConstants.Paints.ANNOTATION);
        icons.add(BUILDER.createRectIcon(0));
        BUILDER.setPen(BpmnConstants.Pens.ANNOTATION);
        BUILDER.moveTo(0.1, 0);
        BUILDER.lineTo(0, 0);
        BUILDER.lineTo(0, 1);
        BUILDER.lineTo(0.1, 1);
        icons.add(BUILDER.getPathIcon());
        leftAnnotation = BUILDER.combineIcons(icons);
      }
      return leftAnnotation;
    } else {
      if (rightAnnotation == null) {
        ArrayList<IIcon> icons = new ArrayList<>();
        BUILDER.setPen(null);
        BUILDER.setPaint(BpmnConstants.Paints.ANNOTATION);
        icons.add(BUILDER.createRectIcon(0));
        BUILDER.setPen(BpmnConstants.Pens.ANNOTATION);
        BUILDER.moveTo(0.9, 0);
        BUILDER.lineTo(1, 0);
        BUILDER.lineTo(1, 1);
        BUILDER.lineTo(0.9, 1);
        icons.add(BUILDER.getPathIcon());
        rightAnnotation = BUILDER.combineIcons(icons);
      }
      return rightAnnotation;
    }

  }

  private static IIcon dataStore;

  public static final IIcon createDataStore() {
    if (dataStore == null) {
      final double halfEllipseHeight = 0.125;
      final double ringOffset = 0.07;

      ArrayList<IIcon> icons = new ArrayList<>();
      BUILDER.setPen(BpmnConstants.Pens.DATA_STORE);
      BUILDER.setPaint(BpmnConstants.Paints.DATA_STORE);

      BUILDER.moveTo(0, halfEllipseHeight);
      BUILDER.lineTo(0, 1 - halfEllipseHeight);
      BUILDER.cubicTo(0, 1, 1, 1, 1, 1 - halfEllipseHeight);
      BUILDER.lineTo(1, halfEllipseHeight);
      BUILDER.cubicTo(1, 0, 0, 0, 0, halfEllipseHeight);
      BUILDER.close();
      icons.add(BUILDER.getPathIcon());

      BUILDER.setPen(BpmnConstants.Pens.DATA_STORE);
      double ellipseCenterY = halfEllipseHeight;
      for (int i = 0; i < 3; i++) {
        BUILDER.moveTo(0, ellipseCenterY);
        BUILDER.cubicTo(0, ellipseCenterY + halfEllipseHeight, 1, ellipseCenterY + halfEllipseHeight, 1, ellipseCenterY);
        ellipseCenterY += ringOffset;
      }
      icons.add(BUILDER.getPathIcon());
      dataStore = BUILDER.combineIcons(icons);
    }
    return dataStore;
  }

  private static final HashMap<ArrowType, IIcon> ARROWS = new HashMap<>(8);

  public static final IIcon createArrowIcon( ArrowType type ) {
    if (!ARROWS.containsKey(type)) {
      IIcon result = null;
      BUILDER.setPen(BpmnConstants.Pens.ARROW);
      switch (type) {
        case DEFAULT_SOURCE:
          BUILDER.moveTo(0.1, 0.1);
          BUILDER.lineTo(0.9, 0.9);
          result = BUILDER.getPathIcon();
          break;
        case ASSOCIATION:
          BUILDER.moveTo(0.5, 0);
          BUILDER.lineTo(1, 0.5);
          BUILDER.lineTo(0.5, 1);
          result = BUILDER.getPathIcon();
          break;
        case CONDITIONAL_SOURCE:
          BUILDER.moveTo(0, 0.5);
          BUILDER.lineTo(0.5, 0);
          BUILDER.lineTo(1, 0.5);
          BUILDER.lineTo(0.5, 1);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        case MESSAGE_SOURCE:
          result = BUILDER.createEllipseIcon();
          break;
        case MESSAGE_TARGET:
          BUILDER.moveTo(0, 0);
          BUILDER.lineTo(1, 0.5);
          BUILDER.lineTo(0, 1);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
        default:
        case SEQUENCE_TARGET:
        case DEFAULT_TARGET:
        case CONDITIONAL_TARGET:
          BUILDER.setPaint(Colors.BLACK);
          BUILDER.moveTo(0, 0);
          BUILDER.lineTo(1, 0.5);
          BUILDER.lineTo(0, 1);
          BUILDER.close();
          result = BUILDER.getPathIcon();
          break;
      }
      ARROWS.put(type, result);
      return result;
    }
    return ARROWS.get(type);
  }

  public static final IIcon createLine( Pen pen, double x1, double y1, double x2, double y2 ) {
    BUILDER.setPen(pen);
    BUILDER.moveTo(x1, y1);
    BUILDER.lineTo(x2, y2);
    return BUILDER.getPathIcon();
  }

  protected static final PointD[] createPolygon( int sideCount, double radius, double rotation ) {
    PointD[] result = new PointD[sideCount];
    double delta = Math.PI * 2.0 / sideCount;

    for (int i = 0; i < sideCount; i++) {
      double angle = delta * i + rotation;
      result[i] = (new PointD(radius * Math.sin(angle) + 0.5, -radius * Math.cos(angle) + 0.5));
    }
    return result;
  }

}
