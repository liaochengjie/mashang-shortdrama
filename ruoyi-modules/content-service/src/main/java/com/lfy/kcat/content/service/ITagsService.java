package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.Tags;
import com.lfy.kcat.content.domain.vo.TagsVo;
import com.lfy.kcat.content.domain.bo.TagsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 标签管理Service接口
 *
 * @author leifengyang
 * @date 2025-10-28
 */
public interface ITagsService {

    /**
     * 查询标签管理
     *
     * @param tagId 主键
     * @return 标签管理
     */
    TagsVo queryById(Long tagId);

    /**
     * 分页查询标签管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 标签管理分页列表
     */
    TableDataInfo<TagsVo> queryPageList(TagsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的标签管理列表
     *
     * @param bo 查询条件
     * @return 标签管理列表
     */
    List<TagsVo> queryList(TagsBo bo);

    /**
     * 新增标签管理
     *
     * @param bo 标签管理
     * @return 是否新增成功
     */
    Boolean insertByBo(TagsBo bo);

    /**
     * 修改标签管理
     *
     * @param bo 标签管理
     * @return 是否修改成功
     */
    Boolean updateByBo(TagsBo bo);

    /**
     * 校验并批量删除标签管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
