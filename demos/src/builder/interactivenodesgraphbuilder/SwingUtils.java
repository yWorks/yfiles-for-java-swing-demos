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

import toolkit.DragAndDropSupport;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.TransferHandler;

/**
 * Provides utility methods for creating Swing components.
 */
class SwingUtils {
  private static final String ADD_DIALOG_TITLE = "Add New Item";
  private static final String ADD_DIALOG_MESSAGE = "Name";
  private static final String TRASH_COMPONENT_NAME = "Trash";
  private static final String PROPERTY_NAME_MODEL = "model";
  private static final String KEY_HANDLER =
          "builder.interactivenodesgraphbuilder.SwingUtils#onChanged";


  private SwingUtils() {
  }

  /**
   * Creates a label for displaying plain text.
   */
  static JLabel newNonValidatingLabel() {
    JLabel label = new JLabel() {
      /**
       * Prevents text changes from triggering undesired rearrangements of
       * all business data editor controls.
       */
      @Override
      public void revalidate() {
      }
    };
    label.setFont(label.getFont().deriveFont(Font.PLAIN));
    return label;
  }

  /**
   * Creates a list view for displaying business data.
   */
  static <T> JList<T> newList(
          Class<T> itemType, Consumer<T> onChanged, boolean handleDropAsChange
  ) {
    JList<T> list = new JList<>(ObservableCollection.EMPTY);
    list.putClientProperty(KEY_HANDLER, onChanged);
    list.setCellRenderer(new PlainRenderer());
    list.setDropMode(DropMode.INSERT);
    list.setTransferHandler(new AddOnDropHandler<T>(itemType, handleDropAsChange));
    return list;
  }

  /**
   * Determines if the given list's model contains the given business data item.
   */
  static <T> boolean contains( JList<T> list, T item ) {
    return getModel(list).contains(item);
  }

  /**
   * Creates a titled pane with controls for adding and removing business data
   * items to and from the given list view.
   * @param factory handles the actual creation of new business data items.
   */
  static <T> JComponent newListPane(
          JList<T> list, String title, Function<String, T> factory
  ) {
    JLabel label = new JLabel(title);
    label.setEnabled(getModel(list).isEditable());

    JPanel titlePane = new JPanel(new BorderLayout());
    titlePane.add(label, BorderLayout.WEST);

    // create and configure the control for adding new business data items 
    JButton add = new JButton("+");
    add.setEnabled(getModel(list).isEditable());
    add.addActionListener(e -> {
      ObservableCollection<T> model = getModel(list);
      if (model.isEditable()) {
        String name = JOptionPane.showInputDialog(
                (JButton) e.getSource(),
                ADD_DIALOG_MESSAGE,
                ADD_DIALOG_TITLE,
                JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isEmpty()) {
          T data = factory.apply(name);
          if (model.add(data)) {
            getHandler(list).accept(data);
          }
        }
      }
    });
    list.addPropertyChangeListener(PROPERTY_NAME_MODEL, evt -> {
      Object newValue = evt.getNewValue();
      boolean enabled =
              newValue instanceof ObservableCollection &&
              ((ObservableCollection) newValue).isEditable();
      add.setEnabled(enabled);
      label.setEnabled(enabled);
    });

    // create and configure the control for removing business data items 
    JButton remove = new JButton("-");
    remove.setEnabled(list.getSelectedIndex() > -1);
    remove.addActionListener(e -> {
      int idx = list.getSelectedIndex();
      if (idx > -1) {
        ObservableCollection<T> model = getModel(list);
        if (model.isEditable()) {
          model.remove(idx);
          getHandler(list).accept(null);
        }
      }
    });
    list.getSelectionModel().addListSelectionListener(e -> {
      int idx = list.getSelectedIndex();
      remove.setEnabled(idx > -1);
    });

    // put everything together
    JPanel buttonPane = new JPanel(new GridLayout(1, 2));
    buttonPane.add(add);
    buttonPane.add(remove);
    titlePane.add(buttonPane, BorderLayout.EAST);

    JPanel listPane = new JPanel(new BorderLayout());
    listPane.add(new JScrollPane(list), BorderLayout.CENTER);
    listPane.add(titlePane, BorderLayout.NORTH);

    return listPane;
  }

