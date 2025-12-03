package com.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.SysAdmin;

/**
 * 管理员服务接口
 */
public interface SysAdminService extends IService<SysAdmin> {
    
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 管理员信息
     */
    SysAdmin login(String username, String password);
}
