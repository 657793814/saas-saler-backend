package com.liuzd.soft.vo.user;

/**
 * @author: liuzd
 * @date: 2025/8/21
 * @email: liuzd2025@qq.com
 * @desc
 */

import com.liuzd.soft.vo.page.PageRequest;
import lombok.Data;

@Data
public class UserPageReq extends PageRequest {
    private String userName;
    private String mobile;
}
