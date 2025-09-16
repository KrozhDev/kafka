import java.util.Properties;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
    public static void main(String[] args) {
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Дополнительные настройки для надежности
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        KafkaProducer<String, String> producer = null;
        
        try {
            // Создание продюсера
            producer = new KafkaProducer<>(properties);

            // Отправка сообщения
            ProducerRecord<String, String> record = new ProducerRecord<>("some-topic", "key-1", "message-1");
            
            // Синхронная отправка с ожиданием результата
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get();
            
            System.out.println("Сообщение успешно отправлено:");
            System.out.println("Топик: " + metadata.topic());
            System.out.println("Партиция: " + metadata.partition());
            System.out.println("Offset: " + metadata.offset());
            
        } catch (Exception e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Закрытие продюсера
            if (producer != null) {
                producer.close();
                System.out.println("Producer закрыт.");
            }
        }
    }
}
