import org.apache.kafka.common.serialization.Serializer;
import java.nio.ByteBuffer;

public class UserSerializer implements Serializer<User> {
    @Override
    public byte[] serialize(String topic, User user) {
        byte[] nameBytes = user.getName().getBytes();
        int nameSize = nameBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + nameSize + 4);
        buffer.putInt(nameSize);
        buffer.put(nameBytes);
        buffer.putInt(user.getId());
        return buffer.array();
    }
}