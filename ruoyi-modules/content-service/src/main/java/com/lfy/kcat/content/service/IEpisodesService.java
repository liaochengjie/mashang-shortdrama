package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.Episodes;
import com.lfy.kcat.content.domain.vo.EpisodesVo;
import com.lfy.kcat.content.domain.bo.EpisodesBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 剧集管理Service接口
 *
 * @author leifengyang
 * @date 2025-10-28
 */
public interface IEpisodesService {

    /**
     * 查询剧集管理
     *
     * @param episodeId 主键
     * @return 剧集管理
     */
    EpisodesVo queryById(Long episodeId);

    /**
     * 分页查询剧集管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 剧集管理分页列表
     */
    TableDataInfo<EpisodesVo> queryPageList(EpisodesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的剧集管理列表
     *
     * @param bo 查询条件
     * @return 剧集管理列表
     */
    List<EpisodesVo> queryList(EpisodesBo bo);

    /**
     * 新增剧集管理
     *
     * @param bo 剧集管理
     * @return 是否新增成功
     */
    Boolean insertByBo(EpisodesBo bo);

    /**
     * 修改剧集管理
     *
     * @param bo 剧集管理
     * @return 是否修改成功
     */
    Boolean updateByBo(EpisodesBo bo);

    /**
     * 校验并批量删除剧集管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
