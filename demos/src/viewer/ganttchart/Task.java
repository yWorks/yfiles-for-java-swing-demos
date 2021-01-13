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
package viewer.ganttchart;

import java.awt.Color;

/**
 * Stores all information about a task in the project schedule. 
 */
public class Task {
  /**
   * The id of this task.
   */
  private int id;
  /**
   * The name of this task.
   */
  private String name;
  /**
   * The color used to render this task.
   */
  private Color color;

  /**
   * The number of subrows this task has. It equals the number of the
   * maximum of activities which overlap.
   */
  private int subrowCount;

  /**
   * Initializes a new {@code Task} instance.
   */
  public Task( int id, String name, Color color ) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.subrowCount = 1;
  }

  /**
   * Returns the id of this task.
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the name of this task.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the color used to render to this task.
   */
  public Color getColor() {
    return color;
  }


  /**
   * Returns the number of subrows this task has.
   */
  public int getSubrowCount() {
    return subrowCount;
  }

  /**
   * Sets the number of subrows this task has.
   */
  public void setSubrowCount( int subrowCount ) {
    this.subrowCount = subrowCount;
  }
}
