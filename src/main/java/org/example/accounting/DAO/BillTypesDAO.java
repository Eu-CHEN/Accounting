package org.example.accounting.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.accounting.DO.BillTypes;
import org.example.accounting.DO.Categories;

import java.util.List;

@Mapper
public interface BillTypesDAO {

    @Select("SELECT * from billtypes")
    List<BillTypes> getCategories();
}
