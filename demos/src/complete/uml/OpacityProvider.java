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
package complete.uml;

import com.yworks.yfiles.graph.INode;

import java.util.WeakHashMap;

/**
 * Stores transient opacity values for fading in and fading out selected
 * labels and add/remove feature buttons of UML nodes.
 */
class OpacityProvider {
  static final OpacityProvider INSTANCE = new OpacityProvider();


  private final WeakHashMap<INode, MutableFloat> opacity = new WeakHashMap<>();

  private OpacityProvider() {
  }

  /**
   * Returns the current opacity for selected features and add/remove feature
   * buttons of the given UML node.
   */
  float getOpacity( INode node ) {
    MutableFloat value = opacity.get(node);
    return value == null ? 0f : value.value;
  }

  /**
   * Sets the current opacity for selected features and add/remove feature
   * buttons of the given UML node.
   */
  void setOpacity( INode node, float opacity ) {
    MutableFloat value = this.opacity.get(node);
    if (value == null) {
      value = new MutableFloat();
      this.opacity.put(node, value);
    }
    value.value = opacity;
  }


  /**
   * Stores a primitive float value. Since an UML node's opacitiy is changed
   * during animations, a lot of different values are set for a given node
   * in a short amount of time. This mutable wrapper class exists to prevent
   * the need for creating lots of new object instances during an animation.
   */
  private static final class MutableFloat {
    float value;
  }
}
