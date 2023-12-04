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
package style.svgnodestyle;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLReferenceType;
import com.yworks.yfiles.graphml.QueryReferenceIdEventArgs;
import com.yworks.yfiles.utils.IEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Forces the values of {@link SVGNodeStyle#getDefinition() the SVG definition
 * property} to be embedded as internal resources in GraphML. 
 */
public class SVGDocumentHandler implements IEventListener<QueryReferenceIdEventArgs> {
  /**
   * Stores the ID for each definition.
   * This is necessary to ensure that definitions which are used in multiple
   * {@link SVGNodeStyle} instance are stored only once in GraphML.
   */
  final Map<Object, Integer> def2id;

  /**
   * Initializes a new <code>SVGDocumentHandler</code> instance.
   */
  public SVGDocumentHandler() {
    def2id = new HashMap<Object, Integer>();
  }

  /**
   * Forces the values of {@link SVGNodeStyle#getDefinition() the SVG definition
   * property} to be embedded as internal resources in GraphML.
   * Additionally, assigns and stores an ID for each definition to ensure that
   * the appropriate definition is deserialized when an
   * {@link SVGNodeStyle} is read from GraphML.
   */
  public void onEvent(Object sender, QueryReferenceIdEventArgs args) {
    final Object value = args.getValue();
    if (value instanceof String) {
      final Object element = args.getContext().getCurrent(Object.class);
      if (element instanceof INode) {
        if (((INode) element).getStyle() instanceof SVGNodeStyle) {
          // determine an appropriate ID for the SVG definition
          Integer id = def2id.get(value);
          if (id == null) {
            id = Integer.valueOf(def2id.size() + 1);
            def2id.put(value, id);
          }

          // set an appropriate ID that makes it possible to deserialize the
          // correct definition when reading a SVGNodeStyle instance from
          // GraphML
          args.setReferenceId("SVGDocument" + id);

          // ensure that the definition is embedded into the GraphML file
          // (the default type is EXTERNAL which assumes that the application
          // reading the GraphML is able to get the referenced value from a
          // source other than the GraphML file)
          args.setReferenceType(GraphMLReferenceType.INTERNAL);
        }
      }
    }
  }

  /**
   * Removes all previously stored IDs.
   * This method should be called after GraphML writing is done to prevent
   * instances of this class from holding onto large amounts of textual data.
   */
  public void clear() {
    def2id.clear();
  }
}
