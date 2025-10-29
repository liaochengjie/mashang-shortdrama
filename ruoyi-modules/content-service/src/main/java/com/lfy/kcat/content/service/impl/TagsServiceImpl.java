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
import com.lfy.kcat.content.domain.bo.TagsBo;
import com.lfy.kcat.content.domain.vo.TagsVo;
import com.lfy.kcat.content.domain.Tags;
import com.lfy.kcat.content.mapper.TagsMapper;
import com.lfy.kcat.content.service.ITagsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 标签管理Service业务层处理
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TagsServiceImpl implements ITagsService {

    private final TagsMapper baseMapper;

    /**
     * 查询标签管理
     *
     * @param tagId 主键
     * @return 标签管理
     */
    @Override
    public TagsVo queryById(Long tagId){
        return baseMapper.selectVoById(tagId);
    }

    /**
     * 分页查询标签管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 标签管理分页列表
     */
    @Override
    public TableDataInfo<TagsVo> queryPageList(TagsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Tags> lqw = buildQueryWrapper(bo);
        Page<TagsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的标签管理列表
     *
     * @param bo 查询条件
     * @return 标签管理列表
     */
    @Override
    public List<TagsVo> queryList(TagsBo bo) {
        LambdaQueryWrapper<Tags> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Tags> buildQueryWrapper(TagsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Tags> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Tags::getTagId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), Tags::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getColor()), Tags::getColor, bo.getColor());
        lqw.eq(bo.getCategoryId() != null, Tags::getCategoryId, bo.getCategoryId());
        lqw.eq(bo.getType() != null, Tags::getType, bo.getType());
        lqw.eq(bo.getIsHot() != null, Tags::getIsHot, bo.getIsHot());
        lqw.eq(bo.getUseCount() != null, Tags::getUseCount, bo.getUseCount());
        lqw.eq(bo.getSortOrder() != null, Tags::getSortOrder, bo.getSortOrder());
        lqw.eq(bo.getStatus() != null, Tags::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增标签管理
     *
     * @param bo 标签管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(TagsBo bo) {
        Tags add = MapstructUtils.convert(bo, Tags.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTagId(add.getTagId());
        }
        return flag;
    }

    /**
     * 修改标签管理
     *
     * @param bo 标签管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(TagsBo bo) {
        Tags update = MapstructUtils.convert(bo, Tags.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Tags entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除标签管理信息
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
