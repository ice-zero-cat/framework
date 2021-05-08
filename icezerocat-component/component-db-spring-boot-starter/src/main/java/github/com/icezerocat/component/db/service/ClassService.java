package github.com.icezerocat.component.db.service;


import github.com.icezerocat.component.common.model.ApClassModel;

/**
 * Description: 类服务
 * CreateDate:  2020/7/16 20:08
 *
 * @author zero
 * @version 1.0
 */
public interface ClassService {

    /**
     * 生成类
     *
     * @param tableName 表明
     * @return ap类
     */
    Class generateClass(String tableName);

    /**
     * 生成类
     *
     * @param apClassModel ap类属性
     * @return ap类
     */
    Class generateClass(ApClassModel apClassModel);
}
