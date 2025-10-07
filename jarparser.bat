@echo off

set JAR_FILE=
for %%i in (build\libs\*.jar) do (
    if not defined JAR_FILE set JAR_FILE=%%i
)

if not exist "%JAR_FILE%" (
    echo jar file not found. Use: gradlew build
    exit /b 1
)

java -jar "%JAR_FILE%" %*