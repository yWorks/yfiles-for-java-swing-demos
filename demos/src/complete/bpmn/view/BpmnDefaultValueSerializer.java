/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.bpmn.view;

import com.yworks.yfiles.graphml.IValueSerializerContext;
import com.yworks.yfiles.graphml.ValueSerializer;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides default value handling of
 * {@link ChoreographyMessageLabelStyle}'s default text placement when reading or writing GraphML.
 * @see ChoreographyMessageLabelStyle#getTextPlacement()
 */
public class BpmnDefaultValueSerializer extends ValueSerializer {
  private static final Map<String, Object> DEFAULTS_MAP;

  @Override
  public boolean canConvertFromString( String value, IValueSerializerContext context ) {
    return DEFAULTS_MAP.containsKey(value);
  }

  @Override
  public Object convertFromString( String value, IValueSerializerContext context ) {
    return DEFAULTS_MAP.get(value);
  }

  static {
    DEFAULTS_MAP = new HashMap<>();
    DEFAULTS_MAP.put("ChoreographyMessageLabelStyle.DefaultTextPlacement", ChoreographyMessageLabelStyle.getDefaultTextPlacement());
  }

}
