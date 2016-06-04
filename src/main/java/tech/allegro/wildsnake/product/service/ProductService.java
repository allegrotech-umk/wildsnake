package tech.allegro.wildsnake.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.allegro.wildsnake.model.ProductDomain;
import tech.allegro.wildsnake.product.model.Product;
import tech.allegro.wildsnake.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static int PAGE_LIMIT = 20;
    private static int FIRST_PAGE = 0;
    private static String NAME="";
    private static String DEFAULT_SORT_BY_NAME = "name";
    private static BigDecimal PRICE_MIN=BigDecimal.ZERO;
    private static BigDecimal PRICE_MAX=BigDecimal.valueOf(Long.MAX_VALUE);


    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDomain getProduct(final String productName) {
        return new ProductDomain(productRepository.findOneByName(productName));
    }

    public List<ProductDomain> getProducts(final Integer page,final Integer size, final String sort, String name, Integer priceMin, Integer priceMax) {
        PageRequest pageRequest = new PageRequest(setPage(page), setReturnSize(size), setSortDirection(sort), DEFAULT_SORT_BY_NAME);
        return productRepository.findByPriceBetweenAndNameIgnoreCaseContaining(pageRequest,setPriceMin(priceMin),setPriceMax(priceMax),setName(name)).getContent().stream().map(ProductDomain::new).collect(Collectors.toList());
    }

    public Integer getTotalPages(final Integer page,final Integer size, final String sort, String name, Integer priceMin, Integer priceMax) {
        PageRequest pageRequest = new PageRequest(setPage(page), setReturnSize(size), setSortDirection(sort), DEFAULT_SORT_BY_NAME);
        return productRepository.findByPriceBetweenAndNameIgnoreCaseContaining(pageRequest,setPriceMin(priceMin),setPriceMax(priceMax),setName(name)).getTotalPages();
    }

    public void deleteProduct(final String productName) {
        productRepository.deleteByName(productName);
    }

    public void createUniqueProduct(final ProductDomain productDomain) {
        productRepository.createUniqueProduct(new Product(
                productDomain.getName(),
                productDomain.getImageUrl(),
                productDomain.getDescription(),
                productDomain.getPrice()));
    }

    public void updateProduct(final String productName, final ProductDomain productDomain) {
        productRepository.updateProduct(
                productDomain.getImageUrl(),
                productDomain.getDescription(),
                productDomain.getPrice(),
                productName);
    }

    private int setPage(final Integer page) {
        return (Objects.isNull(page) || page<1  ? FIRST_PAGE : page-1);
    }

    private int setReturnSize(final Integer size) {
        return (Objects.isNull(size) ? PAGE_LIMIT : size.intValue());
    }

    private BigDecimal setPriceMin(final Integer priceMin){
        return (Objects.isNull(priceMin)? PRICE_MIN:BigDecimal.valueOf(priceMin.longValue()));
    }
    private BigDecimal setPriceMax(final Integer priceMax){
        return (Objects.isNull(priceMax)? PRICE_MAX:BigDecimal.valueOf(priceMax.longValue()));
    }
    private String setName(final String name){
        return (Objects.isNull(name)? NAME:name);
    }

    private Sort.Direction setSortDirection(final String sort) {
        return (StringUtils.isEmpty(sort) ? null : Sort.Direction.fromString(sort));
    }

}
