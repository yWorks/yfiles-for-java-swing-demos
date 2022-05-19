/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package integration.neo4j;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

/**
 * Panel to enter the Neo4j configuration.
 */
class Neo4jConfigurationPanel extends JPanel {
  private final JTextField dbUrlField;
  private final JTextField dbNameField;
  private final JTextField userNameField;
  private final JTextField passwordField;

  public Neo4jConfigurationPanel() {
    super(new GridLayout(4, 2));

    add(new JLabel("Database URL"));
    dbUrlField = new JTextField("neo4j+s://demo.neo4jlabs.com");
    add(dbUrlField);

    add(new JLabel("Database Name"));
    dbNameField = new JTextField("movies");
    add(dbNameField);

    add(new JLabel("User name"));
    userNameField = new JTextField("movies");
    add(userNameField);

    add(new JLabel("Password"));
    passwordField = new JPasswordField("movies");
    add(passwordField);
  }

  public DBInformation getDBInformation() {
    return new DBInformation(
        dbUrlField.getText(),
        dbNameField.getText(),
        userNameField.getText(),
        passwordField.getText());
  }
}
