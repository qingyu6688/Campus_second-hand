package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.SysAdmin;
import com.campus.service.SysAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@Tag(name = "管理员管理")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SysAdminService sysAdminService;

    @Autowired
    private com.campus.common.JwtUtils jwtUtils;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        try {
            SysAdmin admin = sysAdminService.login(username, password);

            Map<String, Object> data = new HashMap<>();
            String token = jwtUtils.generateToken(admin.getId(), admin.getUsername());
            data.put("token", token);

            // Hide password
            admin.setPassword(null);
            data.put("userInfo", admin);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "获取当前管理员信息")
    @PostMapping("/info")
    public Result<SysAdmin> getInfo(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        SysAdmin admin = sysAdminService.getById(userId);
        admin.setPassword(null);
        return Result.success(admin);
    }

    @Operation(summary = "更新管理员信息")
    @PostMapping("/update")
    public Result<String> updateInfo(@RequestBody SysAdmin admin) {
        if (admin.getId() == null) {
            return Result.error("ID不能为空");
        }
        // 如果密码为空，则不修改密码
        if (admin.getPassword() != null && admin.getPassword().isEmpty()) {
            admin.setPassword(null);
        }
        // 如果修改了密码，需要加密
        if (admin.getPassword() != null) {
            admin.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(admin.getPassword(),
                    cn.hutool.crypto.digest.BCrypt.gensalt()));
        }

        sysAdminService.updateById(admin);
        return Result.success("更新成功");
    }
}
