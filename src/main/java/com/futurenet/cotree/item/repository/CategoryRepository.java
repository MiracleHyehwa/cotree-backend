package com.futurenet.cotree.item.repository;

import com.futurenet.cotree.item.dto.response.CategoryListReponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryRepository {
    List<CategoryListReponse> getCategories();
}
