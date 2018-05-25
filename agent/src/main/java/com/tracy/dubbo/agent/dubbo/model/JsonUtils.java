package com.tracy.dubbo.agent.dubbo.model;


import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ken.lj
 */
public class JsonUtils {

    public static void writeObject(Object obj, PrintWriter writer) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.write(obj);
        out.writeTo(writer);
        out.close();
        writer.println();
        writer.flush();
    }

    public static void writeBytes(byte[] b, PrintWriter writer) {
        writer.print(new String(b));
        writer.flush();
    }
}
