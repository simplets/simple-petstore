package test.com.pyxis.petstore.domain.product;

import com.pyxis.petstore.domain.product.Product;
import org.junit.Test;
import test.support.com.pyxis.petstore.builders.ProductBuilder;

import static com.pyxis.matchers.validation.ViolationMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.com.pyxis.petstore.validation.ValidationOf.validationOf;

public class ProductTest {

    String SHOULD_NOT_BE_NULL = "{javax.validation.constraints.NotNull.message}";

    @Test public void
    isInvalidWithoutAName() {
        assertThat(validationOf(aProductWithoutAName()), violates(on("name"), withError(SHOULD_NOT_BE_NULL)));
    }

    @Test public void
    isInvalidWithoutANumber() {
        assertThat(validationOf(aProductWithoutANumber()), violates(on("number"), withError(SHOULD_NOT_BE_NULL)));
    }

    @Test public void
    isValidWithANameAndANumber() {
        assertThat(validationOf(aValidProduct()), succeeds());
    }

    @Test public void
    productIsUniquelyIdentifiedByItsNumber() {
        Product product = aProduct().withNumber("AAA-123").build();
        Product shouldMatch = aProduct().withNumber("AAA-123").build();
        Product shouldNotMatch = aProduct().withNumber("BBB-456").build();
        assertThat("products should match", product, equalTo(shouldMatch));
        assertThat("products hash codes should match", product.hashCode(), equalTo(shouldMatch.hashCode()));
        assertThat("products should not match", product, not(equalTo(shouldNotMatch)));
    }

    private ProductBuilder aProductWithoutAName() {
        return aProduct().withName(null);
    }

    private ProductBuilder aProductWithoutANumber() {
        return aProduct().withNumber(null);
    }

    private ProductBuilder aValidProduct() {
        return aProduct();
    }
}
