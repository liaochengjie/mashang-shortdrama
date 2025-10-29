package com.lfy.kcat.content.domain.bo;

import com.lfy.kcat.content.domain.Actors;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 演员管理业务对象 actors
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Actors.class, reverseConvertGenerate = false)
public class ActorsBo extends BaseEntity {

    /**
     * 演员ID
     */
    @NotNull(message = "演员ID不能为空", groups = { EditGroup.class })
    private Long actorId;

    /**
     * 演员名字
     */
    @NotBlank(message = "演员名字不能为空", groups = { AddGroup.class, EditGroup.class })
    private String actorName;

    /**
     * 头像图
     */
    @NotBlank(message = "头像图不能为空", groups = { AddGroup.class, EditGroup.class })
    private String actorImg;

    /**
     * 演员介绍
     */
    private String actorInfo;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 身高(cm)
     */
    private Long height;

    /**
     * 体重(kg)
     */
    private Long weight;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 参演剧数量
     */
    @NotNull(message = "参演剧数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dramaCount;

    /**
     * 粉丝数量
     */
    @NotNull(message = "粉丝数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long fansCount;

    /**
     * 是否热门演员
     */
    @NotNull(message = "是否热门演员不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long isHot;

    /**
     * 状态：0禁用，1正常
     */
    @NotNull(message = "状态：0禁用，1正常不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;


}
