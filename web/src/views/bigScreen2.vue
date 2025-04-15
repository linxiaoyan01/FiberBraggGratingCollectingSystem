<template>
  <the-header></the-header>
  <a-layout-content style="padding: 0; background: #001529;  overflow-x: hidden;" :style="{margin: 0, height: height_top.height}">
    <div style="height:40px; display: flex;justify-content: space-between" >
      <dv-decoration-8 style="width:250px;height:30px;" />
        <div style="font-size: 28px; width:400px;color: #809ed4;text-align:center;display: inline-block;vertical-align: center;"  >
          <div>海底管道位移变形监测系统</div>
          <div style="transform: translate(-47px, 0px);"><dv-decoration-3 style="width:100%;height:20px;" /></div>
        </div>
      <dv-decoration-8 :reverse=true style="width:250px;height:30px;" />
    </div>
    <div class="warn-info">
      <ul>
        <li><div style="color: #809ed4; font-size: large">设备数量</div><div>7个</div></li>
        <li><div style="color: #809ed4; font-size: large">在线数量</div><div>7个</div></li></ul>
      <ul>
        <li>
          <div class="status-container">
            <div class="warn-text">
              <div class="status-title">
                <div class="light-img">
                  <span class="iconfont icon-dengpao"></span>
                </div>
                <div style="color: #809ed4; font-size: large">健康状态
                  <span><a-button size="small" type="link" class="wenhao" @click="showWarnInfoDialog">⚠</a-button></span>
                </div>
              </div>
              <div style="text-align: center">正常状态</div>
            </div>
          </div>
        </li>
      </ul>
    </div>
    <div class="container fontClass" style="margin-top: 10px">
      <div class="box2 top">
        <dv-border-box-7 class="border-box" >
          <div style="height: 100%; border-color: white">
            <div class="imgTop">
                <span>管道监测示意图</span>
            </div>
            <div style="height: calc(100% - 40px); width: 100%;">
              <div style="height: 100%; display: flex; justify-content: center; align-items: center; padding: 10px;">
                <img 
                  src="@/assets/图片2.png" 
                  style="max-width: 100%; max-height: 100%; width: auto; height: auto; object-fit: contain;" 
                  alt="海缆监测示意图"
                >
              </div>
            </div>
          </div>
        </dv-border-box-7>
      </div>
      <div class="box2 bottom" id="hisnext" >
        <div class="history-charts-row">
          <dv-border-box-7 class="border-box">
            <div class="chart-container">
              <vibration-wave ref="tempChart" class="history-chart"/>
            </div>
          </dv-border-box-7>
          <dv-border-box-7 class="border-box">
            <div class="chart-container">
              <right-bottom-chart ref="strainChart" class="history-chart"/>
            </div>
          </dv-border-box-7>
        </div>
      </div>
    </div>
  </a-layout-content>
  <n-modal v-model:show="showRealImg">
    <n-card
        style="width: 900px"
        title="实物图"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
    >
      <img src="@/assets/realImg2.png" style="width: 100%; height: auto" alt="">
    </n-card>
  </n-modal>
  <n-modal v-model:show="warnInfoDialog">
    <n-card
        style="width: 600px"
        title="说明"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
    >
      <p>正常状态：海缆各状态量处于规定的注意值以内，可以正常运行（低于状态量的阈值80%）</p>
      <p>注意状态：海缆的单项（或多项）状态量变化趋势朝标准限值方向发展，仍可继续运行，但应加强监视（介于状态量阈值的 80%-100%）</p>
      <p>异常状态：单项重要状态量变化较大，已略微超过标准限值，应监视运行，并适时安排检修(介于状态量阈值的100%-120%)</p>
      <p>严重状态：单项重要状态量严重超过标准限值，应尽快安排检修（高于状态量阈值的 120%）</p>
      <p>当整体健康状态是正常状态时，海缆本体和OFFSET的状态也是正常状态，灯泡显示绿色；
        当整体健康状态是注意状态时，灯泡显示黄色，并发送邮件提醒海缆本体和OFFSET的状态；
        当整体健康状态是异常状态时，灯泡显示红色，并发送邮件提醒海缆本体和OFFSET的状态；</p>
      <p>
        温度阈值：50℃<br>
        位移最大变化量：16cm<br>
        应变值阈值：±200微应变<br>
        振动信号短时能量阈值：1dB<br>
      </p>
    </n-card>
  </n-modal>
