package com.changgou.goods.dao;

import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;


public interface SkuMapper extends Mapper<Sku> {
    /**
     * 库存递减
     *
     * @param map
     * @return
     */
    @Update("update tb_sku set num = num - #{num} where id =#{id} and num >= #{num}")
    int decrCount(@Param(value = "id") String id, @Param(value = "num") Integer num);
}
