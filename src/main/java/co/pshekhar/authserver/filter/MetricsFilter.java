package co.pshekhar.authserver.filter;

import co.pshekhar.authserver.util.Constant;
import co.pshekhar.authserver.util.Generator;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class MetricsFilter implements WebFilter {
    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {
        final StopWatch stopWatch = new StopWatch();
        final String traceId = Generator.getRandomString(null, "trc_", 17);
        exchange.getResponse().beforeCommit(() -> {
            stopWatch.stop();
            exchange.getResponse().getHeaders().add(Constant.HEADER_LATENCY_RESPONSE, String.valueOf(stopWatch.getTotalTime(TimeUnit.MILLISECONDS)));
            exchange.getResponse().getHeaders().add(Constant.HEADER_TRACE_ID, traceId);
            return Mono.empty();
        });
        return chain.filter(exchange).doOnRequest(_ignored -> {
            stopWatch.start();
            exchange.getAttributes().put(Constant.HEADER_TRACE_ID, traceId);
            exchange.getAttributes().put(Constant.HEADER_CORRELATION_ID, exchange.getRequest().getHeaders().getFirst(Constant.HEADER_CORRELATION_ID));
        });
    }
}