</template>
<script lang="ts">
import {defineComponent, onMounted, ref, nextTick} from "vue";
import * as echarts from 'echarts';
import CenterTop from '../components/centerTopChart.vue'
import rightBottomChart from '../components/rightBottomChart.vue'
import LineChartTemperature from '../components/centerBottomChart.vue'
import LineChartStrain from '../components/leftTopChart.vue'
import Line3dShape from '../components/rightBottomChart.vue'
import LineChartVibration from '../components/rightTopChart.vue'
import RealTemperature from '../components/leftBottomChart.vue'
import TheHeader from '@/components/theHeader.vue';
import TheFooter from '@/components/theFooter.vue';
import MaxOffset from '@/components/maxOffset.vue';
import VibrationWave from '@/components/vibrationWave.vue';
import ThreeTemperature from "@/components/threeTemperature.vue";
import functionCall from "@/functionCall";
import temperatureHistory from "@/components/temperatureHistory.vue";
import strainHistory from "@/components/strainHistory.vue";
import shortTimeEnergy from "@/components/shortTimeEnergy.vue";
let showCurvePrev = ref(true);
let showCurveNext = ref(false);
let showCurveHis = ref(false);
let showHisNext = ref(false);
let showRealImg = ref(false);
let warnInfoDialog = ref(false);
let clickTime = 1;
const pageTurning = ()=>{
  if(showCurvePrev.value === true && showCurveNext.value === false){
    showCurvePrev.value = false;
    showCurveNext.value = true;
    showCurveHis.value = false;
  } else{
    showCurvePrev.value = true;
    showCurveNext.value = false;
    showCurveHis.value = false;
    showHisNext.value = false;
  }
}
const HispageTurning = () => {
  if (showCurvePrev.value) {
    showCurvePrev.value = false;
    showHisNext.value = true;
    nextTick(() => {
      const charts = document.querySelectorAll('.history-chart, #threeTemperature, #rightBottomChart');
      charts.forEach(chart => {
        const instance = echarts.getInstanceByDom(chart as HTMLElement);
        if (instance) {
          instance.resize();
        }
      });
    });
  } else if (showHisNext.value) {
    showCurvePrev.value = true;
    showHisNext.value = false;
    nextTick(() => {
      const charts = document.querySelectorAll('#threeTemperature, #rightBottomChart');
      charts.forEach(chart => {
        const instance = echarts.getInstanceByDom(chart as HTMLElement);
        if (instance) {
          instance.resize();
        }
      });
    });
  }
}
const imgTurning = () => {
  showRealImg.value = true;
}
const showWarnInfoDialog = () => {
  warnInfoDialog.value = true;
}
export default defineComponent({
  components:{
    CenterTop,
    rightBottomChart,
    LineChartTemperature,
    LineChartVibration,
    LineChartStrain,
    Line3dShape,
    RealTemperature,
    TheHeader,
    TheFooter,
    MaxOffset,
    VibrationWave,
    ThreeTemperature,
    temperatureHistory,
    strainHistory,
    shortTimeEnergy
  },
  setup(){
    const height_top = ref({
      height: ''
    });
    const tempChart = ref(null);
    const strainChart = ref(null);
    onMounted(async ()=>{
      const { data } = await new functionCall().handleQueryOnline();
      if(window.innerHeight != null){
        height_top.value.height = window.innerHeight - 64 +'px';
      }
      window.onresize = () => {
        return (() => {
          height_top.value.height = window.innerHeight - 64 +'px';
        })();
      };
    })
    return{
      height_top,
      functionCall,
      showCurvePrev,
      showCurveNext,
      showCurveHis,
      showHisNext,
      showRealImg,
      warnInfoDialog,
      pageTurning,
      HispageTurning,
      imgTurning,
      showWarnInfoDialog,
      tempChart,
      strainChart
    }
  }
});
</script>
<style scoped>

