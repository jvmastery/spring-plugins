package cn.jvmaster.spring.converter;

import cn.jvmaster.core.util.StringUtils;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

/**
 * 将string参数转换为list参数
 * 对于复杂类型的时候，需要加@RequestParam来指定下
 * @author AI
 * @date 2025/5/23 14:55
 * @version 1.0
**/
@Component
public class StringToListConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, List.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null || "".equals(source)) {
            return Collections.emptyList();
        }

        TypeDescriptor elementTypeDesc = targetType.getElementTypeDescriptor();
        return StringUtils.parseStrToList(URLDecoder.decode((String) source, StandardCharsets.UTF_8), elementTypeDesc.getType());
    }
}
