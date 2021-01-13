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
package complete.bpmn.legacy;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;
import complete.bpmn.view.DataObjectNodeStyle;

public class DataObjectNodeStyleExtension extends MarkupExtension {
  private boolean collection;

  public final boolean isCollection() {
    return this.collection;
  }

  public final void setCollection( boolean value ) {
    this.collection = value;
  }

  private DataObjectType type = DataObjectType.NONE;

  public final DataObjectType getType() {
    return this.type;
  }

  public final void setType( DataObjectType value ) {
    this.type = value;
  }

  private SizeD minimumSize = new SizeD();

  public final SizeD getMinimumSize() {
    return this.minimumSize;
  }

  public final void setMinimumSize( SizeD value ) {
    this.minimumSize = value;
  }

  public DataObjectNodeStyleExtension() {
    setCollection(false);
    setType(DataObjectType.NONE);
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    DataObjectNodeStyle dataObjectNodeStyle = new DataObjectNodeStyle();
    dataObjectNodeStyle.setCollection(isCollection());
    dataObjectNodeStyle.setType(complete.bpmn.view.DataObjectType.fromOrdinal(getType().value()));
    dataObjectNodeStyle.setMinimumSize(getMinimumSize());
    return dataObjectNodeStyle;
  }

}