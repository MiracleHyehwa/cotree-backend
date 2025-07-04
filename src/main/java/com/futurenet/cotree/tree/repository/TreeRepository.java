package com.futurenet.cotree.tree.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TreeRepository {
    void saveTree(Long memberId);
    Integer getMyTree(@Param("memberId") Long memberId);
    int getTreeExp(@Param("memberId") Long memberId);
    int updateExp(@Param("memberId") Long memberId, @Param("exp") int exp);

}
