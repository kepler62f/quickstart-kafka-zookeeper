FROM python:3.9.5-slim-buster

RUN pip install confluent-kafka protobuf requests

WORKDIR /script

COPY protobuf_consumer.py .
COPY message_pb2.py .

CMD ["python", "protobuf_consumer.py"]
