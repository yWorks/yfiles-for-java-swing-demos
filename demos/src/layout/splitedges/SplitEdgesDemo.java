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
package layout.splitedges;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.RecursiveGroupLayout;
import com.yworks.yfiles.layout.RecursiveGroupLayoutData;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;

/**
 * Shows how to align edges at group nodes using {@link RecursiveGroupLayout}
 * together with {@link HierarchicLayout}.
 */
public class SplitEdgesDemo extends AbstractDemo {
  /** Holds split IDs for the edges' source end. */
  private IMapper<IEdge, Object> sourceSplitIds;
  /** Holds split IDs for the edges' target end. */
  private IMapper<IEdge, Object> targetSplitIds;


  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png",
            ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png",
            ICommand.UNGROUP_SELECTION, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createRunLayoutAction());
  }

  /**
   * Creates an action to arrange the displayed graph.
   */
  private Action createRunLayoutAction() {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        runLayout();
      }
    };
    action.putValue(Action.NAME, "Run Layout");
    action.putValue(Action.SHORT_DESCRIPTION, "Arranges the displayed graph.");
    return action;
  }

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // create mappers for storing split IDs for edges
    createMappers();

    // configure user interaction
    initializeInputMode();

    // load a sample GraphML file with suitable split IDs
    loadGraph();

    // calculate an initial arrangement for the sample graph
    runLayout();
  }

  /**
   * Configures user interaction.
   * Enables grouping actions and restricts selection to nodes and edges.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    geim.setGroupingOperationsAllowed(true);

    graphComponent.setInputMode(geim);
  }

  /**
   * Arranges the displayed graph in a recursive fashion.
   * I.e. {@link RecursiveGroupLayout} is used to arrange each group separately
   * with {@link HierarchicLayout}.
   */
  private void runLayout() {
    HierarchicLayout hierarchicLayout = new HierarchicLayout();
    hierarchicLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    // step 1: enable  DirectGroupContentEdgeRouting
    // this is necessary to ensure suitable routes for those split edges inside
    // the corresponding group node
    hierarchicLayout.getEdgeLayoutDescriptor().setDirectGroupContentEdgeRoutingEnabled(true);
    ((SimplexNodePlacer) hierarchicLayout.getNodePlacer()).setBarycenterModeEnabled(true);

    EdgeRouter router = new EdgeRouter();
    router.setScope(Scope.ROUTE_AFFECTED_EDGES);
    router.getDefaultEdgeLayoutDescriptor().setDirectGroupContentEdgeRoutingEnabled(true);

    RecursiveGroupLayout recursiveGroupLayout = new RecursiveGroupLayout();
    recursiveGroupLayout.setCoreLayout(hierarchicLayout);
    recursiveGroupLayout.setInterEdgeRouter(router);
    recursiveGroupLayout.setInterEdgesDpKey(router.getAffectedEdgesDpKey());

    RecursiveGroupLayoutData recursiveGroupLayoutData = new RecursiveGroupLayoutData();
    // step 2: register split IDs for edges
    // this is necessary to ensure that the two edges that make up a
    // "split inter-edge" share the same port location on the corresponding
    // group's border
    recursiveGroupLayoutData.setSourceSplitIds(sourceSplitIds);
    recursiveGroupLayoutData.setTargetSplitIds(targetSplitIds);
    HierarchicLayoutData hierarchicLayoutData = new HierarchicLayoutData();
    hierarchicLayoutData.setEdgeThickness(3);

    CompositeLayoutData compositeLayoutData = new CompositeLayoutData();
    compositeLayoutData.getItems().add(recursiveGroupLayoutData);
    compositeLayoutData.getItems().add(hierarchicLayoutData);

    graphComponent.morphLayout(recursiveGroupLayout, Duration.ofMillis(700), compositeLayoutData);
  }

  /**
   * Creates the mappers used for storing split IDs for edges.
   * @see RecursiveGroupLayoutData#getSourceSplitIds()
   * @see RecursiveGroupLayoutData#getTargetSplitIds()
   */
  private void createMappers() {
    targetSplitIds = new Mapper<>();
    sourceSplitIds = new Mapper<>();
  }

  /**
   * Loads a sample graph. Populates {@link #sourceSplitIds source} and
   * {@link #targetSplitIds target} split IDs from data in the GraphML file.
   */
  private void loadGraph() {
    GraphMLIOHandler graphMLIOHandler = graphComponent.getGraphMLIOHandler();
    graphMLIOHandler.addInputMapper(IEdge.class, Object.class, "sourceSplitIds" , sourceSplitIds);
    graphMLIOHandler.addInputMapper(IEdge.class, Object.class, "targetSplitIds" , targetSplitIds);

    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new SplitEdgesDemo().start();
    });
  }
}
