package com.goals.course.authenticator.configuration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class AddTokenFilter implements ExchangeFilterFunction {

    private final ReactiveRequestContextHolder reactiveRequestContextHolder;

    @Override
    public Mono<ClientResponse> filter(final ClientRequest request, final ExchangeFunction next) {
        return reactiveRequestContextHolder.getAuthToken()
                .flatMap(authToken -> {
                    request.headers().put(HttpHeaders.AUTHORIZATION, List.of(authToken));
                    return next.exchange(request);
                });
    }

}
