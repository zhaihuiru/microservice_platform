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
New-Item -ItemType Directory -Force -Path $output | Out-Null

$suffix = Get-Date -Format "MMddHHmmss"
$password = "Test123456"
$user1Name = "report_u1_$suffix"
$user2Name = "report_u2_$suffix"
$user1Email = "$user1Name@example.com"
$user2Email = "$user2Name@example.com"

Write-Host "Creating test users: $user1Name / $user2Name"

$adminLogin = Login-TestUser $BaseUrl "admin" "admin123456"
Assert-ApiCode "F-01" "Auth" "Admin login" $adminLogin 200 | Out-Null
$adminData = Get-ApiData $adminLogin
$adminToken = if ($adminData) { [string]$adminData.token } else { "" }

$reg1 = Register-TestUser $BaseUrl $user1Name $user1Email $password "Report Test User 1"
Assert-ApiCode "F-02" "Auth" "Register normal user 1" $reg1 200 | Out-Null
$reg2 = Register-TestUser $BaseUrl $user2Name $user2Email $password "Report Test User 2"
Assert-ApiCode "F-03" "Auth" "Register normal user 2" $reg2 200 | Out-Null

$login1 = Login-TestUser $BaseUrl $user1Name $password
Assert-ApiCode "F-04" "Auth" "Normal user 1 login" $login1 200 | Out-Null
$login2 = Login-TestUser $BaseUrl $user2Name $password
Assert-ApiCode "F-05" "Auth" "Normal user 2 login" $login2 200 | Out-Null

$user1Data = Get-ApiData $login1
$user2Data = Get-ApiData $login2
$user1Token = if ($user1Data) { [string]$user1Data.token } else { "" }
$user2Token = if ($user2Data) { [string]$user2Data.token } else { "" }
$user1Id = if ($user1Data) { [long]$user1Data.userId } else { 0 }
$user2Id = if ($user2Data) { [long]$user2Data.userId } else { 0 }

# Authentication and authorization
$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/users/me"
Assert-HttpStatus "F-06" "Authorization" "Access personal API without token" $r 401 | Out-Null

$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/users/me" -Token "invalid.token.value"
Assert-HttpStatus "F-07" "Authorization" "Access personal API with invalid token" $r 401 | Out-Null

$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/admin/users" -Token $user1Token
Assert-HttpStatus "F-08" "Authorization" "Normal user accesses admin API" $r 403 | Out-Null

$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/comments/admin/pending" -Token $user1Token -Headers @{"User-Role"="ADMIN"}
Assert-ApiCode "F-09" "Authorization" "Forged admin header cannot elevate privilege" $r 403 | Out-Null

$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/admin/users?page=0&size=10" -Token $adminToken
Assert-ApiCode "F-10" "Authorization" "Admin queries user list" $r 200 | Out-Null

# Content management and cross-service queries
$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/works/1"
Assert-ApiCode "F-11" "Content" "Query work" $r 200 | Out-Null

$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/characters/1/detail"
Assert-ApiCode "F-12" "Cross-service" "Character detail aggregates work and person" $r 200 | Out-Null

$r = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/persons/1/detail"
Assert-ApiCode "F-13" "Cross-service" "Person detail aggregates work and character" $r 200 | Out-Null

$newWork = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/works" -Token $adminToken -Body @{
    title = "AUTO_TEST_WORK_$suffix"
    coverUrl = ""
    description = "Verify Gateway and work-service admin write operation"
    releaseDate = "2026-07-14"
    status = "TESTING"
}
$createWorkOk = Assert-ApiCode "F-14" "Content" "Admin creates work through Gateway" $newWork 200
if ($createWorkOk) {
    $workData = Get-ApiData $newWork
    $newWorkId = [string]$workData.id
    $delWork = Invoke-JsonRequest -Method DELETE -Uri "$BaseUrl/api/works/$newWorkId" -Token $adminToken
    Assert-ApiCode "F-15" "Content" "Admin deletes test work through Gateway" $delWork 200 | Out-Null
} else {
    Add-TestResult "F-15" "Content" "Admin deletes test work through Gateway" "Delete after successful creation" "Skipped because F-14 failed" $false "Fix Gateway public-path rules by HTTP method first"
}

