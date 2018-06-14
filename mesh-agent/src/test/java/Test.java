import com.tracy.agent.registry.Endpoint;
import com.tracy.agent.router.Router;
import com.tracy.agent.router.WeightedRandomRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tracy.
 * @create 2018-05-31 15:10
 **/
public class Test {
    public static void main(String[] args) throws Exception {
//        IRegistry registry = new EtcdRegistry(System.getProperty(Constants.ETCD_URL));
        Router router = new WeightedRandomRouter();
        Map<String, Integer> count = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            Endpoint endpoint = router.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
            if (count.containsKey(endpoint.getHost())) {
                count.put(endpoint.getHost(), count.get(endpoint.getHost()) + 1);
            } else {
                count.put(endpoint.getHost(), 1);
            }
        }
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            System.out.println("服务器 " + map.getKey() + " 请求次数： " + map.getValue());
        }
    }
}
