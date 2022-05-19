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
package complete.bpmn.legacy;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.view.Colors;
import complete.bpmn.view.ChoreographyNodeStyle;
import complete.bpmn.view.Participant;
import java.util.List;

public class ChoreographyNodeStyleExtension extends MarkupExtension {
  private ChoreographyType type = ChoreographyType.TASK;

  public final ChoreographyType getType() {
    return this.type;
  }

  public final void setType( ChoreographyType value ) {
    this.type = value;
  }

  private LoopCharacteristic loopCharacteristic = LoopCharacteristic.NONE;

  public final LoopCharacteristic getLoopCharacteristic() {
    return this.loopCharacteristic;
  }

  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    this.loopCharacteristic = value;
  }

  private SubState subState = SubState.NONE;

  public final SubState getSubState() {
    return this.subState;
  }

  public final void setSubState( SubState value ) {
    this.subState = value;
  }

  private boolean initiatingMessage;

  public final boolean isInitiatingMessage() {
    return this.initiatingMessage;
  }

  public final void setInitiatingMessage( boolean value ) {
    this.initiatingMessage = value;
  }

  private boolean responseMessage;

  public final boolean isResponseMessage() {
    return this.responseMessage;
  }

  public final void setResponseMessage( boolean value ) {
    this.responseMessage = value;
  }

  private boolean initiatingAtTop;

  public final boolean isInitiatingAtTop() {
    return this.initiatingAtTop;
  }

  public final void setInitiatingAtTop( boolean value ) {
    this.initiatingAtTop = value;
  }

  private SizeD minimumSize = new SizeD();

  public final SizeD getMinimumSize() {
    return this.minimumSize;
  }

  public final void setMinimumSize( SizeD value ) {
    this.minimumSize = value;
  }

  private final ChoreographyNodeStyle.ParticipantList topParticipants = new ChoreographyNodeStyle.ParticipantList();

  public final List<Participant> getTopParticipants() {
    return topParticipants;
  }

  private final ChoreographyNodeStyle.ParticipantList bottomParticipants = new ChoreographyNodeStyle.ParticipantList();

  /**
   * Gets the list of {@link Participant}s at the bottom of the node, ordered from bottom to top.
   * @return The BottomParticipants.
   */
  public final List<Participant> getBottomParticipants() {
    return bottomParticipants;
  }

  private InsetsD insets = new InsetsD();

  public final InsetsD getInsets() {
    return this.insets;
  }

  public final void setInsets( InsetsD value ) {
    this.insets = value;
  }

  public ChoreographyNodeStyleExtension() {
    setType(ChoreographyType.TASK);
    setLoopCharacteristic(LoopCharacteristic.NONE);
    setSubState(SubState.NONE);
    setInitiatingMessage(false);
    setResponseMessage(false);
    setInitiatingAtTop(true);
    setInsets(new InsetsD(5));
    setMinimumSize(SizeD.EMPTY);
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    ChoreographyNodeStyle style = new ChoreographyNodeStyle();
    style.setType(complete.bpmn.view.ChoreographyType.fromOrdinal(getType().value()));
    style.setLoopCharacteristic(complete.bpmn.view.LoopCharacteristic.fromOrdinal(getLoopCharacteristic().value()));
    style.setSubState(complete.bpmn.view.SubState.fromOrdinal(getSubState().value()));
    style.setInitiatingMessage(isInitiatingMessage());
    style.setResponseMessage(isResponseMessage());
    style.setInitiatingAtTop(isInitiatingAtTop());
    style.setInsets(getInsets());
    style.setMinimumSize(getMinimumSize());
    style.setInitiatingColor(Colors.LIGHT_GRAY);
    for (Participant p : getTopParticipants()) {
      style.getTopParticipants().add(p);
    }
    for (Participant p : getBottomParticipants()) {
      style.getBottomParticipants().add(p);
    }
    return style;
  }

}
