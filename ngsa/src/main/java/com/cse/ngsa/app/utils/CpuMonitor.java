package com.cse.ngsa.app.utils;

import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.ObjectName;

import com.sun.management.OperatingSystemMXBean;

import org.springframework.stereotype.Component;

@Component
public class CpuMonitor {
  private double cpuUsagePercent = 0;
  private OperatingSystemMXBean opMxBean = null;

  public double getCpuUsagePercent() {
    // Thread synchronization is not required
    return cpuUsagePercent;
  }

  public CpuMonitor() {
    if ( ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean ) {
      opMxBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
      new Timer().schedule(new TimerTask(){
        @Override
        public void run() {
          calcCpuUsage();
        }

      }, 0, 1000);
    }
  }

  // TODO: Clean up
  public static double getProcessCpuLoad() throws Exception {

    var mbs    = ManagementFactory.getPlatformMBeanServer();
    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
    AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

    if (list.isEmpty())     return Double.NaN;

    Attribute att = (Attribute)list.get(0);
    Double value  = (Double)att.getValue();

    // usually takes a couple of seconds before we get real values
    if (value == -1.0)      return Double.NaN;
    // returns a percentage value with 1 decimal point precision
    return ((int)(value * 100000) / 1000.0);
  }

  private synchronized void calcCpuUsage() {
    if ( opMxBean == null ) {
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
