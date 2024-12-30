package com.assessment.popularity.interceptors;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@NoArgsConstructor
public class WebClientLoggingFilter {

    private static final Logger logger = LoggerFactory.getLogger(WebClientLoggingFilter.class);

    public static ExchangeFilterFunction logRequestAndResponse() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
                    logger.info("Request: {} {}", request.method(), request.url());
                    request.headers().forEach((name, values) -> values.forEach(value -> logger.info("{} : {}", name, value)));
                    return Mono.just(request);
                })
                .andThen(ExchangeFilterFunction.ofResponseProcessor(response -> {
                    try {
                        logger.info("Response: {} {}", response.statusCode(), response.headers().asHttpHeaders());
                    } catch (Exception e) {
                        logger.error("Error logging response", e);
                    }
                    return Mono.just(response);
                }));
    }
}
