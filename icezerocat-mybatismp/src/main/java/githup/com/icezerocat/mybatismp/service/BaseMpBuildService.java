package githup.com.icezerocat.mybatismp.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import githup.com.icezerocat.mybatismp.common.mybatisplus.NoahServiceImpl;

/**
 * Description: 基础mp构建服务
 * CreateDate:  2020/8/26 14:25
 *
 * @author zero
 * @version 1.0
 */
public interface BaseMpBuildService {

    /**
     * 创建service实例
     *
     * @param tableName 表名
     * @return NoahServiceImpl（增强版ServiceImpl-支持批量保存）
     */
    NoahServiceImpl<BaseMapper<Object>, Object> newInstance(String tableName);

    /**
     * 创建service实例
     *
     * @param t   实体类
     * @param <T> 泛型实体类
     * @return NoahServiceImpl（增强版ServiceImpl-支持批量保存）
     */
    <T> NoahServiceImpl<BaseMapper<T>, T> newInstance(T t);

}
