package org.example.accounting.controller.shop;

import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.service.ProductService;
import org.example.accounting.util.CartAddRequest;
import org.example.accounting.util.PageResult;
import org.example.accounting.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;



    @GetMapping("/products")
    public Result<PageResult> products(PageQueryDO pageQueryDO) {
        Result<PageResult> result = new Result<>();
        PageResult pageresult = productService.pageQuery(pageQueryDO);
        result.setData(pageresult);
        result.setMessage("查询成功");
        result.setCode(200);

        return result;
    }

    @PostMapping("/cart/add")
    public Result addToCart(@RequestBody CartAddRequest request){
        System.out.println("接收到的购物车数据: " + request);
        productService.addToCart(request);
        Result result = new Result();
        result.setCode(200);
        result.setMessage("添加成功");
        result.setData("添加成功");

        return result;
    }


}
