package org.lappsgrid.eager.core.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

/**
 *
 */
class Serializer {
    private static ObjectMapper mapper;
    private static ObjectMapper prettyPrinter;

    static {
        mapper = new ObjectMapper()
        mapper.disable(SerializationFeature.INDENT_OUTPUT)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        prettyPrinter = new ObjectMapper()
        prettyPrinter.enable(SerializationFeature.INDENT_OUTPUT)
        prettyPrinter.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
    private Serializer() {}

    /**
     * Parses a JSON string and creates an instance of the specified class.
     * TODO (not before v3.0.0) Rethrow exceptions as LappsIoExceptions
     */
    public static <T> T parse(String json, Class<T> theClass) {
        return (T) mapper.readValue(json, theClass)
    }

//    public static Data<Object> parse(String json) {
//        return (Data) mapper.readValue(json, Data)
//    }

    /**
     * Returns a JSON representation of the object.
     * TODO (not before v3.0.0) Rethrow exceptions as LappsIoExceptions
     */
    public static String toJson(Object object)
    {
        try {
            return mapper.writeValueAsString(object)
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /** Returns a pretty-printed JSON representation of the object.
     * TODO (not before v3.0.0) Rethrow exceptions as LappsIoExceptions
     */
    public static String toPrettyJson(Object object)
    {
        try {
            return prettyPrinter.writeValueAsString(object)
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
