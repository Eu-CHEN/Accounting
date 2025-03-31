package org.example.accounting.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.accounting.DAO.CartItemDAO;
import org.example.accounting.DAO.ProductDAO;
import org.example.accounting.DAO.ShopCartDAO;
import org.example.accounting.DO.BillDO;
import org.example.accounting.DO.CartItemDO;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.ProductDO;
import org.example.accounting.service.ProductService;
import org.example.accounting.util.CartAddRequest;
import org.example.accounting.util.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private ShopCartDAO shopCartDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Override
    public PageResult pageQuery(PageQueryDO pageQueryDO) {
        PageHelper.startPage(pageQueryDO.getPagenum(), pageQueryDO.getPagesize());
        Page<ProductDO> page = productDAO.pageQuery(pageQueryDO);
        return new PageResult(page.getTotal(), page.getPageNum(), page.getResult());
    }

    @Override
    public void addToCart(CartAddRequest request) {
        shopCartDAO.addToCart(request);
        ProductDO productDO = productDAO.getById(request.getProductId());
        CartItemDO cartItemDO = new CartItemDO();
        BeanUtils.copyProperties(cartItemDO, productDO);
        cartItemDO.setProductId(productDO.getId().longValue());
        cartItemDO.setQuantity(request.getQuantity());

        cartItemDAO.addcartItem(cartItemDO);
    }
}
