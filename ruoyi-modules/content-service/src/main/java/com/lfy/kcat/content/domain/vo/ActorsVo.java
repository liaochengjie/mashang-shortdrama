package com.lfy.kcat.content.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lfy.kcat.content.domain.Actors;
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
 * 演员管理视图对象 actors
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Actors.class)
public class ActorsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 演员ID
     */
    @ExcelProperty(value = "演员ID")
    private Long actorId;

    /**
     * 演员名字
     */
    @ExcelProperty(value = "演员名字")
    private String actorName;

    /**
     * 头像图
     */
    @ExcelProperty(value = "头像图")
    private String actorImg;

    /**
     * 演员介绍
     */
    @ExcelProperty(value = "演员介绍")
    private String actorInfo;

    /**
     * 生日
     */
    @ExcelProperty(value = "生日")
    private Date birthday;

    /**
     * 身高(cm)
     */
    @ExcelProperty(value = "身高(cm)")
    private Long height;

    /**
     * 体重(kg)
     */
    @ExcelProperty(value = "体重(kg)")
    private Long weight;

    /**
     * 星座
     */
    @ExcelProperty(value = "星座")
    private String constellation;

    /**
     * 国籍
     */
    @ExcelProperty(value = "国籍")
    private String nationality;

    /**
     * 参演剧数量
     */
    @ExcelProperty(value = "参演剧数量")
    private Long dramaCount;

    /**
     * 粉丝数量
     */
    @ExcelProperty(value = "粉丝数量")
    private Long fansCount;

    /**
     * 是否热门演员
     */
    @ExcelProperty(value = "是否热门演员")
    private Long isHot;

    /**
     * 状态：0禁用，1正常
     */
    @ExcelProperty(value = "状态：0禁用，1正常")
    private Long status;


}
