<template>
  <div id="line-3d-shape" class="line-3d-shape"></div>
</template>

<script>
import {defineComponent, onMounted} from "vue";
import * as echarts from 'echarts';
import 'echarts-gl';

export default defineComponent({
  name: 'm_line-3d-Shape',
  setup() {
    onMounted(()=>{
      var chartDom = document.getElementById('line-3d-shape');
      var myChart = echarts.init(chartDom);
      var option;

      var data = [];
// Parametric curve
      for (let t = 0; t < 25; t += 0.001) {
        const x = (1 + 0.25 * Math.cos(t)) * Math.cos(t);
        var y = (1 + 0.25 * Math.cos(t)) * Math.sin(t);
        var z = t + 2.0 * Math.sin(t);
        data.push([x, y, z]);
      }
      //console.log(data.length);
      option = {
        toolbox: {
          feature: {
            dataView: { show: true, readOnly: false },
            restore: { show: true },
            saveAsImage: { show: true },
          }
        },
        title: {
          text: '海缆实时形状',
          textStyle:{
            color: 'white',
            fontFamily: '宋体',
          },
        },
        tooltip: {},
        backgroundColor: 'transparent',
        visualMap: {
          show: false,
          dimension: 2,
          min: 0,
          max: 30,
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
          type: 'value',
          axisLabel: {
            show: true,
            textStyle: {
              color: 'white',  //更改坐标轴文字颜色
              fontSize : 12      //更改坐标轴文字大小
            }
          },
          nameTextStyle:{
            color: 'white'
          }
        },
        yAxis3D: {
          type: 'value',
          axisLabel: {
            show: true,
            textStyle: {
              color: 'white',  //更改坐标轴文字颜色
              fontSize : 12      //更改坐标轴文字大小
            }
          },
          nameTextStyle:{
            color: 'white'
          }
        },
        zAxis3D: {
          type: 'value',
          axisLabel: {
            show: true,
            textStyle: {
              color: 'white',  //更改坐标轴文字颜色
              fontSize : 12      //更改坐标轴文字大小
            }
          },
          nameTextStyle:{
            color: 'white'
          }
        },
        textStyle:{
          color : 'white'
        },
        grid3D: {
          viewControl: {
            projection: 'orthographic'
          }
        },
        series: [
          {
            type: 'line3D',
            data: data,
            lineStyle: {
              width: 4
            }
          }
        ]
      };
      window.onresize = myChart.resize;
      option && myChart.setOption(option);

    })
    return{
    }
  },
})
</script>
<style scoped>
.line-3d-shape{
  display: flex;
}
</style>