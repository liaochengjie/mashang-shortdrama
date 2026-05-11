package com.lfy.kcat.content.biz.impl;
import com.google.common.collect.Maps;

import com.lfy.kcat.content.biz.DramaPublishService;
import com.lfy.kcat.content.domain.*;
import com.lfy.kcat.content.domain.bo.DramasBo;
import com.lfy.kcat.content.domain.bo.EpisodesBo;
import com.lfy.kcat.content.domain.vo.DramaPublishVo;
import com.lfy.kcat.content.domain.vo.PublishActorsVo;
import com.lfy.kcat.content.feign.CamundaFeignClient;
import com.lfy.kcat.content.mapper.ActorsMapper;
import com.lfy.kcat.content.mapper.DramasMapper;
import com.lfy.kcat.content.service.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author 廖成杰
 * @date 2025/11/4
 */
@Service
@Slf4j
public class DramaPublishServiceImpl implements DramaPublishService {
    @Autowired
    DramasMapper dramasMapper;

    @Autowired
    DramaCategoriesService dramaCategoriesService;

    @Autowired
    DramaTagsService dramaTagsService;

    @Autowired
    DramaActorsService dramaActorsService;

    @Autowired
    ActorsMapper actorsMapper;

    @Autowired
    IEpisodesService episodesService;
    @Autowired
    private IEpisodesService iEpisodesService;
    @Autowired
    DramaAuthService dramaAuthService;

    @Transactional
    @Override
    public Long publishDrama(DramaPublishVo dramaPublishVo) {
        //1、从Vo中解出短剧数据，保存数据库，生成短剧id
        Dramas dramasEntity = buildDramaEntity(dramaPublishVo);
        //数据库保存完短剧数据,mybatis-plus
        dramasMapper.insert(dramasEntity);
        //从Bean中直接获取自增Id
        Long dramaId = dramasEntity.getDramaId();
        log.info("dramaId:{}",dramaId);
        //=========以下所有的保存，都需要给数据库保存短剧id=======

        //2、保存短剧和分类的关联关系
        List<DramaCategories> dramaCategoriesEntity = buildDramaCategoriesEntity(dramaPublishVo, dramaId);
        dramaCategoriesService.saveBatch(dramaCategoriesEntity);
        log.info("分类保存完成:{}",dramaCategoriesEntity);

        //3、保存短剧和标签的关联关系
        List<DramaTags> dramaTagsEntity = getDramaTags(dramaPublishVo, dramaId);
        dramaTagsService.saveBatch(dramaTagsEntity);
        log.info("短剧和标签关系完成:{}",dramaTagsEntity);
        //4、保存演员数据
        List<PublishActorsVo> actors = dramaPublishVo.getActors();
        for (PublishActorsVo actor : actors) {
            //  1：数据库已经有的演员，只需要保存关联关系
            if(actor.getActorId()!=null){
                DramaActors dramaActors = new DramaActors();
                dramaActors.setDramaId(dramaId);
                dramaActors.setActorId(actor.getActorId());
                dramaActors.setRoleName(actor.getRoleName());
                dramaActors.setRoleType(actor.getRoleId());
                dramaActors.setSortOrder(actor.getSortOrder());
                dramaActors.setCreateTime(new Date());
                dramaActorsService.save(dramaActors);
                log.info("已经存在该演员,只录入老演员与短剧关联关系:{}",dramaActors);
            }else{
                //  2：数据库没有的演员，需要先保存演员，再保存关联关系\

                //创建新演员
                Actors actorsEntity = buildActor(actor);

                //保存新演员
                 actorsMapper.insert(actorsEntity);

                log.info("新演员创建并保存成功:{}",actorsEntity);
                //保存关联关系
                DramaActors dramaActors = new DramaActors();
                dramaActors.setDramaId(dramaId);
                dramaActors.setActorId(actorsEntity.getActorId());
                dramaActors.setRoleName(actor.getRoleName());
                dramaActors.setRoleType(actor.getRoleId());
                dramaActors.setSortOrder(actor.getSortOrder());
                dramaActors.setCreateTime(new Date());
                dramaActorsService.save(dramaActors);
                log.info("新演员与短剧关联关系保存成功:{}",dramaActors);
            }
        }




        //5、保存剧集；需要填充关联的短剧ID
        List<EpisodesBo> episodes = dramaPublishVo.getEpisodes();
        for (EpisodesBo episode : episodes) {
            //给每一集设置短剧ID
            episode.setDramaId(dramaId);
            //保存到数据库
            iEpisodesService.insertByBo(episode);
        }
        log.info("保存短剧成功");

        //开启AI审核流程，调用startDramaAuthProcess
        DramaAuthStartDTO dramaAuthStartDTO = new DramaAuthStartDTO();
        dramaAuthStartDTO.setDramaId(dramaId);
        dramaAuthStartDTO.setDramaName(dramasEntity.getTitle());
        dramaAuthStartDTO.setDescription(dramasEntity.getDescription());

        //TODO 保存短剧审核流程
        String processId = startDramaAuthProcess(dramaAuthStartDTO);
        DramaAuth dramaAuth = new DramaAuth();
        dramaAuth.setDramaId(dramaId);
        dramaAuth.setProcessId(processId);
        dramaAuth.setAuthStatus(0);
        dramaAuthService.save(dramaAuth);
        //保存短剧和审核流程的关系
        return dramaId;
    }

