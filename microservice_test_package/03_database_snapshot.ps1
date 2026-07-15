#requires -Version 5.1
param(
    [string]$OutputDirectory = ""
)
. "$PSScriptRoot/TestCommon.ps1"
if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $PSScriptRoot "output"
}
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$queries = [ordered]@{
    "auth_users" = "SELECT COUNT(*) FROM auth_db.auth_user;"
    "works" = "SELECT COUNT(*) FROM work_db.works WHERE is_deleted=0;"
    "characters" = "SELECT COUNT(*) FROM character_db.characters WHERE is_deleted=0;"
    "persons" = "SELECT COUNT(*) FROM person_db.persons WHERE is_deleted=0;"
    "ratings" = "SELECT COUNT(*) FROM rating_db.rating_score;"
    "votes" = "SELECT COUNT(*) FROM rating_db.rating_vote_record;"
    "comments" = "SELECT COUNT(*) FROM comment_db.comment_comment;"
    "comment_likes" = "SELECT COUNT(*) FROM comment_db.comment_like;"
    "favorite_folders" = "SELECT COUNT(*) FROM favorite_db.fav_folder;"
    "favorite_items" = "SELECT COUNT(*) FROM favorite_db.fav_item;"
    "notifications" = "SELECT COUNT(*) FROM notification_db.notice_message WHERE deleted=0;"
    "user_notifications" = "SELECT COUNT(*) FROM notification_db.notice_user_message WHERE deleted=0;"
    "chat_conversations" = "SELECT COUNT(*) FROM chat_db.chat_conversation;"
    "chat_messages" = "SELECT COUNT(*) FROM chat_db.chat_message WHERE deleted=0;"
}

$rows = foreach ($name in $queries.Keys) {
    [pscustomobject]@{
        Metric = $name
        Count = Invoke-DbScalar $queries[$name]
        Time = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")
    }
}

$csv = Join-Path $OutputDirectory "03-database-snapshot.csv"
$rows | Export-Csv -Path $csv -NoTypeInformation -Encoding UTF8
$rows | Format-Table -AutoSize
Write-Host "数据库快照已保存：$csv"
