package com.silibrina.tecnova.commons.modules.route;

import static org.junit.Assert.*;

import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import org.junit.Test;

import java.util.Map;

public class SimplePathPatternTests {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = InvalidConditionException.class)
    public void nullPathPatternTest() {
        new SimplePathPattern(null);
    }

    @Test(expected = InvalidConditionException.class)
    public void invalidIdNameTest() {
        new SimplePathPattern("/user/:my_id");
    }

    @Test
    public void createsPathPatternTest() {
        PathPattern pathPattern = new SimplePathPattern("/user/:id");
        assertNotNull("the pattern should have been created", pathPattern);
    }

    @Test
    public void matchesTest() {
        PathPattern pathPattern = new SimplePathPattern("/user/:id");
        assertNotNull(pathPattern);
        assertFalse("The given path should not match", pathPattern.matches("/user/ttt/test"));
        assertTrue("The given path should match", pathPattern.matches("/user/ttt"));
        assertTrue("The given path should match with final /", pathPattern.matches("/user/ttt/"));
        assertFalse("The given path should match", pathPattern.matches("/user/"));
        assertFalse("The given path should match", pathPattern.matches("/user"));
    }

    @Test
    public void matchesMultiGroup1() {
        PathPattern pathPattern = new SimplePathPattern("/some/:id/thing");
        assertTrue("The given path should match", pathPattern.matches("/some/my_id/thing"));
        assertFalse("The given path should match", pathPattern.matches("/some/my_id"));
    }

    @Test
    public void foundParameters() {
        PathPattern pathPattern = new SimplePathPattern("/user/:id");
        assertNotNull(pathPattern);
        assertTrue("The given path should match", pathPattern.matches("/user/my_id"));
        Map<String, String> parameters = pathPattern.parameters();
        assertEquals("Size should be 1", parameters.size(), 1);
        assertTrue("Should contain key: id", parameters.containsKey("id"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id"));
    }

    @Test
    public void matchesMultiGroup2() {
        PathPattern pathPattern = new SimplePathPattern("/some/:id/:id2");
        assertTrue("The given path should match", pathPattern.matches("/some/my_id/thing"));
        assertFalse("The given path should match", pathPattern.matches("/some/my_id"));
    }

    @Test
    public void foundParametersMultiGroup2Parameters() {
        PathPattern pathPattern = new SimplePathPattern("/some/:id/:id2");

        assertTrue("The given path should match", pathPattern.matches("/some/my_id/thing"));
        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 2", parameters.size(), 2);
        assertTrue("Should contain key: id", parameters.containsKey("id"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id"));
        assertTrue("Should contain key: id2", parameters.containsKey("id2"));
        assertTrue("Should contain value: thing", parameters.containsValue("thing"));
    }

    @Test
    public void foundParametersMultiGroup3Parameters() {
        PathPattern pathPattern = new SimplePathPattern("/some/:id/magic/:id2");

        assertTrue("The given path should match", pathPattern.matches("/some/my_id/magic/thing"));
        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 2", parameters.size(), 2);
        assertTrue("Should contain key: id", parameters.containsKey("id"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id"));
        assertTrue("Should contain key: id2", parameters.containsKey("id2"));
        assertTrue("Should contain value: thing", parameters.containsValue("thing"));
    }

    @Test
    public void dynamicMultiExpandingSlash1() {
        PathPattern pathPattern = new SimplePathPattern("/some/*mykey");

        assertTrue("The given path should match", pathPattern.matches("/some/my_id/magic/thing"));
        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 1", parameters.size(), 1);
        assertTrue("Should contain key: id", parameters.containsKey("mykey"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id/magic/thing"));
    }

    @Test
    public void dynamicMultiExpandingSlash2() {
        PathPattern pathPattern = new SimplePathPattern("/some/*mykey/thing");

        assertTrue("The given path should match", pathPattern.matches("/some/my_id/magic/thing"));
        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 1", parameters.size(), 1);
        assertTrue("Should contain key: id", parameters.containsKey("mykey"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id/magic"));
    }

    @Test
    public void dynamicMultiGroupExpandingSlash1() {
        PathPattern pathPattern = new SimplePathPattern("/some/*mykey/thing/*mykey2");

        assertTrue("The given path should match", pathPattern.matches("/some/my_id/magic/thing/trust"));
        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 2", parameters.size(), 2);
        assertTrue("Should contain key: id", parameters.containsKey("mykey"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id/magic"));
        assertTrue("Should contain key: id", parameters.containsKey("mykey2"));
        assertTrue("Should contain value: my_id", parameters.containsValue("trust"));
    }

    @Test
    public void dynamicCustomRegex() {
        PathPattern pathPattern = new SimplePathPattern("/some/$mykey<.+>");

        assertTrue("The given path should match", pathPattern.matches("/some/my_id"));
        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 1", parameters.size(), 1);
        assertTrue("Should contain key: id", parameters.containsKey("mykey"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id"));
    }

    @Test
    public void dynamicMultiCustomRegex() {
        PathPattern pathPattern = new SimplePathPattern("/some/$mykey<.+>/thing/$mykey2<[0-9]+>");

        assertFalse("The given path should not match", pathPattern.matches("/some/my_id"));
        assertFalse("The given path should not match", pathPattern.matches("/some/my_id/thing"));
        assertFalse("The given path should not match", pathPattern.matches("/some/my_id/thing/test"));
        assertTrue("The given path should match", pathPattern.matches("/some/my_id/thing/123"));

        Map<String, String> parameters = pathPattern.parameters();

        assertEquals("Size should be 2", parameters.size(), 2);
        assertTrue("Should contain key: id", parameters.containsKey("mykey"));
        assertTrue("Should contain value: my_id", parameters.containsValue("my_id"));
        assertTrue("Should contain key: id", parameters.containsKey("mykey2"));
        assertTrue("Should contain value: my_id", parameters.containsValue("123"));
    }

}
