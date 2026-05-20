param(
    [int]$Port = 42069
)

$ErrorActionPreference = "Stop"

function Send-RawHttpRequest {
    param(
        [string]$HostName,
        [int]$Port,
        [string]$RequestText
    )

    $client = [System.Net.Sockets.TcpClient]::new($HostName, $Port)
    try {
        $stream = $client.GetStream()
        try {
            $requestBytes = [System.Text.Encoding]::ASCII.GetBytes($RequestText)
            $stream.Write($requestBytes, 0, $requestBytes.Length)
            $stream.Flush()

            $buffer = New-Object byte[] 4096
            $response = New-Object System.Text.StringBuilder

            while (($read = $stream.Read($buffer, 0, $buffer.Length)) -gt 0) {
                [void]$response.Append([System.Text.Encoding]::ASCII.GetString($buffer, 0, $read))
            }

            return $response.ToString()
        }
        finally {
            $stream.Close()
        }
    }
    finally {
        $client.Close()
    }
}

function Write-HttpExchange {
    param(
        [string]$Name,
        [string]$RequestText,
        [string]$ResponseText
    )

    Write-Host ""
    Write-Host "=== $Name ==="
    Write-Host "--- Request ---"
    Write-Host $RequestText
    Write-Host "--- Response ---"
    Write-Host $ResponseText
}

function Assert-Contains {
    param(
        [string]$Haystack,
        [string]$Needle,
        [string]$Message
    )

    if (-not $Haystack.Contains($Needle)) {
        throw "Assertion failed: $Message`nExpected to find: $Needle`nActual response:`n$Haystack"
    }
}

$tests = @(
    @{
        Name = "root"
        Request = "GET / HTTP/1.1`r`nHost: localhost`r`nConnection: close`r`n`r`n"
        Expect = @("HTTP/1.1 200", "<h1>Hello World!</h1>")
    },
    @{
        Name = "yourproblem"
        Request = "GET /yourproblem HTTP/1.1`r`nHost: localhost`r`nConnection: close`r`n`r`n"
        Expect = @("HTTP/1.1 400", "<h1>400 - Your Problem</h1>")
    },
    @{
        Name = "myproblem"
        Request = "GET /myproblem HTTP/1.1`r`nHost: localhost`r`nConnection: close`r`n`r`n"
        Expect = @("HTTP/1.1 500", "<h1>500 - My Problem</h1>")
    }
)

foreach ($test in $tests) {
    $response = Send-RawHttpRequest -HostName "127.0.0.1" -Port $Port -RequestText $test.Request
    Write-HttpExchange -Name $test.Name -RequestText $test.Request -ResponseText $response
    foreach ($expected in $test.Expect) {
        Assert-Contains -Haystack $response -Needle $expected -Message "Test '$($test.Name)' failed"
    }
    Write-Host "PASS $($test.Name)"
}

Write-Host "All tests passed."
