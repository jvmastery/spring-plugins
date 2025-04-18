package cn.jvmaster.spring.util;

import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.util.DateUtils;
import cn.jvmaster.core.util.StringUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 表达式占位符解析
 * @author AI
 * @date 2024/12/31 15:59
 * @version 1.0
**/
public class ExpressionUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final Map<String, Expression> EXPRESSION_MAP = new HashMap<>();

    /**
     *  解析文本表达式，将占位符替换成对应的数据
     *  表达式为： #{#user}
     * @param exp           待解析的字符串
     * @param listener      上下文构建条件参数
     * @return  解析后字符串
     */
    public static String parse(String exp, Consumer<EvaluationContext> listener) {
        return parse(exp, String.class, true, listener);
    }

    /**
     *  计算表达式结果
     *
     * @param exp       待解析的表达式
     * @param clazz     解析成功后的对象
     * @param listener  上下文构建条件参数
     * @return 解析后字符串
     * @param <T> 返回数据类型
     */
    public static <T> T calculate(String exp, Class<T> clazz, Consumer<EvaluationContext> listener) {
        return parse(exp, clazz, false, listener);
    }

    /**
     *  解析表达式，生成结果
     *
     * @param exp 待解析的表达式
     * @param clazz 解析成功后的对象
     * @param isTemplate  是否是模板解析
     * @param listener 构建上下文对象
     * @param <T> 返回数据类型
     */
    private static <T> T parse(String exp, Class<T> clazz, boolean isTemplate, Consumer<EvaluationContext> listener) {
        if(StringUtils.isEmpty(exp)) {
            return null;
        }

        // 生成解析对象
        Expression expression = getExpression(exp, isTemplate);

        // 构建上下文环境
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.registerFunction("format", DateUtils.class.getDeclaredMethod("covert", new Class[]{Date.class, String.class}));
            listener.accept(context);

            return expression.getValue(context, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取表达式对象
     * @param exp   待解析的表达式
     * @param isTemplate    是否是文本模板
     * @return  表达式对象
     */
    private static Expression getExpression(String exp, boolean isTemplate) {
        if (EXPRESSION_MAP.containsKey(exp)) {
            return EXPRESSION_MAP.get(exp);
        }

        Expression expression = isTemplate ? PARSER.parseExpression(exp, new TemplateParserContext()) : PARSER.parseExpression(exp);
        EXPRESSION_MAP.put(exp, expression);

        return expression;
    }
}
