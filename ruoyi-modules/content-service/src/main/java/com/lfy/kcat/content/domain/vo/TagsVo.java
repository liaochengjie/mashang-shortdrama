package com.lfy.kcat.content.domain.vo;

import com.lfy.kcat.content.domain.Tags;
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
 * 标签管理视图对象 tags
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Tags.class)
public class TagsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @ExcelProperty(value = "标签ID")
    private Long tagId;

    /**
     * 标签名称
     */
    @ExcelProperty(value = "标签名称")
    private String name;

    /**
     * 标签颜色
     */
    @ExcelProperty(value = "标签颜色")
    private String color;

    /**
     * 所属分类ID
     */
    @ExcelProperty(value = "所属分类ID")
    private Long categoryId;

    /**
     * 标签类型：1内容标签，2用户标签
     */
    @ExcelProperty(value = "标签类型：1内容标签，2用户标签")
    private Long type;

    /**
     * 是否热门标签
     */
    @ExcelProperty(value = "是否热门标签")
    private Long isHot;

    /**
     * 使用次数
     */
    @ExcelProperty(value = "使用次数")
    private Long useCount;

    /**
     * 排序顺序
     */
    @ExcelProperty(value = "排序顺序")
    private Long sortOrder;

    /**
     * 状态：0禁用，1正常
     */
    @ExcelProperty(value = "状态：0禁用，1正常")
    private Long status;


}
