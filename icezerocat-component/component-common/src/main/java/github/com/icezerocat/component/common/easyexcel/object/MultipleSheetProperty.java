package github.com.icezerocat.component.common.easyexcel.object;

import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import lombok.Data;

import java.util.List;


/**
 * Description: 多表Sheet属性
 * CreateDate:  2020/7/13 14:16
 *
 * @author zero
 * @version 1.0
 */
@Data
public class MultipleSheetProperty {

    private List<? extends BaseRowModel> data;

    private Sheet sheet;
}
