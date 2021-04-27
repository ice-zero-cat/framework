package com.github.icezerocat.component.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 0.0.0
 * ProjectName: [websocket]
 * Package: [com.example.println.websocket.utils.Dom4jUtil]
 * Description Dom4j操作xml工具类
 * Date 2020/3/18 14:56
 */
@Slf4j
@SuppressWarnings("unused")
public class Dom4jUtil {

    public static String getWebPath() {
        return System.getProperty("user.dir").replaceAll("\\\\", "/");
    }

    /**
     * 通过文件的路径获取xml的document对象
     *
     * @param path 文件的路径
     * @return 返回文档对象
     */
    public static Document getXmlByFilePath(String path) {
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            document = reader.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static Document getDocument(String path) {
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            document = reader.read(new ClassPathResource(path).getFile());
        } catch (Exception e) {
            log.error("加载xml文件失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 通过xml字符获取document文档
     *
     * @param xmlStr 要序列化的xml字符
     * @return 返回文档对象
     */
    public static Document getXmlByString(String xmlStr) {
        if (StringUtils.isEmpty(xmlStr)) {
            return null;
        }
        Document document = null;
        try {
            document = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            log.error("获取文档对象失败：{}", e.getMessage());
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 获取某个元素的所有的子节点
     *
     * @param node 制定节点
     * @return 返回所有的子节点
     */
    public static List<Element> getChildElements(Element node) {
        if (null == node) {
            return null;
        }
        return node.elements();
    }

    /**
     * 获取指定节点的子节点
     *
     * @param node      父节点
     * @param childNode 指定名称的子节点
     * @return 返回指定的子节点
     */
    public static Element getChildElement(Element node, String childNode) {
        if (null == node || null == childNode || "".equals(childNode)) {
            return null;
        }
        return node.element(childNode);
    }

    /**
     * 获取所有的属性值
     *
     * @param node 节点
     * @param arg  属性数组
     * @return 指定的属性map
     */
    public static Map<String, String> getAttributes(Element node, String... arg) {
        if (node == null || arg.length == 0) {
            return null;
        }
        Map<String, String> attrMap = new HashMap<>();
        for (String attr : arg) {
            String attrValue = node.attributeValue(attr);
            attrMap.put(attr, attrValue);
        }
        return attrMap;
    }

    /**
     * 获取element的单个属性
     *
     * @param node 需要获取属性的节点对象
     * @param attr 需要获取的属性值
     * @return 返回属性的值
     */
    public static String getAttribute(Element node, String attr) {
        if (null == node || attr == null || "".equals(attr)) {
            return "";
        }
        return node.attributeValue(attr);
    }

    /**
     * 添加孩子节点元素
     *
     * @param parent     父节点
     * @param childName  孩子节点名称
     * @param childValue 孩子节点值
     * @return 新增节点
     */
    public static Element addChild(Element parent, String childName, String childValue) {
        // 添加节点元素
        Element child = parent.addElement(childName);
        // 为元素设值
        child.setText(childValue == null ? "" : childValue);
        return child;
    }

    /**
     * DOM4j的Document对象转为XML报文串
     *
     * @param document 文档
     * @param charset  字符集
     * @return 经过解析后的xml字符串
     */
    public static String documentToString(Document document, String charset) {
        StringWriter stringWriter = new StringWriter();
        // 获得格式化输出流
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置字符集,默认为UTF-8
        format.setEncoding(charset);
        // 写文件流
        XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
        try {
            xmlWriter.write(document);
            xmlWriter.flush();
            xmlWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }

    /**
     * 去掉声明头的
     *
     * @param document 文档
     * @param charset  字符集
     * @return 去掉声明头的报文串
     */
    public static String documentToStringNoDeclaredHeader(Document document, String charset) {
        String xml = documentToString(document, charset);
        return xml.replaceFirst("\\s*<[^<>]+>\\s*", "");
    }

    /**
     * 解析XML为Document对象
     *
     * @param xml 被解析的XMl
     * @return Document
     */
    public static Element parseXml(String xml) throws SAXException {
        StringReader sr = new StringReader(xml);
        SAXReader saxReader = new SAXReader();
        saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document document = null;
        try {
            document = saxReader.read(sr);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document != null ? document.getRootElement() : null;
    }

    /**
     * 获取节点文本
     *
     * @param element 节点
     * @return 文本
     */
    public static String getText(Element element) {
        try {
            return element.getTextTrim();
        } catch (Exception e) {
            throw new RuntimeException(e + "->指定【" + element.getName() + "】节点读取错误");
        }

    }

    /**
     * 获取element对象的text的值
     *
     * @param e   节点的对象
     * @param tag 节点的tag
     * @return text值
     */
    public static String getText(Element e, String tag) {
        Element element = e.element(tag);
        if (element != null) {
            return element.getText();
        } else {
            return null;
        }
    }

    /**
     * 获取去除空格的字符串
     *
     * @param element 父元素
     * @param tag     子元素标签
     * @return 子元素字符串
     */
    public static String getTextTrim(Element element, String tag) {
        Element e = element.element(tag);
        if (e != null) {
            return e.getTextTrim();
        } else {
            return null;
        }
    }

    /**
     * 获取节点值.节点必须不能为空，否则抛错
     *
     * @param parent 父节点
     * @param tag    想要获取的子节点
     * @return 返回子节点
     */
    public static String getTextTrimNotNull(Element parent, String tag) {
        Element e = parent.element(tag);
        if (e == null) {
            throw new NullPointerException("节点为空");
        } else {
            return e.getTextTrim();
        }
    }

    /**
     * 节点必须不能为空，否则抛错
     *
     * @param parent 父节点
     * @param tag    想要获取的子节点
     * @return 子节点
     */
    public static Element elementNotNull(Element parent, String tag) {
        Element e = parent.element(tag);
        if (e == null) {
            throw new NullPointerException("节点为空");
        } else {
            return e;
        }
    }

    /**
     * 将文档对象写入对应的文件中
     *
     * @param document 文档对象
     * @param path     写入文档的路径
     * @return 文件路径
     */
    public static String writeXmlToFile(Document document, String path) {
        if (document == null || path == null) {
            return "";
        }
        XMLWriter writer;
        try {
            String webPath = Dom4jUtil.getWebPath() + "/";
            //判断本地路径前是否有/
            if (path.indexOf("/") == 0) {
                path = path.substring(1);
            }
            path = webPath + path;
            File file = new File(path);
            //判断目录是否存在
            if (!file.getParentFile().exists()) {
                boolean mkdirsBl = file.getParentFile().mkdirs();
                if (!mkdirsBl) {
                    log.error("创建文件目录失败:{}", path);
                }
            }
            //存在则覆盖
            if (file.exists()) {
                writer = new XMLWriter(new FileWriter(file));
            } else {
                writer = new XMLWriter(new FileWriter(path));
            }
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            log.error("保存xml文件出错：{}", e.getMessage());
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 创建Document及根节点
     *
     * @param rootName       根节点名
     * @param attributeName  属性名
     * @param attributeVaule 属性值
     * @return 文档
     */
    public static Document createDocument(String rootName, String attributeName, String attributeVaule) {
        Document document;
        try {
            document = DocumentHelper.createDocument();
            Element root = document.addElement(rootName);
            root.addAttribute(attributeName, attributeVaule);
        } catch (Exception e) {
            throw new RuntimeException(e + "->创建的【" + rootName + "】根节点出现错误");
        }
        return document;
    }

    /**
     * 删除xml文件节点
     *
     * @param document    文档
     * @param elementName 元素名称
     * @return 选择元素的节点
     */
    public static Document deleteElementByName(Document document, String elementName) {
        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator("file");
        while (iterator.hasNext()) {
            Element element = iterator.next();
            // 根据属性名获取属性值
            Attribute attribute = element.attribute("name");
            if (attribute.getValue().equals(elementName)) {
                root.remove(element);
                document.setRootElement(root);
                break;
            }
        }
        return document;
    }

    /**
     * 删除属性等于某个值的元素
     *
     * @param document  XML文档
     * @param xpath     xpath路径表达式
     * @param attrName  属性名
     * @param attrValue 属性值
     * @return 节点
     */
    public static Document deleteElementByAttribute(Document document, String xpath, String attrName, String attrValue) {
        for (Object object : document.selectNodes(xpath)) {
            Element element = (Element) object;
            Element parentElement = element.getParent();
            // 根据属性名获取属性值
            Attribute attribute = element.attribute(attrName);
            if (attribute.getValue().equals(attrValue)) {
                parentElement.remove(element);
            }
        }
        return document;
    }

    /**
     * 修改xml某节点的值
     *
     * @param inputXml      原xml文件
     * @param nodes         要修改的节点
     * @param attributeName 属性名称
     * @param value         新值
     * @param outXml        输出文件路径及文件名 如果输出文件为null，则默认为原xml文件
     */
    public static void modifyDocument(File inputXml, String nodes, String attributeName, String value, String outXml) throws SAXException {
        try {
            SAXReader saxReader = new SAXReader();
            saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = saxReader.read(inputXml);
            List list = document.selectNodes(nodes);
            for (Object object : list) {
                Attribute attribute = (Attribute) object;
                if (attribute.getName().equals(attributeName)) {
                    attribute.setValue(value);
                }
            }
            XMLWriter output;
            //指定输出文件
            if (outXml != null) {
                output = new XMLWriter(new FileWriter(new File(outXml)));
            } else {
                //输出文件为原文件
                output = new XMLWriter(new FileWriter(inputXml));
            }
            output.write(document);
            output.close();
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 通过元素的ID来获取元素-(注意:将id从小写改成了大写)
     *
     * @param document 文档
     * @param ID       id
     * @return 元素
     */
    public static Element elementByID(Document document, String ID) {
        Element element = null;
        String docStr = documentToString(document, null);
        docStr = docStr.replaceAll("id=", "ID=");
        document = getXmlByString(docStr);
        if (document != null) {
            element = document.elementByID(ID);
        }
        return element;
    }

}
