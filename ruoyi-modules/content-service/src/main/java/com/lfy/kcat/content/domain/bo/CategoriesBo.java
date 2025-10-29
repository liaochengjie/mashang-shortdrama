package com.lfy.kcat.content.domain.bo;

import com.lfy.kcat.content.domain.Categories;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 分类管理业务对象 categories
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Categories.class, reverseConvertGenerate = false)
public class CategoriesBo extends BaseEntity {

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空", groups = { EditGroup.class })
    private Long categoryId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "分类层级不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long level;

    /**
     * 分类路径
     */
    private String path;

    /**
     * 排序顺序
     */
    @NotNull(message = "排序顺序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sortOrder;

    /**
     * 是否热门分类
     */
    @NotNull(message = "是否热门分类不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isHot;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 状态：0禁用，1正常
     */
    @NotNull(message = "状态：0禁用，1正常不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;


}
