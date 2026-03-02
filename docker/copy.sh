#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20250523.sql ./mysql/db
cp ../sql/ry_config_20250902.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../edu-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy edu-gateway "
cp ../edu-gateway/target/edu-gateway.jar ./ruoyi/gateway/jar

echo "begin copy edu-auth "
cp ../edu-auth/target/edu-auth.jar ./ruoyi/auth/jar

echo "begin copy edu-visual "
cp ../edu-visual/edu-monitor/target/edu-visual-monitor.jar  ./ruoyi/visual/monitor/jar

echo "begin copy edu-modules-system "
cp ../edu-modules/edu-system/target/edu-modules-system.jar ./ruoyi/modules/system/jar

echo "begin copy edu-modules-file "
cp ../edu-modules/edu-file/target/edu-modules-file.jar ./ruoyi/modules/file/jar

echo "begin copy edu-modules-job "
cp ../edu-modules/edu-job/target/edu-modules-job.jar ./ruoyi/modules/job/jar

echo "begin copy edu-modules-gen "
cp ../edu-modules/edu-gen/target/edu-modules-gen.jar ./ruoyi/modules/gen/jar

