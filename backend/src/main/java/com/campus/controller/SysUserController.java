package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.SysUser;
import com.campus.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "分页获取用户列表")
    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        if (cn.hutool.core.util.StrUtil.isNotBlank(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (cn.hutool.core.util.StrUtil.isNotBlank(nickname)) {
            wrapper.like(SysUser::getNickname, nickname);
        }

        wrapper.orderByDesc(SysUser::getCreateTime);

        sysUserService.page(page, wrapper);

        // 脱敏
        page.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(page);
    }

    /**
     * 获取所有用户信息
     * 该接口用于获取系统中的所有用户列表
     *
     * @return 返回一个包含用户列表的Result对象，成功状态码和用户数据
     */
    @Operation(summary = "获取所有用户") // 接口描述，用于API文档生成
    @GetMapping("/list") // HTTP GET请求映射，指定请求路径为/list
    public Result<List<SysUser>> list() { // 方法返回Result类型，包含SysUser列表
        return Result.success(sysUserService.list()); // 调用服务层方法获取用户列表并封装成Result返回
    }

    /**
     * 根据用户ID获取用户信息
     * 该接口用于通过指定的用户ID查询并返回对应的用户详细信息
     *
     * @param id 用户ID，必须为Long类型，通过路径变量传递
     * @return 返回一个Result对象，其中包含查询到的SysUser用户信息
     *         如果查询成功，Result的状态为成功，并携带用户数据
     */
    /**
     * 根据用户ID获取用户信息
     * 该接口用于通过指定的用户ID查询并返回对应的用户详细信息
     *
     * @param id 用户ID，必须为Long类型，通过路径变量传递
     * @return 返回一个Result对象，其中包含查询到的SysUser用户信息
     *         如果查询成功，Result的状态为成功，并携带用户数据
     */
    @Operation(summary = "根据ID获取用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        // 调用sysUserService的getById方法，传入id参数获取用户信息
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null); // 脱敏，防止密码泄露及被前端误传回导致二次加密
        }
        // 将查询结果封装到Result对象中并返回，确保API响应格式统一
        return Result.success(user);
    }

    @Operation(summary = "新增/修改用户")
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody SysUser user) {
        // 如果是新增用户，检查用户名是否存在
        if (user.getId() == null) {
            long count = sysUserService.lambdaQuery()
                    .eq(SysUser::getUsername, user.getUsername())
                    .count();
            if (count > 0) {
                return Result.error("用户名已存在");
            }
            // 加密密码
            String hashedPassword = cn.hutool.crypto.digest.BCrypt.hashpw(user.getPassword());
            user.setPassword(hashedPassword);
        } else {
            // 如果是修改，且密码不为空，则重新加密
            // 注意：防止二次加密，如果前端误传了加密后的密码（通常是60位且以$2a$开头），则不处理
            String password = user.getPassword();
            if (cn.hutool.core.util.StrUtil.isNotBlank(password)) {
                // 简单的判断是否已经是加密后的密码
                if (password.length() == 60 && password.startsWith("$2a$")) {
                    // 认为是已加密密码，不处理（或者设为null以防万一，取决于MyBatisPlus更新策略）
                    // 这里假设MyBatisPlus配置了不更新null值，或者我们显式设为null
                    user.setPassword(null);
                } else {
                    String hashedPassword = cn.hutool.crypto.digest.BCrypt.hashpw(password);
                    user.setPassword(hashedPassword);
                }
            } else {
                // 如果密码为空，表示不修改密码，设为null避免被更新
                user.setPassword(null);
            }
        }
        return Result.success(sysUserService.saveOrUpdate(user));
    }

    /**
     * 删除用户的接口方法
     * 通过用户ID删除指定用户
     *
     * @param id 用户ID，通过路径变量传递
     * @return 返回操作结果，包含操作是否成功的信息
     */
    @Operation(summary = "删除用户") // API操作描述，表示这是一个删除用户的接口
    @DeleteMapping("/{id}") // HTTP映射，指定DELETE请求方式的访问路径为/{id}
    public Result<Boolean> remove(@PathVariable Long id) { // 方法声明，接收路径变量id作为参数
        return Result.success(sysUserService.removeById(id)); // 调用服务层方法删除用户，并返回操作结果
    }

    @Autowired
    private com.campus.common.JwtUtils jwtUtils;

    @Autowired
    private com.campus.service.BizGoodsService bizGoodsService;

    @Autowired
    private com.campus.mapper.BizFavoriteMapper bizFavoriteMapper;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<java.util.Map<String, Object>> login(@RequestBody SysUser user) {
        SysUser dbUser = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .one();
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        // 校验密码
        if (!cn.hutool.crypto.digest.BCrypt.checkpw(user.getPassword(), dbUser.getPassword())) {
            return Result.error("密码错误");
        }

        // 生成 Token
        String token = jwtUtils.generateToken(dbUser.getId(), dbUser.getUsername());

        // 脱敏
        dbUser.setPassword(null);

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("token", token);
        map.put("user", dbUser);

        return Result.success(map);
    }

    @Operation(summary = "获取用户统计信息")
    @GetMapping("/stats")
    public Result<java.util.Map<String, Long>> getUserStats(jakarta.servlet.http.HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            return Result.error("未登录");
        }
        Long userId = Long.valueOf(userIdObj.toString());

        // 1. Published Count
        long publishedCount = bizGoodsService.lambdaQuery()
                .eq(com.campus.entity.BizGoods::getSellerId, userId)
                .eq(com.campus.entity.BizGoods::getIsDeleted, 0)
                .count();

        // 2. Sold Count
        long soldCount = bizGoodsService.lambdaQuery()
                .eq(com.campus.entity.BizGoods::getSellerId, userId)
                .eq(com.campus.entity.BizGoods::getStatus, 2) // 2: Sold
                .eq(com.campus.entity.BizGoods::getIsDeleted, 0)
                .count();

        // 3. My Favorites Count
        long favoriteCount = bizFavoriteMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.campus.entity.BizFavorite>()
                        .eq("user_id", userId));

        java.util.Map<String, Long> map = new java.util.HashMap<>();
        map.put("publishedCount", publishedCount);
        map.put("soldCount", soldCount);
        map.put("favoriteCount", favoriteCount);

        return Result.success(map);
    }

    @Operation(summary = "获取用户公开信息")
    @GetMapping("/info/{id}")
    public Result<java.util.Map<String, Object>> getUserPublicInfo(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 脱敏
        user.setPassword(null);
        user.setMobile(null); // 隐藏手机号

        // 获取统计信息
        // 1. Published Count (Active)
        long publishedCount = bizGoodsService.lambdaQuery()
                .eq(com.campus.entity.BizGoods::getSellerId, id)
                .eq(com.campus.entity.BizGoods::getIsDeleted, 0)
                .eq(com.campus.entity.BizGoods::getStatus, 0) // Only active
                .count();

        // 2. Sold Count
        long soldCount = bizGoodsService.lambdaQuery()
                .eq(com.campus.entity.BizGoods::getSellerId, id)
                .eq(com.campus.entity.BizGoods::getStatus, 2)
                .eq(com.campus.entity.BizGoods::getIsDeleted, 0)
                .count();

        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("user", user);
        map.put("stats", java.util.Map.of(
                "publishedCount", publishedCount,
                "soldCount", soldCount));

        return Result.success(map);
    }
}
