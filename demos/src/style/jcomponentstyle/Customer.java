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
package style.jcomponentstyle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A business object that describes a customer in our simple model.
 */
public class Customer {

  /**
   * The key that can be used with {@link #addPropertyChangeListener(String, java.beans.PropertyChangeListener) addPropertyChangeListener}
   * to be notified about the changes of the 'name' property.
   */
  public static final String NAME = "Customer.name";

  /**
   * The key that can be used with {@link #addPropertyChangeListener(String, java.beans.PropertyChangeListener) addPropertyChangeListener}
   * to be notified about the changes of the 'id' property.
   */
  public static final String ID = "Customer.id";

  /**
   * The key that can be used with {@link #addPropertyChangeListener(String, java.beans.PropertyChangeListener) addPropertyChangeListener}
   * to be notified about the changes of the 'location' property.
   */
  public static final String LOCATION = "Customer.location";

  private String name;
  private int id;
  private String location;

  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  /**
   * Creates a new instance - for deserialization only.
   */
  public Customer() {
    this("", -1, "");
  }

  /**
   * Creates a new instance with the given name, id, and location.
   */
  public Customer(String name, int id, String location) {
    this.name = name;
    this.id = id;
    this.location = location;
  }

  /**
   * Returns the customer id.
   */
  public int getId() {
    return id;
  }

  /**
   * Specifies the customer id.
   */
  public void setId(int id) {
    if (this.id != id) {
      int oldId = this.id;
      this.id = id;
      this.pcs.firePropertyChange(ID, oldId, id);
    }
  }

  /**
   * Returns the customer location.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Specifies the customer location.
   */
  public void setLocation(String location) {
    if (this.location == null ? null != location : !this.location.equals(location)) {
      String oldLocation = this.location;
      this.location = location;
      this.pcs.firePropertyChange(LOCATION, oldLocation, location);
    }
  }

  /**
   * Returns the customer name.
   */
  public String getName() {
    return name;
  }

  /**
   * Specifies the customer name.
   */
  public void setName(String name) {
    if (this.name == null ? null != name : !this.name.equals(name)) {
      String oldName = this.name;
      this.name = name;
      this.pcs.firePropertyChange(NAME, oldName, name);
    }
  }

  /**
   * Adds a PropertyChangedListener for the specified property.
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes a previously added PropertyChangedListener for the specified property.
   */
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(propertyName, listener);
  }
}
