#!/bin/bash
echo "Running Maven..."
mvn clean package

if [ $? != 0 ]; then
    echo "FAILURE -- Please check the code and try recompiling."
else
    echo "Copying package..."

    cp ./target/prototpe.jar .

    echo "SUCCESS. Please execute run.sh"
fi




