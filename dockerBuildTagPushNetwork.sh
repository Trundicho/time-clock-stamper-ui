#!/bin/bash
docker login -u trundicho
docker build -t time-clock-stamper-ui:1.2 .
docker tag time-clock-stamper-ui:1.2 trundicho/time-clock-stamper-ui:1.2
docker push trundicho/time-clock-stamper-ui:1.2
docker network create mynetwork
docker network connect mynetwork time-clock-stamper-ui