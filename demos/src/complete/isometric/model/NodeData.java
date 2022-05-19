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
package complete.isometric.model;


import com.yworks.yfiles.view.Pen;

import java.awt.Color;

/**
 * Data object that shall be represented by a node.
 */
public class NodeData {

  public NodeData() {
  }

  public NodeData(String id, Color color, Geometry geometry, String label, String group) {
    this.id = id;
    this.color = color;
    this.geometry = geometry;
    this.label = label;
    this.group = group;
  }

  public NodeData(String id, String label, Geometry geometry, Color color, Pen pen) {
    this.id = id;
    this.color = color;
    this.pen = pen;
    this.geometry = geometry;
    this.label = label;
  }


  // The base color of the node.
  private Color color;

  /**
   * The ID used by {@link EdgeData} to define the source ({@link EdgeData#from})  and
   * target ({@link EdgeData#to}) node as well as to define the parent node ({@link #group}).
   */
  public String id;

  /**
   * The pen used for the outline of the faces.
   */
  public Pen pen;

  /**
   * The geometry of the node.
   */
  public Geometry geometry;

  /**
   * The text that should be used as label.
   */
  public String label;

  /**
   * The id of the parent group node.
   */
  public String group;

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen(Pen pen) {
    this.pen = pen;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }
}
