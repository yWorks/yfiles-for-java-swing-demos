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
package toolkit;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.labelmodels.GroupNodeLabelModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.CornerStyle;
import com.yworks.yfiles.graph.styles.DefaultEdgePathCropper;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyleIconType;
import com.yworks.yfiles.graph.styles.GroupNodeStyleTabPosition;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.LabelShape;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;

import java.awt.Font;
import java.awt.Paint;

public final class DemoStyles {
  private DemoStyles() {
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param edgeLabelTheme Optional color set names for demo edge label styles. The default is the edge theme.
   * @param groupTheme Optional color set names for demo group node styles. The default is {@link toolkit.Themes#PALETTE12}.
   * @param groupLabelTheme Optional color set names for demo group node label styles. The default is group node theme.
   * @param foldingEnabled whether to use collapsable group node style.
   * @param extraCropLength the extra crop length for the {@link DefaultEdgePathCropper}.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme, toolkit.Palette edgeLabelTheme, toolkit.Palette groupTheme, toolkit.Palette groupLabelTheme, boolean foldingEnabled, double extraCropLength ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, edgeTheme, edgeLabelTheme, groupTheme, groupLabelTheme, foldingEnabled, extraCropLength, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param edgeLabelTheme Optional color set names for demo edge label styles. The default is the edge theme.
   * @param groupTheme Optional color set names for demo group node styles. The default is {@link toolkit.Themes#PALETTE12}.
   * @param groupLabelTheme Optional color set names for demo group node label styles. The default is group node theme.
   * @param foldingEnabled whether to use collapsable group node style.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme, toolkit.Palette edgeLabelTheme, toolkit.Palette groupTheme, toolkit.Palette groupLabelTheme, boolean foldingEnabled ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, edgeTheme, edgeLabelTheme, groupTheme, groupLabelTheme, foldingEnabled, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param edgeLabelTheme Optional color set names for demo edge label styles. The default is the edge theme.
   * @param groupTheme Optional color set names for demo group node styles. The default is {@link toolkit.Themes#PALETTE12}.
   * @param groupLabelTheme Optional color set names for demo group node label styles. The default is group node theme.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme, toolkit.Palette edgeLabelTheme, toolkit.Palette groupTheme, toolkit.Palette groupLabelTheme ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, edgeTheme, edgeLabelTheme, groupTheme, groupLabelTheme, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param edgeLabelTheme Optional color set names for demo edge label styles. The default is the edge theme.
   * @param groupTheme Optional color set names for demo group node styles. The default is {@link toolkit.Themes#PALETTE12}.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme, toolkit.Palette edgeLabelTheme, toolkit.Palette groupTheme ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, edgeTheme, edgeLabelTheme, groupTheme, (toolkit.Palette)null, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param edgeLabelTheme Optional color set names for demo edge label styles. The default is the edge theme.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme, toolkit.Palette edgeLabelTheme ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, edgeTheme, edgeLabelTheme, (toolkit.Palette)null, (toolkit.Palette)null, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, edgeTheme, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme ) {
    initDemoStyles(graph, nodeTheme, nodeLabelTheme, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme ) {
    initDemoStyles(graph, nodeTheme, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   */
  public static final void initDemoStyles( IGraph graph ) {
    initDemoStyles(graph, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, false, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param foldingEnabled whether to use collapsable group node style.
   */
  public static final void initDemoStyles( IGraph graph, boolean foldingEnabled ) {
    initDemoStyles(graph, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, (toolkit.Palette)null, foldingEnabled, 2.0, (ShapeNodeShape)null);
  }

  /**
   * Initializes graph defaults with nicely configured built-in yFiles styles.
   * @param graph The graph on which the default styles and style-related setting are set.
   * @param nodeTheme Optional color set names for demo node styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param nodeLabelTheme Optional color set names for demo node label styles. The default is the node theme.
   * @param edgeTheme Optional color set names for demo edge styles. The default is {@link toolkit.Themes#PALETTE_ORANGE}.
   * @param edgeLabelTheme Optional color set names for demo edge label styles. The default is the edge theme.
   * @param groupTheme Optional color set names for demo group node styles. The default is {@link toolkit.Themes#PALETTE12}.
   * @param groupLabelTheme Optional color set names for demo group node label styles. The default is group node theme.
   * @param foldingEnabled whether to use collapsable group node style.
   * @param extraCropLength the extra crop length for the {@link DefaultEdgePathCropper}.
   * @param shape the optional shape of the node style, if {@code null} a {@link RectangleNodeStyle} is used.
   */
  public static final void initDemoStyles( IGraph graph, toolkit.Palette nodeTheme, toolkit.Palette nodeLabelTheme, toolkit.Palette edgeTheme, toolkit.Palette edgeLabelTheme, toolkit.Palette groupTheme, toolkit.Palette groupLabelTheme, boolean foldingEnabled, double extraCropLength, ShapeNodeShape shape ) {
    nodeTheme = nodeTheme != null ? nodeTheme : toolkit.Themes.PALETTE_ORANGE;
    nodeLabelTheme = nodeLabelTheme != null ? nodeLabelTheme : nodeTheme;
    edgeTheme = edgeTheme != null ? edgeTheme : toolkit.Themes.PALETTE_ORANGE;
    edgeLabelTheme = edgeLabelTheme != null ? edgeLabelTheme : edgeTheme;
    groupTheme = groupTheme != null ? groupTheme : toolkit.Themes.PALETTE12;
    groupLabelTheme = groupLabelTheme != null ? groupLabelTheme : groupTheme;

    graph.getNodeDefaults().setStyle(shape == null ? (INodeStyle)createDemoNodeStyle(nodeTheme) : createDemoShapeNodeStyle(shape, nodeTheme));
    graph.getNodeDefaults().getLabelDefaults().setStyle(createDemoNodeLabelStyle(nodeLabelTheme));

    graph.getGroupNodeDefaults().setStyle(createDemoGroupStyle(groupTheme, foldingEnabled));
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(createDemoGroupLabelStyle(groupLabelTheme));
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(new GroupNodeLabelModel().createTabBackgroundParameter());

    graph.getEdgeDefaults().setStyle(createDemoEdgeStyle(edgeTheme));
    DefaultEdgePathCropper defaultEdgePathCropper = new DefaultEdgePathCropper();
    defaultEdgePathCropper.setCroppingAtPortEnabled(false);
    defaultEdgePathCropper.setExtraCropLength(extraCropLength);
    graph.getDecorator().getPortDecorator().getEdgePathCropperDecorator().setImplementation(defaultEdgePathCropper);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(createDemoEdgeLabelStyle(edgeLabelTheme));
  }

  /**
   * Creates a new rectangular node style whose colors match the given palette.
   */
  public static final RectangleNodeStyle createDemoNodeStyle() {
    return createDemoNodeStyle((toolkit.Palette)null);
  }

  /**
   * Creates a new rectangular node style whose colors match the given palette.
   */
  public static final RectangleNodeStyle createDemoNodeStyle( toolkit.Palette palette ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE_ORANGE;
    RectangleNodeStyle rectangleNodeStyle = new RectangleNodeStyle();
    rectangleNodeStyle.setPaint(palette.getBackgroundPaint());
    rectangleNodeStyle.setPen((Pen)new Pen(palette.getOutlinePaint(), 1.5));
    rectangleNodeStyle.setCornerStyle(CornerStyle.ROUND);
    rectangleNodeStyle.setCornerSize(3.5);
    return rectangleNodeStyle;
  }

  /**
   * Creates a new node style with the given shape whose colors match the given palette.
   */
  public static final ShapeNodeStyle createDemoShapeNodeStyle( ShapeNodeShape shape ) {
    return createDemoShapeNodeStyle(shape, (toolkit.Palette)null);
  }

  /**
   * Creates a new node style with the given shape whose colors match the given palette.
   */
  public static final ShapeNodeStyle createDemoShapeNodeStyle( ShapeNodeShape shape, toolkit.Palette palette ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE_ORANGE;
    ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle();
    shapeNodeStyle.setShape(shape);
    shapeNodeStyle.setPaint(palette.getBackgroundPaint());
    shapeNodeStyle.setPen((Pen)new Pen(palette.getOutlinePaint(), 1.5));
    return shapeNodeStyle;
  }

  /**
   * Creates a new polyline edge style whose colors match the given palette.
   */
  public static final PolylineEdgeStyle createDemoEdgeStyle( toolkit.Palette palette ) {
    return createDemoEdgeStyle(palette, true);
  }

  /**
   * Creates a new polyline edge style whose colors match the given palette.
   */
  public static final PolylineEdgeStyle createDemoEdgeStyle() {
    return createDemoEdgeStyle((toolkit.Palette)null, true);
  }

  /**
   * Creates a new polyline edge style whose colors match the given palette.
   */
  public static final PolylineEdgeStyle createDemoEdgeStyle( toolkit.Palette palette, boolean showTargetArrow ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE_ORANGE;
    PolylineEdgeStyle polylineEdgeStyle = new PolylineEdgeStyle();
    polylineEdgeStyle.setPen((Pen)new Pen(palette.getOutlinePaint(), 1.5));
    polylineEdgeStyle.setTargetArrow(showTargetArrow ? DemoStyles.initializer(new Arrow(), palette.getOutlinePaint(), ArrowType.TRIANGLE) : IArrow.NONE);
    return polylineEdgeStyle;
  }

  private static Arrow initializer( final Arrow instance, Paint p1, ArrowType p2 ) {
    instance.setPaint(p1);
    instance.setType(p2);
    return instance;
  }

  /**
   * Creates a new node label style whose colors match the given palette.
   */
  public static final DefaultLabelStyle createDemoNodeLabelStyle() {
    return createDemoNodeLabelStyle((toolkit.Palette)null);
  }

  /**
   * Creates a new node label style whose colors match the given palette.
   */
  public static final DefaultLabelStyle createDemoNodeLabelStyle( toolkit.Palette palette ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE_ORANGE;
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setShape(LabelShape.ROUND_RECTANGLE);
    labelStyle.setBackgroundPaint(palette.getNodeLabelBackgroundPaint());
    labelStyle.setTextPaint(palette.getTextPaint());
    labelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    labelStyle.setTextAlignment(TextAlignment.CENTER);
    labelStyle.setInsets(InsetsD.fromLTRB(4, 2, 4, 1));
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    return labelStyle;
  }

  /**
   * Creates a new edge label style whose colors match the given palette.
   */
  public static final DefaultLabelStyle createDemoEdgeLabelStyle() {
    return createDemoEdgeLabelStyle((toolkit.Palette)null);
  }

  /**
   * Creates a new edge label style whose colors match the given palette.
   */
  public static final DefaultLabelStyle createDemoEdgeLabelStyle( toolkit.Palette palette ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE_ORANGE;
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setShape(LabelShape.ROUND_RECTANGLE);
    labelStyle.setBackgroundPaint(palette.getEdgeLabelBackgroundPaint());
    labelStyle.setTextPaint(palette.getTextPaint());
    labelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    labelStyle.setTextAlignment(TextAlignment.CENTER);
    labelStyle.setInsets(InsetsD.fromLTRB(4, 2, 4, 1));
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    return labelStyle;
  }

  /**
   * Creates a new group label style whose colors match the given palette.
   */
  public static final ILabelStyle createDemoGroupLabelStyle() {
    return createDemoGroupLabelStyle((toolkit.Palette)null);
  }

  /**
   * Creates a new group label style whose colors match the given palette.
   */
  public static final ILabelStyle createDemoGroupLabelStyle( toolkit.Palette palette ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE12;
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    defaultLabelStyle.setTextAlignment(TextAlignment.LEFT);
    defaultLabelStyle.setTextClippingEnabled(false);
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    defaultLabelStyle.setTextPaint(palette.getNodeLabelBackgroundPaint());
    // for group node labels using the GroupNodeLabelModel we remove the insets and reduce the font size
    // so the label fits inside the default GroupNodeStyle.TabHeight
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    defaultLabelStyle.setFont(new Font("Dialog", Font.PLAIN, 11));
    defaultLabelStyle.setUsingFractionalFontMetricsEnabled(true);
    return defaultLabelStyle;
  }

  /**
   * Creates a new group node style whose colors match the default palette.
   */
  public static final GroupNodeStyle createDemoGroupStyle( boolean foldingEnabled ) {
    return createDemoGroupStyle(null, foldingEnabled);
  }

  /**
   * Creates a new group node style whose colors match the given palette.
   */
  public static final GroupNodeStyle createDemoGroupStyle(toolkit.Palette palette) {
    return createDemoGroupStyle(palette, false);
  }

  /**
   * Creates a new group node style.
   */
  public static final GroupNodeStyle createDemoGroupStyle() {
    return createDemoGroupStyle((toolkit.Palette)null, false);
  }

  /**
   * Creates a new group node style whose colors match the given palette.
   */
  public static final GroupNodeStyle createDemoGroupStyle( toolkit.Palette palette, boolean foldingEnabled ) {
    palette = palette != null ? palette : toolkit.Themes.PALETTE12;
    GroupNodeStyle groupNodeStyle = new GroupNodeStyle();
    groupNodeStyle.setGroupIcon(foldingEnabled ? GroupNodeStyleIconType.MINUS : GroupNodeStyleIconType.NONE);
    groupNodeStyle.setFolderIcon(GroupNodeStyleIconType.PLUS);
    groupNodeStyle.setTabPaint(foldingEnabled ? palette.getNodeLabelBackgroundPaint() : palette.getBackgroundPaint());
    groupNodeStyle.setPen((Pen)new Pen(palette.getBackgroundPaint(), 2.0));
    groupNodeStyle.setTabBackgroundPaint(foldingEnabled ? palette.getBackgroundPaint() : null);
    groupNodeStyle.setTabPosition(foldingEnabled ? GroupNodeStyleTabPosition.TOP_TRAILING : GroupNodeStyleTabPosition.TOP);
    groupNodeStyle.setTabWidth(30.0);
    groupNodeStyle.setTabHeight(20.0);
    groupNodeStyle.setTabInset(3.0);
    groupNodeStyle.setIconOffset(2.0);
    groupNodeStyle.setIconSize(14.0);
    groupNodeStyle.setIconForegroundPaint(palette.getBackgroundPaint());
    groupNodeStyle.setContentAreaHitTransparent(true);
    return groupNodeStyle;
  }

}
