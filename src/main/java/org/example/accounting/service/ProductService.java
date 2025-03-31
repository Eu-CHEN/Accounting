package org.example.accounting.service;

import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.util.CartAddRequest;
import org.example.accounting.util.PageResult;

public interface ProductService {

    PageResult pageQuery(PageQueryDO pageQueryDO);

    void addToCart(CartAddRequest request);
}
