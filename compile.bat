@echo off

echo Compiling Java HTTP Server...

javac -d out ^
src/HTTPServerMain.java ^
src/internal/request/*.java ^
src/internal/response/*.java ^
src/internal/server/*.java ^
src/internal/headers/*.java

if %ERRORLEVEL% == 0 (
    echo Compilation successful.
) else (
    echo Compilation failed.
)