  /**
   * Creates a trash pane that deletes business data items on Drag and Drop
   * drop operations.
   * @param onTrashed handles the actual deletion of business data items. 
   */
  static <T> JComponent newTrash( Class<T> itemType, Consumer<T> onTrashed ) {
    JPanel trashPane = new JPanel();
    trashPane.setBorder(BorderFactory.createTitledBorder(TRASH_COMPONENT_NAME));
    trashPane.setTransferHandler(new TrashOnDropHandler<>(itemType, onTrashed));
    return trashPane;
  }


  private static <T> ObservableCollection<T> getModel( JList<T> list ) {
    return (ObservableCollection<T>) list.getModel();
  }

  private static <T> Consumer<T> getHandler( JList<T> list ) {
    return (Consumer<T>) list.getClientProperty(KEY_HANDLER);
  }



  /**
   * Renders list items with a plain font instead of a bold font.
   */
  private static final class PlainRenderer<T> implements ListCellRenderer<T> {
    final DefaultListCellRenderer dlcr;
    final Font font;

    PlainRenderer() {
      dlcr = new DefaultListCellRenderer();
      font = dlcr.getFont().deriveFont(Font.PLAIN);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends T> list,
            T value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
      dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      dlcr.setFont(font);
      return dlcr;
    }
  }


  /**
   * Accepts Drag and Drop drop operations that transfer business data from
   * another Swing component in the same JVM.
   */
  private abstract static class AbstractDropHandler<T> extends TransferHandler {
    final DataFlavor itemFlavor;

    AbstractDropHandler( Class<T> itemType ) {
      itemFlavor = DragAndDropSupport.newFlavor(itemType);
    }

    @Override
    public boolean canImport( TransferSupport support ) {
      DataFlavor[] transferFlavors = support.getDataFlavors();
      if (transferFlavors != null) {
        for (int i = 0; i < transferFlavors.length; ++i) {
          DataFlavor flavor = transferFlavors[i];
          if (itemFlavor.equals(flavor)) {
            return true;
          }
        }
      }
      return false;
    }

    T getTransferData( TransferSupport support ) {
      Transferable t = support.getTransferable();
      try {
        return (T) t.getTransferData(itemFlavor);
      } catch (Exception ex) {
        return null;
      }
    }
  }

  /**
   * Adds new business data items from a Swing component in the same JVM
   * to the handlers target list.
   */
  private static final class AddOnDropHandler<T> extends AbstractDropHandler<T> {
    final boolean handleDropAsChange;

    AddOnDropHandler( Class<T> itemType, boolean handleDropAsChange ) {
      super(itemType);
      this.handleDropAsChange = handleDropAsChange;
    }

    @Override
    public boolean canImport( TransferSupport support ) {
      return isEditable(support.getComponent()) && super.canImport(support);
    }

    @Override
    public boolean importData( TransferSupport support ) {
      Component tgt = support.getComponent();
      if (tgt instanceof JList) {
        T data = getTransferData(support);
        if (data != null) {
          JList<T> list = (JList<T>) tgt;
          ObservableCollection<T> model = getModel(list);
          if (!model.contains(data)) {
            if (add(model, support.getDropLocation(), data)) {
              if (handleDropAsChange) {
                getHandler(list).accept(data);
              }
              return true;
            } else {
              return false;
            }
          }
        }
      }
      return false;
    }

    /**
     * Adds the given business data item to the given list.
     */
    boolean add( ObservableCollection model, DropLocation location, T data ) {
      if (location instanceof JList.DropLocation) {
        model.add(((JList.DropLocation) location).getIndex(), data);
        return true;
      } else {
        return model.add(data);
      }
    }

    /**
     * Determines if the given component is a list whose model is editable.
     */
    boolean isEditable( Component c ) {
      if (c instanceof JList) {
        return getModel((JList<T>) c).isEditable();
      } else {
        return false;
      }
    }
  }

  /**
   * Deletes business data items transfered from another Swing component in the
   * same JVM. The actual deletion of the business data is delegated to the
   * associated consumer.
   */
  private static final class TrashOnDropHandler<T> extends AbstractDropHandler<T> {
    final Consumer<T> onTrashed;

    TrashOnDropHandler( Class<T> itemType, Consumer<T> onTrashed ) {
      super(itemType);
      this.onTrashed = onTrashed;
    }

    @Override
    public boolean importData( TransferSupport support ) {
      Component tgt = support.getComponent();
      if (tgt instanceof JPanel) {
        T data = getTransferData(support);
        if (data != null) {
          onTrashed.accept(data);
        }
      }
      return false;
    }
  }
}