.light-img{
  display: flex;
  align-items: center;
  margin-left: 10px;
}
.warn-text{
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.icon-dengpao{
  color: green;
  font-size: 32px;
  animation: blink 500ms linear infinite;
  display: inline-block;
  vertical-align: middle;
}
@keyframes blink {
  0%{opacity: 1;}
  100%{opacity: 0}
}
/*.icon-wenhao{*/
/*  background-color: transparent;*/
/*  border-color: transparent;*/
/*  cursor: pointer;*/
/*}*/

.wenhao{
  padding: 0 4px;
  height: auto;
  line-height: 1;
}
.light-right{
  vertical-align: center;
}
.warn-info{
  margin-top: 10px;
  height:60px;
  display: flex;
  justify-content: space-between
}
.fontClass{
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  color:  #809ed4;
}
li{
  display: inline-block;
  height: 60px;
  margin-left: 100px;
  text-align:center;
  color: #809ed4;
  vertical-align: center;
}
ul:last-child{
  margin-right: 100px;
}
.imgTop{
  font-size: large;
  margin-left:10px;
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  justify-content: space-between
}
.container{
  display: flex;
  flex-direction: column;
}
.top{
  height: 30%;
  width:100%;
}
.bottom{
  height: 40%;
  display: flex;
  flex-direction: row;
}
.columnCenter{
  height: 20%;
  font-size: xx-small;
}
.box{
  display: flex;
  flex-direction: column;
}
.box2{
  display: flex;
  flex-direction: row;
}
.charts{
  width: 94%;
  margin: 3%;
  display: flex;
}
#pie.charts{
  height: 100%;
  width: 100%;
}
img {
  width: 100%;
  height: auto;
}
.border-box{
  width: 100%;
  height: 100%;
  display: flex;
  padding: 15px;
  box-sizing: border-box;
}
.spacial-border-box{
  width: 100%;
  height: fit-content;
  display: flex;
}
.box2.top {
  height: 30%;
  width: 100%;
  margin-bottom: 10px;
}
.border-box > div {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 1;
}

/* 按钮样式 */
:deep(.ant-btn) {
  color: #809ed4;
  border-color: #809ed4;
  &:hover {
    color: #c48d29;
    border-color: #c48d29;
  }
}

/* 边框盒子样式 */
:deep(.dv-border-box-7) {
  --border-color: #809ed4;
}

/* 装饰组件样式 */
:deep(.dv-decoration-8),
:deep(.dv-decoration-3) {
  --color: #809ed4;
  --accent-color: #809ed4;
}

.status-container {
  display: flex;
  align-items: center;
}

.status-title {
  display: flex;
  align-items: center;
}

.light-img {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.warn-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.history-charts-row {
  display: flex;
  width: 100%;
  height: 100%;
  gap: 20px;
}

.history-charts-row .border-box {
  flex: 1;
  height: 100%;
  min-width: 0;
}

.history-chart {
  width: 100% !important;
  height: 100% !important;
  position: absolute;
  top: 0;
  left: 0;
}

#hisnext {
  flex-direction: column;
  height: 400px;
  padding: 20px;
  box-sizing: border-box;
}

.chart-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.box2.bottom {
  height: 510px;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  box-sizing: border-box;
}

.charts-row {
  display: flex;
  width: 100%;
  height: 50%;
  gap: 20px;
}

.charts-row .border-box {
  flex: 1;
  height: 100%;
  min-width: 0;
}

.charts {
  width: 100%;
  height: 100% !important;
  margin: 0;
}

</style>