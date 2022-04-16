package guru.qa.allure.notifications.util;

import guru.qa.allure.notifications.json.JSON;
import kong.unirest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static guru.qa.allure.notifications.config.enums.Headers.URL_ENCODED;

public class LogInterceptor implements Interceptor {
    private final Logger log =
            LoggerFactory.getLogger(LogInterceptor.class);
    private final JSON json = new JSON();

    @Override
    public void onRequest(HttpRequest<?> request, Config config) {
        log.info("\n===REQUEST===\nURL: {}", request.getUrl());
        request.getBody().ifPresent(body ->
                logRequestBody(body, request.getHeaders()));
    }

    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
        log.info("\n===RESPONSE===\nSTATUS CODE: {}\nBODY: \n{}",
                response.getStatus(),
                json.prettyPrint(response.getBody().toString()));
    }

    private void logRequestBody(Body body, Headers headers) {
        if (body.isMultiPart()) {
            log.info("BODY: \n{}", body.multiParts());
            return;
        }
        if (headers.get("Content-Type").contains(URL_ENCODED.header())) {
            log.info("BODY: \n{}", body.uniPart());
            return;
        }
        log.info("BODY: \n{}", json.prettyPrint(body.uniPart().toString()));
    }
}