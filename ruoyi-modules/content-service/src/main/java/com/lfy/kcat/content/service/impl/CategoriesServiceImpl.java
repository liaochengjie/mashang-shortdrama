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
import com.lfy.kcat.content.domain.bo.CategoriesBo;
import com.lfy.kcat.content.domain.vo.CategoriesVo;
import com.lfy.kcat.content.domain.Categories;
import com.lfy.kcat.content.mapper.CategoriesMapper;
import com.lfy.kcat.content.service.ICategoriesService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 分类管理Service业务层处理
 *
 * @author leifengyang
 * @date 2025-10-28
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CategoriesServiceImpl implements ICategoriesService {

    private final CategoriesMapper baseMapper;

    /**
     * 查询分类管理
     *
     * @param categoryId 主键
     * @return 分类管理
     */
    @Override
    public CategoriesVo queryById(Long categoryId){
        return baseMapper.selectVoById(categoryId);
    }

    /**
     * 分页查询分类管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分类管理分页列表
     */
    @Override
    public TableDataInfo<CategoriesVo> queryPageList(CategoriesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Categories> lqw = buildQueryWrapper(bo);
        Page<CategoriesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的分类管理列表
     *
     * @param bo 查询条件
     * @return 分类管理列表
     */
    @Override
    public List<CategoriesVo> queryList(CategoriesBo bo) {
        LambdaQueryWrapper<Categories> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Categories> buildQueryWrapper(CategoriesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Categories> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Categories::getCategoryId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), Categories::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getIcon()), Categories::getIcon, bo.getIcon());
        lqw.eq(StringUtils.isNotBlank(bo.getBanner()), Categories::getBanner, bo.getBanner());
        lqw.eq(bo.getPId() != null, Categories::getPId, bo.getPId());
        lqw.eq(bo.getLevel() != null, Categories::getLevel, bo.getLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getPath()), Categories::getPath, bo.getPath());
        lqw.eq(bo.getSortOrder() != null, Categories::getSortOrder, bo.getSortOrder());
        lqw.eq(bo.getIsHot() != null, Categories::getIsHot, bo.getIsHot());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Categories::getDescription, bo.getDescription());
        lqw.eq(bo.getStatus() != null, Categories::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增分类管理
     *
     * @param bo 分类管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(CategoriesBo bo) {
        Categories add = MapstructUtils.convert(bo, Categories.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCategoryId(add.getCategoryId());
        }
        return flag;
    }

    /**
     * 修改分类管理
     *
     * @param bo 分类管理
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(CategoriesBo bo) {
        Categories update = MapstructUtils.convert(bo, Categories.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Categories entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除分类管理信息
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
