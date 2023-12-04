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
package complete.isometric.model;

import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class IsometricData {
  /**
   * A data set for normal nodes.
   */
  public static List<NodeData> NodesData = Arrays.asList(
          new NodeData("tablet1",
                  new Color(255, 153, 0),
                  new Geometry(23, 5, 30),
                  "Tablet",
                  "development"),
          new NodeData(
                  "tablet2",
                  new Color(255, 153, 0),
                  new Geometry(23, 5, 30),
                  "Tablet",
                  "sales"
          ),
          new NodeData(
                  "tablet3",
                  new Color(255, 153, 0),
                  new Geometry(23, 5, 30),
                  "Tablet",
                  "sales"
          ),
          new NodeData(
                  "tablet4",
                  new Color(255, 153, 0),
                  new Geometry(23, 5, 30),
                  "Tablet",
                  "it"
          ),
          new NodeData(
                  "server1",
                  new Color(153, 51, 255),
                  new Geometry(29, 30, 47),
                  "Server",
                  "development"
          ),
          new NodeData(
                  "server2",
                  new Color(153, 51, 255),
                  new Geometry(29, 30, 47),
                  "Server",
                  "it"
          ),
          new NodeData(
                  "pc1",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "development"
          ),
          new NodeData(
                  "pc2",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "development"
          ),
          new NodeData(
                  "pc3",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "development"
          ),
          new NodeData(
                  "pc4",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "development"
          ),
          new NodeData(
                  "pc5",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "management"
          ),
          new NodeData(
                  "pc6",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "management"
          ),
          new NodeData(
                  "pc7",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "management"
          ),
          new NodeData(
                  "pc8",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "production"
          ),
          new NodeData(
                  "pc9",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "production"
          ),
          new NodeData(
                  "pc10",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "it"
          ),
          new NodeData(
                  "pc11",
                  new Color(153, 204, 0),
                  new Geometry(15, 30, 47),
                  "PC",
                  "it"
          ),
          new NodeData(
                  "laptop1",
                  new Color(0, 204, 255),
                  new Geometry(43, 10, 24),
                  "Laptop",
                  "development"
          ),
          new NodeData(
                  "laptop2",
                  new Color(0, 204, 255),
                  new Geometry(43, 10, 24),
                  "Laptop",
                  "development"
          ),
          new NodeData(
                  "laptop3",
                  new Color(0, 204, 255),
                  new Geometry(43, 10, 24),
                  "Laptop",
                  "sales"
          ),
          new NodeData(
                  "laptop4",
                  new Color(0, 204, 255),
                  new Geometry(43, 10, 24),
                  "Laptop",
                  "sales"
          ),
          new NodeData(
                  "laptop5",
                  new Color(0, 204, 255),
                  new Geometry(43, 10, 24),
                  "Laptop",
                  "it"
          ),
          new NodeData(
                  "db",
                  new Color(153, 51, 255),
                  new Geometry(20, 30, 20),
                  "DB",
                  "it"
          ),
          new NodeData(
                  "hub1",
                  new Color(192, 192, 192),
                  new Geometry(38, 7, 24),
                  "Hub",
                  "development"
          ),
          new NodeData(
                  "hub2",
                  new Color(192, 192, 192),
                  new Geometry(38, 7, 24),
                  "Hub",
                  "management"
          ),
          new NodeData(
                  "hub3",
                  new Color(192, 192, 192),
                  new Geometry(38, 7, 24),
                  "Hub",
                  "production"
          ),
          new NodeData(
                  "hub4",
                  new Color(192, 192, 192),
                  new Geometry(38, 7, 24),
                  "Hub",
                  "sales"
          ),
          new NodeData(
                  "hub5",
                  new Color(192, 192, 192),
                  new Geometry(38, 7, 24),
                  "Hub",
                  "it"
          ),
          new NodeData(
                  "switch",
                  new Color(255, 102, 0),
                  new Geometry(63, 15, 30),
                  "Switch",
                  ""
          ),
          new NodeData(
                  "gateway",
                  new Color(153, 51, 255),
                  new Geometry(29, 30, 47),
                  "Gateway",
                  ""
          ),
          new NodeData(
                  "firewall",
                  new Color(255, 0, 0),
                  new Geometry(57, 30, 10),
                  "Firewall",
                  ""
          ));

  /**
   * A data set for edges.
   */
  public static List<EdgeData> EdgesData = Arrays.asList(
          new EdgeData("server1", "hub1"),
          new EdgeData("pc1", "hub1"),
          new EdgeData("pc2", "hub1"),
          new EdgeData("hub1", "pc3"),
          new EdgeData("hub1", "pc4"),
          new EdgeData("laptop1", "hub1"),
          new EdgeData("laptop2", "hub1"),
          new EdgeData("tablet1", "hub1"),
          new EdgeData("hub1", "switch", "10 GBytes/s"),
          new EdgeData("pc5", "hub2"),
          new EdgeData("pc6", "hub2"),
          new EdgeData("pc7", "hub2"),
          new EdgeData("hub2", "switch", "1 GByte/s"),
          new EdgeData("pc8", "hub3"),
          new EdgeData("pc9", "hub3"),
          new EdgeData("hub3", "switch", "1 GByte/s"),
          new EdgeData("tablet2", "hub4"),
          new EdgeData("tablet3", "hub4"),
          new EdgeData("laptop3", "hub4"),
          new EdgeData("laptop4", "hub4"),
          new EdgeData("hub4", "switch", "1 GByte/s"),
          new EdgeData("tablet4", "hub5"),
          new EdgeData("laptop5", "hub5"),
          new EdgeData("pc10", "hub5"),
          new EdgeData("hub5", "pc11"),
          new EdgeData("hub5", "switch", "1 GByte/s"),
          new EdgeData("server2", "switch", "10 GByte/s"),
          new EdgeData("db", "switch", "10 GByte/s"),
          new EdgeData("switch", "gateway", "100 MByte/s"),
          new EdgeData("gateway", "firewall")
  );

  /**
   * A data set for group nodes.
   */
  public static List<NodeData> GroupsData = Arrays.asList(
          new NodeData(
                  "development",
                  "Development",
                  new Geometry(10, 0, 10),
                  new Color(202, 236, 255, 128),
                  new Pen(new Color(153, 204, 255), 1)
          ),
          new NodeData(
                  "management",
                  "Management",
                  new Geometry(10, 0, 10),
                  new Color(202, 236, 255, 128),
                  new Pen(new Color(153, 204, 255), 1)
          ),
          new NodeData(
                  "production",
                  "Production",
                  new Geometry(10, 0, 10),
                  new Color(202, 236, 255, 128),
                  new Pen(new Color(153, 204, 255), 1)
          ),
          new NodeData(
                  "sales",
                  "Sales",
                  new Geometry(10, 0, 10),
                  new Color(202, 236, 255, 128),
                  new Pen(new Color(153, 204, 255), 1)
          ),
          new NodeData(
                  "it",
                  "IT",
                  new Geometry(10, 0, 10),
                  new Color(202, 236, 255, 128),
                  new Pen(new Color(153, 204, 255), 1)
          )
  );
}
