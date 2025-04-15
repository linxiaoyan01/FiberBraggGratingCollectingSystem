<template>
  <the-header></the-header>
  <a-layout-content style="padding: 0; background: #001529;  overflow-x: hidden;" :style="{margin: 0, height: height_top.height}">
    <div style=" height:60px; display: flex;justify-content: space-between" >
      <dv-decoration-8 style="width:350px;" />
        <div style="font-size: 30px; width:400px;color: cadetblue;text-align:center;display: inline-block;vertical-align: center;"  >
          <div>海底电缆智能健康监测系统</div>
          <div style="transform: translate(-47px, 0px);"><dv-decoration-3 style="width:100%;height:30px;" /></div>
        </div>
      <dv-decoration-8 :reverse=true style="width:350px;" />
    </div>
    <div class="warn-info">
      <ul>
        <li><div style="color: cadetblue; font-size: x-large">设备数量</div><div>54个</div></li>
        <li><div style="color: cadetblue; font-size: x-large">在线数量</div><div>54个</div></li></ul>
      <ul>
        <li>
          <div class="warn-text">
            <div style="color: cadetblue; font-size: x-large">健康状态
              <span><a-button size="small" type="link" class="wenhao" @click="showWarnInfoDialog" style="visibility: visible">⚠</a-button></span>
            </div>
            <div style="padding-right: 30px">正常状态</div>
          </div>
          <div class="light-img">
            <span class="iconfont icon-dengpao"></span>
          </div>
        </li>
      </ul>
    </div>
    <div class="container fontClass" style="margin-top: 10px">
      <div class="box2 top">
        <dv-border-box-7 class="border-box" >
          <div style="height: 300px; border-color: white">
            <div class="imgTop" >
                <span >海缆监测示意图</span>
                <span style="right: 0px">
                  <a-button @click="imgTurning">实物图预览</a-button>
                </span>
            </div>
            <img src="@/assets/拱形.png" style="height: 100%; width: 100%" alt="">
          </div>
        </dv-border-box-7>
      </div>
      <div class="box2 bottom" id="prev" v-show="showCurvePrev">
          <dv-border-box-7 class="border-box">
            <line-chart-strain class="charts" style="height: 400px"/>
          </dv-border-box-7>
          <dv-border-box-7 class="border-box">
            <three-temperature class="charts" style="height: 400px"></three-temperature>
          </dv-border-box-7>
          <dv-border-box-7 class="border-box">
            <vibration-wave class="charts" style="height: 400px;"></vibration-wave>
          </dv-border-box-7>
          <dv-border-box-7 class="border-box">
            <line3d-shape class="charts" style="height: 400px;"/>
          </dv-border-box-7>
      </div>
      <div class="box2 bottom" id="next" v-show="showCurveNext">
        <dv-border-box-7 class="border-box">
          <line-chart-vibration class="charts" style="height: 400px; width: 600px"/>
        </dv-border-box-7>
        <dv-border-box-7 class="border-box">
          <shortTimeEnergy class="charts" style="height: 400px; width:600px"></shortTimeEnergy>
        </dv-border-box-7>
        <dv-border-box-7 class="border-box">
          <line-chart-temperature class="charts" style="height: 400px; width:600px"></line-chart-temperature>
        </dv-border-box-7>
      </div>
      <div class="box2 bottom" id="his" v-show="showCurveHis">
        <dv-border-box-7 class="border-box">
          <temperatureHistory class="charts" style="height:400px; width: 600px"></temperatureHistory>
        </dv-border-box-7>
        <dv-border-box-7 class="border-box">
          <strainHistory class="charts" style="height:400px; width: 600px"></strainHistory>
        </dv-border-box-7>
        <dv-border-box-7 class="border-box">
          <max-offset   class="charts" style="height:400px; width: 600px"/>
        </dv-border-box-7>
      </div>
      <div class="box2 bottom" id="hisnext" v-show="showHisNext">
        <dv-border-box-7 class="border-box">
          <vibrationHis class="charts" style="height:400px; width: 800px"></vibrationHis>
        </dv-border-box-7>
        <dv-border-box-7 class="border-box">
          <shortTimeHis class="charts" style="height:400px; width: 800px"></shortTimeHis>
        </dv-border-box-7>
      </div>
      <div style="display: flex; flex-direction: row-reverse; margin-bottom: 0px" >
        <a-button @click="pageTurning">曲线图翻页</a-button>
        <!-- <a-button @click="HispageTurning">历史数据图</a-button> -->
      </div>
    </div>
  </a-layout-content>
  <n-modal v-model:show="showRealImg">
    <n-card
        style="width: 600px"
        title="实物图"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
    >
      <img src="@/assets/realImg.png" style="height: 100%; width: 100%" alt="">
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
<script>
import {defineComponent, onMounted, ref} from "vue";
import CenterTop from '../components/centerTopChart.vue'
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
import shortTimeEnergy from "@/components/shortTimeEnergy";
import shortTimeHis from "@/components/shortTimeHis";
import vibrationHis from "@/components/vibrationHis";
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
const HispageTurning = ()=>{
  if(showCurvePrev.value === true || showCurveNext.value == true){
    showCurvePrev.value = false;
    showCurveNext.value = false;
    showCurveHis.value = true;
    showHisNext.value =false;
  } else if(showCurveHis.value === true){
    showCurvePrev.value = false;
    showCurveNext.value = false;
    showCurveHis.value = false;
    showHisNext.value = true;
  }else{
    showCurvePrev.value = false;
    showCurveNext.value = false;
    showCurveHis.value = true;
    showHisNext.value = false;
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
    strainHistory,
    shortTimeEnergy,
    shortTimeHis,
    vibrationHis,
    temperatureHistory
  },
  setup(){
    const height_top = ref({
      height: ''
    });
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
      showWarnInfoDialog
    }
  }
});
</script>
<style scoped>

.light-img{
  float: right;
  padding-top: 0;
}
.warn-text{
  float: left;
}
.icon-dengpao{
  color: green;
  font-size: xxx-large;
  animation: blink 500ms linear infinite;
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
  padding-left: 0;
  border-color: transparent;
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
  color:  cadetblue;
}
li{
  display: inline-block;
  height: 60px;
  margin-left: 100px;
  text-align:center;
  color: white;
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
img{
  width: auto;
  height: auto;
  max-width: 98%;
  margin-top: 1%;
  margin-left: 1%;
  margin-right: 1%;
  max-height: 64%;
}
.border-box{
  width: 100%;
  height: 100%;
  display: flex;
}
.spacial-border-box{
  width: 100%;
  height: fit-content;
  display: flex;
}

</style>