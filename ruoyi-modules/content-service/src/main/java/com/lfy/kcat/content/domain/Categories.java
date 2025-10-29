package com.lfy.kcat.content.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 分类管理对象 categories
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("categories")
public class Categories extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "category_id")
    private Long categoryId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标URL
     */
    private String icon;

    /**
     * 分类横幅图URL
     */
    private String banner;

    /**
     * 父分类ID
     */
    private Long pId;

    /**
     * 分类层级
     */
    private Long level;

    /**
     * 分类路径
     */
    private String path;

    /**
     * 排序顺序
     */
    private Long sortOrder;

    /**
     * 是否热门分类
     */
    private Long isHot;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 状态：0禁用，1正常
     */
    private Long status;


}
