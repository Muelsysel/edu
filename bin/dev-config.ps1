$ProjectRoot = "D:\.develop\edu-achievement01\edu-achievement-master"

$RedisExe = "D:\.develop\Redis-x64-3.2.100\redis-server.exe"
$RedisConfig = "D:\.develop\Redis-x64-3.2.100\redis.windows.conf"

$NacosStartup = "D:\.develop\nacos\nacos-server-2.2.3\bin\startup.cmd"

$MinioExe = "D:\.develop\minio\minio.exe"
$MinioDataDir = "D:\.develop\minio\data"

$UiDir = Join-Path $ProjectRoot "edu-ui"

$JavaOpts = "-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m"

$Services = @(
    @{ Name = "Auth"; Jar = Join-Path $ProjectRoot "edu-auth\target\edu-auth.jar" },
    @{ Name = "Gateway"; Jar = Join-Path $ProjectRoot "edu-gateway\target\edu-gateway.jar" },
    @{ Name = "System"; Jar = Join-Path $ProjectRoot "edu-modules\edu-system\target\edu-modules-system.jar" },
    @{ Name = "Achievement"; Jar = Join-Path $ProjectRoot "edu-modules\edu-achievement\target\edu-modules-achievement.jar" },
    @{ Name = "File"; Jar = Join-Path $ProjectRoot "edu-modules\edu-file\target\edu-modules-file.jar" }
)
