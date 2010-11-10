#!/bin/bash
echo "Running Maven..."
mvn clean package

echo "Copying package..."

cp ./target/prototpe.jar .

echo "Done. Please execute run.sh"


