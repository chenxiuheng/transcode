package com.thunisoft.trascode.utils.placehold;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * <pre>
 * <strong>文件打开工作</strong>
 * 
 * 
 * </pre>
 * @since mrs 2.0
 * @author 陈修恒
 * @date 2013-8-6
 */
public class FileOpener {
    public static StringReader openXml (File file, IValueFinder finder) throws IOException {
        SAXReader reader = new SAXReader();
        
        try {
            Document doc = reader.read(file);
            StringReader strReader = new StringReader(doc.asXML());
            
            PlaceholderReplacer replacer = new PlaceholderReplacer(finder);
            return new StringReader(replacer.replace(strReader));
        } catch (DocumentException  e) {
            throw new IOException(file + " is not XML format");
        }
    }
    
    public static Properties openProperties(File file) throws IOException {
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            Properties p = new Properties();
            p.load(inStream);
            
            return p;
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }
}
