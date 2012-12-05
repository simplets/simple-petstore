package test.unit.org.testinfected.support.routing;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.simpleframework.http.parse.PathParser;
import org.testinfected.support.routing.DynamicPath;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

public class DynamicPathTest {

    @Test public void
    matchesIdenticalStaticPaths() {
        DynamicPath dynamicPath = new DynamicPath("/products");
        assertThat("no match", dynamicPath.matches(path("/products")));
    }

    @Test public void
    rejectsDifferentStaticPaths() {
        DynamicPath dynamicPath = new DynamicPath("/products");
        assertThat("match", !dynamicPath.matches(path("/items")));
    }

    @Test public void
    ignoresDynamicSegmentsWhenMatching() {
        DynamicPath dynamicPath = new DynamicPath("/products/:number/items/:id");
        assertThat("no match", dynamicPath.matches(path("/products/LAB-1234/items/12345678")));
    }

    @Test public void
    expectsPathWithSameNumberOfSegments() {
        DynamicPath dynamicPath = new DynamicPath("/products/:number/items/:id");
        assertThat("match", !dynamicPath.matches(path("/products/LAB-1234")));
    }

    @Test public void
    staticPathsHaveNoBoundParameters() {
        DynamicPath dynamicPath = new DynamicPath("/products");
        Map<String, String> boundParameters = dynamicPath.extractBoundParameters("/products");
        assertThat("bound parameters values", boundParameters.values(), Matchers.<String>empty());
    }

    @Test public void
    extractBoundParametersFromDynamicSegments() {
        DynamicPath dynamicPath = new DynamicPath("/products/:number/items/:id");
        Map<String, String> boundParameters = dynamicPath.extractBoundParameters("/products/LAB-1234/items/12345678");
        assertThat("bound parameters values", boundParameters.values(), hasSize(2));
        assertThat("bound parameters", boundParameters, allOf(hasEntry("number", "LAB-1234"), hasEntry("id", "12345678")));
    }

    private PathParser path(final String path) {
        return new PathParser(path);
    }
}