package github.com.icezerocat.component.mp.model;

import lombok.Data;

/**
 * Description: mp返回泛型结果
 * CreateDate:  2021/5/28 11:26
 *
 * @author zero
 * @version 1.0
 */
@Data
public class MpResult<T> {
    /**
     * 结果数据
     */
    private T data;

    /**
     * 构造函数
     *
     * @param data 数据
     */
    private MpResult(T data) {
        this.data = data;
    }

    /**
     * 实例
     *
     * @param t   数据
     * @param <T> 数据类型
     * @return 数据
     */
    public static <T> MpResult<T> getInstance(T t) {
        return new MpResult<>(t);
    }
}
