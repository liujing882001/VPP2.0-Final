if [ "$#" -eq 1 ]; then
  if [ "$1" == "test" ] || [ "$1" == "production" ] || [ "$1" == "china" ] || [ "$1" == "demo" ] || [ "$1" == "poc" ] || [ "$1" == "lingang" ]; then
    export ENV=$1
    docker stop vpp-load_prediction-$ENV-c1
    docker rm vpp-load_prediction-$ENV-c1
    # 如果不是测试环境
    if [ "$ENV" != "test" ]; then
      docker rmi vpp-load_prediction-c1
      docker load -i vpp-load_prediction-c1.tar
    fi
    docker run -d -v ./config/vpp-load_prediction-cfgs:/app/config/vpp-load_prediction-cfgs -v ./logs:/app/logs -e "ENV=$ENV" --name vpp-load_prediction-$ENV-c1 vpp-load_prediction-c1
  else
    echo "请输入正确的启动环境名称(test|production|china|demo|poc|lingang)"
  fi
else
  echo "请输入启动环境名称"
fi
