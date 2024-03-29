package top.kaluna.modbusTcp.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.kaluna.modbusTcp.domain.*;

import top.kaluna.modbusTcp.mapper.BreakpointRecordCustMapper;
import top.kaluna.modbusTcp.mapper.BreakpointRecordFinishMapper;
import top.kaluna.modbusTcp.mapper.BreakpointRecordMapper;
import top.kaluna.modbusTcp.mapper.FbgValueInfoMapper;
import top.kaluna.modbusTcp.req.BreakpointRecordQueryReq;
import top.kaluna.modbusTcp.resp.BreakpointRecordQueryResp;
import top.kaluna.modbusTcp.resp.PageResp;
import top.kaluna.modbusTcp.util.CopyUtil;
import top.kaluna.modbusTcp.util.DateUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuery
 * @date 2022/3/5/0005 - 10:48
 */
@Service
public class BreakpointRecordService {
    private static final Logger LOG = LoggerFactory.getLogger(BreakpointRecordService.class);
    @Resource
    private BreakpointRecordCustMapper breakpointRecordCustMapper;
    @Resource
    private BreakpointRecordMapper breakpointRecordMapper;
    @Resource
    private BreakpointRecordFinishMapper breakpointRecordFinishMapper;
    @Resource
    private FbgValueInfoMapper fbgValueInfoMapper;
    public void insertBreakpointInfoByScanFbgValue(){
        Long createTime = DateUtil.getNowTime().getTime();
        breakpointRecordCustMapper.insertBreakpointInfoByScanPhysicalValue(createTime);
    }

    public PageResp<BreakpointRecordQueryResp> list(BreakpointRecordQueryReq req) {
        BreakpointRecordExample breakpointRecordExample = new BreakpointRecordExample();
        BreakpointRecordExample.Criteria criteria = breakpointRecordExample.createCriteria();

        criteria.andStateEqualTo(req.getState());
        List<BreakpointRecord> breakpointRecords = breakpointRecordMapper.selectByExample(breakpointRecordExample);

        PageHelper.startPage(req.getPage(), req.getPageSize());
        PageInfo<BreakpointRecord> pageInfo = new PageInfo<>(breakpointRecords);
        LOG.info("BreakpointRecord总行数：{}", pageInfo.getTotal());
        LOG.info("BreakpointRecord总页数：{}",pageInfo.getPages());

        final List<BreakpointRecordQueryResp> respsList = CopyUtil.copyList(breakpointRecords, BreakpointRecordQueryResp.class);
        PageResp<BreakpointRecordQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respsList);
        return pageResp;
    }

    public void update(Long id) {
        BreakpointRecordExample breakpointRecordExample = new BreakpointRecordExample();
        BreakpointRecordExample.Criteria criteria = breakpointRecordExample.createCriteria();

        criteria.andIdEqualTo(id);
        List<BreakpointRecord> breakpointRecords = breakpointRecordMapper.selectByExample(breakpointRecordExample);

        Byte state = 1;
        int arrayNum = 0;

        breakpointRecords.forEach((BreakpointRecord breakpointRecord)->{
            breakpointRecord.setState(state);
            breakpointRecord.setArrayNum(arrayNum);
            BreakpointRecordFinish breakpointRecordFinish = CopyUtil.copy(breakpointRecord, BreakpointRecordFinish.class);
            breakpointRecordFinish.setId(null);
            breakpointRecordFinishMapper.insert(breakpointRecordFinish);
        });
        breakpointRecordMapper.deleteByExample(breakpointRecordExample);
    }

    //在线的个数计算 将断点记录表中的所有(arrayNum-1)相加起来下来 由于有一些通道没有断点，将该通道下的所有传感器加起来
    public int calculateOnLine() {

        int sum = 0;
        List channelList = new ArrayList();
        final List<BreakpointRecord> breakpointRecords = breakpointRecordCustMapper.calculateOnline();
        List<FbgValueInfo> fbgValueInfos = fbgValueInfoMapper.selectAllRecord();

        for (int i = 0; i < breakpointRecords.size(); i++) {
            channelList.add(breakpointRecords.get(i).getChannel());
            sum += breakpointRecords.get(i).getArrayNum()-1;
        }
        List<Integer> channelUsed = new ArrayList<>();
        for (int i = 0; i < fbgValueInfos.size(); i++) {
            Integer channel = fbgValueInfos.get(i).getChannel();
            if(!channelList.contains(channel) && !channelUsed.contains(channel)){
                channelUsed.add(channel);
                sum += countFbgValueInfoSize(fbgValueInfos.get(i).getChannel());
            }
        }
        return sum;
    }
    public int countFbgValueInfoSize(int channel){
        FbgValueInfoExample fbgValueInfoExample = new FbgValueInfoExample();
        final FbgValueInfoExample.Criteria criteria = fbgValueInfoExample.createCriteria();
        criteria.andChannelEqualTo(channel);
        final List<FbgValueInfo> fbgValueInfos1 = fbgValueInfoMapper.selectByExample(fbgValueInfoExample);
        return fbgValueInfos1.size();
    }
}
