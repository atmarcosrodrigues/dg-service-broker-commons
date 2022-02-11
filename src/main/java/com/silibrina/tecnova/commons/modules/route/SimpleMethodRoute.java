package com.silibrina.tecnova.commons.modules.route;

import com.silibrina.tecnova.commons.annotations.Route;
import com.silibrina.tecnova.commons.exceptions.MalformedRouteException;
import com.silibrina.tecnova.commons.modules.OpenDataModule;
import play.mvc.Http;
import play.mvc.Result;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import static com.silibrina.tecnova.commons.exceptions.ExitStatus.ROUTE_ERROR_STATUS;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkNotNullCondition;

/**
 * Simple implementation of a method route. It has a build method where all necessary parameters are
 * extracted from a {@link Method} object.
 * You can have more information about this class by consulting its interface {@link MethodRoute}
 */
public class SimpleMethodRoute implements MethodRoute {

    private final String httpMethod;
    private final PathPattern pathPattern;
    private final Method method;

    private SimpleMethodRoute(String httpMethod, PathPattern pathPattern, Method method) {
        checkNotNullCondition("httpMethod can not be null", ROUTE_ERROR_STATUS, httpMethod);
        checkNotNullCondition("pathPattern method can not be null", ROUTE_ERROR_STATUS, pathPattern);
        checkNotNullCondition("method can not be null", ROUTE_ERROR_STATUS, method);

        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
        this.method = method;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public PathPattern getPathPattern() {
        return pathPattern;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Result execute(String path, Http.Request request) {
        try {
            checkNotNullCondition("path can not be null", path);
            checkNotNullCondition("request can not be null", request);

            OpenDataModule declaringClass = prepareOpenDataModule(path, request);

            return (Result) method.invoke(declaringClass);
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException  e) {
            throw new MalformedRouteException(e);
        }
    }

    private OpenDataModule prepareOpenDataModule(String path, Http.Request request) throws InstantiationException, IllegalAccessException {
        OpenDataModule declaringClass = (OpenDataModule) method.getDeclaringClass().newInstance();
        declaringClass.setParameters(pathPattern.matches(path) ? pathPattern.parameters() : null);
        declaringClass.setPath(path);
        declaringClass.setRequest(request);
        return declaringClass;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [httpMethod: %s, pathPattern: %s, method: %s]",
                this.getClass().getSimpleName(), httpMethod, pathPattern, method);
    }

    @Override
    public int compareTo(@Nonnull MethodRoute o) {
        return getPathPattern().compareTo(o.getPathPattern());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleMethodRoute)) return false;

        SimpleMethodRoute that = (SimpleMethodRoute) o;

        if (getHttpMethod() != null ? !getHttpMethod().equals(that.getHttpMethod()) : that.getHttpMethod() != null)
            return false;
        if (getPathPattern() != null ? !getPathPattern().equals(that.getPathPattern()) : that.getPathPattern() != null)
            return false;
        return getMethod() != null ? getMethod().equals(that.getMethod()) : that.getMethod() == null;

    }

    @Override
    public int hashCode() {
        int result = getHttpMethod() != null ? getHttpMethod().hashCode() : 0;
        result = 31 * result + (getPathPattern() != null ? getPathPattern().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        return result;
    }

    public static SimpleMethodRoute buildRoute(Method method) {
        Route route = method.getAnnotation(Route.class);

        String httpMethod = route.method();
        PathPattern pathPattern = new SimplePathPattern(route.path());

        return new SimpleMethodRoute(httpMethod, pathPattern, method);
    }
}
