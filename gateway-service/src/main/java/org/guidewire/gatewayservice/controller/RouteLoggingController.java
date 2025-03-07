package org.guidewire.gatewayservice.controller;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RouteLoggingController {

    private final RouteLocator routeLocator;

    public RouteLoggingController(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @GetMapping("/gateway/routes")
    public Flux<Map<String, String>> getAllRoutes() {
        return routeLocator.getRoutes()
                .map(route -> {
                    Map<String, String> routeInfo = new HashMap<>();
                    routeInfo.put("id", route.getId());
                    routeInfo.put("uri", route.getUri().toString());
                    routeInfo.put("predicates", route.getPredicate().toString());
                    routeInfo.put("filters", route.getFilters().toString());
                    return routeInfo;
                });
    }
}
