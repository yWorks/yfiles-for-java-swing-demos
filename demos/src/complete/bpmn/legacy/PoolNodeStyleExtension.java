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
package complete.bpmn.legacy;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.TableNodeStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.MarkupExtension;
import complete.bpmn.view.PoolNodeStyle;

@GraphML(contentProperty = "TableNodeStyle")
public class PoolNodeStyleExtension extends MarkupExtension {
  private boolean multipleInstance;

  public final boolean isMultipleInstance() {
    return this.multipleInstance;
  }

  public final void setMultipleInstance( boolean value ) {
    this.multipleInstance = value;
  }

  private boolean vertical;

  private boolean isVertical() {
    return this.vertical;
  }

  private void setVertical( boolean value ) {
    this.vertical = value;
  }

  private TableNodeStyle tableNodeStyle;

  public final TableNodeStyle getTableNodeStyle() {
    return this.tableNodeStyle;
  }

  public final void setTableNodeStyle( TableNodeStyle value ) {
    this.tableNodeStyle = value;
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    PoolNodeStyle poolNodeStyle = new PoolNodeStyle(isVertical());
    poolNodeStyle.setMultipleInstance(isMultipleInstance());
    poolNodeStyle.setTableNodeStyle(getTableNodeStyle());
    return poolNodeStyle;
  }

}
