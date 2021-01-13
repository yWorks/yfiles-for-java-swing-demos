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
package viewer.graphmlcompatibility;

import viewer.graphmlcompatibility.extensions.core.ArcEdgeStyleExtension;
import viewer.graphmlcompatibility.extensions.core.CollapsibleNodeStyleDecoratorExtension;
import viewer.graphmlcompatibility.extensions.core.EdgeViewStateExtension;
import viewer.graphmlcompatibility.extensions.core.GeneralPathNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.core.IconLabelStyleExtension;
import viewer.graphmlcompatibility.extensions.core.ImageNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.core.LabelDefaultsExtension;
import viewer.graphmlcompatibility.extensions.core.LabelExtension;
import viewer.graphmlcompatibility.extensions.core.MemoryIconLabelStyleExtension;
import viewer.graphmlcompatibility.extensions.core.NodeStyleLabelStyleAdapterExtension;
import viewer.graphmlcompatibility.extensions.core.NodeViewStateExtension;
import viewer.graphmlcompatibility.extensions.core.PenExtension;
import viewer.graphmlcompatibility.extensions.core.PolylineEdgeStyleExtension;
import viewer.graphmlcompatibility.extensions.core.PortDefaultsExtension;
import viewer.graphmlcompatibility.extensions.core.PortExtension;
import viewer.graphmlcompatibility.extensions.core.ShapeNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.core.ShinyPlateNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.core.SimpleLabelStyleExtension;

import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.VoidEdgeStyle;
import com.yworks.yfiles.graph.styles.VoidLabelStyle;
import com.yworks.yfiles.graph.styles.VoidNodeStyle;
import com.yworks.yfiles.graph.styles.VoidPathGeometry;
import com.yworks.yfiles.graph.styles.VoidPortStyle;
import com.yworks.yfiles.graph.styles.VoidShapeGeometry;
import com.yworks.yfiles.graph.styles.VoidStripeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.graphml.SerializationProperties;
import com.yworks.yfiles.view.GraphComponent;

/**
 * Configures {@link GraphMLIOHandler} instances for reading yFiles for Java (Swing)
 * 3.0.x GraphML files.
 * <p>
 * The basic approach for programmatically converting yFiles for Java (Swing) 3.0.x
 * GraphML files into yFiles for Java (Swing) 3.1.x GraphML files is as follows:
 * </p>
 * <pre>
 * public static void convert(File src, File tgt) throws IOException {
 *   DefaultGraph graph = new DefaultGraph();
 *
 *   GraphMLIOHandler reader = new GraphMLIOHandler();
 *   CompatibilitySupport.configureIOHandler(reader);
 *   reader.read(graph, src.getAbsolutePath());
 *
 *   GraphMLIOHandler writer = new GraphMLIOHandler();
 *   writer.write(graph, tgt.getAbsolutePath());
 * }
 * </pre>
 * <p>
 * This approach assumes a source document representing a flat graph (i.e. a
 * graph without group or folder nodes).  
 * </p>
 */
public class CompatibilitySupport {
  private static final String COMMON_2_NS =
          "http://www.yworks.com/xml/yfiles-common/2.0";
  private static final String MARKUP_2_NS =
          "http://www.yworks.com/xml/yfiles-common/markup/2.0";
  private static final String YFILES_JAVA_3_NS =
          "http://www.yworks.com/xml/yfiles-for-java/3.0";

  private CompatibilitySupport() {
  }

