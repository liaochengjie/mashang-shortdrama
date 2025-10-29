package org.dromara.system.factory;

import org.dromara.system.domain.SysDept;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author leifengyang
 * @version 1.0
 * @date 2025/9/4 9:07
 * @description:
 * 必须调用 getObject() 得到的返回值才放到容器中；小型工厂
 * 1. BeanFactory：大型工厂。所有的Bean都通过它制造； Spring的底层工厂；ioc容器要利用它创建和获取对象
 * 2. FactoryBean：小型工厂。不能什么都造，只能造泛型指定的组件； isSingleton 控制是否单例
 */
@Component //
public class MyFactoryBean implements FactoryBean<SysDept> {
    @Override
    public SysDept getObject() throws Exception {
        return new SysDept();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
