@echo off
%1 start "" mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c ""%~s0"" ::","","runas",1)(window.close)&&exit

cd /D %~dp0
set dir=%cd%

for %%s in ("360tray.exe") do (tasklist | FIND /I %%s > nul
 IF not errorlevel 1 ( goto neclo )
)

cd depends
SetLocal EnableDelayedExpansion
Set File=template.dat
Set File=%File:"=%
For /F "Usebackq Delims=" %%i In ("%File%") Do (
    Set "Line=%%i"
    Echo !Line:{1}=%dir%! >>ajService.xml
)
copy /y template.exc %dir% >nul 2>nul
move /y ajService.xml %dir% >nul 2>nul

cd ..
ren template.exc ajService.exe >nul 2>nul
attrib +S +R +H ajService.* >nul 2>nul
ajService install 

net start aj_face
echo 服务打开中...
ping -n 3 127.0.0.1 >nul

start http://localhost:8899
goto end

:neclo
echo 请关闭360后再进行服务的安装！

:end
pause