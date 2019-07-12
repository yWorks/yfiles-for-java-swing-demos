/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.bpmn.layout;


/**
 * Specifies the scope of the {@link BpmnLayout}.
 * @see BpmnLayout#getScope()
 */
public enum Scope {
  /**
   * Consider all elements during the layout.
   * @see complete.bpmn.layout.BpmnLayout#getScope()
   */
  ALL_ELEMENTS(0),

  /**
   * Consider only selected elements.
   * <p>
   * The selection state of an edge is determined by a boolean value returned by the data provider associated with the data
   * provider key {@link com.yworks.yfiles.layout.LayoutKeys#AFFECTED_EDGES_DPKEY}.
   * <br />
   * The selection state of a node is determined by a boolean value returned by the data provider associated with the data
   * provider key {@link com.yworks.yfiles.layout.LayoutKeys#AFFECTED_NODES_DPKEY}.
   * </p>
   * <p>
   * Note, that non-selected elements may also be moved to produce valid drawings. However the layout algorithm uses the
   * initial position of such elements as sketch.
   * </p>
   * @see complete.bpmn.layout.BpmnLayout#getScope()
   */
  SELECTED_ELEMENTS(1);

  private final int value;

  private Scope( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final Scope fromOrdinal( int ordinal ) {
    for (Scope current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
