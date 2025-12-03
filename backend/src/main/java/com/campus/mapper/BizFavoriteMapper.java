package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.BizFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏 Mapper
 */
@Mapper
public interface BizFavoriteMapper extends BaseMapper<BizFavorite> {

    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM biz_favorite f LEFT JOIN biz_goods g ON f.goods_id = g.id WHERE g.seller_id = #{userId}")
    Long countLikesReceived(Long userId);

    @org.apache.ibatis.annotations.Select("SELECT g.* FROM biz_goods g INNER JOIN biz_favorite f ON g.id = f.goods_id WHERE f.user_id = #{userId} AND g.is_deleted = 0 ORDER BY f.create_time DESC")
    com.baomidou.mybatisplus.core.metadata.IPage<com.campus.entity.BizGoods> selectUserFavoriteGoods(
            com.baomidou.mybatisplus.core.metadata.IPage<com.campus.entity.BizGoods> page,
            @org.apache.ibatis.annotations.Param("userId") Long userId);
}
