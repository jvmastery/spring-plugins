package cn.jvmaster.security.serializer;

import cn.jvmaster.core.util.DateUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * 记住我对象的反序列化
 * @author AI
 * @date 2024/9/7 22:02
 */
public class PersistentRememberMeTokenDeserializer extends StdDeserializer<PersistentRememberMeToken> {
    public PersistentRememberMeTokenDeserializer() {
        this(null);
    }

    protected PersistentRememberMeTokenDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PersistentRememberMeToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode date = node.get("date").get(1);
        // 从 JSON 对象中提取字段
        return new PersistentRememberMeToken(node.get("username").asText(),
                node.get("series").asText(),
                node.get("tokenValue").asText(),
                date == null ? null : DateUtils.convert(date.asText())
                );
    }
}
