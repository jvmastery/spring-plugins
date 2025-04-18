package cn.jvmaster.core.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import jdk.javadoc.doclet.Taglet;

/**
 * 扩展doc中自定义@date标签
 * @author AI
 * @date 2025/4/17 11:08
 * @version 1.0
**/ 
public class DateTaglet implements Taglet {

    @Override
    public Set<Location> getAllowedLocations() {
        return Set.of();
    }

    @Override
    public boolean isInlineTag() {
        return false;
    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public String toString(List<? extends DocTree> tags, Element element) {
        if (!tags.isEmpty()) {
            DocTree tag = tags.getFirst();
            if (tag instanceof TextTree) {
                return "<p><b>Date:</b> " + ((TextTree) tag).getBody() + "</p>";
            }
        }
        return "<p><b>Date:</b> Unknown</p>";
    }
}
