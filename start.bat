@echo off
chcp 65001 > nul
title VocabMaster - Quizlet Pro
echo ================================================================
echo           VOCABMASTER - ÔN TẬP TỪ VỰNG CHUẨN QUIZLET
echo ================================================================
echo.
echo [1] Đang khởi động Backend Spring Boot & Frontend Vue 3...
echo [2] Cơ sở dữ liệu: File H2 Persistent tại .\backend\data\vocabdb.mv.db
echo [3] Trình duyệt sẽ tự động mở tại: http://localhost:8888
echo.
echo (Nhấn Ctrl + C để dừng ứng dụng khi hoàn thành học tập)
echo ================================================================
echo.

cd /d "%~dp0"

:: Mở trình duyệt sau 3 giây khi server sẵn sàng
start "" cmd /c "timeout /t 3 /nobreak >nul & start http://localhost:8888"

:: Chạy ứng dụng Spring Boot
java -jar backend/target/demo-0.0.1-SNAPSHOT.jar

pause
