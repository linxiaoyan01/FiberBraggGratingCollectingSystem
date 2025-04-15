//package top.kaluna.modbusTcp.runner;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import org.jboss.logging.MDC;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import top.kaluna.modbusTcp.domain.FbgValue;
//import top.kaluna.modbusTcp.mapper.FbgValueMapper;
//import top.kaluna.modbusTcp.service.WsService;
//import top.kaluna.modbusTcp.util.NumTypeChangeUtil;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static top.kaluna.modbusTcp.util.ReadXlsxUtil.readxlsxVal;
//
//@Component
//@Order(1)
//public class UdpCollectRunner  implements ApplicationRunner {
//    private static final Logger LOG = LoggerFactory.getLogger(UdpCollectRunner.class);
//    @Resource
//    private WsService wsService;
//    @Resource
//    private FbgValueMapper fbgValueMapper;
//    @Resource
//    private RedisTemplate redisTemplate;
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        start();
//    }
//    public void start() throws InterruptedException, IOException {
//        boolean started = true;
//        // 创建服务端接收socket对象
//        LOG.info("监听23端口开始");
//        DatagramSocket ds = new DatagramSocket(23);
//        // 创建数据包用于接收传感器物理值数据
//        byte[] bytes = new byte[1024];
//        DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
//        //接受振动波长数据
//        byte[] waveBytes = new byte[1024];
//        DatagramPacket waveDp = new DatagramPacket(waveBytes, bytes.length);
//        // 调用DatagramSocket对象的方法接收数据
//
//        Thread thread = new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    ds.receive(dp);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                long datetime  = new Date().getTime();
//                // 解析数据包，并把数据在控制台显示
//                byte[] datas = dp.getData();
//                List<FbgValue> fbgValues = new ArrayList<>();
//                //获取温度
//                float t1 = NumTypeChangeUtil.bytesToFloat2(datas, 8);
//                float t2 = NumTypeChangeUtil.bytesToFloat2(datas, 15);
//                float t3 = NumTypeChangeUtil.bytesToFloat2(datas, 109);
//                fbgValues.add(new FbgValue(1L,new BigDecimal(Float.toString(t1)),1, datetime, 1));
//                fbgValues.add(new FbgValue(2L,new BigDecimal(Float.toString(t2)),1, datetime, 2));
//                fbgValues.add(new FbgValue(3L,new BigDecimal(Float.toString(t3)),1, datetime, 6));
//                //获取振动
//                float vibration = NumTypeChangeUtil.bytesToFloat2(datas, 59);
//                fbgValues.add(new FbgValue(4L,new BigDecimal(Float.toString(vibration)),1, datetime, 4));
//                //获取应变
//                List<FbgValue> channel3 = new ArrayList<>();
//                int sequence = 5;int count = 1;
//                for (int i = 20; i < 20 + 36; i = i + 6) {
//                    float strain = NumTypeChangeUtil.bytesToFloat2(datas, i + 2);
//                    channel3.add(new FbgValue((long)sequence,new BigDecimal(Float.toString(strain)),count, datetime, 3));
//                    sequence++;
//                    count++;
//                }
//                fbgValues.addAll(channel3);
//                List<FbgValue> channel5 = new ArrayList<>();
//                int sequence2 = 11;int count2 = 1;
//                for (int i = 64; i < 64 + 42; i = i + 6) {
//                    float strain = NumTypeChangeUtil.bytesToFloat2(datas, i + 2);
//                    channel5.add(new FbgValue((long)sequence2,new BigDecimal(Float.toString(strain)),count2, datetime, 5));
//                    sequence2++;
//                    count2++;
//                }
//                fbgValues.addAll(channel5);
//                //获取振动波长
//                float vibration_wave =NumTypeChangeUtil.bytesToFloat3(datas, 57) / 1000 +1520;
//                fbgValues.add(new FbgValue(18L,new BigDecimal(Float.toString(vibration_wave)),1, datetime, 4, 1));
//                //websocket推送
//                String logId = (String) MDC.get("LOG_ID");
//                String jsonString = JSONObject.toJSONString(fbgValues, SerializerFeature.MapSortField);
//                System.out.println("线程2："+jsonString);
//                wsService.sendInfo(jsonString, logId);
//                //存储到数据库
//                fbgValueMapper.multipleInsert(fbgValues);
//            }
//        });
//        //新启一个线程专门收集振动波长 每5秒渲染一次页面
//        AtomicInteger count = new AtomicInteger(1);
//        Thread vibrationWaveThread = new Thread(() ->{
//            while (true) {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    ds.receive(waveDp);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                // 解析数据包，并把数据在控制台显示
//                byte[] datas = waveDp.getData();
//                //获取振动波长
//                float vibrationWave =NumTypeChangeUtil.bytesToFloat3(datas, 57) / 1000 +1520;
//                String key = "viberationWave";
//                if(count.get() <= 500){
//                    redisTemplate.opsForList().rightPush(key, vibrationWave);
//                    count.getAndIncrement();
//                }
//                if(count.get() == 501){
//                    LOG.info("成功放入redis,key为{},放入{}条", key, 500);
//                    count.set(1);
//                    List list = redisTemplate.opsForList().range(key, 0, -1);
//                    redisTemplate.delete(key);
//                    LOG.info("500条数据：{}", list.toString());
//                    //websocket推送
//                    String logId = (String) MDC.get("LOG_ID");
//                    String jsonString = JSONObject.toJSONString(list, SerializerFeature.MapSortField);
//                    wsService.sendVibrationWaveInfo(jsonString, logId);
//                }
//            }
//        });
//
//        Thread MEMSThread =new Thread(() ->{
//            while(true){
//                List<Double> MEMSvalue = new ArrayList<>();
//                for(int i=2;i<23;i=i+4){
//                    try {
//                        MEMSvalue=readxlsxVal("/root/fbgData/data.xlsx",i,0);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    String logId = (String) MDC.get("LOG_ID");
//                    String jsonString = JSONObject.toJSONString(MEMSvalue, SerializerFeature.MapSortField);
//                    wsService.sendMEMSInfo(jsonString, logId);
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        thread.start();
//        vibrationWaveThread.start();
//        MEMSThread.start();
//        //ds.close();
//    }
//
//}
