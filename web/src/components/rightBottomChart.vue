<template>
    <div id="rightBottomChart" class="rightBottomChart"></div>
</template>

<script>
import {defineComponent, onMounted} from "vue";
import * as echarts from 'echarts';
import "echarts-gl"
import {all, create} from 'mathjs'
import axios from "axios";
import interp1 from 'interp1';
import {Tool} from "@/util/tool";
const config = {}
const math = create(all, config)
function queryLast15() {
  return axios.get("/txt/queryLast15")
}

const Spline = require('cubic-spline');
async function generateData(queryLast15Result) {
  /*******15*3******/
  queryLast15Result = queryLast15Result.data.content
  //console.log("queryLast15Result", queryLast15Result)
  let data = Array.from(Array(15), () => new Array(3));
  for (let i = 0; i < 15; i++) {
    data[i][0] = queryLast15Result[i].aX;
    data[i][1] = queryLast15Result[i].aY;
    data[i][2] = queryLast15Result[i].aZ;
  }
  let datajs1 = data;

  let angleXY = []
  for (let i = 0; i < 15; i++) {
    angleXY[i] = math.acos((datajs1[i][1]) / Math.sqrt(Math.pow(datajs1[i][0], 2) + Math.pow(datajs1[i][1], 2) + Math.pow(datajs1[i][2], 2)))
  }
  let angle_raw3 = angleXY;
  let l3 = 1;
  let p_before_test3 = math.matrix([0, 0, 0]);
  let H3 = math.matrix([[1, 0, 0], [0, 1, 0], [0, 0, 1]]);
  let p_test3 = Array.from(Array(3), () => new Array(15));
  p_test3 = math.subset(p_test3, math.index(0,0), 0);
  p_test3 = math.subset(p_test3, math.index(1,0), 0);
  p_test3 = math.subset(p_test3, math.index(2,0), 0);
  let angle3 = [];
  let p_after_test3 = math.matrix();
  let p_temp3 =  math.matrix();
  for (let row3 = 0; row3 < angle_raw3.length - 1; row3++) {
    angle3[row3] = (angle_raw3[row3 + 1] - angle_raw3[row3]) / 180 * Math.PI;
    let R3 = Math.abs(l3 / angle3[row3])
    p_temp3 = math.matrix();
    if (angle3[row3] === 0) {
      p_temp3 = math.matrix([l3, 0, 0]);
    } else if (angle3[row3] > 0) {
      p_temp3 =  math.transpose(math.matrix([R3 * Math.sin(angle3[row3]), 0, -(R3 - R3 * Math.cos(angle3[row3]))]));
    } else{
      p_temp3 =  math.transpose(math.matrix([-R3 * Math.sin(angle3[row3]), 0, (R3 - R3 * Math.cos(angle3[row3]))]));
    }
    p_after_test3 = math.add(math.multiply(H3, p_temp3), math.transpose(p_before_test3));
    p_test3 = math.subset(p_test3, math.index(0,row3+1), math.subset(p_after_test3, math.index(0)));
    p_test3 = math.subset(p_test3, math.index(1,row3+1), math.subset(p_after_test3, math.index(1)));
    p_test3 = math.subset(p_test3, math.index(2, row3+1), math.subset(p_after_test3,math.index(2)));
    p_before_test3 = math.transpose(p_after_test3);
    let delta_R2 = math.matrix([[math.cos(angle3[row3]), 0, math.sin(angle3[row3])],
      [0, 1, 0], [-math.sin(angle3[row3]), 0, math.cos(angle3[row3])]]);
    H3 = math.multiply(H3, delta_R2);
  }
  let addMatrix = math.matrix([[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0,0,0,0.015, 0.016, 0.013, 0.011]])
  p_test3 = math.add(p_test3, addMatrix);
  // console.log("angle3", angle3)
  // console.log("p_test3",p_test3)
  // console.log("p_before_test3",p_before_test3)
  // console.log("p_after_test3",  p_after_test3)
  // console.log("p_temp3", p_temp3)
  //let x13 = Math.min(p_test3[1]):k13:Math.max(p_test3[1]);
  //p_test3[1]实际x轴范围 p_test3[3]：函数   x13 实际要插值的范围  spline：三次样条插值
  //let y13 = math.inte(p_test3[1],p_test3[3],x13,'spline');
  let xs = []
  let row0 = math.subset(p_test3, math.index(0,[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]));
  for(let i = 0; i < 15; i++){
    xs.push(row0.get([0,i]));
  }
  xs = xs.map(Number);
  //console.log("xs", xs)
  let row2 = math.subset(p_test3, math.index(2,[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]));
  let vs = [];
  for(let i = 0; i < 15; i++){
    vs.push(row2.get([0,i]));
  }
  //console.log("vs", vs)
  let k13 = (Math.max(...xs) - Math.min(...xs)) / 1000;
  //console.log("k13", k13)
  let xqs = [];
  for(let i = Math.min(...xs); i < Math.max(...xs); i = i + k13 ){
    xqs.push(i)

  }
  //let vqs = interp1(xs, vs, xqs, "nearest");
  const spline = new Spline(xs, vs);

  let resultData = [];
  // for (let i = 0; i < xqs.length; i++) {
  //   let row = []
  //   row.push(xqs[i]);
  //   row.push(vqs[i]);
  //   resultData.push(row);
  // }
  let z = 0;
  for (let i = 0; i < 15; i++) {
    let row = []
    row.push(xs[i]);
    row.push(z);
    row.push(vs[i]);
    resultData.push(row);
  }
  // for (let i = 0; i < xqs.length; i++) {
  //   let row = []
  //   row.push(xqs[i]);
  //   row.push(spline.at(xqs[i]));
  //   resultData.push(row);
  // }
  //console.log("resultData", resultData)
  return resultData;
}

