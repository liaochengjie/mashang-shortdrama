package com.lfy.kcat.content.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 标签管理对象 tags
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tags")
public class Tags extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @TableId(value = "tag_id")
    private Long tagId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 标签类型：1内容标签，2用户标签
     */
    private Long type;

    /**
     * 是否热门标签
     */
    private Long isHot;

    /**
     * 使用次数
     */
    private Long useCount;

    /**
     * 排序顺序
     */
    private Long sortOrder;

    /**
     * 状态：0禁用，1正常
     */
    private Long status;


}
