#!/bin/bash

docker run -d -p 4560:4560 -p 4561:4561 -p 4562:4562 -p 4563:4563 -v ./logstash/pipeline/:/usr/share/logstash/pipeline/ --name logstash logstash:8.13.4
docker run -d -p 8848:8848 -p 9848:9848 -p 9849:9849 -e MODE=standalone -e MYSQL_SERVICE_HOST=10.211.55.3 -e MYSQL_SERVICE_DB_NAME=sophie_mall -e MYSQL_SERVICE_USER=sophie -e MYSQL_SERVICE_PASSWORD=12345678 --name sophie-nacos nacos/nacos-server:v2.3.2-slim
sudo keytool -importcert -file ~/.cert/http_ca.crt -alias esCA -keystore /opt/jdk-17.0.2/lib/security/cacerts
#docker exec -it sophie-es /usr/share/elasticsearch/bin/elasticsearch-reset-password -u elastic
#docker exec -it sophie-es /usr/share/elasticsearch/bin/elasticsearch-create-enrollment-token -s kibana --url https://localhost:9200
#docker run -d --name sophie-kibana -p 5601:5601 kibana:8.11.3