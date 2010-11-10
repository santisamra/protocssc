@echo off
CALL mvn clean package
copy target\prototpe.jar prototpe.jar /y
