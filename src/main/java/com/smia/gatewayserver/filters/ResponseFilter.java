package com.smia.gatewayserver.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    Tracer tracer;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {

            final String traceId = Optional.ofNullable(tracer.currentSpan())
                    .map(Span::context)
                    .map(TraceContext::traceIdString)
                    .orElse("null");

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                //String correlationId = filterUtils.getCorrelationId(requestHeaders);
                String authToken = filterUtils.getAuthToken(requestHeaders);
                //exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
                logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
                exchange.getResponse().getHeaders().add(FilterUtils.AUTH_TOKEN, authToken);

                logger.info(">>> Adding the correlation id to the outbound headers. {} <<<", traceId);
                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
                logger.debug("[Completing outgoing request for {}.]", exchange.getRequest().getURI());

            }));
        };
    }
}
