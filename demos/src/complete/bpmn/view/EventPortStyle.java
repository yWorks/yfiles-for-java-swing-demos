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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.styles.DefaultEdgePathCropper;
import com.yworks.yfiles.graph.styles.IEdgePathCropper;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.IPortStyleRenderer;
import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An {@link IPortStyle} implementation representing an Event attached to an Activity boundary according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class EventPortStyle implements IPortStyle, Cloneable {
  private final IPortStyleRenderer renderer;


  /**
   * Gets the event type for this style.
   * @return The Type.
   * @see #setType(EventType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "COMPENSATION")
  public final EventType getType() {
    return getEventNodeStyle().getType();
  }

  /**
   * Sets the event type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "COMPENSATION")
  public final void setType( EventType value ) {
    getEventNodeStyle().setType(value);
  }

  /**
   * Gets the event characteristic for this style.
   * @return The Characteristic.
   * @see #setCharacteristic(EventCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "BOUNDARY_INTERRUPTING")
  public final EventCharacteristic getCharacteristic() {
    return getEventNodeStyle().getCharacteristic();
  }

  /**
   * Sets the event characteristic for this style.
   * @param value The Characteristic to set.
   * @see #getCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "BOUNDARY_INTERRUPTING")
  public final void setCharacteristic( EventCharacteristic value ) {
    getEventNodeStyle().setCharacteristic(value);
  }

  /**
   * Gets the size the port style is rendered with.
   * @return The RenderSize.
   * @see #setRenderSize(SizeD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "20,20", classValue = SizeD.class)
  public final SizeD getRenderSize() {
    return getAdapter().getRenderSize();
  }

  /**
   * Sets the size the port style is rendered with.
   * @param value The RenderSize to set.
   * @see #getRenderSize()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "20,20", classValue = SizeD.class)
  public final void setRenderSize( SizeD value ) {
    getAdapter().setRenderSize(value);
  }


  private NodeStylePortStyleAdapter adapter;

  final NodeStylePortStyleAdapter getAdapter() {
    return this.adapter;
  }

  final void setAdapter( NodeStylePortStyleAdapter value ) {
    this.adapter = value;
  }

  /**
   * Creates a new instance.
   */
  public EventPortStyle() {
    EventNodeStyle eventNodeStyle = new EventNodeStyle();
    eventNodeStyle.setCharacteristic(EventCharacteristic.BOUNDARY_INTERRUPTING);
    eventNodeStyle.setType(EventType.COMPENSATION);
    NodeStylePortStyleAdapter nodeStylePortStyleAdapter = new NodeStylePortStyleAdapter(eventNodeStyle);
    nodeStylePortStyleAdapter.setRenderSize(BpmnConstants.Sizes.EVENT_PORT);
    setAdapter(nodeStylePortStyleAdapter);
    renderer = EventPortStyleRenderer.INSTANCE;
  }

  final EventNodeStyle getEventNodeStyle() {
    INodeStyle nodeStyle = getAdapter().getNodeStyle();
    return (nodeStyle instanceof EventNodeStyle) ? (EventNodeStyle)nodeStyle : null;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final EventPortStyle clone() {
    try {
      return (EventPortStyle)super.clone();
    }catch (CloneNotSupportedException exception) {
      throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
    }
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IPortStyleRenderer getRenderer() {
    return renderer;
  }

  /**
   * Renderer used by {@link EventPortStyle}.
   */
  private static class EventPortStyleRenderer implements IPortStyleRenderer, ILookup {
    public static final EventPortStyleRenderer INSTANCE = new EventPortStyleRenderer();

    private ILookup fallbackLookup;

    public final IVisualCreator getVisualCreator( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getVisualCreator(item, adapter);
    }

    public final IBoundsProvider getBoundsProvider( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getBoundsProvider(item, adapter);
    }

    public final IVisibilityTestable getVisibilityTestable( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getVisibilityTestable(item, adapter);
    }

    public final IHitTestable getHitTestable( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getHitTestable(item, adapter);
    }

    public final IMarqueeTestable getMarqueeTestable( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getMarqueeTestable(item, adapter);
    }

    public final ILookup getContext( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      fallbackLookup = adapter.getRenderer().getContext(item, adapter);
      return this;
    }

    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      if (type == IEdgePathCropper.class) {
        return (TLookup)EventPortStyle.EventPortEdgePathCropper.CALCULATOR_INSTANCE;
      }
      return fallbackLookup.lookup(type);
    }

  }

  /**
   * IEdgePathCropper instance that crops the edge at the circular port bounds.
   */
  private static class EventPortEdgePathCropper extends DefaultEdgePathCropper {
    public static final EventPortEdgePathCropper CALCULATOR_INSTANCE = new EventPortEdgePathCropper();

    private EventPortEdgePathCropper() {
      setCroppingAtPortEnabled(true);
    }

    @Override
    protected IShapeGeometry getPortGeometry( IPort port ) {
      return port.getStyle().getRenderer().getContext(port, port.getStyle()).lookup(IShapeGeometry.class);
    }

  }

}
