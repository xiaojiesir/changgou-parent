package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {

    @Autowired
    private ContentFeign contentFeign;
    //字符串
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /***
     * 增加数据监听
     * @param eventType 当前操作的类型:增加
     * @param rowData 发生变更的数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("列名:" + column.getName() + "===" + "数据为" + column.getValue());

        }
    }

    /***
     * 修改数据监听
     * @param eventType 当前操作的类型:修改
     * @param rowData 发生变更的数据
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("列名:" + column.getName() + "===" + "修改前数据为" + column.getValue());

        }
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("列名:" + column.getName() + "===" + "修改后数据为" + column.getValue());

        }
    }

    /***
     * 删除数据监听
     * @param eventType 当前操作的类型:删除
     * @param rowData 发生变更的数据
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("列名:" + column.getName() + "===" + "修改前数据为" + column.getValue());

        }

    }

    /***
     * 自定义监听
     * @param eventType 当前操作的类型:删除
     * @param rowData 发生变更的数据
     */
    @ListenPoint(eventType = {CanalEntry.EventType.CREATE, CanalEntry.EventType.DELETE, CanalEntry.EventType.UPDATE},
            schema = {"changgou_content"}, //指定监听的数据库
            table = {"tb_content"}, //指定监听的数据表
            destination = "example"//指定实例地址
    )
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println(eventType.getNumber());
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("列名:" + column.getName() + "===" + "修改前数据为" + column.getValue());

        }
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("列名:" + column.getName() + "===" + "修改后数据为" + column.getValue());
        }

    }


    //自定义数据库的 操作来监听
    //destination = "example"
    @ListenPoint(destination = "example",
            schema = "changgou_content",
            table = {"tb_content", "tb_content_category"},
            eventType = {
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE,
                    CanalEntry.EventType.INSERT})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.获取列名 为category_id的值
        String categoryId = getColumnValue(eventType, rowData);
        //2.调用feign 获取该分类下的所有的广告集合
        Result<List<Content>> categoryresut = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> data = categoryresut.getData();
        //3.使用redisTemplate存储到redis中
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        String categoryId = "";
        //判断 如果是删除  则获取beforlist
        if (eventType == CanalEntry.EventType.DELETE) {
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if (column.getName().equalsIgnoreCase("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        } else {
            //判断 如果是添加 或者是更新 获取afterlist
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if (column.getName().equalsIgnoreCase("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        return categoryId;
    }
}

