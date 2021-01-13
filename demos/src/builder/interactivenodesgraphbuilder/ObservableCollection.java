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
package builder.interactivenodesgraphbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * Stores the business data items displayed in the business data list views.
 * This class ensures that the Swing lists and the yFiles graph builder work
 * on the same model. Otherwise synchronization in the form of copying elements
 * from lists' models to the graph builder's iterables (and vice versa) would
 * be necessary.
 */
class ObservableCollection<T> extends AbstractListModel<T> implements Iterable<T> {
  static ObservableCollection EMPTY = new EmptyObservableCollection();


  private final List<T> data;

  ObservableCollection() {
    data = new ArrayList<>();
  }

  /*
   * #####################################################################
   * Iterable
   * #####################################################################
   */

  @Override
  public Iterator<T> iterator() {
    return data.iterator();
  }

  /*
   * #####################################################################
   * ListModel
   * #####################################################################
   */

  @Override
  public int getSize() {
    return data.size();
  }

  @Override
  public T getElementAt( int index ) {
    return data.get(index);
  }

  /*
   * #####################################################################
   * miscelaneous
   * #####################################################################
   */

  boolean add( T item ) {
    if (data.add(item)) {
      int index = data.size() - 1;
      fireIntervalAdded(this, index, index);
      return true;
    } else {
      return false;
    }
  }

  void add( int index, T item ) {
    data.add(index, item);
    fireIntervalAdded(this, index, index);
  }

  void remove( int index ) {
    data.remove(index);
    fireIntervalRemoved(this, index, index);
  }

  boolean remove( T item ) {
    int idx = data.indexOf(item);
    if (idx > -1) {
      remove(idx);
      return true;
    } else {
      return false;
    }
  }

  boolean contains( T item ) {
    return data.contains(item);
  }

  boolean isEditable() {
    return true;
  }



  private static final class EmptyObservableCollection<T> extends ObservableCollection<T> {
    @Override
    boolean add( T item ) {
      return false;
    }

    @Override
    void add( int index, final T item ) {
    }

    @Override
    void remove( int index ) {
    }

    @Override
    boolean remove( T item ) {
      return false;
    }

    @Override
    boolean isEditable() {
      return false;
    }
  }
}
