/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package integration.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Application class that starts the JavaFX integration demo.
 * @see JavaFxDemoController
 */
public class JavaFxDemo extends Application {
  @Override
  public void start(final Stage primaryStage) throws Exception {
    Image[] windowIcons = {
        new Image("resources/logo_16.png"),
        new Image("resources/logo_24.png"),
        new Image("resources/logo_32.png"),
        new Image("resources/logo_48.png"),
        new Image("resources/logo_64.png"),
        new Image("resources/logo_128.png")};

    Parent root = FXMLLoader.load(getClass().getResource("JavaFxDemo.fxml"));
    Scene scene = new Scene(root, 1365, 768);

    String css = getClass().getResource("JavaFxDemo.css").toExternalForm();
    scene.getStylesheets().add(css);

    primaryStage.setScene(scene);
    primaryStage.setTitle("JavaFX Integration Demo - yFiles for Java (Swing)");
    primaryStage.getIcons().addAll(windowIcons);
    primaryStage.show();
  }

   public static void main(final String[] args) {
    launch(args);
  }
}
