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

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;
import complete.bpmn.view.EventNodeStyle;

public class EventNodeStyleExtension extends MarkupExtension {
  private EventType type = EventType.PLAIN;

  public final EventType getType() {
    return this.type;
  }

  public final void setType( EventType value ) {
    this.type = value;
  }

  private EventCharacteristic characteristic = EventCharacteristic.START;

  public final EventCharacteristic getCharacteristic() {
    return this.characteristic;
  }

  public final void setCharacteristic( EventCharacteristic value ) {
    this.characteristic = value;
  }

  private SizeD minimumSize = new SizeD();

  public final SizeD getMinimumSize() {
    return this.minimumSize;
  }

  public final void setMinimumSize( SizeD value ) {
    this.minimumSize = value;
  }

  public EventNodeStyleExtension() {
    setType(EventType.PLAIN);
    setCharacteristic(EventCharacteristic.START);
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    EventNodeStyle eventNodeStyle = new EventNodeStyle();
    eventNodeStyle.setType(complete.bpmn.view.EventType.fromOrdinal(getType().value()));
    eventNodeStyle.setCharacteristic(complete.bpmn.view.EventCharacteristic.fromOrdinal(getCharacteristic().value()));
    eventNodeStyle.setMinimumSize(getMinimumSize());
    return eventNodeStyle;
  }

}
