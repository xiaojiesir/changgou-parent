package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    @GetMapping(value = "/import")
    public Result importData() {
        skuService.importData();
        return new Result(true, StatusCode.OK, "执行操作成功");
    }

    /**
     * 搜索
     *
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap) {
        return skuService.search(searchMap);
    }
}

