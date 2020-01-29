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
package complete.uml;

import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.Pen;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * This is a factory for uml class elements conforming to the UML diagrams.
 */
public class UmlStyleFactory {
  private static final Color COLOR_YELLOW = new Color(213, 255, 179);
  
  /** Color that is used to paint edges. */
  private static final Color COLOR_EDGE = Color.DARK_GRAY;

  /** Color that is used to paint background areas of the UML nodes. */
  public static final Color COLOR_BACKGROUND = Color.WHITE;

  /** Color that is used to paint foreground areas of the UML nodes. */
  public static final Color COLOR_FOREGROUND = new Color(34, 139, 34);

  /** Color that is used to paint selected areas like button or list items. */
  public static final Color COLOR_SELECTION = COLOR_YELLOW;

  /** Line type for the outline of the edge creation buttons. */
  public static final BasicStroke LINE_EDGE_CREATION_BUTTON_OUTLINE = new BasicStroke(2);
  

  /**
   * Creates a {@link IEdgeStyle} that represents an association as defined in UML 2.0.
   */
  public static IEdgeStyle createAssociationStyle() {
    PolylineEdgeStyle association = new PolylineEdgeStyle();
    association.setPen(createRelationPen());
    return association;
  }

  /**
   * Creates a {@link IEdgeStyle} that represents a dependency as defined in UML 2.0.
   */
  public static IEdgeStyle createDependencyStyle() {
    PolylineEdgeStyle dependency = new PolylineEdgeStyle();
    dependency.setPen(createInheritancePen());
    dependency.setTargetArrow(Arrow.SIMPLE);
    return dependency;
  }

  /**
   * Creates a {@link IEdgeStyle} that represents a generalization as defined in UML 2.0.
   */
  public static IEdgeStyle createGeneralizationStyle() {
    PolylineEdgeStyle generalization = new PolylineEdgeStyle();
    generalization.setPen(createRelationPen());
    generalization.setTargetArrow(new Arrow(ArrowType.TRIANGLE, new Pen(COLOR_EDGE), Color.WHITE));
    return generalization;
  }

  /**
   * Creates a {@link IEdgeStyle} that represents a realization as defined in UML 2.0.
   */
  public static IEdgeStyle createRealizationStyle() {
    PolylineEdgeStyle realization = new PolylineEdgeStyle();
    realization.setPen(createInheritancePen());
    realization.setTargetArrow(new Arrow(ArrowType.TRIANGLE, new Pen(COLOR_EDGE), Color.WHITE));
    return realization;
  }

  /**
   * Creates a {@link IEdgeStyle} that represents an aggregation as defined in UML 2.0.
   */
  public static IEdgeStyle createAggregationStyle() {
    PolylineEdgeStyle aggregation = new PolylineEdgeStyle();
    aggregation.setPen(createRelationPen());
    aggregation.setSourceArrow(new Arrow(ArrowType.DIAMOND, new Pen(COLOR_EDGE), Color.WHITE));
    return aggregation;
  }

  /**
   * Creates a {@link IEdgeStyle} that represents a composition as defined in UML 2.0.
   */
  public static IEdgeStyle createCompositionStyle() {
    PolylineEdgeStyle composition = new PolylineEdgeStyle();
    composition.setPen(createRelationPen());
    composition.setSourceArrow(Arrow.DIAMOND);
    return composition;
  }

  /**
   * Creates a solid pen to paint relation-edges.
   */
  public static Pen createRelationPen() {
    return new Pen(COLOR_EDGE);
  }

  /**
   * Creates a dashed pen to paint inheritance-edges.
   */
  public static Pen createInheritancePen() {
    Pen pen = new Pen(COLOR_EDGE);
    pen.setDashStyle(DashStyle.getDash());
    return pen;
  }
  
  /**
    * Checks whether or not the given {@link IEdgeStyle} visualizes a UML realization.
    */
   static boolean isRealization(IEdgeStyle style) {
     if (style instanceof PolylineEdgeStyle) {
       PolylineEdgeStyle polylineEdgeStyle = (PolylineEdgeStyle) style;
       IArrow targetArrow = polylineEdgeStyle.getTargetArrow();
       if (targetArrow instanceof Arrow) {
         Arrow arrow = (Arrow) targetArrow;
         return arrow.getType() == ArrowType.TRIANGLE && arrow.getPaint() == Color.WHITE
             && polylineEdgeStyle.getPen().getDashStyle() == DashStyle.getSolid();
       }
     }
     return false;
   }
 
   /**
    * Checks whether or not the given {@link IEdgeStyle} visualizes a UML realization or generalization.
    */
   static boolean isInheritance(IEdgeStyle style) {
     if (style instanceof PolylineEdgeStyle) {
       PolylineEdgeStyle polylineEdgeStyle = (PolylineEdgeStyle) style;
       IArrow targetArrow = polylineEdgeStyle.getTargetArrow();
       if (targetArrow instanceof Arrow) {
         Arrow arrow = (Arrow) targetArrow;
         return arrow.getType() == ArrowType.TRIANGLE && arrow.getPaint() == Color.WHITE;
       }
     }
     return false;
   }
}
