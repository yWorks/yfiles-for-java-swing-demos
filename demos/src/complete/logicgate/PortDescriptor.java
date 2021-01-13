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
package complete.logicgate;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.labelmodels.FreePortLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;

/**
 * Helper class that describes properties that are necessary to create port candidates in this demo.
 */
public class PortDescriptor {

  private double x;

  /**
   * Gets relative x coordinate of the port.
   */
  public double getX() {
    return x;
  }

  /**
   * Sets relative x coordinate of the port.
   */
  public void setX(double x) {
    this.x = x;
  }

  private double y;

  /**
   * Gets relative y coordinate of the port.
   */
  public double getY() {
    return y;
  }

  /**
   *  Sets relative y coordinate of the port.
   */
  public void setY(double y) {
    this.y = y;
  }

  private String labelText;

  /**
   * Gets the text of the label.
   */
  public String getLabelText() {
    return labelText;
  }

  /**
   * Sets the text of the label.
   */
  public void setLabelText(String labelText) {
    this.labelText = labelText;
  }

  private ILabelModelParameter labelPlacementWithEdge;

  /**
   * Gets the placement of the label.
   */
  public ILabelModelParameter getLabelPlacementWithEdge() {
    return labelPlacementWithEdge;
  }

  /**
   * Sets the placement of the label.
   */
  public void setLabelPlacementWithEdge(ILabelModelParameter labelPlacementWithEdge) {
    this.labelPlacementWithEdge = labelPlacementWithEdge;
  }

  private EdgeDirection edgeDirection;

  /**
   * Gets the direction of a port (to allow incoming and outgoing edges).
   */
  public EdgeDirection getEdgeDirection() {
    return edgeDirection;
  }

  /**
   * Sets the direction of a port (to allow incoming and outgoing edges).
   */
  public void setEdgeDirection(EdgeDirection edgeDirection) {
    this.edgeDirection = edgeDirection;
  }

  /**
   * Initializes a new instance of {@link PortDescriptor}.
   * @param x                      The relative x coordinate of the port.
   * @param y                      The relative y coordinate of the port.
   * @param edgeDirection          The direction of a port (to allow incoming and outgoing edges).
   * @param labelText              The text of the label.
   * @param labelPlacementWithEdge The placement of the label.
   */
  public PortDescriptor(double x, double y, EdgeDirection edgeDirection, String labelText,
                        ILabelModelParameter labelPlacementWithEdge) {
    this.x = x;
    this.y = y;
    this.labelText = labelText;
    this.labelPlacementWithEdge = labelPlacementWithEdge;
    this.edgeDirection = edgeDirection;
  }

  /**
   * Default constructor for serialization.
   */
  public PortDescriptor() {
  }

  /**
   * Creates an array of {@link PortDescriptor}s belonging to the type of the node as specified by its
   * {@link LogicGateType} set as its tag or an empty list if there is no {@link LogicGateType}.
   */
  public static PortDescriptor[] createPortDescriptors(LogicGateType logicGateType) {
    ILabelModelParameter inside = new InsideOutsidePortLabelModel().createInsideParameter();
    ILabelModelParameter aboveEdgeLeft =
        FreePortLabelModel.INSTANCE.createParameter(new PointD(-5, -3), new PointD(1, 1), PointD.ORIGIN, 0);
    ILabelModelParameter aboveEdgeRight =
        FreePortLabelModel.INSTANCE.createParameter(new PointD(5, -3), new PointD(0, 1), PointD.ORIGIN, 0);
    ILabelModelParameter belowEdgeLeft =
        FreePortLabelModel.INSTANCE.createParameter(new PointD(-5, 3), new PointD(1, 0), PointD.ORIGIN, 0);

    switch (logicGateType) {
      default:
      case AND:
      case NAND:
      case OR:
      case NOR:
        return new PortDescriptor[]{
            new PortDescriptor(0, 5, EdgeDirection.IN, "in1", aboveEdgeLeft),
            new PortDescriptor(0, 25, EdgeDirection.IN, "in2", belowEdgeLeft),
            new PortDescriptor(50, 15, EdgeDirection.OUT, "out", aboveEdgeRight)
        };
      case NOT:
        return new PortDescriptor[]{
            new PortDescriptor(0, 15, EdgeDirection.IN, "in", aboveEdgeLeft),
            new PortDescriptor(50, 15, EdgeDirection.OUT, "out", aboveEdgeRight)
        };
      case TIMER:
        return new PortDescriptor[]{
            new PortDescriptor(0, 20, EdgeDirection.IN, "gnd", inside),
            new PortDescriptor(0, 40, EdgeDirection.IN, "trig", inside),
            new PortDescriptor(0, 80, EdgeDirection.OUT, "out", inside),
            new PortDescriptor(0, 100, EdgeDirection.IN, "rst", inside),
            new PortDescriptor(70, 20, EdgeDirection.IN, "Vcc", inside),
            new PortDescriptor(70, 40, EdgeDirection.OUT, "dis", inside),
            new PortDescriptor(70, 80, EdgeDirection.IN, "thr", inside),
            new PortDescriptor(70, 100, EdgeDirection.IN, "ctrl", inside)
        };
      case AD_CONVERTER:
        return new PortDescriptor[]{
            new PortDescriptor(0, 20, EdgeDirection.IN, "Vin", inside),
            new PortDescriptor(0, 40, EdgeDirection.IN, "gnd", inside),
            new PortDescriptor(0, 80, EdgeDirection.IN, "Vref", inside),
            new PortDescriptor(0, 100, EdgeDirection.IN, "clk", inside),
            new PortDescriptor(70, 20, EdgeDirection.OUT, "d1", inside),
            new PortDescriptor(70, 40, EdgeDirection.OUT, "d2", inside),
            new PortDescriptor(70, 100, EdgeDirection.OUT, "sign", inside)
        };
    }
  }

  /**
   * Describes the direction of a port (to allow incoming and outgoing edges).
   */
  public enum EdgeDirection
  {
    IN,
    OUT
  }
}
