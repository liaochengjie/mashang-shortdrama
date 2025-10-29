package com.lfy.kcat.content.domain.bo;

import com.lfy.kcat.content.domain.Tags;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 标签管理业务对象 tags
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Tags.class, reverseConvertGenerate = false)
public class TagsBo extends BaseEntity {

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空", groups = { EditGroup.class })
    private Long tagId;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 所属分类ID
     */
    @NotNull(message = "所属分类ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long categoryId;

    /**
     * 标签类型：1内容标签，2用户标签
     */
    @NotNull(message = "标签类型：1内容标签，2用户标签不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 是否热门标签
     */
    @NotNull(message = "是否热门标签不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isHot;

    /**
     * 使用次数
     */
    @NotNull(message = "使用次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long useCount;

    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sortOrder;

    /**
     * 状态：0禁用，1正常
     */
    @NotNull(message = "状态：0禁用，1正常不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;


}
