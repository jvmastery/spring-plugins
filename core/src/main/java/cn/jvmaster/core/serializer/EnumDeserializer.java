package cn.jvmaster.core.serializer;

import cn.jvmaster.core.constant.Constant;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;

/**
 * enum反序列化方式
 * @author AI
 * @date 2025/4/1 10:53
 * @version 1.0
**/
public class EnumDeserializer<T extends Enum<T> & Constant<?>> extends JsonDeserializer<T> implements ContextualDeserializer {
    private Class<T> enumType;

    public EnumDeserializer() {
    }

    public EnumDeserializer(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getValueAsString();
        if (value == null) {
            return null;
        }

        try {
            for (T e : enumType.getEnumConstants()) {
                if (String.valueOf(e.getCode()).equals(value)) {
                    return e;
                }
            }
        } catch (NumberFormatException ignored) {}

        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        JavaType type = beanProperty.getType();
        Class<?> rawClass = type.getRawClass();
        if (Enum.class.isAssignableFrom(rawClass)) {
            @SuppressWarnings("unchecked")
            Class<T> enumClass = (Class<T>) rawClass;
            return new EnumDeserializer<>(enumClass);
        }
        throw new JsonMappingException(deserializationContext.getParser(), "EnumDeserializer only supports enums");
    }
}
