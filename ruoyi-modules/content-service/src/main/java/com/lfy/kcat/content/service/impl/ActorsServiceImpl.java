package com.lfy.kcat.content.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.lfy.kcat.content.domain.bo.ActorsBo;
import com.lfy.kcat.content.domain.vo.ActorsVo;
import com.lfy.kcat.content.domain.Actors;
import com.lfy.kcat.content.mapper.ActorsMapper;
import com.lfy.kcat.content.service.IActorsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 演员管理Service业务层处理
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ActorsServiceImpl implements IActorsService {

    private final ActorsMapper baseMapper;

    /**
     * 查询演员管理
     *
     * @param actorId 主键
     * @return 演员管理
     */
    @Override
    public ActorsVo queryById(Long actorId){
        return baseMapper.selectVoById(actorId);
    }

    /**
     * 分页查询演员管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 演员管理分页列表
     */
    @Override
    public TableDataInfo<ActorsVo> queryPageList(ActorsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Actors> lqw = buildQueryWrapper(bo);
        Page<ActorsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的演员管理列表
     *
     * @param bo 查询条件
     * @return 演员管理列表
     */
    @Override
    public List<ActorsVo> queryList(ActorsBo bo) {
        LambdaQueryWrapper<Actors> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Actors> buildQueryWrapper(ActorsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Actors> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Actors::getActorId);
        lqw.like(StringUtils.isNotBlank(bo.getActorName()), Actors::getActorName, bo.getActorName());
        lqw.eq(StringUtils.isNotBlank(bo.getActorImg()), Actors::getActorImg, bo.getActorImg());
        lqw.eq(StringUtils.isNotBlank(bo.getActorInfo()), Actors::getActorInfo, bo.getActorInfo());
        lqw.eq(bo.getBirthday() != null, Actors::getBirthday, bo.getBirthday());
        lqw.eq(bo.getHeight() != null, Actors::getHeight, bo.getHeight());
        lqw.eq(bo.getWeight() != null, Actors::getWeight, bo.getWeight());
        lqw.eq(StringUtils.isNotBlank(bo.getConstellation()), Actors::getConstellation, bo.getConstellation());
        lqw.eq(StringUtils.isNotBlank(bo.getNationality()), Actors::getNationality, bo.getNationality());
        lqw.eq(bo.getDramaCount() != null, Actors::getDramaCount, bo.getDramaCount());
        lqw.eq(bo.getFansCount() != null, Actors::getFansCount, bo.getFansCount());
        lqw.eq(bo.getIsHot() != null, Actors::getIsHot, bo.getIsHot());
        lqw.eq(bo.getStatus() != null, Actors::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增演员管理
     *
     * @param bo 演员管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ActorsBo bo) {
        Actors add = MapstructUtils.convert(bo, Actors.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setActorId(add.getActorId());
        }
        return flag;
    }

    /**
     * 修改演员管理
     *
     * @param bo 演员管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ActorsBo bo) {
        Actors update = MapstructUtils.convert(bo, Actors.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Actors entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除演员管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
