package com.lfy.kcat.content.config;

import com.lfy.kcat.content.biz.RagReleaseService;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;

/** Actor/tag/category CRUD edits also invalidate the corresponding draft version.
 * @author liaochengjie
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RagFactChangeAspect {
    private final org.springframework.beans.factory.ObjectProvider<RagReleaseService> releaseProvider;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    @Around("execution(* com.lfy.kcat.content.service.impl.ActorsServiceImpl.updateByBo(..)) || execution(* com.lfy.kcat.content.service.impl.TagsServiceImpl.updateByBo(..)) || execution(* com.lfy.kcat.content.service.impl.CategoriesServiceImpl.updateByBo(..)) || " +
        "execution(* com.lfy.kcat.content.service.impl.ActorsServiceImpl.deleteWithValidByIds(..)) || execution(* com.lfy.kcat.content.service.impl.TagsServiceImpl.deleteWithValidByIds(..)) || execution(* com.lfy.kcat.content.service.impl.CategoriesServiceImpl.deleteWithValidByIds(..)) || " +
        "((target(com.lfy.kcat.content.service.DramaActorsService) || target(com.lfy.kcat.content.service.DramaTagsService) || target(com.lfy.kcat.content.service.DramaCategoriesService)) && " +
        "(execution(* save*(..)) || execution(* update*(..)) || execution(* remove*(..))))")
    public Object changed(ProceedingJoinPoint join) throws Throwable {
        RagReleaseService release=releaseProvider.getObject();
        if (!release.enabled()) return join.proceed();
        try {
            return transaction.execute(tx -> {
                try {
                    Object result=join.proceed();
                    // The content catalog is small in v1; hash comparison suppresses unchanged captures.
                    List<Long> ids=jdbc.queryForList("SELECT drama_id FROM kcat_rag_release WHERE deleted=0",Long.class);
                    for (Long id:ids) release.refreshIfManaged(id);
                    return result;
                } catch (Throwable e) { throw new Wrapped(e); }
            });
        } catch (Wrapped e) { throw e.getCause(); }
    }
    private static class Wrapped extends RuntimeException { Wrapped(Throwable cause) { super(cause); } }
}