    /**
     * 通过远程调用用来进行短剧AI审核流程返回流程ID的
     * @param dramaAuthStartDTO
     * @return
     */
    @Autowired
    CamundaFeignClient camundaFeignClient;
    @Override
    public String startDramaAuthProcess(DramaAuthStartDTO dramaAuthStartDTO) {
        //远程调用camundaFeignClient的startDramaAuthProcess方法，获取流程ID
        R r = camundaFeignClient.startDramaAuthProcess(dramaAuthStartDTO);
        return r.getData().toString();
    }

    @NotNull
    private static Actors buildActor(PublishActorsVo actor) {
        Actors actorsEntity = new Actors();
        actorsEntity.setActorName(actor.getActorName());
        actorsEntity.setActorImg("");
        actorsEntity.setActorInfo("");
        actorsEntity.setHeight(0L);
        actorsEntity.setWeight(0L);
        actorsEntity.setConstellation("");
        actorsEntity.setNationality("");
        actorsEntity.setDramaCount(0L);
        actorsEntity.setFansCount(0L);
        actorsEntity.setIsHot(0L);
        actorsEntity.setStatus(0L);
        actorsEntity.setSearchValue("");
        actorsEntity.setCreateDept(0L);
        actorsEntity.setCreateBy(0L);
        actorsEntity.setCreateTime(new Date());
        actorsEntity.setUpdateBy(0L);
        actorsEntity.setUpdateTime(new Date());
        actorsEntity.setParams(Maps.newHashMap());
        return actorsEntity;
    }

    @NotNull
    private static List<DramaTags> getDramaTags(DramaPublishVo dramaPublishVo, Long dramaId) {
        List<Long> tags = dramaPublishVo.getTags();
        List<DramaTags> list1 = tags.stream().map(tagId -> {
            DramaTags dramaTags = new DramaTags();
            dramaTags.setDramaId(dramaId);
            dramaTags.setTagId(tagId);
            dramaTags.setCreateTime(new Date());
            return dramaTags;
        }).toList();
        return list1;
    }

    @NotNull
    private static List<DramaCategories> buildDramaCategoriesEntity(DramaPublishVo dramaPublishVo, Long dramaId) {
        List<Long> categories = dramaPublishVo.getCategories();
        //处理所有分类的id，然后将他们处理之后存入到DramaCategories的关系类中，变成可以存入数据库的类
        List<DramaCategories> list = categories.stream()
            .map(val -> {
                    DramaCategories dramaCategoriesEntity = new DramaCategories();
                    dramaCategoriesEntity.setDramaId(dramaId);
                    dramaCategoriesEntity.setCategoryId(val);
                    dramaCategoriesEntity.setIsPrimary(0);
                    dramaCategoriesEntity.setCreateTime(new Date());
                    return dramaCategoriesEntity;
                }
            ).toList();
        return list;
    }

    private  Dramas buildDramaEntity(DramaPublishVo dramaPublishVo) {
        DramasBo dramasBo = dramaPublishVo.getDrama();
        Dramas dramas=new Dramas();
        BeanUtils.copyProperties(dramasBo,dramas);
        dramas.setFollowCount(0L);
        dramas.setLikeCount(0L);
        dramas.setPlayCount(0L);
        dramas.setCreateTime(new Date());
        dramas.setUpdateTime(new Date());
        return dramas;
    }
}
