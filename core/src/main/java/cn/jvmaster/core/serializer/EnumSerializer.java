package cn.jvmaster.core.serializer;

import cn.jvmaster.core.annotation.UsingHttpEnumSerializer;
import cn.jvmaster.core.constant.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举类型序列化方式
 * @author AI
 * @date 2025/4/1 10:31
 * @version 1.0
 **/
public class EnumSerializer<T extends Enum<T> & Constant<?>> extends JsonSerializer<T> {

    @Override
    public void serialize(T constant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (constant == null) {
            jsonGenerator.writeNull();
            return;
        }

        // 是否使用自定义序列化方式
        boolean usingHttpEnumSerializer = constant.getClass().isAnnotationPresent(UsingHttpEnumSerializer.class);
        if (!usingHttpEnumSerializer) {
            // 默认方式
            jsonGenerator.writeString(constant.name());
            return;
        }

        // 自定义方式
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("code", constant.getCode());
        try {
            for (Field field : constant.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonIgnore.class)) {
                    // 忽略字段
                    continue;
                }

                // 过滤掉非成员变量（如 `$VALUES`、`serialVersionUID`）
                if (!field.isEnumConstant() && !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fieldMap.put(field.getName(), field.get(constant));
                }
            }

            jsonGenerator.writeObject(fieldMap);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
