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
package layout.edgebundling;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.GraphModelManager;
import com.yworks.yfiles.view.HierarchicNestingPolicy;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.ICanvasObjectInstaller;
import com.yworks.yfiles.view.ModelManager;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;

import java.awt.Color;

/**
 * Installs a visual representation of a highlight decoration for edges and nodes such
 * that an edge/node highlight is drawn below the node group.
 */
class DemoHighlightManager extends HighlightIndicatorManager<IModelItem> {
  private final ICanvasObjectGroup edgeHighlightGroup;

  /**
   * Initializes a new highlight manager for the given graph component.
   */
  DemoHighlightManager( GraphComponent graphComponent ) {
    super(graphComponent);
    GraphModelManager modelManager = graphComponent.getGraphModelManager();
    modelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.NONE);
    edgeHighlightGroup = modelManager.getContentGroup().addGroup();
    edgeHighlightGroup.below(modelManager.getNodeGroup());
  }

  /**
   * Retrieves the Canvas Object group to use for the given item.
   */
  @Override
  protected ICanvasObjectGroup getCanvasObjectGroup( IModelItem item ) {
    if (item instanceof IEdge) {
      return edgeHighlightGroup;
    }
    return super.getCanvasObjectGroup(item);
  }

  /**
   * Retrieves the installer for the given item.
   * Called from {@link ModelManager#install(Object)}.
   */
  @Override
  protected ICanvasObjectInstaller getInstaller( IModelItem item ) {
    if (item instanceof IEdge) {
      EdgeStyleDecorationInstaller installer = new EdgeStyleDecorationInstaller();
      installer.setEdgeStyle(new DemoEdgeStyle(6, Color.RED, Colors.GOLD));
      installer.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
      return installer;
    } else if (item instanceof INode) {
      NodeStyleDecorationInstaller installer = new NodeStyleDecorationInstaller();
      installer.setNodeStyle(new DemoNodeStyle(Color.RED));
      installer.setMargins(InsetsD.EMPTY);
      installer.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);
      return installer;
    }
    return super.getInstaller(item);
  }
}
