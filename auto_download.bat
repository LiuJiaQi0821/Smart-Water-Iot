@echo off
cd /d "%~dp0"

echo [1/2] 正在修复本地缺失的文件...
git checkout .

echo [2/2] 正在从 GitHub 拉取最新更新...
git pull

if %errorlevel% neq 0 (
    echo [!] 出现错误，请检查网络或 Git 状态
    pause
) else (
    echo [OK] 恢复并更新完成！
    timeout /t 2
)