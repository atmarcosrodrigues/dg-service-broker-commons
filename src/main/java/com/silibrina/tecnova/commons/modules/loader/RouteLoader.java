package com.silibrina.tecnova.commons.modules.loader;

import com.silibrina.tecnova.commons.annotations.Route;
import com.silibrina.tecnova.commons.modules.OpenDataModule;
import com.silibrina.tecnova.commons.modules.route.MethodRoute;
import com.silibrina.tecnova.commons.modules.route.SimpleMethodRoute;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import play.Environment;
import play.mvc.Result;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.silibrina.tecnova.commons.utils.Preconditions.checkCondition;

/**
 * Load method based routes. It will search for classes extending {@link OpenDataModule}
 * and methods inside these classes annotated with {@link Route}.
 * From this, extract the {@link MethodRoute} objects representing the routes.
 */
class RouteLoader {

    /**
     * Search for the methods with {@link Route} annotation and extracts an object
     * With relevant information about the route.
     *
     * @return the routes.
     */
    Set<MethodRoute> getRoutes() {
        Set<Method> methods = getMethods();
        Set<MethodRoute> routes = new HashSet<>(methods.size());

        routes.addAll(methods.stream().map(SimpleMethodRoute::buildRoute).collect(Collectors.toSet()));

        return routes;
    }

    private Set<Method> getMethods() {
        Set<Class<? extends OpenDataModule>> modules = getModules();
        Set<Method> routeMethods = new LinkedHashSet<>();

        for (Class<? extends OpenDataModule> module : modules) {
            routeMethods.addAll(getRouteMethods(module));
        }

        return routeMethods;
    }

    private Set<Method> getRouteMethods(Class<? extends OpenDataModule> module) {
        Method[] methods = module.getMethods();
        Set<Method> routeMethods = new LinkedHashSet<>();

        for (Method method : methods) {
            Route route = method.getAnnotation(Route.class);
            if (route != null && route.enable()) {
                checkRoute(method);
                routeMethods.add(method);
            }
        }

        return routeMethods;
    }

    private void checkRoute(Method method) {
        checkCondition(String.format(Locale.getDefault(),
                "A route method must return %s but it is %s", Result.class.getCanonicalName(), method.getReturnType())
                , method.getReturnType().equals(Result.class));

    }

    private Set<Class<? extends OpenDataModule>> getModules() {
        Set<Class<? extends OpenDataModule>> modules = getReflections().getSubTypesOf(OpenDataModule.class);
        Set<Class<? extends OpenDataModule>> enabledModules = new HashSet<>();

        for (Class<? extends OpenDataModule> module : modules) {
            Route route = module.getAnnotation(Route.class);
            if (route == null || route.enable()) {
                enabledModules.add(module);
            }
        }
        return enabledModules;
    }

    /**
     * If running tests, search the whole project.
     * If not, search only modules package.
     *
     * @return the package reference.
     */
    private static String getRefPackage() {
        if (Environment.simple().isTest()) {
            return "com.silibrina.tecnova";
        }
        return "com.silibrina.tecnova.opendata.modules";
    }

    private Reflections getReflections() {
        return new Reflections(getRefPackage(), new SubTypesScanner());
    }
}
