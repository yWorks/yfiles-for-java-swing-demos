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
package viewer.largegraphs.styles.levelofdetail;

import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Immutable container for maintaining a list of zoom level / style pairs for use in level-of-detail styles.
 * <p>
 *  Styles need to be added to this container in ascending order of their zoom levels. This container cannot be
 *  empty when it's used. {@link #getStyle} and {@link #hasSameStyle} require at least one style to work correctly.
 * </p>
 *
 * @param <T> Either {@link INodeStyle}, {@link IEdgeStyle}, or {@link ILabelStyle}. This type parameter is not
 *           constrained as there is no suitable base interface for the aforementioned style interfaces.
 */
public class LevelOfDetailStyleContainer<T> implements Iterable<T> {

  // The list of styles
  private List<T> styles = new ArrayList<>();

  // The list of zoom levels for the styles.
  private List<Double> zoomLevels = new ArrayList<Double>();


  /**
   * Adds the given zoom level / style pair.
   * <p>
   * Styles need to be added in ascending order of their zoom levels.
   * </p>
   * @param zoomLevel The zoom level.
   * @param style The style.
   */
  public void add(double zoomLevel, T style) {
    zoomLevels.add(zoomLevel);
    styles.add(style);
  }

  /**
   * Gets the style for the given zoom level.
   * @param zoomLevel The zoom level.
   * @return The style for the given zoom level.
   */
  public T getStyle(double zoomLevel) {
    return styles.get(getIndex(zoomLevel));
  }

  /**
   * Determines whether two zoom levels would correspond to the same style.
   * @param z1 The first zoom level.
   * @param z2 The second zoom level.
   * @return <code>true</code>, if both zoom levels would fall into the same style »bucket«, <code>false</code> otherwise.
   */
  public boolean hasSameStyle(double z1, double z2) {
    return getIndex(z1) == getIndex(z2);
  }

  /**
   * Helper method to get the index in the list of styles or zoom levels corresponding to the given zoom level.
   * @param zoomLevel The zoom level to look up a list index for.
   * @return The list index for the given zoom level.
   */
  private int getIndex(double zoomLevel) {
    if (zoomLevels.get(0) > zoomLevel) {
      return 0;
    }

    for (int i = 1; i < zoomLevels.size(); i++) {
      if (zoomLevels.get(i) > zoomLevel) {
        return i - 1;
      }
    }

    return styles.size() - 1;
  }

  @Override
  public Iterator<T> iterator() {
    return styles.iterator();
  }
}
