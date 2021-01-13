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
package toolkit.aggregation;

import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEnumerator;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utility class providing static methods to create IEnumerables.
 */
public class EnumerableExtensions {

  /**
   * Returns an {@link IEnumerable} for all items in <code>items</code> that satisfies the <code>predicate</code>.
   * @param items The backing iterable of items.
   * @param predicate The predicate that has to be satisfied.
   * @param <T> The type of the items.
   * @return An {@link IEnumerable} for all items in <code>items</code> that satisfies the <code>predicate</code>.
   */
  static <T> IEnumerable<T> filter( final Iterable<T> items, final Predicate<T> predicate ) {
    return new FilterEnumerable<T>(items, predicate);
  }

  /**
   * Returns an {@link IEnumerable} containing all elements that are returned by <code>mapping</code> for all items in
   * <code>items</code>.
   * @param items The backing iterable of items.
   * @param mapping The mapping from item to elements.
   * @param <T> The type of the items.
   * @param <R> The type of the mapped elements.
   * @return An {@link IEnumerable} containing all elements that are returned by <code>mapping</code> for all items in.
   */
  static <T, R> IEnumerable<R> flatMap( final Iterable<T> items, final Function<T, Iterable<R>> mapping ) {
    return new FlatMapEnumerable<T, R>(items, mapping);
  }

  static class FilterEnumerable<T> implements IEnumerable<T> {
    private final Iterable<T> other;
    private final Predicate<T> predicate;

    FilterEnumerable( Iterable<T> other, Predicate<T> predicate ) {
      this.other = other;
      this.predicate = predicate;
    }

    @Override
    public IEnumerator<T> enumerator() {
      return IEnumerable.create(StreamSupport.stream(other.spliterator(), false)
          .filter(predicate)
          .collect(Collectors.toList()))
          .enumerator();
    }
  }

  static class FlatMapEnumerable<T, R> implements IEnumerable<R> {
    private final Iterable<T> other;
    private final Function<T, Iterable<R>> mapping;

    FlatMapEnumerable( Iterable<T> other, Function<T, Iterable<R>> mapping) {
      this.other = other;
      this.mapping = mapping;
    }

    @Override
    public IEnumerator<R> enumerator() {
      return IEnumerable.create(StreamSupport.stream(other.spliterator(), false)
          .flatMap(t -> IEnumerable.create(mapping.apply(t)).stream())
          .collect(Collectors.toList())).enumerator();
    }
  }
}
