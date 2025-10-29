package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.ContentAudits;
import com.lfy.kcat.content.domain.vo.ContentAuditsVo;
import com.lfy.kcat.content.domain.bo.ContentAuditsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 内容审核Service接口
 *
 * @author leifengyang
 * @date 2025-10-28
 */
public interface IContentAuditsService {

    /**
     * 查询内容审核
     *
     * @param auditId 主键
     * @return 内容审核
     */
    ContentAuditsVo queryById(Long auditId);

    /**
     * 分页查询内容审核列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 内容审核分页列表
     */
    TableDataInfo<ContentAuditsVo> queryPageList(ContentAuditsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的内容审核列表
     *
     * @param bo 查询条件
     * @return 内容审核列表
     */
    List<ContentAuditsVo> queryList(ContentAuditsBo bo);

    /**
     * 新增内容审核
     *
     * @param bo 内容审核
     * @return 是否新增成功
     */
    Boolean insertByBo(ContentAuditsBo bo);

    /**
     * 修改内容审核
     *
     * @param bo 内容审核
     * @return 是否修改成功
     */
    Boolean updateByBo(ContentAuditsBo bo);

    /**
     * 校验并批量删除内容审核信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
