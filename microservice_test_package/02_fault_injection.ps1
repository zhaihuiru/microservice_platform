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
$contextPath = Join-Path $output "test-context.json"

if (-not (Test-Path $contextPath)) {
    throw "Missing output/test-context.json. Run 01_functional_and_idempotency.ps1 first."
}

$ctx = Get-Content $contextPath -Raw | ConvertFrom-Json

$adminToken = [string]$ctx.adminToken
$user1Token = [string]$ctx.user1Token
$user2Id = [long]$ctx.user2Id
$folderId = [long]$ctx.folderId
$conversationId = [long]$ctx.conversationId
$suffix = Get-Date -Format "MMddHHmmss"

$stopped = @()

function Mark-Stopped {
    param([string]$Service)
    if ($stopped -notcontains $Service) {
        $script:stopped += $Service
    }
}

function Mark-Started {
    param([string]$Service)
    $script:stopped = @($script:stopped | Where-Object { $_ -ne $Service })
}

try {
    Write-Host ""
    Write-Host "=== Fault scenario 1: work-service unavailable ===" -ForegroundColor Cyan

    Invoke-Compose -ProjectRoot $ProjectRoot stop work-service
    Mark-Stopped "work-service"

    $workDown = Wait-Api -Uri "$BaseUrl/api/works/1" -TimeoutSeconds 60 -ShouldBeAvailable $false
    Assert-Condition "GS-01" "Fault injection" "work-service becomes unavailable" $workDown "Unavailable" ([string]$workDown) | Out-Null

    $commentFail = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/comments" -Token $user1Token -Body @{
        workId = 1
        content = "FAULT_WORK_DOWN_$suffix"
    }
    $commentBlocked = ((Get-ApiCode $commentFail) -ne 200)
    Assert-Condition "GS-02" "Fault isolation" "Comment creation is blocked while work-service is down" $commentBlocked "Non-200 business result" "HTTP=$($commentFail.Status), code=$(Get-ApiCode $commentFail)" $commentFail.Raw | Out-Null

    if ($folderId -gt 0) {
        $favoriteFail = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/favorites" -Token $user1Token -Body @{
            folderId = $folderId
            workId = 1
        }
        $favoriteBlocked = ((Get-ApiCode $favoriteFail) -ne 200)
        Assert-Condition "GS-03" "Fault isolation" "Favorite creation is blocked while work-service is down" $favoriteBlocked "Non-200 business result" "HTTP=$($favoriteFail.Status), code=$(Get-ApiCode $favoriteFail)" $favoriteFail.Raw | Out-Null
    }

    Invoke-Compose -ProjectRoot $ProjectRoot start work-service
    Mark-Started "work-service"

    $workRecovered = Wait-Api -Uri "$BaseUrl/api/works/1" -TimeoutSeconds 120 -ShouldBeAvailable $true
    Assert-Condition "GS-04" "Recovery" "work-service is rediscovered after restart" $workRecovered "Available" ([string]$workRecovered) | Out-Null

    $commentAfter = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/comments" -Token $user1Token -Body @{
        workId = 1
        content = "FAULT_WORK_RECOVERED_$suffix"
    }
    Assert-ApiCode "GS-05" "Recovery" "Comment cross-service validation works after recovery" $commentAfter 200 | Out-Null

    Write-Host ""
    Write-Host "=== Fault scenario 2: auth-service unavailable ===" -ForegroundColor Cyan

    Invoke-Compose -ProjectRoot $ProjectRoot stop auth-service
    Mark-Stopped "auth-service"

    $authDown = Wait-Api -Uri "$BaseUrl/api/auth/ping" -TimeoutSeconds 60 -ShouldBeAvailable $false
    Assert-Condition "GS-10" "Fault injection" "auth-service becomes unavailable" $authDown "Unavailable" ([string]$authDown) | Out-Null

    $broadcastFail = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/notifications/admin/broadcast" -Token $adminToken -Body @{
        title = "FAULT_AUTH_DOWN_$suffix"
        content = "Broadcast while auth-service is unavailable"
        noticeType = "SYSTEM"
        targetType = "SYSTEM"
        targetId = $null
    }
    $broadcastBlocked = ((Get-ApiCode $broadcastFail) -ne 200)
    Assert-Condition "GS-11" "Fallback" "Broadcast fails in a controlled way while auth-service is down" $broadcastBlocked "Non-200 business result" "HTTP=$($broadcastFail.Status), code=$(Get-ApiCode $broadcastFail)" $broadcastFail.Raw | Out-Null

    Invoke-Compose -ProjectRoot $ProjectRoot start auth-service
    Mark-Started "auth-service"

    $authRecovered = Wait-Api -Uri "$BaseUrl/api/auth/ping" -TimeoutSeconds 120 -ShouldBeAvailable $true
    Assert-Condition "GS-12" "Recovery" "auth-service is rediscovered after restart" $authRecovered "Available" ([string]$authRecovered) | Out-Null

    $broadcastAfter = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/notifications/admin/broadcast" -Token $adminToken -Body @{
        title = "FAULT_AUTH_RECOVERED_$suffix"
        content = "Broadcast after auth-service recovery"
        noticeType = "SYSTEM"
        targetType = "SYSTEM"
        targetId = $null
    }
    Assert-ApiCode "GS-13" "Recovery" "Broadcast succeeds after auth-service recovery" $broadcastAfter 200 | Out-Null

    Write-Host ""
    Write-Host "=== Fault scenario 3: notification-service unavailable ===" -ForegroundColor Cyan

    if ($conversationId -gt 0) {
        Invoke-Compose -ProjectRoot $ProjectRoot stop notification-service
        Mark-Stopped "notification-service"

        $noticeDown = Wait-Api -Uri "$BaseUrl/api/notifications/ping" -TimeoutSeconds 60 -ShouldBeAvailable $false
        Assert-Condition "GS-20" "Fault injection" "notification-service becomes unavailable" $noticeDown "Unavailable" ([string]$noticeDown) | Out-Null

        $messageDown = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/chats/conversations/$conversationId/messages" -Token $user1Token -Body @{
            clientMessageId = "fault-notice-down-$suffix"
            messageType = "TEXT"
            content = "Chat message while notification-service is unavailable"
            mediaUrl = $null
            replyToMessageId = $null
            mentionUserIds = @($user2Id)
        }
        Assert-ApiCode "GS-21" "Degradation" "Chat main flow succeeds while notification-service is down" $messageDown 200 | Out-Null

        Invoke-Compose -ProjectRoot $ProjectRoot start notification-service
        Mark-Started "notification-service"

        $noticeRecovered = Wait-Api -Uri "$BaseUrl/api/notifications/ping" -TimeoutSeconds 120 -ShouldBeAvailable $true
        Assert-Condition "GS-22" "Recovery" "notification-service is rediscovered after restart" $noticeRecovered "Available" ([string]$noticeRecovered) | Out-Null

        $messageAfter = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/chats/conversations/$conversationId/messages" -Token $user1Token -Body @{
            clientMessageId = "fault-notice-recovered-$suffix"
            messageType = "TEXT"
            content = "Chat message after notification-service recovery"
            mediaUrl = $null
            replyToMessageId = $null
            mentionUserIds = @($user2Id)
        }
        Assert-ApiCode "GS-23" "Recovery" "Chat flow succeeds after notification-service recovery" $messageAfter 200 | Out-Null
    }
    else {
        Add-TestResult "GS-20" "Fault injection" "Notification fault scenario" "conversationId > 0" "Skipped" $false "No conversation ID in test-context.json"
    }
}
finally {
    foreach ($service in @($stopped)) {
        try {
            Write-Host ("Restoring service: " + $service) -ForegroundColor Yellow
            Invoke-Compose -ProjectRoot $ProjectRoot start $service
        }
        catch {
            Write-Warning ("Automatic recovery failed for " + $service)
        }
    }
}

try {
    $infra = Join-Path $ProjectRoot "deploy/docker-compose.yml"
    $app = Join-Path $ProjectRoot "deploy/docker-compose-app.yml"
    $oldPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = "Continue"
        $psOutput = & docker compose -f $infra -f $app ps 2>&1
    }
    finally {
        $ErrorActionPreference = $oldPreference
    }
    ($psOutput -join [Environment]::NewLine) | Set-Content -Path (Join-Path $output "docker-compose-ps-after-service-fault.txt") -Encoding UTF8
}
catch {
}

Export-TestResults -OutputDirectory $output -BaseName "02-service-governance-fault-results"
