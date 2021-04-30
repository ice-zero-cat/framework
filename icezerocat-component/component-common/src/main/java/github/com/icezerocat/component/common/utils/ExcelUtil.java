package github.com.icezerocat.component.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.fastjson.JSON;
import github.com.icezerocat.component.common.easyexcel.listener.ExcelListener;
import github.com.icezerocat.component.common.easyexcel.object.MultipleSheetProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by zmj
 * On 2019/11/11.
 *
 * @author 0.0.0
 */
@Slf4j
@SuppressWarnings("all")
public class ExcelUtil {

    private static Sheet initSheet;

    static {
        initSheet = new Sheet(1, 0);
        initSheet.setSheetName("sheet");
        //设置自适应宽度
        initSheet.setAutoWidth(Boolean.TRUE);
    }

    /**
     * list动态对象转换
     *
     * @param objectList list动态对象
     * @return 无对象list
     */
    public static List<List<Object>> getLists(List<?> objectList) {
        List<List<Object>> lists = new ArrayList<>();
        for (Object object : objectList) {
            List<Object> valueList = new ArrayList<>();
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    valueList.add(field.get(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            lists.add(valueList);
        }
        return lists;
    }

    /**
     * 读取少于1000行数据
     *
     * @param filePath 文件绝对路径
     * @return 读取的数据对象
     */
    public static List<Object> readLessThan1000Row(String filePath) {
        return readLessThan1000RowBySheet(filePath, null);
    }

    /**
     * 读小于1000行数据, 带样式
     * filePath 文件绝对路径
     * initSheet ：
     * sheetNo: sheet页码，默认为1
     * headLineMun: 从第几行开始读取数据，默认为0, 表示从第一行开始读取
     * clazz: 返回数据中Object的类名
     *
     * @param filePath 文件路径
     * @param sheet    excel页签对象设置
     * @return 读取的数据对象
     */
    public static List<Object> readLessThan1000RowBySheet(String filePath, Sheet sheet) {
        if (!StringUtils.hasText(filePath)) {
            return null;
        }

        sheet = sheet != null ? sheet : initSheet;

        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(filePath);
            return EasyExcelFactory.read(fileStream, sheet);
        } catch (FileNotFoundException e) {
            log.info("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException e) {
                log.info("excel文件读取失败, 失败原因：{}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 读大于1000行数据
     *
     * @param filePath 文件路径
     * @return 读取的数据
     */
    public static List<Object> readMoreThan1000Row(String filePath) {
        return readMoreThan1000RowBySheet(filePath, null);
    }

    /**
     * 读大于1000行数据, 带样式
     *
     * @param filePath 文件觉得路径
     * @param sheet    sheet设置
     * @return 读取的数据
     */
    public static List<Object> readMoreThan1000RowBySheet(String filePath, Sheet sheet) {
        if (!StringUtils.hasText(filePath)) {
            return null;
        }

        sheet = sheet != null ? sheet : initSheet;

        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(filePath);
            ExcelListener excelListener = new ExcelListener();
            EasyExcelFactory.readBySax(fileStream, sheet, excelListener);
            return excelListener.getData();
        } catch (FileNotFoundException e) {
            log.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException e) {
                log.error("excel文件读取失败, 失败原因：{}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 读大于1000行数据, 带样式
     *
     * @param multipartFile 上传的文件
     * @param sheet         设置
     * @return 读取的数据
     */
    public static List<Object> readMoreThan1000RowBySheet(MultipartFile multipartFile, Sheet sheet) {

        sheet = sheet != null ? sheet : initSheet;


        CommonsMultipartFile cFile = (CommonsMultipartFile) multipartFile;
        DiskFileItem fileItem = (DiskFileItem) cFile.getFileItem();
        InputStream fileStream = null;
        try {
            fileStream = fileItem.getInputStream();
            ExcelListener excelListener = new ExcelListener();
            EasyExcelFactory.readBySax(fileStream, sheet, excelListener);
            return excelListener.getData();
        } catch (IOException e) {
            log.error("获取输入流出错！");
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException e) {
                log.error("excel文件读取失败, 失败原因：{}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel    文件
     * @param rowModel 实体类映射，继承 BaseRowModel 类
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel) {
        return readExcel(excel, rowModel, 1, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel       文件
     * @param rowModel    实体类映射，继承 BaseRowModel 类
     * @param sheetNo     sheet 的序号 从1开始
     * @param headLineNum 表头行数，默认为1
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, int sheetNo, int headLineNum) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = null;
        try {
            reader = getReader(excel, excelListener);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("excelReader 读取文件失败：{}", e.getMessage());
        }
        if (reader == null) {
            return null;
        }
        reader.read(new Sheet(sheetNo, headLineNum, rowModel.getClass()));
        return excelListener.getData();
    }

    /**
     * 读取指定sheetName的Excel(多个 sheet)
     *
     * @param excel     文件
     * @param rowModel  实体类映射，继承 BaseRowModel 类
     * @param sheetName sheet名字
     * @return Excel 数据 list
     */
    public static List<Object> readExcel(MultipartFile excel, BaseRowModel rowModel, String sheetName) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader reader = null;
        try {
            reader = getReader(excel, excelListener);
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("excelReader 读取文件失败：{}", e.getMessage());
        }
        if (reader == null) {
            return null;
        }
        for (Sheet sheet : reader.getSheets()) {
            if (rowModel != null) {
                sheet.setClazz(rowModel.getClass());
            }
            //读取指定名称的sheet
            if (sheet.getSheetName().contains(sheetName)) {
                reader.read(sheet);
                break;
            }
        }
        return excelListener.getData();
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel         需要解析的 Excel 文件
     * @param excelListener new ExcelListener()
     * @throws IOException 文本读取异常
     */
    private static ExcelReader getReader(MultipartFile excel, ExcelListener excelListener) throws IOException {
        String filename = excel.getOriginalFilename();
        if (filename != null && (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx"))) {
            InputStream is = new BufferedInputStream(excel.getInputStream());
            return new ExcelReader(is, null, excelListener, false);
        } else {
            return null;
        }
    }

    /**
     * 文件下载并且失败的时候返回json（默认失败了会返回一个有部分数据的Excel）
     *
     * @param response  请求
     * @param fileName  文件名
     * @param excelData 数据
     * @throws IOException IO异常
     */
    public static void downloadFailedUsingJson(HttpServletResponse response, String fileName, Map<String, List<List<Object>>> excelData) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            //fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            List<List<Object>> lists = excelData.get("head");
            List<List<String>> headList = new ArrayList<>();
            for (List<Object> objectList : lists) {
                List<String> columnHeadList = objectList.stream().map(String::valueOf).collect(Collectors.toList());
                headList.add(columnHeadList);
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream()).head(headList).autoCloseStream(Boolean.FALSE).sheet(fileName)
                    .doWrite(excelData.get("data"));
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<>(2);
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    /**
     * 生成excle
     *
     * @param filePath 绝对路径, 如：/home/user/Downloads/aaa.xlsx
     * @param data     数据源
     * @param head     表头
     */
    public static void writeBySimple(String filePath, List<List<Object>> data, List<String> head) {
        writeSimpleBySheet(filePath, data, head, null);
    }

    /**
     * 生成excle
     *
     * @param filePath 绝对路径, 如：/home/user/Downloads/aaa.xlsx
     * @param data     数据源
     * @param sheet    excle页面样式
     * @param head     表头
     */
    public static void writeSimpleBySheet(String filePath, List<List<Object>> data, List<String> head, Sheet sheet) {
        sheet = (sheet != null) ? sheet : initSheet;

        if (head != null) {
            List<List<String>> list = new ArrayList<>();
            head.forEach(h -> list.add(Collections.singletonList(h)));
            sheet.setHead(list);
        }

        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            outputStream = new FileOutputStream(filePath);
            writer = EasyExcelFactory.getWriter(outputStream);
            writer.write1(data, sheet);
        } catch (FileNotFoundException e) {
            log.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {
            try {
                if (writer != null) {
                    writer.finish();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException e) {
                log.error("excel文件导出失败, 失败原因：{}", e.getMessage());
            }
        }
    }

    /**
     * 生成excle
     *
     * @param filePath 绝对路径, 如：/home/user/Downloads/aaa.xlsx
     * @param data     数据源
     */
    public static void writeWithTemplate(String filePath, List<? extends BaseRowModel> data) {
        writeWithTemplateAndSheet(filePath, data, null);
    }

    /**
     * 生成excle
     *
     * @param filePath 绝对路径, 如：/home/user/Downloads/aaa.xlsx
     * @param data     数据源
     * @param sheet    excle页面样式
     */
    public static void writeWithTemplateAndSheet(String filePath, List<? extends BaseRowModel> data, Sheet sheet) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        sheet = (sheet != null) ? sheet : initSheet;
        sheet.setClazz(data.get(0).getClass());

        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            outputStream = new FileOutputStream(filePath);
            writer = EasyExcelFactory.getWriter(outputStream);
            writer.write(data, sheet);
        } catch (FileNotFoundException e) {
            log.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {
            try {
                if (writer != null) {
                    writer.finish();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("excel文件导出失败, 失败原因：{}", e.getMessage());
            }
        }

    }

    /**
     * 生成多Sheet的excle
     *
     * @param filePath              绝对路径, 如：/home/user/Downloads/aaa.xlsx
     * @param multipleSheelPropetys 多表Sheet属性
     */
    public static void writeWithMultipleSheel(String filePath, List<MultipleSheetProperty> multipleSheelPropetys) {
        if (CollectionUtils.isEmpty(multipleSheelPropetys)) {
            return;
        }

        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            outputStream = new FileOutputStream(filePath);
            writer = EasyExcelFactory.getWriter(outputStream);
            for (MultipleSheetProperty multipleSheetProperty : multipleSheelPropetys) {
                Sheet sheet = multipleSheetProperty.getSheet() != null ? multipleSheetProperty.getSheet() : initSheet;
                if (!CollectionUtils.isEmpty(multipleSheetProperty.getData())) {
                    sheet.setClazz(multipleSheetProperty.getData().get(0).getClass());
                }
                writer.write(multipleSheetProperty.getData(), sheet);
            }

        } catch (FileNotFoundException e) {
            log.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {
            try {
                if (writer != null) {
                    writer.finish();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("excel文件导出失败, 失败原因：{}", e.getMessage());
            }
        }

    }

    /**
     * 泛型事件分析监听器
     *
     * @param consumer  消费者对象-泛型声明实体类
     * @param threshold 阈值
     * @param <T>       实体类
     * @return 事件监听解析器
     */
    public static <T> AnalysisEventListener<T> getListener(Consumer<List<T>> consumer, int threshold) {
        return new AnalysisEventListener<T>() {
            private LinkedList<T> linkedList = new LinkedList<>();

            @Override
            public void invoke(T t, AnalysisContext analysisContext) {
                linkedList.add(t);
                if (threshold <= linkedList.size()) {
                    consumer.accept(linkedList);
                    linkedList.clear();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                if (linkedList.size() > 0) {
                    consumer.accept(linkedList);
                }
            }
        };
    }

    /**
     * 泛型事件分析监听器,默认阈值1000
     *
     * @param consumer 消费者对象-泛型声明实体类
     * @param <T>      实体类
     * @return 事件监听解析器
     */
    public static <T> AnalysisEventListener<T> getListener(Consumer<List<T>> consumer) {
        return getListener(consumer, 1000);
    }

}
