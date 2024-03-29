package top.kaluna.modbusTcp.runner;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
//import jdk.jpackage.internal.Log;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.kaluna.modbusTcp.domain.FbgValue;
import top.kaluna.modbusTcp.mapper.FbgValueMapper;
import top.kaluna.modbusTcp.service.WsService;
import top.kaluna.modbusTcp.util.NumTypeChangeUtil;
import top.kaluna.modbusTcp.websocket.WebSocketServer;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Order(1)
public class UdpCollectRunner  implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(UdpCollectRunner.class);
    @Resource
    private WsService wsService;
    @Resource
    private FbgValueMapper fbgValueMapper;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }
    public void start() throws InterruptedException, IOException {
        boolean started = true;
        // 创建服务端接收socket对象
        LOG.info("监听23端口开始");
        DatagramSocket ds = new DatagramSocket(23);
        // 创建数据包用于接收数据
        byte[] bytes = new byte[1024];
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
        // 调用DatagramSocket对象的方法接收数据
        Thread thread = new Thread(() -> {
            while (true) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    ds.receive(dp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long datetime  = new Date().getTime();
                // 解析数据包，并把数据在控制台显示
                byte[] datas = dp.getData();
                List<FbgValue> fbgValues = new ArrayList<>();
                //获取温度
                float t1 = NumTypeChangeUtil.bytesToFloat2(datas, 8);
                float t2 = NumTypeChangeUtil.bytesToFloat2(datas, 15);
                float t3 = NumTypeChangeUtil.bytesToFloat2(datas, 109);
                fbgValues.add(new FbgValue(1L,new BigDecimal(Float.toString(t1)),1, datetime, 1));
                fbgValues.add(new FbgValue(2L,new BigDecimal(Float.toString(t2)),1, datetime, 2));
                fbgValues.add(new FbgValue(3L,new BigDecimal(Float.toString(t3)),1, datetime, 6));
                //获取振动
                float vibration = NumTypeChangeUtil.bytesToFloat2(datas, 59);
                fbgValues.add(new FbgValue(4L,new BigDecimal(Float.toString(vibration)),1, datetime, 4));
                //获取应变
                List<FbgValue> channel3 = new ArrayList<>();
                int sequence = 5;int count = 1;
                for (int i = 20; i < 20 + 36; i = i + 6) {
                    float strain = NumTypeChangeUtil.bytesToFloat2(datas, i + 2);
                    channel3.add(new FbgValue((long)sequence,new BigDecimal(Float.toString(strain)),count, datetime, 3));
                    sequence++;
                    count++;
                }
                fbgValues.addAll(channel3);
                List<FbgValue> channel5 = new ArrayList<>();
                int sequence2 = 11;int count2 = 1;
                for (int i = 64; i < 64 + 42; i = i + 6) {
                    float strain = NumTypeChangeUtil.bytesToFloat2(datas, i + 2);
                    channel5.add(new FbgValue((long)sequence2,new BigDecimal(Float.toString(strain)),count2, datetime, 5));
                    sequence2++;
                    count2++;
                }
                fbgValues.addAll(channel5);
                //获取振动波长
                float vibration_wave =NumTypeChangeUtil.bytesToFloat3(datas, 57) / 1000 + 1520;
                fbgValues.add(new FbgValue(18L,new BigDecimal(Float.toString(vibration_wave)),1, datetime, 4));
                //websocket推送
                String logId = (String) MDC.get("LOG_ID");
                String jsonString = JSONObject.toJSONString(fbgValues, SerializerFeature.MapSortField);

                wsService.sendInfo(jsonString, logId);
                //存储到数据库
                fbgValueMapper.multipleInsert(fbgValues);
            }
        });
        thread.start();
        //ds.close();
    }
}
