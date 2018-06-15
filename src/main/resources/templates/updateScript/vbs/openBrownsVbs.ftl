dim Wsh
Set Wsh=WScript.CreateObject("WScript.Shell")
On error Resume Next

'Wsh.Run "taskkill /f  /t /im chrome.exe"

'Wsh.Run "taskkill /f  /t /im iexplorer.exe"

'Wsh.Run "taskkill /f  /t /im firefox.exe"

Wsh.Run "http://localhost:8080"