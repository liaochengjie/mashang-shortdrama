package com.lfy.kcat.content.domain.vo;

import com.lfy.kcat.content.domain.Categories;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 分类管理视图对象 categories
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Categories.class)
public class CategoriesVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @ExcelProperty(value = "分类ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @ExcelProperty(value = "分类名称")
    private String name;

    /**
     * 分类图标URL
     */
    @ExcelProperty(value = "分类图标URL")
    private String icon;

    /**
     * 分类横幅图URL
     */
    @ExcelProperty(value = "分类横幅图URL")
    private String banner;

    /**
     * 父分类ID
     */
    @ExcelProperty(value = "父分类ID")
    private Long pId;

    /**
     * 分类层级
     */
    @ExcelProperty(value = "分类层级")
    private Long level;

    /**
     * 分类路径
     */
    @ExcelProperty(value = "分类路径")
    private String path;

    /**
     * 排序顺序
     */
    @ExcelProperty(value = "排序顺序")
    private Long sortOrder;

    /**
     * 是否热门分类
     */
    @ExcelProperty(value = "是否热门分类")
    private Long isHot;

    /**
     * 分类描述
     */
    @ExcelProperty(value = "分类描述")
    private String description;

    /**
     * 状态：0禁用，1正常
     */
    @ExcelProperty(value = "状态：0禁用，1正常")
    private Long status;


}
