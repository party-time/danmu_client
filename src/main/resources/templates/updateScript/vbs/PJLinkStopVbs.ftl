set WshShell = WScript.CreateObject("WScript.Shell")
WshShell.run "taskkill /f /im IEU_Lite.exe",vbhide