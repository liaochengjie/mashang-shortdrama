package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.domain.vo.DramasVo;
import com.lfy.kcat.content.domain.bo.DramasBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 短剧管理Service接口
 *
 * @author leifengyang
 * @date 2025-10-28
 */
public interface IDramasService {

    /**
     * 查询短剧管理
     *
     * @param dramaId 主键
     * @return 短剧管理
     */
    DramasVo queryById(Long dramaId);

    /**
     * 分页查询短剧管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 短剧管理分页列表
     */
    TableDataInfo<DramasVo> queryPageList(DramasBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的短剧管理列表
     *
     * @param bo 查询条件
     * @return 短剧管理列表
     */
    List<DramasVo> queryList(DramasBo bo);

    /**
     * 新增短剧管理
     *
     * @param bo 短剧管理
     * @return 是否新增成功
     */
    Boolean insertByBo(DramasBo bo);

    /**
     * 修改短剧管理
     *
     * @param bo 短剧管理
     * @return 是否修改成功
     */
    Boolean updateByBo(DramasBo bo);

    /**
     * 校验并批量删除短剧管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
