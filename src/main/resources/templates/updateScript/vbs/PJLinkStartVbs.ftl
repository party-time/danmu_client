set WshShell = WScript.CreateObject("WScript.Shell")
WScript.Sleep 2000
WshShell.run "taskkill /f /im IEU_Lite.exe",vbhide

WScript.Sleep 5000
WshShell.run "D:\IEU_Lite(removable-media)\IEU_Lite.exe"
WScript.Sleep 10000
WshShell.SendKeys "%R"



WScript.Sleep 10000
WshShell.SendKeys "{ENTER}"
