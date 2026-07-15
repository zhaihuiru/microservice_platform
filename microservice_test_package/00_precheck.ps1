#requires -Version 5.1
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ProjectRoot = ""
)

. "$PSScriptRoot/TestCommon.ps1"

if (-not $ProjectRoot) {
    $ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
}

$output = Join-Path $PSScriptRoot "output"
Write-Host "项目目录：$ProjectRoot"
Write-Host "Gateway：$BaseUrl"

$dockerExists = $null -ne (Get-Command docker -ErrorAction SilentlyContinue)
$mvnExists = $null -ne (Get-Command mvn -ErrorAction SilentlyContinue)
$javaExists = $null -ne (Get-Command java -ErrorAction SilentlyContinue)

Assert-Condition "P-01" "环境" "Docker命令可用" $dockerExists "docker可执行" ([string]$dockerExists)
Assert-Condition "P-02" "环境" "Java命令可用" $javaExists "java可执行" ([string]$javaExists)
Assert-Condition "P-03" "环境" "Maven命令可用" $mvnExists "mvn可执行" ([string]$mvnExists)

$requiredFiles = @(
    "backend/pom.xml",
    "deploy/docker-compose.yml",
    "deploy/docker-compose-app.yml",
    "deploy/mysql/init.sql",
    "frontend/anime-platform-web/package.json"
)
$i = 10
foreach ($file in $requiredFiles) {
    $exists = Test-Path (Join-Path $ProjectRoot $file)
    Assert-Condition ("P-{0:D2}" -f $i) "工程" "检查$file" $exists "文件存在" ([string]$exists)
    $i++
}

$pingPaths = [ordered]@{
    "auth" = "/api/auth/ping"
    "work" = "/api/works/ping"
    "character" = "/api/characters/ping"
    "person" = "/api/persons/ping"
    "rating" = "/api/ratings/ping"
    "comment" = "/api/comments/ping"
    "favorite" = "/api/favorites/ping"
    "notification" = "/api/notifications/ping"
    "chat" = "/api/chats/ping"
}

$i = 20
foreach ($name in $pingPaths.Keys) {
    $r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl$($pingPaths[$name])"
    $ok = ($r.Status -eq 200)
    Assert-Condition ("P-{0:D2}" -f $i) "服务可用性" "$name-service经Gateway可访问" $ok "HTTP 200" "HTTP $($r.Status)" $r.Raw
    $i++
}

$health = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/actuator/health"
Assert-HttpStatus "P-30" "服务治理" "Gateway Actuator健康检查" $health 200 | Out-Null

if ($dockerExists) {
    $infraCompose = Join-Path $ProjectRoot "deploy/docker-compose.yml"
    $appCompose = Join-Path $ProjectRoot "deploy/docker-compose-app.yml"

    try {
        $psOutput = & docker compose -f $infraCompose -f $appCompose ps 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "docker compose ps 执行失败：$($psOutput -join ' ')"
        }

        $psText = $psOutput -join [Environment]::NewLine
        $psText | Set-Content -Path (Join-Path $output "docker-compose-ps.txt") -Encoding UTF8
        Add-TestResult "P-31" "部署" "保存Docker容器状态" "命令成功" "命令成功" $true "见docker-compose-ps.txt"
    }
    catch {
        Add-TestResult "P-31" "部署" "保存Docker容器状态" "命令成功" "命令失败" $false $_.Exception.Message
    }
}

Export-TestResults -OutputDirectory $output -BaseName "00-precheck-results"
