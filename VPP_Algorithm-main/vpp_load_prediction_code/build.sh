# 只在测试环境构建镜像
ENV="test"
docker stop vpp-load_prediction-$ENV-c1
docker rm vpp-load_prediction-$ENV-c1
docker rmi vpp-load_prediction-c1
docker build -t vpp-load_prediction-c1 .
docker save -o ../images/vpp-load_prediction-c1.tar vpp-load_prediction-c1
echo "vpp-load_prediction-c1 build success and save to ../images/vpp-load_prediction-c1.tar"
