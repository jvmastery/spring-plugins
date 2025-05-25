package cn.jvmaster.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * 由于web端，long会丢精度，因此需要转换成string类型
 * @author AI
 * @date 2025/5/21 14:30
 * @version 1.0
**/
public class LongToStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(value != null) {
            String stringValue = String.valueOf(value);
            if(stringValue.length() > 15) {
                jsonGenerator.writeString(stringValue);
            } else {
                jsonGenerator.writeNumber(value);
            }
        }
    }
}
