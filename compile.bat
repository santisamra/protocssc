@echo off
mvn clean package
copy target\prototpe.jar prototpe.jar /y