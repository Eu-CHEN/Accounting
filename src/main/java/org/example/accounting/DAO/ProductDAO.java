package org.example.accounting.DAO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.example.accounting.DO.PageQueryDO;
import org.example.accounting.DO.ProductDO;

@Mapper
public interface ProductDAO {

    Page<ProductDO> pageQuery(PageQueryDO pageQueryDO);


    ProductDO getById(Long productId);
}
