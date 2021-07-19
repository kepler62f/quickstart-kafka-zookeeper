import com.google.protobuf.Timestamp;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import main.java.message.Message.Transaction;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

public class Application {
    private static final String TOPIC = "bankTransactions";
    private static final String BOOTSTRAP_SERVERS = "localhost:19092";
    public static void main(String[] args) {
        Producer<String, Transaction> kafkaProducer = createKafkaProducer(BOOTSTRAP_SERVERS);
        try {
            produceMessages(10, kafkaProducer);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }

    public static void produceMessages(int numberOfMessages, Producer<String, Transaction> kafkaProducer) throws ExecutionException, InterruptedException {
		Random randgen = new Random();
        for (int i = 0; i < numberOfMessages; i++) {
	        Instant time = Instant.now();
	        Timestamp timestamp = Timestamp.newBuilder()
	        	.setSeconds(time.getEpochSecond())
	        	.setNanos(time.getNano())
	        	.build();
            String s = String.format("%03d", i);
			Transaction transaction = Transaction.newBuilder()
	                                            .setTransactionId("tid"+s)
	                                            .setAccountNumber("acc"+s)
	                                            .setTransactionReference("transaction"+s)
	                                            .setTransactionDatetime(timestamp)
	                                            .setAmount(randgen.nextFloat() * 100)
	                                            .build();

            ProducerRecord<String, Transaction> record = new ProducerRecord<>(TOPIC, "tid"+s, transaction);
            RecordMetadata recordMetadata = kafkaProducer.send(record).get();
			System.out.println(String.format("Transaction record with (key: %s, value %s), was sent to (partition: %d, offset: %d)", record.key(), record.value(), recordMetadata.partition(), recordMetadata.offset()));
        }
    }

    public static Producer<String, Transaction> createKafkaProducer(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "events-producer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);
        properties.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        return new KafkaProducer<String, Transaction>(properties);
    }
}
