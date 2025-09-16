import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.TopicConfig;

import java.util.Properties;

public class ReplicationConfigExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Для синхронной репликации
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        props.put(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2"); // Минимум 2 реплики должны подтвердить запись

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>("my-topic", "key", "value");
        producer.send(record);
        producer.close();
    }
}