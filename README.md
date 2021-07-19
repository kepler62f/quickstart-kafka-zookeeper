# Quickstart Kafka Cluster

This project is an example of an event streaming cluster using Docker-based Kafka images. 

The cluster comprises three Kafka brokers, a schema registry and three ZooKeeper servers. 
They are defined as services in the `docker-compose.yml`.

There is also sample code for a Java event producer and a Python event consumer. They use 
Google's protocol buffers as the event's message format.

## Architecture

[Diagram](./diagram.jpg)

## Requirements

* Current version of Docker (Tested on version 20.10.7)
* Current version of Docker Compose (Tested on version 1.28.6)
* Network connection for downloading of images

## Try it out

* Start the cluster by running `docker-compose up -d`
* Create a Kafka topic called `bankTransactions` with three partitions 

```
docker run --net=host --rm confluentinc/cp-kafka:5.4.4 \
kafka-topics --create --topic bankTransactions \
--partitions 3 --replication-factor 1 --if-not-exists \
--zookeeper localhost:22181
```

* Compile the protocol buffer class for Java and Python

```
cd ./kafka-producer/proto
docker run -v `pwd`:/defs namely/protoc-all -f message.proto -l java
docker run -v `pwd`:/defs namely/protoc-all -f message.proto -l python
cp ./gen/pb-java/message/Message.java ../src/main/java/message/
cp ./gen/pb_python/message_pb2.py ../../kafka-consumer/
```

* In the `java-producer` directory, build the Java producer docker image: `DOCKER_BUILDKIT=1 docker build -t kafka-producer .`
* In the `python-consumer` directory, build the Python consumer docker image: `docker build -t kafka-consumer .`
* Open a terminal and run the consumer: `docker run --net=host kafka-consumer`
* Open another terminal and run the producer. It should automatically generate some events: `docker run --net=host kafka-producer`
