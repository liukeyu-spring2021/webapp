#!/bin/bash
cd /home/ubuntu
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -c file:/home/ubuntu/cloudwatch-config.json \
    -s
setsid java -jar CloudNativeWebApp-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &