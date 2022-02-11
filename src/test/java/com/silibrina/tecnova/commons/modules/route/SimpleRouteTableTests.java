package com.silibrina.tecnova.commons.modules.route;

import com.silibrina.tecnova.commons.annotations.Route;
import com.silibrina.tecnova.commons.modules.OpenDataModule;
import com.silibrina.tecnova.commons.modules.loader.RouteTable;
import com.silibrina.tecnova.commons.modules.loader.SimpleRouteTable;
import org.junit.Test;
import play.mvc.Result;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleRouteTableTests {

    public class RouteTest extends OpenDataModule {

        public RouteTest() {
        }

        @Route(path = "something/:id", method = "GET")
        public Result exec() {
            return () -> null;
        }
    }

    @Test
    public void getRouteTest() throws IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchMethodException {
        RouteTable routeTable = new SimpleRouteTable();
        MethodRoute route = routeTable.getRoute("GET", "something/1");
        assertNotNull("Should have found a route", route);

        RouteTest instance = getInstance(route);
        assertTrue("Should be an instance or RouteTest", route.getMethod().getDeclaringClass().equals(RouteTest.class));
        assertTrue("Should return a Result", (route.getMethod().invoke(instance) instanceof Result));
        assertTrue("Should match the route", route.getPathPattern().matches("something/1"));
        assertEquals("Should have the same given http method", "GET", route.getHttpMethod());
    }

    private RouteTest getInstance(MethodRoute route) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        /*
         * default constructor in inside classes always receives the
         * container class implicitly
         */
        return (RouteTest) route.getMethod().getDeclaringClass()
                .getDeclaredConstructor(new Class[] {SimpleRouteTableTests.class})
                .newInstance(this);
    }
}
