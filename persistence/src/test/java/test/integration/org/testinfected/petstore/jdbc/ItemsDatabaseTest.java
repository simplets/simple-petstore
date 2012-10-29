package test.integration.org.testinfected.petstore.jdbc;

import com.pyxis.petstore.domain.product.Item;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testinfected.petstore.Transactor;
import org.testinfected.petstore.UnitOfWork;
import org.testinfected.petstore.jdbc.ItemDatabase;
import org.testinfected.petstore.jdbc.JDBCTransactor;
import org.testinfected.petstore.jdbc.ProductsDatabase;
import test.support.com.pyxis.petstore.builders.Builder;
import test.support.org.testinfected.petstore.jdbc.Database;
import test.support.org.testinfected.petstore.jdbc.TestDatabaseEnvironment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.testinfected.petstore.jdbc.DatabaseIdentifier.idOf;
import static test.support.com.pyxis.petstore.builders.Builders.build;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.a;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.org.testinfected.petstore.jdbc.HasFieldWithValue.hasField;

public class ItemsDatabaseTest {

    Database database = Database.in(TestDatabaseEnvironment.load());
    Connection connection = database.connect();
    Transactor transactor = new JDBCTransactor(connection);
    ProductCatalog productCatalog = new ProductsDatabase(connection);

    ItemDatabase itemInventory = new ItemDatabase(connection);

    @Before public void
    resetDatabase() throws Exception {
        database.prepare();
    }

    @After public void
    closeConnection() throws SQLException {
        connection.close();
    }

    @SuppressWarnings("unchecked")
    @Test public void
    findsItemsByProductNumber() throws Exception {
        Product product = aProduct().withNumber("LAB-1234").build();
        givenInCatalog(product);
        givenInInventory(anItem().of(product), anItem().of(product));

        List<Item> availableItems = itemInventory.findByProductNumber("LAB-1234");
        assertThat("available items", availableItems, hasSize(2));
        assertThat("available items", availableItems, everyItem(hasProductNumber("LAB-1234")));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    findsNothingIfProductHasNoAssociatedItemInInventory() throws Exception {
        Product productWithNoItem = aProduct().withNumber("DAL-5432").build();
        Product productWithItems = aProduct().withNumber("BOU-6789").build();
        givenInCatalog(productWithItems, productWithNoItem);
        givenInInventory(anItem().of(productWithItems));

        List<Item> availableItems = itemInventory.findByProductNumber(productWithNoItem.getNumber());
        assertThat("available items", availableItems, Matchers.<Item>empty());
    }

    @SuppressWarnings("unchecked")
    @Test public void
    storesAndRetrievesCompleteItemDetails() throws Exception {
        Product labrador = aProduct().build();
        Product dalmatian = aProduct().build();
        givenInCatalog(labrador, dalmatian);

        Collection<Item> sampleItems = build(
                a(labrador).withNumber("12345678").describedAs("Chocolate male").priced("58.00"),
                a(dalmatian).withNumber("87654321"));

        for (final Item item : sampleItems) {
            transactor.perform(new UnitOfWork() {
                public void execute() throws Exception {
                    itemInventory.add(item);
                }
            });
            List<Item> found = itemInventory.findByProductNumber(item.getProductNumber());
            assertThat("item", uniqueElement(found), sameItemAs(item));
        }
    }

    private Item uniqueElement(List<Item> items) {
        if (items.isEmpty()) throw new AssertionError("No item matches");
        if (items.size() > 1) throw new AssertionError("Several items match");
        return items.get(0);
    }

    private Matcher<Item> sameItemAs(Item original) {
        return allOf(hasField("id", equalTo(idOf(original).get())),
                hasProperty("number", equalTo(original.getNumber())),
                hasProperty("price", equalTo(original.getPrice())),
                hasProperty("description", equalTo(original.getDescription())),
                hasProperty("productNumber", equalTo(original.getProductNumber())));
    }

    private Matcher<Item> hasProductNumber(final String number) {
        return new FeatureMatcher<Item, String>(equalTo(number), "has product number", "product number") {
            @Override protected String featureValueOf(Item actual) {
                return actual.getProductNumber();
            }
        };
    }

    private void givenInCatalog(final Product... products) throws Exception {
        givenInCatalog(asList(products));
    }

    private void givenInCatalog(final List<Product> products) throws Exception {
        for (final Product product : products) givenInCatalog(product);
    }

    private void givenInCatalog(final Product product) throws Exception {
        transactor.perform(new UnitOfWork() {
            public void execute() throws Exception {
                productCatalog.add(product);
            }
        });
    }

    private void givenInInventory(final Builder<Item>... items) throws Exception {
        givenInInventory(build(items));
    }

    private void givenInInventory(final List<Item> items) throws Exception {
        for (final Item item : items) givenInInventory(item);
    }

    private void givenInInventory(final Item item) throws Exception {
        transactor.perform(new UnitOfWork() {
            public void execute() throws Exception {
                itemInventory.add(item);
            }
        });
    }
}
