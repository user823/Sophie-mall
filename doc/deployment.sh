#!bin/bash

docker run -d -p 27017:27017 --name sophie-mongo -e MONGO_INITDB_ROOT_USERNAME=sophie -e MONGO_INITDB_ROOT_PASSWORD=12345678 mongo
# docker run -it --rm mongo mongosh -u sophie -p 12345678 --authenticationDatabase --host 10.211.55.3 mall-main