package github.com.icezerocat.mybatismp.service;

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
     * @param <T>    实体类
     * @return BaseMapper Bean操作对象
     * @throws Exception 代理异常
     */
    <T extends BaseMapper> T proxy(Class<T> tClass) throws Exception;
}
