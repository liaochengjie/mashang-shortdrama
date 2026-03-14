package com.lfy.kcat.content.job;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfy.kcat.content.biz.BloomFilterTemplate;
import com.lfy.kcat.content.config.RedisConst;
import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.mapper.DramasMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
@Slf4j
public class BloomFilterJob {

    @Autowired
    DramasMapper dramasMapper;

    @Autowired
    BloomFilterTemplate bloomFilterTemplate;
    @Autowired
    RedissonClient redissonClient;


    @XxlJob("reBuildBloomFilter")
    public void reBuildBloomFilter(){
        log.info("开始重新构建布隆过滤器");
        //创建一个新的布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF_NEW);
        //初始化布隆过滤器
        boolean bloomFilterInit = bloomFilter.tryInit(1000000, 0.001);
        if (bloomFilterInit){
            log.info("初始化布隆过滤器成功");
            //往新的布隆过滤器中添加所有的短剧ID
            int currentPage = 1;
            while(true){
                //查询数据库中的短剧ID
                Page<Dramas> dramasPage = dramasMapper.selectPage(new Page<>(currentPage, 1000L), null);
                for (Dramas record : dramasPage.getRecords()) {
                    bloomFilter.add(record.getDramaId());
                }
                if (dramasPage.hasNext()){
                    currentPage++;
                }else{
                    break;
                }
            }
            log.info("新的布隆过滤器添加短剧ID完成");
            //先改名旧的布隆过滤器(通过改名添加uuid以防止重新)
            String uuid = UUID.randomUUID().toString();
            RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF);
            oldBloomFilter.rename(RedisConst.DRAMA_BF+":"+uuid);
            //将新的布隆过滤器改名
            bloomFilter.rename(RedisConst.DRAMA_BF);
            //然后异步删除旧的布隆过滤器
            oldBloomFilter.deleteAsync();
            log.info("旧的布隆过滤器:{}已异步删除",RedisConst.DRAMA_BF+":"+uuid);
        }
        log.info("布隆过滤器重构完成");
    }

    @XxlJob("checkAllDramaIdAndUpdateBloomFilter")
    public void checkAllDramaIdAndUpdateBloomFilter() {
        //从数据库查询所有的短剧ID
        Long currentPage = 1L;
        while(true){
            Page<Dramas> dramasPage = dramasMapper.selectPage(new Page<>(currentPage, 1000L), null);
            for (Dramas dramas : dramasPage.getRecords()) {
                bloomFilterTemplate.addDramaIdBloomFilter(dramas.getDramaId());
                log.info("正在添加短剧:{}到布隆过滤器中", dramas);
            }
            if(dramasPage.hasNext()){
                currentPage++;
            }else{
                break;
            }
        }

    }
}
