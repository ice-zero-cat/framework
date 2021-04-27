package com.github.icezerocat.component.common.easyexcel.common;

import java.util.List;
import java.util.function.Consumer;

/**
 * Description: easyExcel读写接口
 * CreateDate:  2020/8/13 20:42
 *
 * @param <T> 声明读取对象
 * @author zero
 * @version 1.0
 */
public interface EasyExcelRead<T> {

    /**
     * 读写excel表数据后操作
     * <p>
     * 示例：
     * {@code
     * public Consumer<List<T>> readOperation() {
     * return t -> System.out.println(t);
     * }
     * }
     *
     * @return 业务逻辑代码
     */
    Consumer<List<T>> readOperation();
}
