package github.com.icezerocat.core.common.easyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 解析监听器，
 *             每解析一行会回调invoke()方法。
 *             整个excel解析结束会执行doAfterAllAnalysed()方法
 * CreateDate:  2020/7/13 14:06
 *
 * @author zero
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExcelListener extends AnalysisEventListener {

    private List<Object> data = new ArrayList<>();

    /**
     * 逐行解析
     * object : 当前行的数据
     */
    @Override
    public void invoke(Object object, AnalysisContext context) {
        //当前行,达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        //context.getCurrentRowNum();
        if (object != null) {
            data.add(object);
        }
    }


    /**
     * 解析完所有数据后会调用该方法
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //解析结束销毁不用的资源
    }

}