# Rating idempotency: second request updates the same row
$rating1 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/ratings" -Token $user1Token -Body @{workId=1; score=6}
Assert-ApiCode "F-20" "Interaction" "Submit rating first time" $rating1 200 | Out-Null
$rating2 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/ratings" -Token $user1Token -Body @{workId=1; score=9}
Assert-ApiCode "F-21" "Idempotency" "Repeated rating updates original record" $rating2 200 | Out-Null

$ratingData1 = Get-ApiData $rating1
$ratingData2 = Get-ApiData $rating2
$sameRatingId = ($ratingData1 -and $ratingData2 -and ([string]$ratingData1.id -eq [string]$ratingData2.id))
Assert-Condition "F-22" "Idempotency" "Two rating responses have the same ID" $sameRatingId "Same ID" "first=$($ratingData1.id), second=$($ratingData2.id)" | Out-Null

$currentRating = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/ratings/work/1" -Token $user1Token
$crData = Get-ApiData $currentRating
Assert-Condition "F-23" "Idempotency" "Final score equals second submitted value" ($crData -and [int]$crData.score -eq 9) "score=9" "score=$($crData.score)" $currentRating.Raw | Out-Null

# Vote duplicate prevention
$topicId = [long]("99" + (Get-Date -Format "MMddHHmmss"))
$vote1 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/votes" -Token $user1Token -Body @{topicId=$topicId; targetId=1}
Assert-ApiCode "F-24" "Interaction" "Submit vote first time" $vote1 200 | Out-Null
$vote2 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/votes" -Token $user1Token -Body @{topicId=$topicId; targetId=2}
Assert-ApiCode "F-25" "Idempotency" "Repeated vote for same topic is rejected" $vote2 400 | Out-Null

# Comment, moderation, and like toggle
$commentText = "REPORT_COMMENT_$suffix"
$comment = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/comments" -Token $user1Token -Body @{workId=1; content=$commentText}
Assert-ApiCode "F-30" "Interaction" "Create comment with cross-service work validation" $comment 200 | Out-Null
$commentData = Get-ApiData $comment
$commentId = if ($commentData) { [long]$commentData.id } else { 0 }

if ($commentId -gt 0) {
    $audit = Invoke-JsonRequest -Method PUT -Uri "$BaseUrl/api/comments/admin/$commentId/status" -Token $adminToken -Body @{status="APPROVED"}
    Assert-ApiCode "F-31" "Moderation" "Admin approves comment" $audit 200 | Out-Null

    $publicComments = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/comments/work/1" -Token $user1Token
    $pcData = Get-ApiData $publicComments
    $found = $false
    if ($pcData) {
        foreach ($item in $pcData) {
            if ([string]$item.id -eq [string]$commentId) { $found = $true }
        }
    }
    Assert-Condition "F-32" "Interaction" "Approved comment is visible" $found "Comment list contains $commentId" ([string]$found) $publicComments.Raw | Out-Null

    $like1 = Invoke-JsonRequest -Method PUT -Uri "$BaseUrl/api/comments/$commentId/like" -Token $user2Token
    Assert-ApiCode "F-33" "Interaction" "Like comment first time" $like1 200 | Out-Null
    $like2 = Invoke-JsonRequest -Method PUT -Uri "$BaseUrl/api/comments/$commentId/like" -Token $user2Token
    Assert-ApiCode "F-34" "Duplicate prevention" "Second action removes like" $like2 200 | Out-Null
}

# Favorite and duplicate prevention
$folder = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/favorites/folders" -Token $user1Token -Body @{
    folderName = "AUTO_TEST_FOLDER_$suffix"
    isPublic = $false
}
Assert-ApiCode "F-40" "Interaction" "Create favorite folder" $folder 200 | Out-Null
$folderData = Get-ApiData $folder
$folderId = if ($folderData) { [long]$folderData.id } else { 0 }

if ($folderId -gt 0) {
    $fav1 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/favorites" -Token $user1Token -Body @{folderId=$folderId; workId=1}
    Assert-ApiCode "F-41" "Cross-service" "Validate work before adding favorite" $fav1 200 | Out-Null
    $fav2 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/favorites" -Token $user1Token -Body @{folderId=$folderId; workId=1}
    Assert-ApiCode "F-42" "Idempotency" "Repeated favorite is rejected" $fav2 400 | Out-Null
}

