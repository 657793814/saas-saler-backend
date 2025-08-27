package com.liuzd.soft.vo.page;

/**
 * @author: liuzd
 * @date: 2025/8/21
 * @email: liuzd2025@qq.com
 * @desc
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private long total;
    private int current;
    private int size;
    private List<T> records;
    private int pages;

    public PageResult(List<T> records, long total, int current, int size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }
}
