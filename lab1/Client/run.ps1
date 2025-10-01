param(
    [int]$N = 5
)

for ($i = 1; $i -le $N; $i++) {
    $name = "Client" + $i
    Write-Host "Starting $name"
    Start-Process -NoNewWindow -FilePath "gradlew.bat" -ArgumentList "run --args=$name"
}
