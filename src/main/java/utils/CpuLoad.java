package utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.AccessControlException;

public class CpuLoad {

    private static CpuLoad instance;
    private ThreadMXBean threadMXBean;

    private CpuLoad(){
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        try {
            if (this.threadMXBean.isThreadCpuTimeSupported())
                this.threadMXBean.setThreadCpuTimeEnabled(true);
            else
                throw new AccessControlException("");
        }
        catch (AccessControlException e) {
            System.out.println("CPU Usage monitoring is not available!");
            System.exit(0);
        }
    }

    public ThreadMXBean getThreadMXBean(){
        return this.threadMXBean;
    }

    public static CpuLoad getInstance(){
        if(instance == null){
            instance = new CpuLoad();
        }
        return instance;
    }
}
