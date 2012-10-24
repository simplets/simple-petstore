package test.unit.org.testinfected.petstore.controllers;

import com.pyxis.petstore.domain.product.Item;
import com.pyxis.petstore.domain.product.ItemInventory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testinfected.petstore.Controller;
import org.testinfected.petstore.controllers.ListItems;
import test.support.com.pyxis.petstore.builders.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static test.support.com.pyxis.petstore.builders.Builders.build;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

@RunWith(JMock.class)
public class ListItemsTest {

    Mockery context = new JUnit4Mockery();
    ItemInventory itemInventory = context.mock(ItemInventory.class);
    ListItems listItems = new ListItems(itemInventory);

    Controller.Request request = context.mock(Controller.Request.class);
    Controller.Response response = context.mock(Controller.Response.class);
    List<Item> items = new ArrayList<Item>();
    String productNumber = "LAB-1234";

    @Before public void
    stubHttpRequest() {
        context.checking(new Expectations() {{
            allowing(request).getParameter("number"); will(returnValue(productNumber));
        }});
    }

    @SuppressWarnings("unchecked")
    @Test public void
    rendersItemsInInventoryMatchingProductNumber() throws Exception {
        searchYields(anItem().of(aProduct().withNumber(productNumber)));

        context.checking(new Expectations() {{
            oneOf(response).render(with("items"), with(allOf(hasEntry("match-found", true), hasEntry("items", items))));
        }});

        listItems.process(request, response);
    }

    @SuppressWarnings("unchecked")
    @Test public void
    indicatesNoMatchWhenNoProductItemIsAvailable() throws Exception {
        searchYieldsNothing();

        context.checking(new Expectations() {{
            oneOf(response).render(with("items"), with(hasEntry("match-found", false)));
        }});

        listItems.process(request, response);
    }

    @SuppressWarnings("unchecked")
    @Test public void
    makesMatchCountAvailableToView() throws Exception {
        searchYields(anItem(), anItem(), anItem());

        context.checking(new Expectations() {{
            oneOf(response).render(with(any(String.class)), with(hasEntry("match-count", 3)));
        }});

        listItems.process(request, response);
    }

    private Matcher<Map<? extends String, ?>> hasEntry(String name, Object value) {
        return Matchers.hasEntry(name, value);
    }

    @SuppressWarnings("unchecked")
    private void searchYieldsNothing() {
        searchYields();
    }

    private void searchYields(final Builder<Item>... results) {
        this.items.addAll(build(results));

        context.checking(new Expectations() {{
            allowing(itemInventory).findByProductNumber(productNumber); will(returnValue(items));
        }});
    }
}
