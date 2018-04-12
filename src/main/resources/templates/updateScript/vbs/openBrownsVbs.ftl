dim Wsh
Set Wsh=WScript.CreateObject("WScript.Shell")
On error Resume Next
Wsh.Run "http://localhost:8080"