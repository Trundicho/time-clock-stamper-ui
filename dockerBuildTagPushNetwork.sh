#!/bin/bash
sudo docker login -u trundicho
sudo docker build -t time-clock-stamper-ui:1.2 .
sudo docker tag time-clock-stamper-ui:1.2 trundicho/time-clock-stamper-ui:1.2
sudo docker push trundicho/time-clock-stamper-ui:1.2
sudo docker network create mynetwork
sudo docker container stop time-clock-stamper-ui
sudo docker container rm time-clock-stamper-ui
sudo docker run --network mynetwork --name time-clock-stamper-ui -p 8082:8080 time-clock-stamper-ui:1.2
