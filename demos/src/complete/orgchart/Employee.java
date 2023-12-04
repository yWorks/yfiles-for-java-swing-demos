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
package complete.orgchart;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Representation for the business model of an employee.
 * An employee consists of
 * <ul>
 *   <li>A first and last name.</li>
 *   <li>The position the employee is in.</li>
 *   <li>Contact data like phone, email and fax.</li>
 *   <li>The business unit the employee is part of.</li>
 *   <li>The employee's current status, i.e. present, travelling etc.</li>
 *   <li>A picture or icon.</li>
 * </ul>
 */
public class Employee {

  // fields for the various properties of an employee. The properties are complemented by getter/setter methods for each of them.

  // the following fields are defined in GraphMl

  private String name;

  private String firstName;

  private String position;

  private String fax;

  private String businessUnit;

  private String status;

  private String icon;

  private String phone;

  private String email;

  private String layout;

  private boolean assistant;

  // the following fields are determined by the code behind logic in the OrgChartDemo class (for example, method #initializeEmployeeHierarchy)

  /**
   * The employee of the company that is the direct superior of the current employee.
   * If this field is null this means that this employee is the CEO of the company.
   * This information is stored here to have a convenient access to display the information in the properties view for the employee.
   */
  private Employee superior;

  /**
   * A list of employees of the company that are direct subordinates of the current employee.
   * This information is stored here to have a convenient access to display the information in the properties view for the employee.
   */
  private Collection<Employee> subordinates = new ArrayList<>();

  /**
   * The icon for the employee. To enhance the performance, this information is stored in this class for convenient use in the visual
   * representation classes (the node styles and visuals). The icon is lazily loaded.
   */
  private BufferedImage iconImage;

  /**
   * Zero-argument constructor that is necessary for the GraphML deserialization process.
   * The framework uses this constructor to get an instance of the xml representation and fills
   * the properties using the setter methods.
   */
  public Employee(){}

  // the getter and setter methods for the properties. For a description of the various fields have a look at the fields or class commentary.

  public boolean isAssistant() {
    return assistant;
  }

  public void setAssistant(final boolean assistant) {
    this.assistant = assistant;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(final String position) {
    this.position = position;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(final String fax) {
    this.fax = fax;
  }

  public String getBusinessUnit() {
    return businessUnit;
  }

  public void setBusinessUnit(final String businessUnit) {
    this.businessUnit = businessUnit;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout(final String layout) {
    this.layout = layout;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public Employee getSuperior() {
    return superior;
  }

  public void setSuperior(final Employee superior) {
    this.superior = superior;
  }

  /**
   * Gets the list of subordinates of this employee.
   */
  public Collection<Employee> getSubordinates() {
    return subordinates;
  }

  /**
   * Provides the image for the employee by loading it lazily if requested.
   * @return an BufferedImage with the referenced icon for this employee.
   */
  public BufferedImage getIconImage() {
    if (iconImage == null) {
      try {
        iconImage = ImageIO.read(getClass().getResourceAsStream("resources/" + getIcon() + ".png"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return iconImage;
  }
}
