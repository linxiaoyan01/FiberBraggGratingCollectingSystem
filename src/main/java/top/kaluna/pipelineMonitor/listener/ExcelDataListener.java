package top.kaluna.pipelineMonitor.listener;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kaluna.pipelineMonitor.domain.ExcelData;
import top.kaluna.pipelineMonitor.service.ExcelService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuery
 * @date 2022/5/19/0019 - 11:48
 */
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class ExcelDataListener implements ReadListener<ExcelData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    private ExcelService excelService;
    List<ExcelData> list = new ArrayList<>();

    public  ExcelDataListener(ExcelService excelService){
        this.excelService = excelService;
    }

    @Override
    public void invoke(ExcelData data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            //saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        LOGGER.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            LOGGER.error("第{}行，第{}列解析异常", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex());
        }
    }
    /**
     * 所有数据解析完成了 都会来调用
     * @param analysisContext
     */
    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成！");
    }
    /**
     * 加上存储数据库
     */
    private void saveData() throws IOException {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        excelService.save(list);
        LOGGER.info("存储数据库成功！");
    }
}
