package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.BizCategory;
import com.campus.service.BizCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@Tag(name = "商品分类管理")
@RestController
@RequestMapping("/api/category")
public class BizCategoryController {

    @Autowired
    private BizCategoryService bizCategoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public Result<List<BizCategory>> list() {
        return Result.success(bizCategoryService.list());
    }

    @Operation(summary = "新增/修改分类")
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody BizCategory category) {
        return Result.success(bizCategoryService.saveOrUpdate(category));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(bizCategoryService.removeById(id));
    }
}