# Chat and message idempotency
$conv = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/chats/conversations/private" -Token $user1Token -Body @{peerUserId=$user2Id}
Assert-ApiCode "F-50" "Chat" "Create private conversation" $conv 200 | Out-Null
$convData = Get-ApiData $conv
$conversationId = if ($convData) { [long]$convData.id } else { 0 }

$clientMessageId = "report-msg-$suffix"
if ($conversationId -gt 0) {
    $msgBody = @{
        clientMessageId = $clientMessageId
        messageType = "TEXT"
        content = "Automated report test message"
        mediaUrl = $null
        replyToMessageId = $null
        mentionUserIds = @()
    }
    $msg1 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/chats/conversations/$conversationId/messages" -Token $user1Token -Body $msgBody
    Assert-ApiCode "F-51" "Chat" "Send chat message first time" $msg1 200 | Out-Null
    $msg2 = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/chats/conversations/$conversationId/messages" -Token $user1Token -Body $msgBody
    Assert-ApiCode "F-52" "Idempotency" "Repeat send with same clientMessageId" $msg2 200 | Out-Null
    $m1 = Get-ApiData $msg1
    $m2 = Get-ApiData $msg2
    Assert-Condition "F-53" "Idempotency" "Repeated message returns same message ID" ($m1 -and $m2 -and [string]$m1.id -eq [string]$m2.id) "Same ID" "first=$($m1.id), second=$($m2.id)" | Out-Null
}

# Notification basic functions
$noticeTitle = "AUTO_TEST_NOTICE_$suffix"
$notice = Invoke-JsonRequest -Method POST -Uri "$BaseUrl/api/notifications/admin/users/$user2Id" -Token $adminToken -Body @{
    receiverId = $user2Id
    senderId = $null
    title = $noticeTitle
    content = "Notification function automated test"
    noticeType = "SYSTEM"
    targetType = "SYSTEM"
    targetId = $null
}
Assert-ApiCode "F-60" "Notification" "Admin sends targeted notification" $notice 200 | Out-Null

$myNotices = Invoke-JsonRequest -Method GET -Uri "$BaseUrl/api/notifications/my?page=0&size=20" -Token $user2Token
Assert-ApiCode "F-61" "Notification" "User queries own notifications" $myNotices 200 | Out-Null

# Database-level verification
try {
    $ratingCount = [int](Invoke-DbScalar "SELECT COUNT(*) FROM rating_db.rating_score WHERE user_id=$user1Id AND work_id=1;")
    Assert-Condition "F-70" "Database" "Repeated rating keeps one row" ($ratingCount -eq 1) "count=1" "count=$ratingCount" | Out-Null

    $voteCount = [int](Invoke-DbScalar "SELECT COUNT(*) FROM rating_db.rating_vote_record WHERE user_id=$user1Id AND topic_id=$topicId;")
    Assert-Condition "F-71" "Database" "Repeated vote keeps one row" ($voteCount -eq 1) "count=1" "count=$voteCount" | Out-Null

    if ($folderId -gt 0) {
        $favCount = [int](Invoke-DbScalar "SELECT COUNT(*) FROM favorite_db.fav_item WHERE folder_id=$folderId AND work_id=1;")
        Assert-Condition "F-72" "Database" "Repeated favorite keeps one row" ($favCount -eq 1) "count=1" "count=$favCount" | Out-Null
    }

    if ($conversationId -gt 0) {
        $msgCount = [int](Invoke-DbScalar "SELECT COUNT(*) FROM chat_db.chat_message WHERE conversation_id=$conversationId AND sender_id=$user1Id AND client_message_id='$clientMessageId';")
        Assert-Condition "F-73" "Database" "Repeated chat message keeps one row" ($msgCount -eq 1) "count=1" "count=$msgCount" | Out-Null
    }
}
catch {
    Add-TestResult "F-79" "Database" "Database consistency verification" "Query succeeds" "Query failed" $false $_.Exception.Message
}

$context = [pscustomobject]@{
    baseUrl = $BaseUrl
    adminToken = $adminToken
    user1Token = $user1Token
    user2Token = $user2Token
    user1Id = $user1Id
    user2Id = $user2Id
    folderId = $folderId
    conversationId = $conversationId
    suffix = $suffix
}
$context | ConvertTo-Json -Depth 10 | Set-Content -Path (Join-Path $output "test-context.json") -Encoding UTF8

Export-TestResults -OutputDirectory $output -BaseName "01-functional-results"
