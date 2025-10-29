package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.Categories;
import com.lfy.kcat.content.domain.vo.CategoriesVo;
import com.lfy.kcat.content.domain.bo.CategoriesBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 分类管理Service接口
 *
 * @author leifengyang
 * @date 2025-10-28
 */
public interface ICategoriesService {

    /**
     * 查询分类管理
     *
     * @param categoryId 主键
     * @return 分类管理
     */
    CategoriesVo queryById(Long categoryId);

    /**
     * 分页查询分类管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分类管理分页列表
     */
    TableDataInfo<CategoriesVo> queryPageList(CategoriesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的分类管理列表
     *
     * @param bo 查询条件
     * @return 分类管理列表
     */
    List<CategoriesVo> queryList(CategoriesBo bo);

    /**
     * 新增分类管理
     *
     * @param bo 分类管理
     * @return 是否新增成功
     */
    Boolean insertByBo(CategoriesBo bo);

    /**
     * 修改分类管理
     *
     * @param bo 分类管理
     * @return 是否修改成功
     */
    Boolean updateByBo(CategoriesBo bo);

    /**
     * 校验并批量删除分类管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
