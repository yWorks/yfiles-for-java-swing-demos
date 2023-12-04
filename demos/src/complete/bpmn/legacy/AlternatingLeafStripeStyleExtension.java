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
import com.yworks.yfiles.graphml.MarkupExtension;
import complete.bpmn.view.AlternatingLeafStripeStyle;
import complete.bpmn.view.StripeDescriptor;

public class AlternatingLeafStripeStyleExtension extends MarkupExtension {
  private StripeDescriptor evenLeafDescriptor;

  public final StripeDescriptor getEvenLeafDescriptor() {
    return this.evenLeafDescriptor;
  }

  public final void setEvenLeafDescriptor( StripeDescriptor value ) {
    this.evenLeafDescriptor = value;
  }

  private StripeDescriptor parentDescriptor;

  public final StripeDescriptor getParentDescriptor() {
    return this.parentDescriptor;
  }

  public final void setParentDescriptor( StripeDescriptor value ) {
    this.parentDescriptor = value;
  }

  private StripeDescriptor oddLeafDescriptor;

  public final StripeDescriptor getOddLeafDescriptor() {
    return this.oddLeafDescriptor;
  }

  public final void setOddLeafDescriptor( StripeDescriptor value ) {
    this.oddLeafDescriptor = value;
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    AlternatingLeafStripeStyle alternatingLeafStripeStyle = new AlternatingLeafStripeStyle();
    alternatingLeafStripeStyle.setEvenLeafDescriptor(getEvenLeafDescriptor());
    alternatingLeafStripeStyle.setParentDescriptor(getParentDescriptor());
    alternatingLeafStripeStyle.setOddLeafDescriptor(getOddLeafDescriptor());
    return alternatingLeafStripeStyle;
  }

}