function generateSimulatedData() {
  const data = [];
  // 生成30个点的模拟数据
  for (let i = 0; i < 30; i++) {
    // 使用正弦函数生成波浪形状
    const x = i;
    const y = 0;
    const z = Math.sin(i * 0.5) * 2 + Math.random() * 0.5 - 1.5; // 生成-3到0之间的波浪形状
    data.push([x, y, z]);
  }
  return data;
}

export default defineComponent({
  name: 'rightBottomChart',
  setup() {
    onMounted(async () => {
      const chartDom = document.getElementById('rightBottomChart');
      const myChart = echarts.init(chartDom);
      let option;
      
      option = {
        toolbox: {
          feature: {
            // dataView: { show: true, readOnly: false },
            // restore: { show: true },
            // saveAsImage: { show: true },
          }
        },
        title: {
          text: '海底电缆实时形状',
          textStyle: {
            color: '#ffffff',
            fontFamily: '宋体',
            fontSize: 14,
          },
          left: 10,
          top: 5,
        },
        tooltip: {},
        backgroundColor: 'transparent',
        visualMap: {
          show: false,
          dimension: 2,
          min: -3,
          max: 0,
          inRange: {
            color: [
              '#313695',
              '#4575b4',
              '#74add1',
              '#abd9e9',
              '#e0f3f8',
              '#ffffbf',
              '#fee090',
              '#fdae61',
              '#f46d43',
              '#d73027',
              '#a50026'
            ]
          }
        },
        xAxis3D: {
          name: 'x',
          type: 'value',
          axisLabel: {
            show: true,
            textStyle: {
              color: '#ffffff',
              fontSize : 12
            }
          },
          nameTextStyle:{
            color: '#ffffff'
          }
        },
        yAxis3D: {
          name: 'y',
          type: 'value',
          axisLabel: {
            show: true,
            textStyle: {
              color: '#ffffff',
              fontSize : 12
            }
          },
          nameTextStyle:{
            color: '#ffffff'
          }
        },
        zAxis3D: {
          name: '长\n度\n(m)',
          nameLocation: 'center',
          nameGap: 30,
          max: 0.5,
          min: -2,
          type: 'value',
          axisLabel: {
            show: true,
            textStyle: {
              color: '#ffffff',
              fontSize : 12
            }
          },
          nameTextStyle:{
            color: '#ffffff'
          }
        },
        textStyle:{
          color : '#ffffff'
        },
        grid3D: {
          viewControl: {
            projection: 'orthographic'
          }
        },
        series: [
          {
            type: 'line3D',
            smooth: 'spline',
            clip: true,
            symbol: "emptyCircle",
            symbolSize: 4,
            symbolRotate: null,
            showSymbol: true,
            data: generateSimulatedData(),
            lineStyle: {
              width: 4
            }
          }
        ]
      };

      // 初始更新
      myChart.setOption(option);

      // 定时更新数据
      function updateChart() {
        option.series[0].data = generateSimulatedData();
        myChart.setOption(option);
      }

      // 每3秒更新一次数据
      setInterval(updateChart, 3000);

      // 窗口大小改变时重置图表大小
      window.addEventListener('resize', () => {
        myChart.resize();
      });
    })

    return {}
  },
})
</script>
<style scoped>
.rightBottomChart{
  width: 500px;
  height: 500px;
  display: flex;
}
</style>