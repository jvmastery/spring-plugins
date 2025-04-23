package cn.jvmaster.core.serializer;

import cn.jvmaster.core.tree.NaryTree;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * 树的序列化，一帮情况下，不需要根节点
 * @author AI
 * @date 2025/4/23 17:41
 * @version 1.0
**/
public class TreeSerializer extends JsonSerializer<NaryTree<?>> {

    @Override
    public void serialize(NaryTree tree, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(tree.getRoot());
    }
}
