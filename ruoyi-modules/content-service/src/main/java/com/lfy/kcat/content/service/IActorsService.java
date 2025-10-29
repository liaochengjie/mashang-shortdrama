package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.Actors;
import com.lfy.kcat.content.domain.vo.ActorsVo;
import com.lfy.kcat.content.domain.bo.ActorsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 演员管理Service接口
 *
 * @author leifengyang
 * @date 2025-10-28
 */
public interface IActorsService {

    /**
     * 查询演员管理
     *
     * @param actorId 主键
     * @return 演员管理
     */
    ActorsVo queryById(Long actorId);

    /**
     * 分页查询演员管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 演员管理分页列表
     */
    TableDataInfo<ActorsVo> queryPageList(ActorsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的演员管理列表
     *
     * @param bo 查询条件
     * @return 演员管理列表
     */
    List<ActorsVo> queryList(ActorsBo bo);

    /**
     * 新增演员管理
     *
     * @param bo 演员管理
     * @return 是否新增成功
     */
    Boolean insertByBo(ActorsBo bo);

    /**
     * 修改演员管理
     *
     * @param bo 演员管理
     * @return 是否修改成功
     */
    Boolean updateByBo(ActorsBo bo);

    /**
     * 校验并批量删除演员管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
