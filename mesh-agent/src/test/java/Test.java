import com.alibaba.fastjson.JSON;
import com.tracy.agent.router.AbstractRouter;
import com.tracy.agent.router.WeightedRandomRouter;

/**
 * @author tracy.
 * @create 2018-05-31 15:10
 **/
public class Test {
    public static void main(String[] args) throws Exception {
        AbstractRouter router = new WeightedRandomRouter();
        int i = 0;
        while (i++ < 10000) {
            System.out.println(JSON.toJSONString(router.find("com.alibaba.dubbo.performance.demo.provider.IHelloService")));
        }
    }
}
