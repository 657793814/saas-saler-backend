package com.liuzd.soft.dto;

import com.liuzd.soft.entity.TRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * p_tenants dto类
 *
 * @author liuzd01
 * date  2023-09-20 11:10:02
 * email liuzd2025@qq.com
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TRoleDto {

    private Integer id;

    private String name;

    private String desc;

    private Integer enable;


    public TRoleDto(TRoleEntity tRoleEntity) {
        this.id = tRoleEntity.getId();
        this.name = tRoleEntity.getName();
        this.desc = tRoleEntity.getDesc();
        this.enable = tRoleEntity.getEnable();
    }
}

