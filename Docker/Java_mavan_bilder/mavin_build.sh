#/bin/bash


#docker build . -t mavan_docker_builder:latest

#if ![ -d "./.mavan_docker_builder_files"]
#    mkdir "./.mavan_docker_builder_files"
#fi

docker run --rm \
-v $PWD:/app \
-v mavan_docker_builder_m2_files:/root/.m2
--name=mavan_docker_builder \
FROM maven:3.6.3-openjdk- \
cd /app && mvn clean package
#/bin/bash


