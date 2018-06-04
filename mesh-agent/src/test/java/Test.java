import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * @author tracy.
 * @create 2018-05-31 15:10
 **/
public class Test {
    public static void main(String[] args) throws Exception {
//        IRegistry registry = new EtcdRegistry(System.getProperty(Constants.ETCD_URL));
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        System.out.println("Total RAM：" + mem.getTotalPhysicalMemorySize() / 1024 / 1024 + "MB");
        System.out.println("Available　RAM：" + mem.getFreePhysicalMemorySize() / 1024 / 1024 + "MB");
    }
}
