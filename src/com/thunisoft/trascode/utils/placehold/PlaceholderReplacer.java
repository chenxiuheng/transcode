package com.thunisoft.trascode.utils.placehold;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 * <pre>
 * <strong>占位符替换器</strong>
 * 
 * 对于文件中出现如  ${name } 的内容，自动使用 props.propertis 中的配置项替换
 * </pre>
 * @since mrs 2.0
 * @author 陈修恒
 * @date 2013-8-6
 */
public class PlaceholderReplacer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private IValueFinder finder;
    
    public PlaceholderReplacer (IValueFinder finder) {
        this.finder = finder;
    }
    
    public PlaceholderReplacer (Map<String, String> properties) {
        this.finder = new MemoryValueFinder(properties);
    }

    public String replace(String source) throws IOException {
        return replace(new StringReader(source));
    }
    
    /**
     * 把输入源替换成新的输入源
     * 
     * 新的 Reader 中，${name } 将会被替换
     * @param source 
     * @return
     * @since  mrs 2.0
     * @author 陈修恒
     * @throws IOException 
     */
    public String replace (Reader source) throws IOException {
        StringBuilder b = new StringBuilder();
        
        int ch;
        while ((ch = source.read()) != -1) {
            if ('$' == ch) {
                replacedLine(source, b);
            } else if (Character.isWhitespace(ch)){
                b.append(' ');
            } else {
                b.append((char)ch);
            }
        }

        logger.debug(b.toString());
        return b.toString();
    }

    private void replacedLine(Reader source, StringBuilder b) throws IOException {
        int ch = source.read();
        
        // 发现'{'，则表达式开始
        if (ch != '{') {
            b.append("${");
            return;
        }
        
        StringBuilder key = new StringBuilder();
        while ((ch = source.read()) != '}' && ch != -1) {
            key.append((char)ch);
        }
        
        // 发现 '}'，则表达式结束
        String value = null;
        if ('}' == ch) {
            value = finder.get(key.toString().trim());
        }

        if (null != value) {
            b.append(value);
            logger.debug("replace ${{}} by {}", key, value);
        } else {
            b.append("${");
            b.append(key);
            if ('}' == ch) {
                b.append("}");
            }

            logger.warn("没有找到配置项 ${{}}", key);
        }
       
    }
}
