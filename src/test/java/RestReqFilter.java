import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestReqFilter implements Filter {
    private static int desiredStatusCode;
    private final Logger log = LogManager.getLogger(RestReqFilter.class);
    public RestReqFilter(int statusCode) {
        desiredStatusCode = statusCode;
    }

    public RestReqFilter() {
        desiredStatusCode = 0;
    }

    @Override
    public Response filter(FilterableRequestSpecification reqSpec, FilterableResponseSpecification resSpec, FilterContext filterContext) {

        var response = filterContext.next(reqSpec, resSpec);

        if (desiredStatusCode != 0 && desiredStatusCode != response.statusCode())
            log.error("""
                            Response code: {}
                            Response Body:
                            {}
                            You can use following cURL to reproduce the same request
                            {}""", response.statusCode(), response.asPrettyString(),
                    createCurl(reqSpec.getMethod(), reqSpec.getURI(), reqSpec.getHeaders(), reqSpec.getBody()));
        else
            log.info("""
                            Response code: {}
                            Response Body:
                            {}
                            You can use following cURL to reproduce the same request
                            {}""", response.statusCode(), response.asPrettyString(),
                    createCurl(reqSpec.getMethod(), reqSpec.getURI(), reqSpec.getHeaders(), reqSpec.getBody()));

        return response;
    }


    private String createCurl(String method, String url, Headers headers, String body) {
        StringBuilder curl = new StringBuilder(String.format("%ncurl --location --request %s %s \\\n", method, url));

        if (headers.size() > 1) {
            headers.asList().forEach(h -> curl.append(String.format("--header '%s'\\\n",
                    h.toString().replaceFirst("=", ":"))));
        }
        if (body != null && !body.isEmpty() && !body.isBlank() && !body.equals("null")) {
            curl.append(String.format("--data-raw '%s'", body));
        }
        return curl.toString();
    }

}
