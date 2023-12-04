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
package mavendemo;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.GraphViewerInputMode;

import javax.swing.JFrame;

/**
 * A simple demo using yFiles for Java (Swing) built with Maven.
 */
public class MavenDemo {

  private void launch(){
    JFrame frame = new JFrame("yFiles for Java (Swing) Maven Demo");

    // Instantiate a GraphComponent, set the InputMode and add some graph elements.
    GraphComponent graphComponent = new GraphComponent();
    graphComponent.setInputMode(new GraphViewerInputMode());
    createGraphElements(graphComponent.getGraph());

    // Add the graph control to the frame and show it.
    frame.getContentPane().add(graphComponent);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    // Fit the graph contents _after_ setting the frame to visible.
    graphComponent.fitGraphBounds();
  }

  private void createGraphElements(IGraph graph) {
    INode n1 = graph.createNode(new PointD(100, 100));
    INode n2 = graph.createNode(new PointD(200, 100));
    INode n3 = graph.createNode(new PointD(150, 200));

    graph.createEdge(n1, n2);
    graph.createEdge(n2, n3);
    graph.createEdge(n3, n1);
  }

  public static void main(String... args){
    new MavenDemo().launch();
  }
}
