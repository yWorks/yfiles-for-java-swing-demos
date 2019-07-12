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
package complete.uml;

import com.yworks.yfiles.graph.AbstractUndoUnit;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IUndoUnit;
import com.yworks.yfiles.graph.UndoEngine;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Creates undo units for {@link UmlClassModel} state changes.
 */
class UndoFactory {
  private UndoFactory() {
  }

  /**
   * Creates an undo unit that reverts adding a new attribute or operation
   * to the given model.
   */
  static void addAddFeatureUnit(
          IGraph graph,
          UmlClassModel model, int featureListId, String text
  ) {
    addUndoUnit(graph, new AddFeature(model, featureListId, text));
  }

  /**
   * Creates an undo unit that reverts removing an attribute or operation
   * from the given model.
   */
  static void addRemoveFeatureUnit(
          IGraph graph,
          UmlClassModel model, List<String> featureList, int featureIndex, String featureValue
  ) {
    int id = model.getAttributes() == featureList
            ? UmlClassModel.LIST_ATTRIBUTES : UmlClassModel.LIST_OPERATIONS;
    addUndoUnit(graph, new RemoveFeature(model, id, featureIndex, featureValue));
  }

  /**
   * Creates an undo unit that reverts changing the selected list ID and/or
   * selected list index of the given model.
   */
  static void addSelectedListUnit(
          IGraph graph, UmlClassModel model, int oldListId, int oldIdx
  ) {
    addUndoUnit(graph, new SelectedList(model, oldListId, oldIdx));
  }

  /**
   * Creates an undo unit that reverts changing the visibility of the given
   * model's details, attributes, or operations.
   */
  static void addVisibilityChangedUnit(
          IGraph graph,
          UmlClassModel model, String type,
          Function<UmlClassModel, Boolean> isVisible,
          BiConsumer<UmlClassModel, Boolean> setVisible
  ) {
    addUndoUnit(graph, new VisibilityChanged(model, type, isVisible, setVisible));
  }

  private static void addUndoUnit( IGraph graph, IUndoUnit unit ) {
    UndoEngine undoEngine = graph.lookup(UndoEngine.class);
    if (undoEngine != null) {
      undoEngine.addUnit(unit);
    }
  }



  private abstract static class AbstractFeature extends AbstractUndoUnit {
    AbstractFeature( String name ) {
      super(name);
    }

    static String asString( int featureListId ) {
      return UmlClassModel.LIST_ATTRIBUTES == featureListId
             ? "attribute" : "operation";
    }

    static List<String> getList( UmlClassModel model, int id ) {
      return UmlClassModel.LIST_ATTRIBUTES == id
             ? model.getAttributes() : model.getOperations();
    }
  }

  /**
   * Undo unit for adding a new attribute or operation to an UML class model.
   */
  private static class AddFeature extends AbstractFeature {
    final UmlClassModel model;
    final String text;
    final int featureListId;

    AddFeature(
            UmlClassModel model, int featureListId, String text
    ) {
      super("UML add " + asString(featureListId));
      this.model = model;
      this.text = text;
      this.featureListId = featureListId;
    }

    @Override
    public void undo() {
      List<String> features = getList(model, featureListId);
      features.remove(features.size() - 1);
    }

    @Override
    public void redo() {
      getList(model, featureListId).add(text);
    }
  }

  /**
   * Undo unit for removing an attribute or operation from an UML class model.
   */
  private static class RemoveFeature extends AbstractFeature {
    final UmlClassModel model;
    final int listId;
    final int listIdx;
    final String value;

    RemoveFeature(
            UmlClassModel model,
            int featureListId, int featureIndex, String featureValue
    ) {
      super("UML remove " + asString(featureListId));
      this.model = model;
      this.listId = featureListId;
      this.listIdx = featureIndex;
      this.value = featureValue;

    }

    @Override
    public void undo() {
      getList(model, listId).add(listIdx, value);
    }

    @Override
    public void redo() {
      getList(model, listId).remove(listIdx);
    }
  }

  /**
   * Undo unit for changing the selected list ID or selected list index
   * of an UML class model.
   */
  private static class SelectedList extends AbstractUndoUnit {
    final UmlClassModel model;
    final int oldListId;
    final int newListId;
    final int oldIdx;
    final int newIdx;

    SelectedList( UmlClassModel model, int oldListId, int oldIdx ) {
      super("UML selected list");
      this.model = model;
      this.oldListId = oldListId;
      this.newListId = model.getSelectedList();
      this.oldIdx = oldIdx;
      this.newIdx = model.getSelectedListIndex();
    }

    @Override
    public void undo() {
      model.setSelectedList(oldListId);
      model.setSelectedListIndex(oldIdx);
    }

    @Override
    public void redo() {
      model.setSelectedList(newListId);
      model.setSelectedListIndex(newIdx);
    }
  }


  /**
   * Undo unit for visibility changes of UML class model sections.
   */
  private static final class VisibilityChanged extends AbstractUndoUnit {
    final UmlClassModel model;
    final boolean newState;
    final BiConsumer<UmlClassModel, Boolean> setVisible;

    VisibilityChanged(
            UmlClassModel model, String type,
            Function<UmlClassModel, Boolean> isVisible,
            BiConsumer<UmlClassModel, Boolean> setVisible
    ) {
      super("UML " + type + " visibility");
      this.model = model;
      this.newState = isVisible.apply(model);
      this.setVisible = setVisible;
    }

    @Override
    public void undo() {
      setVisibleImpl(!newState);
    }

    @Override
    public void redo() {
      setVisibleImpl(newState);
    }

    private void setVisibleImpl(boolean visible) {
      setVisible.accept(model, visible ? Boolean.TRUE : Boolean.FALSE);
    }
  }
}
