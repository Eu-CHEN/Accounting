package org.example.accounting.DAO;

import org.apache.ibatis.annotations.*;
import org.example.accounting.DO.Categories;

import java.util.List;

@Mapper
public interface CategoriesDAO {

    @Select("SELECT * from categories")
    List<Categories> getTypes();

    /**
     * 添加类别
     * @param categories
     */
    @Insert("INSERT INTO categories (billfication, username,addTime) " +
            "VALUES (#{billfication}, #{username},now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addCategories(Categories categories);

    /**
     * 根据id删除类别
     * @param categories
     */
    @Delete("DELETE FROM categories WHERE id = #{id}")
    void deleteCategories(Categories categories);

    /**
     * 根据id修改类目
     * @param categories
     */
    @Update("UPDATE categories " +
            "SET billfication = #{billfication}, username = #{username} " +
            "WHERE id = #{id}")
    void updateCategories(Categories categories);
}
