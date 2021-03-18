#!/bin/bash
cd /home/ubuntu
sudo fuser -k 8080/tcp

rm CloudNativeWebApp-0.0.1-SNAPSHOT.jar
