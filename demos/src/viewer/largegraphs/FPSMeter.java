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
package viewer.largegraphs;

import com.yworks.yfiles.view.GraphComponent;

import java.awt.EventQueue;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Helper class that calculates the Frames per second.
 */
public class FPSMeter {
  private final GraphComponent control;
  private final Runnable callback;

  private boolean recording;
  private boolean isRunning = false;
  private Instant first;
  private Instant last;
  private int frameCount;
  private int[] frameCountHistory = new int[10];
  private Instant[] timeStameHistory = new Instant[10];
  private int updateCount = 0;
  private Timer timer;

  // region FPS property

  private String fps = "";

  public String getFps() {
    return fps;
  }

  public int getFrameCount() {
    return frameCount;
  }

  // endregion

  public boolean isRecording() {
    return recording;
  }

  public void setRecording(boolean recording) {
    this.recording = recording;
    if (!recording) {
      reset();
    }
  }

  public FPSMeter(GraphComponent control, Runnable callback) {
    this.control = control;
    this.callback = callback;
    control.addUpdatedListener((source, args) -> calcFps());
  }

  /**
   * Calculates and shows the frame rate. To be called once on each new frame.
   */
  private void calcFps() {
    if (!this.recording) {
      if (isRunning) {
        reset();
      }
      return;
    }
    if (!isRunning) {
      frameCount = 0;
      first = last = Instant.now();
      isRunning = true;
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          long d = Duration.between(first, last).toMillis();
          int currentFps = d == 0 ? 0 : (int) Math.floor(frameCount * 1000.0 / d);
          int index = updateCount % 10;
          if (updateCount >= 10) {
            Instant historic = timeStameHistory[index];
            double delay = historic != null ? Duration.between(historic, last).toMillis() / 1000.0 : 0;
            if (delay <= 0) {
              // threading problems
              delay = 1;
            }
            currentFps = (int) Math.floor((frameCount - frameCountHistory[index]) / delay);
          }
          frameCountHistory[index] = frameCount;
          timeStameHistory[index] = last;
          updateCount++;

          fps = Integer.toString(currentFps);

          EventQueue.invokeLater(callback);
        }
      }, 0, 100);
      return;
    }

    last = Instant.now();
    frameCount++;
  }

  private void reset() {
    timer.cancel();
    first = null;
    last = null;
    frameCountHistory = new int[10];
    timeStameHistory = new Instant[10];
    updateCount = 0;
    isRunning = false;
  }
}
