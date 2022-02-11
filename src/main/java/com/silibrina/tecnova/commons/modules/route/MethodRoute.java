package com.silibrina.tecnova.commons.modules.route;

import play.mvc.Http.Request;
import play.mvc.Result;

import java.lang.reflect.Method;

/**
 * Defines a route with basically parts.
 * - An http method
 * - A path pattern (with matches the path in the request)
 * - A method to be executed when matching the given route.
 *
 */
public interface MethodRoute extends Comparable<MethodRoute> {

    /**
     * HTTP method for this route.
     * This methods must match an obey play defined http methods:
     * GET, POST, PUT, DELETE, HEAD, OPTIONS and PATCH
     *
     * @return the method for this request.
     */
    String getHttpMethod();

    /**
     * The path pattern that handles the path of the request.
     * This object checks if the path matches and extract parameters
     * based on the given pattern.
     *
     * @return The object that deals with path checking for this route.
     */
    PathPattern getPathPattern();

    /**
     * The plugins that must deal with a request that matches this route.
     *
     * @return the instances for the plugins that deal with this request.
     */
    /**
     * The method that deal with the request matching this route.
     *
     * @return the instance of the method.
     */
    Method getMethod();

    /**
     * Properly executes the method of this route, making available
     * some parameters to the underlying method implementation like a {@link Request},
     * the extracted parameters from the path and the relative path.
     *
     * @param path The relative path for this request.
     * @param request The request object provided by play framework.
     * @return the play framework result.
     */
    Result execute(String path, Request request);
}