package com.silibrina.tecnova.commons.modules.loader;

import com.silibrina.tecnova.commons.annotations.Route;
import com.silibrina.tecnova.commons.modules.OpenDataModule;
import com.silibrina.tecnova.commons.modules.route.MethodRoute;
import com.silibrina.tecnova.commons.modules.route.SimpleMethodRoute;
import org.junit.Assert;
import org.junit.Test;
import play.mvc.Result;

import java.lang.reflect.Method;
import java.util.Set;

public class RouteLoaderTests {

    private class TestRoute2 extends OpenDataModule {

        @Route(path = "somepath", method = "GET")
        public Result exec() { return null; }
    }

    @Test
    public void routeIsLoadedTest() throws NoSuchMethodException {
        Method method = TestRoute2.class.getMethod("exec");
        SimpleMethodRoute methodRoute = SimpleMethodRoute.buildRoute(method);

        Set<MethodRoute> routes = new RouteLoader().getRoutes();
        Assert.assertTrue("Should have found this method during loading", routes.contains(methodRoute));
    }
}
