package com.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.SysAdmin;
import com.campus.mapper.SysAdminMapper;
import com.campus.service.SysAdminService;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 */
@Service
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements SysAdminService {

    @Override
    public SysAdmin login(String username, String password) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getUsername, username);
        SysAdmin admin = this.getOne(wrapper);

        if (admin == null) {
            throw new RuntimeException("用户不存在");
        }

        // 校验密码
        if (!cn.hutool.crypto.digest.BCrypt.checkpw(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }

        return admin;
    }
}
