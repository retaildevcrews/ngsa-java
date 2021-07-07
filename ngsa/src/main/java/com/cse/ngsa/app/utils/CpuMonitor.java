package com.cse.ngsa.app.utils;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.stereotype.Component;

/**
 * Cpu Usage Monitor Class.
 */
@Component
public class CpuMonitor {
  private double cpuUsagePercent = 0;
  private OperatingSystemMXBean opMxBean = null;

  /**
   * Gets the CPU Usage as Percentage.
   * @return Double CPU Usage Percentage
   */
  public double getCpuUsagePercent() {
    // Thread synchronization is not required
    return cpuUsagePercent;
  }

  /**
   * Constructor.
   */
  public CpuMonitor() {
    if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
      opMxBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          calcCpuUsage();
        }

      }, 0, 1000);
    }
  }

  private synchronized void calcCpuUsage() {
    if (opMxBean == null) {
      return;
    }

    // Alternative cpu usage calculation
    // They have similar results
    // long curSystemTime = System.nanoTime();
    // long curCpuTime = opMxBean.getProcessCpuTime();
    // double cpuUsage = (double) ( curCpuTime - lastCpuTime ) / ( curSystemTime - lastSystemTime );
    // lastSystemTime = curSystemTime;
    // lastCpuTime = curCpuTime;
    // cpuUsagePercent = 100 * cpuUsage / processorCount;

    cpuUsagePercent = 100 * opMxBean.getProcessCpuLoad();
    // To Soften the curve
    // cpuUsagePercent = (cpuUsagePercent + 100 * opMxBean.getProcessCpuLoad()) / 2; 
  }
}
