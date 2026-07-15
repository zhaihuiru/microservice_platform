#requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Net.Http

$httpClientVariable = Get-Variable -Name HttpClient -Scope Script -ErrorAction SilentlyContinue
if ($null -eq $httpClientVariable -or $null -eq $httpClientVariable.Value) {
    $script:HttpClient = New-Object System.Net.Http.HttpClient
    $script:HttpClient.Timeout = [TimeSpan]::FromSeconds(20)
}

$script:Results = @()

function New-RequestId {
    return [Guid]::NewGuid().ToString()
}

function Invoke-JsonRequest {
    param(
        [Parameter(Mandatory=$true)][string]$Method,
        [Parameter(Mandatory=$true)][string]$Uri,
        [string]$Token,
        [object]$Body,
        [hashtable]$Headers = @{}
    )

    $httpMethod = New-Object System.Net.Http.HttpMethod($Method.ToUpperInvariant())
    $request = New-Object System.Net.Http.HttpRequestMessage($httpMethod, $Uri)
    $request.Headers.TryAddWithoutValidation("X-Request-Id", (New-RequestId)) | Out-Null

    if ($Token) {
        $request.Headers.TryAddWithoutValidation("Authorization", "Bearer $Token") | Out-Null
    }

    foreach ($key in $Headers.Keys) {
        $request.Headers.Remove([string]$key) | Out-Null
        $request.Headers.TryAddWithoutValidation([string]$key, [string]$Headers[$key]) | Out-Null
    }

    if ($null -ne $Body) {
        $jsonBody = $Body | ConvertTo-Json -Depth 20 -Compress
        $request.Content = New-Object System.Net.Http.StringContent(
            $jsonBody,
            [Text.Encoding]::UTF8,
            "application/json"
        )
    }

    try {
        $response = $script:HttpClient.SendAsync($request).GetAwaiter().GetResult()
        $raw = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        $json = $null
        if ($raw -and $raw.Trim().Length -gt 0) {
            try { $json = $raw | ConvertFrom-Json } catch { $json = $null }
        }

        return [pscustomobject]@{
            Status = [int]$response.StatusCode
            IsSuccessStatus = $response.IsSuccessStatusCode
            Raw = $raw
            Json = $json
            RequestId = if ($response.Headers.Contains("X-Request-Id")) {
                ($response.Headers.GetValues("X-Request-Id") -join ",")
            } else { "" }
        }
    }
    catch {
        return [pscustomobject]@{
            Status = 0
            IsSuccessStatus = $false
            Raw = $_.Exception.Message
            Json = $null
            RequestId = ""
        }
    }
    finally {
        $request.Dispose()
    }
}

function Get-ApiCode {
    param($Response)
    if ($null -ne $Response.Json -and
        $Response.Json.PSObject.Properties.Name -contains "code") {
        return [int]$Response.Json.code
    }
    return $null
}

function Get-ApiData {
    param($Response)
    if ($null -ne $Response.Json -and
        $Response.Json.PSObject.Properties.Name -contains "data") {
        return $Response.Json.data
    }
    return $null
}

function Add-TestResult {
    param(
        [string]$Id,
        [string]$Category,
        [string]$Name,
        [string]$Expected,
        [string]$Actual,
        [bool]$Passed,
        [string]$Evidence = ""
    )

    $status = if ($Passed) { "PASS" } else { "FAIL" }
    $script:Results += [pscustomobject]@{
        Id = $Id
        Category = $Category
        Name = $Name
        Expected = $Expected
        Actual = $Actual
        Result = $status
        Evidence = $Evidence
        Time = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")
    }

    $symbol = if ($Passed) { "[PASS]" } else { "[FAIL]" }
    Write-Host "$symbol $Id $Name" -ForegroundColor $(if ($Passed) { "Green" } else { "Red" })
    if (-not $Passed -and $Evidence) {
        Write-Host "       $Evidence" -ForegroundColor Yellow
    }
}

function Assert-HttpStatus {
    param(
        [string]$Id,
        [string]$Category,
        [string]$Name,
        $Response,
        [int]$ExpectedStatus
    )
    $passed = $Response.Status -eq $ExpectedStatus
    Add-TestResult $Id $Category $Name "HTTP $ExpectedStatus" "HTTP $($Response.Status)" $passed $Response.Raw
    return $passed
}

function Assert-ApiCode {
    param(
        [string]$Id,
        [string]$Category,
        [string]$Name,
        $Response,
        [int]$ExpectedCode
    )
    $actualCode = Get-ApiCode $Response
    $passed = $actualCode -eq $ExpectedCode
    Add-TestResult $Id $Category $Name "ApiResponse.code=$ExpectedCode" "HTTP=$($Response.Status), code=$actualCode" $passed $Response.Raw
    return $passed
}

function Assert-Condition {
    param(
        [string]$Id,
        [string]$Category,
        [string]$Name,
        [bool]$Condition,
        [string]$Expected,
        [string]$Actual,
        [string]$Evidence = ""
    )
    Add-TestResult $Id $Category $Name $Expected $Actual $Condition $Evidence
    return $Condition
}

