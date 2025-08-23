package com.liuzd.soft.vo.page;

/**
 * @author: liuzd
 * @date: 2025/8/21
 * @email: liuzd2025@qq.com
 * @desc
 */

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long total;
    private int current;
    private int size;
    private List<T> records;
}
