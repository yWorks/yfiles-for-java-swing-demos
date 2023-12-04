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
package viewer.ganttchart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of tasks in a project schedule and provides methods
 * for mapping graph coordinates to dates and tasks (and vice versa).
 */
public class GanttDataUtil {
  /**
   * The width in the graph coordinate system that corresponds to one day.
   */
  public static final int DAY_WIDTH = 80;
  /**
   * The spacing between tasks.
   */
  public static final int TASK_SPACING = 10;
  /**
   * The spacing between activities.
   */
  public static final int ACTIVITY_SPACING = 20;
  /**
   * The height of an activity.
   */
  public static final int ACTIVITY_HEIGHT = 40;

  /**
   * The date from which we start.
   */
  private final LocalDateTime originDate;

  private final List<Task> tasks;

  private final DateTimeFormatter dMHms;
  private final DateTimeFormatter dMy;


  /**
   * Creates a new {@code GanttDataUtil} instance.
   */
  public GanttDataUtil() {
    originDate = LocalDateTime.of(LocalDate.parse(Activity.ORIGIN_DATE), LocalTime.MIN);
    tasks = new ArrayList<Task>();
    dMHms = DateTimeFormatter.ofPattern("d 'of' MMMM HH:mm:ss");
    dMy = DateTimeFormatter.ofPattern("d 'of' MMMM yyyy");
  }

  /**
   * Returns a human-readable representation of the given date-time.
   */
  public String getDateString( LocalDateTime ldt ) {
    return ldt.format(dMy);
  }

  /**
   * Returns a human-readable representation of given date-time that
   * corresponds to the given x-coordinate.
   */
  public String getDateTimeString( double x ) {
    return getDate(x).format(dMHms);
  }

  /**
   * Returns the x-coordinate that corresponds to the given date-time.
   */
  public int getX( LocalDateTime date ) {
    long hours = originDate.until(date, ChronoUnit.MINUTES) / 60;
    long x = getDayOffset(hours) * GanttDataUtil.DAY_WIDTH;
    return (int) x;
  }

  private long getDayOffset( final long hours ) {
    long rest = hours % 24;
    return hours / 24 - (hours > -1 || rest == 0 ? 0 : 1);
  }

  /**
   * Returns the date-time that corresponds to the given x-coordinate.
   */
  public LocalDateTime getDate( double x ) {
    double duration = x / GanttDataUtil.DAY_WIDTH;
    double durationMin = duration * 24 * 60;
    return originDate.plusMinutes((long) durationMin);
  }

  /**
   * Returns the y-coordinate for the given activity.
   */
  public int getActivityY( Activity activity ) {
    Task task = activity.getTask();
    int idx = activity.getSubrowIndex();
    return getTaskY(task) + idx * ACTIVITY_HEIGHT + (idx + 1) * ACTIVITY_SPACING;
  }

  /**
   * Returns the y-coordinate for the given task.
   */
  public int getTaskY( Task task ) {
    int y = TASK_SPACING;
    for (Task pred : tasks) {
      if (pred == task) {
        break;
      } else {
        y += getCompleteTaskHeight(pred) + TASK_SPACING;
      }
    }
    return y;
  }

  /**
   * Gets the task at the given y coordinate.
   */
  public Task getTask( double y ) {
    int currentY = 0;
    for (Task task : tasks) {
      currentY += getCompleteTaskHeight(task) + GanttDataUtil.TASK_SPACING;
      if (currentY > y) {
        return task;
      }
    }
    return tasks.get(tasks.size() - 1);
  }

  /**
   * Returns all tasks for this project schedule.
   */
  public List<Task> getTasks() {
    return tasks;
  }

  /**
   * Adds a tasks to this project schedule.
   */
  public void addTask( Task task ) {
    tasks.add(task);
  }

  /**
   * Calculates the height of the given tasks.
   */
  public double getCompleteTaskHeight( Task task ) {
    int rows = Math.max(1, task.getSubrowCount());
    return rows * (GanttDataUtil.ACTIVITY_HEIGHT
      + GanttDataUtil.ACTIVITY_SPACING) + GanttDataUtil.ACTIVITY_SPACING;
  }


  /**
   * Calculates the total activity duration in hours.
   */
  public static double getTotalActivityDuration( Activity activity ) {
    long duration = activity.getStartDate().until(activity.getEndDate(), ChronoUnit.HOURS);
    return (duration + (activity.getLeadTime()) + (activity.getFollowUpTime()));
  }

  /**
   * Calculates the length in world coordinates from the given duration in hours.
   */
  public static double hoursToWorldLength( double hours ) {
    return hours / 24.0 * GanttDataUtil.DAY_WIDTH;
  }

  /**
   * Calculates the duration in hours from the given length in world coordinates.
   */
  public static double worldLengthToHours( double worldLength ) {
    return (worldLength * 24 / GanttDataUtil.DAY_WIDTH);
  }
}
