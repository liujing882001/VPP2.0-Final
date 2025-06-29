# 只在测试环境构建镜像
ENV="test"
docker stop vpp-es_schedule-$ENV-c1
docker rm vpp-es_schedule-$ENV-c1
docker rmi vpp-es_schedule-c1
docker build -t vpp-es_schedule-c1 .
docker save -o ../images/vpp-es_schedule-c1.tar vpp-es_schedule-c1
echo "vpp-es_schedule-c1 build success and save to ../images/vpp-es_schedule-c1.tar"