function Register-TestUser {
    param(
        [string]$BaseUrl,
        [string]$Username,
        [string]$Email,
        [string]$Password,
        [string]$Nickname
    )
    return Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/auth/register" -Body @{
        username = $Username
        email = $Email
        password = $Password
        nickname = $Nickname
    }
}

function Login-TestUser {
    param(
        [string]$BaseUrl,
        [string]$Username,
        [string]$Password
    )
    return Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/auth/login" -Body @{
        username = $Username
        password = $Password
    }
}

function Export-TestResults {
    param(
        [Parameter(Mandatory=$true)][string]$OutputDirectory,
        [Parameter(Mandatory=$true)][string]$BaseName
    )

    New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
    $csv = Join-Path $OutputDirectory ($BaseName + ".csv")
    $md = Join-Path $OutputDirectory ($BaseName + ".md")

    $script:Results | Export-Csv -Path $csv -NoTypeInformation -Encoding UTF8

    $pass = ($script:Results | Where-Object { $_.Result -eq "PASS" } | Measure-Object).Count
    $fail = ($script:Results | Where-Object { $_.Result -eq "FAIL" } | Measure-Object).Count
    $total = ($script:Results | Measure-Object).Count

    $lines = @()
    $lines += "# Test Results"
    $lines += ""
    $lines += ("- Total: " + [string]$total)
    $lines += ("- Passed: " + [string]$pass)
    $lines += ("- Failed: " + [string]$fail)
    $lines += ""
    $lines += "| ID | Category | Test | Expected | Actual | Result |"
    $lines += "|---|---|---|---|---|---|"

    foreach ($r in $script:Results) {
        $e = ([string]$r.Expected).Replace("|", "\|").Replace("`r", " ").Replace("`n", " ")
        $a = ([string]$r.Actual).Replace("|", "\|").Replace("`r", " ").Replace("`n", " ")
        $n = ([string]$r.Name).Replace("|", "\|")
        $row = "| " + [string]$r.Id + " | " + [string]$r.Category + " | " + $n + " | " + $e + " | " + $a + " | " + [string]$r.Result + " |"
        $lines += $row
    }

    $lines | Set-Content -Path $md -Encoding UTF8

    Write-Host ""
    Write-Host ("Test results: total=" + [string]$total + ", passed=" + [string]$pass + ", failed=" + [string]$fail) -ForegroundColor Cyan
    Write-Host ("CSV: " + $csv)
    Write-Host ("Markdown: " + $md)
}

function Invoke-DbScalar {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Sql,

        [string]$Container = "anime-mysql",

        [string]$Password = "123456"
    )

    $oldErrorActionPreference = $ErrorActionPreference
    $queryOutput = @()
    $queryExitCode = -1

    try {
        $ErrorActionPreference = "Continue"

        $queryOutput = & docker exec `
            -e "MYSQL_PWD=$Password" `
            $Container `
            mysql `
            -N `
            -s `
            -uroot `
            -e $Sql 2>&1

        $queryExitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $oldErrorActionPreference
    }

    if ($queryExitCode -ne 0) {
        $errorText = $queryOutput -join " "
        throw ("Database query failed: " + $errorText)
    }

    $cleanOutput = @(
        $queryOutput |
        Where-Object {
            ([string]$_) -notmatch "^mysql: \[Warning\]"
        }
    )

    if ($cleanOutput.Count -eq 0) {
        return ""
    }

    return ([string]($cleanOutput | Select-Object -First 1)).Trim()
}

function Invoke-Compose {
    param(
        [Parameter(Mandatory=$true)][string]$ProjectRoot,
        [Parameter(ValueFromRemainingArguments=$true)][string[]]$ComposeArgs
    )

    $infra = Join-Path $ProjectRoot "deploy/docker-compose.yml"
    $app = Join-Path $ProjectRoot "deploy/docker-compose-app.yml"

    $oldErrorActionPreference = $ErrorActionPreference
    try {
        # Docker Compose会把普通warning写到stderr。PowerShell 5.1在Stop模式下
        # 可能把该warning当成终止错误，因此执行原生命令时暂时改为Continue。
        $ErrorActionPreference = "Continue"
        $composeOutput = & docker compose -f $infra -f $app @ComposeArgs 2>&1
        $composeExitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $oldErrorActionPreference
    }

    $filteredOutput = @($composeOutput | Where-Object {
        ([string]$_) -notmatch 'attribute `version` is obsolete'
    })
    if ($filteredOutput.Count -gt 0) {
        $filteredOutput | ForEach-Object { Write-Host ([string]$_) }
    }

    if ($composeExitCode -ne 0) {
        throw ("docker compose command failed: " + ($ComposeArgs -join " ") + "; output: " + ($composeOutput -join " "))
    }
}

function Wait-Api {
    param(
        [Parameter(Mandatory=$true)][string]$Uri,
        [int]$TimeoutSeconds = 90,
        [bool]$ShouldBeAvailable = $true
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        $r = Invoke-JsonRequest -Method GET -Uri $Uri
        $available = ($r.Status -ge 200 -and $r.Status -lt 500)
        if ($ShouldBeAvailable -and $available) { return $true }
        if (-not $ShouldBeAvailable -and -not $available) { return $true }
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)

    return $false
}
