package com.liuzd.soft.vo.page;

/**
 * @author: liuzd
 * @date: 2025/8/21
 * @email: liuzd2025@qq.com
 * @desc
 */

import lombok.Data;

@Data
public class PageRequest {
    private int current = 1;
    private int size = 10;
}
