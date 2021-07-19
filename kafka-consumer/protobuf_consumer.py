import datetime
import message_pb2
from confluent_kafka import DeserializingConsumer
from confluent_kafka.error import ValueDeserializationError
from confluent_kafka.schema_registry.protobuf import ProtobufDeserializer
from confluent_kafka.serialization import StringDeserializer


def format_datetime(epoch):
    return datetime.datetime.fromtimestamp(epoch).strftime('%c')


def main():
    topic = 'bankTransactions'
    protobuf_deserializer = ProtobufDeserializer(message_pb2.Transaction)
    string_deserializer = StringDeserializer('utf_8')
    consumer_conf = {'bootstrap.servers': 'localhost:19092',
                     'key.deserializer': string_deserializer,
                     'value.deserializer': protobuf_deserializer,
                     'group.id': 'events-consumer',
                     'auto.offset.reset': 'earliest'}
    consumer = DeserializingConsumer(consumer_conf)
    consumer.subscribe([topic])
    while True:
        try:
            try:
                msg = consumer.poll(1.0)
            except ValueDeserializationError:
                print('confluent_kafka.error.ValueDeserializationError', flush=True)
            if msg is None:
                continue
            transaction = msg.value()
            if transaction is not None:
                output_string = (
                    f"Transaction ID: {transaction.transaction_id}  "
                    f"Account #: {transaction.account_number}  "
                    f"Amount: {f'{transaction.amount:.2f}  ':>8}"
                    f"Timestamp: {format_datetime(transaction.transaction_datetime.seconds)}"
                )
                print(output_string, flush=True)
        except KeyboardInterrupt:
            break
    consumer.close()


if __name__ == '__main__':
    main()
