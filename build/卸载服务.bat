@echo off
%1 start "" mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c ""%~s0"" ::","","runas",1)(window.close)&&exit
cd /D %~dp0

net stop aj_face

ajService uninstall
attrib -S -R -H ajService.* >nul 2>nul
del ajService.* >nul 2>nul

pause