  /**
   * Configures the given {@link GraphMLIOHandler} instance for reading
   * yFiles for Java (Swing) 3.0.x GraphML files.
   * <p>
   * <b>Note:</b> Do not use {@link GraphMLIOHandler} instances configured with
   * this method for writing GraphML files.  
   * </p>
   * @param reader the {@link GraphMLIOHandler} instance that will be
   * configured for reading yFiles for Java (Swing) 3.0.x GraphML files.
   */
  public static void configureIOHandler( GraphMLIOHandler reader ) {
    // enable CSharp style enumeration value name parsing, e.g.
    // CharacterEllipsis instead of CHARACTER_ELLIPSIS
    reader.getDeserializationPropertyOverrides().set(
            SerializationProperties.IGNORE_PROPERTY_CASE,
            Boolean.TRUE);
    // optionally:
    // prevent unknown GraphML elements and attributes from aborting the
    // the parse process
//    reader.getDeserializationPropertyOverrides().set(
//            SerializationProperties.IGNORE_XAML_DESERIALIZATION_ERRORS,
//            Boolean.TRUE);

    Class c = GraphMLIOHandler.class;

    // xmlns:x="http://www.yworks.com/xml/yfiles-common/markup/2.0"
    addPackage(reader, MARKUP_2_NS, "com.yworks.yfiles.markup.primitives", c);

    // xmlns:y="http://www.yworks.com/xml/yfiles-common/2.0"
    addPackage(reader, COMMON_2_NS, "com.yworks.yfiles.graph", FoldingManager.class);
    addPackage(reader, COMMON_2_NS, "com.yworks.yfiles.graphml", c);
    addPackage(reader, COMMON_2_NS, "com.yworks.yfiles.markup.common", c);
    addType(reader, COMMON_2_NS, "EdgeViewState", EdgeViewStateExtension.class);
    addType(reader, COMMON_2_NS, "Label", LabelExtension.class);
    addType(reader, COMMON_2_NS, "LabelDefaults", LabelDefaultsExtension.class);
    // the custom label extension needs to be registered for "Label" and
    // "LabelExtension" both to prevent the GraphML framework from using
    // the internal class com.yworks.yfiles.markup.common.LabelExtension
    // instead of the custom implementation
    addType(reader, COMMON_2_NS, "LabelExtension", LabelExtension.class);
    addType(reader, COMMON_2_NS, "NodeViewState", NodeViewStateExtension.class);
    addType(reader, COMMON_2_NS, "Port", PortExtension.class);
    addType(reader, COMMON_2_NS, "PortDefaults", PortDefaultsExtension.class);
    // the custom port extension needs to be registered for "Port" and
    // "PortExtension" both to prevent the GraphML framework from using
    // the internal class com.yworks.yfiles.markup.common.PortExtension
    // instead of the custom implementation
    addType(reader, COMMON_2_NS, "PortExtension", PortExtension.class);
    addType(reader, COMMON_2_NS, "VoidNodeStyle", VoidNodeStyle.class);
    addType(reader, COMMON_2_NS, "VoidLabelStyle", VoidLabelStyle.class);
    addType(reader, COMMON_2_NS, "VoidEdgeStyle", VoidEdgeStyle.class);
    addType(reader, COMMON_2_NS, "VoidPortStyle", VoidPortStyle.class);
    addType(reader, COMMON_2_NS, "VoidStripeStyle", VoidStripeStyle.class);
    addType(reader, COMMON_2_NS, "VoidPathGeometry", VoidPathGeometry.class);
    addType(reader, COMMON_2_NS, "VoidShapeGeometry", VoidShapeGeometry.class);

    addType(reader, YFILES_JAVA_3_NS, "ArcEdgeStyle", ArcEdgeStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "CollapsibleNodeStyleDecorator", CollapsibleNodeStyleDecoratorExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "GeneralPathMarkup", com.yworks.yfiles.markup.common.GeneralPathExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "GeneralPathNodeStyle", GeneralPathNodeStyleExtension.class);
    // GenericModel has been renamed to GenericLabelModel
    addType(reader, YFILES_JAVA_3_NS, "GenericModel", com.yworks.yfiles.markup.common.GenericLabelModelExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "IconLabelStyle", IconLabelStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "ImageNodeStyle", ImageNodeStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "MemoryIconLabelStyle", MemoryIconLabelStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "NodeStyleLabelStyleAdapter", NodeStyleLabelStyleAdapterExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "Pen", PenExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "PolylineEdgeStyle", PolylineEdgeStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "ShapeNodeStyle", ShapeNodeStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "SimpleLabelStyle", SimpleLabelStyleExtension.class);
    addType(reader, YFILES_JAVA_3_NS, "ShinyPlateNodeStyle", ShinyPlateNodeStyleExtension.class);

    // xmlns:yfj="http://www.yworks.com/xml/yfiles-for-java/3.0"
    addPackage(reader, YFILES_JAVA_3_NS, "com.yworks.yfiles.graph.styles", Arrow.class);
    addPackage(reader, YFILES_JAVA_3_NS, "com.yworks.yfiles.markup.common", c);
    addPackage(reader, YFILES_JAVA_3_NS, "com.yworks.yfiles.markup.platform.java", c);
    addPackage(reader, YFILES_JAVA_3_NS, "com.yworks.yfiles.view", GraphComponent.class);

    // label models used to be in the platfrom specific namespace but
    // have been "moved" to the yFiles common namespace
    addPackage(reader, YFILES_JAVA_3_NS, "com.yworks.yfiles.graph.labelmodels", FreeNodeLabelModel.class);
    // port location models used to be in the platfrom specific namespace but
    // have been "moved" to the yFiles common namespace
    addPackage(reader, YFILES_JAVA_3_NS, "com.yworks.yfiles.graph.portlocationmodels", FreeNodePortLocationModel.class);
  }

  /**
   * Registers a Java type for parsing the given XML tag in the given namespace.
   */
  private static void addType(
          GraphMLIOHandler reader, String namespace, String tagName, Class type
  ) {
    reader.addXamlNamespaceMapping(namespace, tagName, type);
  }

  /**
   * Registers a Java package for parsing XML tags from the given namespace.
   * @param c a sample {@link Class} instance whose class loader is able to
   * instantiate types for the given package.
   */
  private static void addPackage(
          GraphMLIOHandler reader, String namespace, String packageName, Class c
  ) {
    reader.addXamlNamespaceMapping(namespace, packageName, c.getClassLoader());
  }
}
