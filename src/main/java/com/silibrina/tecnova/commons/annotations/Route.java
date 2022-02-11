package com.silibrina.tecnova.commons.annotations;

import com.silibrina.tecnova.commons.modules.route.PathPattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify a route method.
 * When disabled, this method will not be considered during the load phase.
 * A route must have a path (route) written in play framework style, you can
 * know more about this at {@link PathPattern} and an http method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Route {

    /**
     * If the route is enable or disabled. If disabled, the loader will not
     * consider it.
     *
     * @return true if it is enabled, false otherwise.
     */
    boolean enable() default true;

    /**
     * The path for the route. This path is in play framework routes style,
     * including regex.
     * You can have more information information about it by looking at
     * {@link PathPattern} and its implementations.
     *
     * @return The path to match in this route.
     */
    String path();

    /**
     * HTTP method for this route (REST style).
     * E.g. (GET, POST, PUT, DELETE, etc.)
     *
     * @return The http method.
     */
    String method();
}
