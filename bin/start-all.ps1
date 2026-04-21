param(
    [switch]$StartUi
)

$ErrorActionPreference = "Stop"

. "$PSScriptRoot\dev-config.ps1"

function Assert-PathExists {
    param(
        [string]$Path,
        [string]$Label
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        throw "$Label 不存在: $Path"
    }
}

function Start-ToolProcess {
    param(
        [string]$Name,
        [string]$FilePath,
        [string]$Arguments,
        [string]$WorkingDirectory
    )

    Write-Host "启动 $Name ..."
    Start-Process -FilePath $FilePath -ArgumentList $Arguments -WorkingDirectory $WorkingDirectory
}

function Start-JarService {
    param(
        [string]$Name,
        [string]$JarPath
    )

    Assert-PathExists -Path $JarPath -Label "$Name Jar"
    $jarDir = Split-Path -Path $JarPath -Parent
    $cmdArgs = "/c title $Name && java -Dfile.encoding=utf-8 $JavaOpts -jar `"$JarPath`""
    Write-Host "启动 $Name ..."
    Start-Process -FilePath "cmd.exe" -ArgumentList $cmdArgs -WorkingDirectory $jarDir
}

Assert-PathExists -Path $RedisExe -Label "Redis"
Assert-PathExists -Path $RedisConfig -Label "Redis 配置"
Assert-PathExists -Path $NacosStartup -Label "Nacos"
Assert-PathExists -Path $MinioExe -Label "MinIO"
Assert-PathExists -Path $MinioDataDir -Label "MinIO 数据目录"

Start-ToolProcess -Name "Redis" -FilePath $RedisExe -Arguments "`"$RedisConfig`"" -WorkingDirectory (Split-Path $RedisExe -Parent)
Start-ToolProcess -Name "Nacos" -FilePath "cmd.exe" -Arguments "/c `"$NacosStartup`" -m standalone" -WorkingDirectory (Split-Path $NacosStartup -Parent)
Start-ToolProcess -Name "MinIO" -FilePath $MinioExe -Arguments "server `"$MinioDataDir`"" -WorkingDirectory (Split-Path $MinioExe -Parent)

Write-Host "等待基础设施启动 10 秒 ..."
Start-Sleep -Seconds 10

foreach ($service in $Services) {
    Start-JarService -Name $service.Name -JarPath $service.Jar
    Start-Sleep -Seconds 2
}

if ($StartUi) {
    Assert-PathExists -Path $UiDir -Label "前端目录"
    Write-Host "启动 UI ..."
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c pnpm run dev" -WorkingDirectory $UiDir
}

