package com.silibrina.tecnova.commons.modules.loader;

import com.silibrina.tecnova.commons.exceptions.RouteNotFoundException;
import com.silibrina.tecnova.commons.modules.route.MethodRoute;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Route table based one hash map.Note that, the key of the table is the http method
 * and iterates over the retrieved routes to find one that matches the path.
 */
public class SimpleRouteTable implements RouteTable {
    private final Map<String, Set<MethodRoute>> routeTable;

    public SimpleRouteTable() {
        routeTable = loadRouteTable();
    }

    private Map<String, Set<MethodRoute>> loadRouteTable() {
        Set<MethodRoute> routes = new RouteLoader().getRoutes();
        Map<String, Set<MethodRoute>> routeTable = new HashMap<>();

        for (MethodRoute route : routes) {
            if (!routeTable.containsKey(route.getHttpMethod())) {
                routeTable.put(route.getHttpMethod(), new TreeSet<>());
            }

            routeTable.get(route.getHttpMethod()).add(route);
        }

        return routeTable;
    }

    @Override
    public MethodRoute getRoute(String httpMethod, String path) {
        Set<MethodRoute> routes = routeTable.get(httpMethod);
        if (routes == null) {
            throw new RouteNotFoundException("No method for this route");
        }

        for (MethodRoute route : routes) {
            if (route.getPathPattern().matches(path)) {
                return route;
            }
        }

        throw new RouteNotFoundException("No method for this route");
    }

}
