package com.gitee.sop.gatewaycommon.util;

import com.gitee.sop.gatewaycommon.result.ApiResult;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author tanghc
 */
public class XmlUtil {
    private static XStream xStream = new XStream(new StaxDriver(new NoNameCoder()));
    static {
        xStream.processAnnotations(ApiResult.class);
        xStream.aliasSystemAttribute(null, "class");
    }

    public static String serialize(Object obj) {
        return getXStream().toXML(obj);
    }

    public static <T> T unserialize(String xml, Class<T> clazz) {
        xStream.processAnnotations(clazz);
        Object object = xStream.fromXML(xml);
        T cast = clazz.cast(object);
        return cast;
    }

    public static XStream getXStream() {
        return xStream;
    }
}
