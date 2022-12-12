import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

import static com.jayway.jsonpath.JsonPath.read;
import static com.jayway.jsonpath.JsonPath.using;

public class JSONHelper {
    public final Logger log = LogManager.getLogger(ParseHelper.class);

    private DocumentContext getJsonDocumentContext(String json) {
        var configuration = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();
        return using(configuration).parse(json);
    }

    public String updateJsonValue(String json, String jsonKey, Object newValue) {
        DocumentContext context = getJsonDocumentContext(json);
        ParseHelper parseHelper = new ParseHelper();
        try {
            var o = read(json, jsonKey);
            var valueType = o.getClass().getSimpleName();

            switch (valueType) {
                case "Integer" -> {
                    Integer integerValue = parseHelper.parsStringToInt(String.valueOf(newValue));
                    if (integerValue != null)
                        context.set(jsonKey, integerValue);
                }
                case "BigInteger" -> {
                    BigInteger bigInteger = parseHelper.parsStringToBigint((String) newValue);
                    if (bigInteger != null)
                        context.set(jsonKey, bigInteger);
                }
                case "Boolean" -> {
                    Boolean boolValue = parseHelper.parseStringToBoolean(String.valueOf(newValue));
                    if (boolValue != null)
                        context.set(jsonKey, boolValue);
                }
                case "Float" -> {
                    Float floatValue = parseHelper.parsStringToFloat(String.valueOf(newValue));
                    if (floatValue != null)
                        context.set(json, floatValue);
                }
                case "Double" -> {
                    Double doubleValue = parseHelper.parsStringToDouble(String.valueOf(newValue));
                    if (doubleValue != null)
                        context.set(jsonKey, doubleValue);
                }
                default -> context.set(jsonKey, newValue);
            }
            return context.jsonString();
        } catch (PathNotFoundException je) {
            log.warn("{} is couldn't find, in JSON \n{}", jsonKey, json);
            return json;
        }
    }

}
