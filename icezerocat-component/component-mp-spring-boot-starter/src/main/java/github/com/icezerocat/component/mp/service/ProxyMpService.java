package github.com.icezerocat.component.mp.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * Description: 代理mp服务
 * CreateDate:  2020/9/14 10:18
 *
 * @author zero
 * @version 1.0
 */
public interface ProxyMpService {

    /**
     * mybatisPlus代理BaseMapper实现类
     *
     * @param tClass BaseMapper接口类
     * @param <T>    BaseMapper泛型接口
     * @return BaseMapper Bean操作对象
     * @throws Exception 代理异常
     */
    <T extends BaseMapper> T proxy(Class<T> tClass) throws Exception;

    /**
     * 移除BaseMapper代理
     *
     * @param <T>    BaseMapper泛型接口
     * @param tClass BaseMapper接口类
     * @return 移除结果
     */
    <T extends BaseMapper> boolean removeProxy(Class<T> tClass);
}
