package com.silibrina.tecnova.commons.modules.loader;

import com.silibrina.tecnova.commons.exceptions.RouteNotFoundException;
import com.silibrina.tecnova.commons.modules.route.MethodRoute;

/**
 * Defines a table relating request information to plugins.
 * This tables are defined according to plugins route configuration.
 */
public interface RouteTable {

    /**
     * Get the route method that matches the given route (http method
     * + path). If more than one route method should match the given
     * route-path combination, it will give the first one found.
     *
     * @param httpMethod the REST method (GET, POST...)
     * @param path The relative path for this call.
     * @throws RouteNotFoundException if no matching route is found.
     * @return A set of plugins listening for this combination
     */
    MethodRoute getRoute(String httpMethod, String path);
}
