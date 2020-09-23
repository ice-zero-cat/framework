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
    <T extends BaseMapper> T proxy(Class<T> tClass) throws Exception;
}
