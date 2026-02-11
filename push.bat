@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
cd /d "%~dp0"

set VERSION_FILE=version.txt
if not exist %VERSION_FILE% echo 1.0.2 > %VERSION_FILE%

set /p CURRENT_VER=<%VERSION_FILE%
set CURRENT_VER=%CURRENT_VER: =%
for /f "tokens=1,2,3 delims=." %%a in ("%CURRENT_VER%") do (
    set /a V1=%%a, V2=%%b, V3=%%c
)

set /a V3+=1
if %V3% GTR 9 ( set /a V3=0 & set /a V2+=1 )
if %V2% GTR 9 ( set /a V2=0 & set /a V1+=1 )
set NEXT_VER=V%V1%.%V2%.%V3%

echo ===========================================
echo   当前建议版本: %NEXT_VER%
echo ===========================================
set /p USER_DESC="请输入修改描述 (直接回车则仅使用版本号): "

if "%USER_DESC%"=="" (
    set "FINAL_MSG=%NEXT_VER%"
) else (
    echo %USER_DESC% | findstr /R "^V[0-9]" >nul
    if !errorlevel! equ 0 (
        set "FINAL_MSG=%USER_DESC%"
    ) else (
        set "FINAL_MSG=%NEXT_VER% %USER_DESC%"
    )
)

if "%USER_DESC%"=="" echo %V1%.%V2%.%V3% > %VERSION_FILE%

echo.
echo [1/2] 正在提交: "!FINAL_MSG!"
git add .
git commit -m "!FINAL_MSG!"

echo [2/2] 正在推送至 GitHub...
git push origin main

if %errorlevel% equ 0 (
    echo ===========================================
    echo   [SUCCESS] 上传成功！
    echo   提交备注信息为: !FINAL_MSG!
    echo ===========================================
) else (
    echo.
    echo   [ERROR] 上传失败，请检查网络或 Git 冲突。
)

echo.
echo 请按任意键退出...
pause >nul