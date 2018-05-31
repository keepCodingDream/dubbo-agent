# build etcd
cd etcd
docker build -t etcd .

#run etcd(--network:来指定网络，使用--network-alias来指定网络别名)
docker run --name 'etcd' --network dubbo-agent --network-alias etcd -d etcd

#build services
cd ../
docker build -t agent .

docker run --name 'agent-provider' --network dubbo-agent --network-alias provider-small -d agent provider-small

#启动并绑定主机8080端口
docker run --name 'agent-consumer' -p 8080:8087 --network dubbo-agent --network-alias agent-consumer -d agent consumer

#主机测试
http://localhost:8080/mapping?interface=com.alibaba.dubbo.performance.demo.provider.IHelloService&method=hash&parameterTypesString=Ljava/lang/String;&parameter=lurenjie