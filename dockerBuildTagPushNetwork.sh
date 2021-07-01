#!/bin/bash
sudo docker login -u trundicho
sudo docker build -t time-clock-stamper-ui:1.2 .
sudo docker tag time-clock-stamper-ui:1.2 trundicho/time-clock-stamper-ui:1.2
sudo docker push trundicho/time-clock-stamper-ui:1.2
#todo start container
sudo docker network create mynetwork
sudo docker network connect mynetwork time-clock-stamper-ui