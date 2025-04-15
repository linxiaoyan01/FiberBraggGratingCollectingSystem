<template>
  <div id="centerBottom" class="centerBottom"></div>
</template>

<script lang="js">
import {defineComponent, onMounted, toRefs, reactive} from "vue";
import * as echarts from "echarts";
import axios from "axios";

export default defineComponent({
  name: 'line-chart-temperature',
  setup() {
    const state = reactive({
      option : {
        title: {
          text: '过去24小时温度最大最小值',
          textStyle:{
            color: '#ffffff',
            fontFamily: '宋体',
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        toolbox: {
          show: true,
          feature: {
            dataView: { readOnly: false },
            magicType: { type: ['line', 'bar'] },
            restore: {},
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data:  [-1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -11, -12, -14, -15, -16, -17, -18, -19, -20, -21, -22, -23, -24],
          axisLabel: {
            show: true,
            textStyle: {
              color: '#ffffff',  //更改坐标轴文字颜色
              fontSize : 12      //更改坐标轴文字大小
            }
          },
        },
        yAxis: {
          type: 'value',
          boundaryGap: [0, '100%'],
          axisLabel: {
            formatter: '{value} °C',
            show: true,
            textStyle: {
              color: '#ffffff',  //更改坐标轴文字颜色
              fontSize : 12      //更改坐标轴文字大小
            }
          }
        },
        legend: {
            orient:"vertical",
            backgroundColor: "transparent",
            inactiveColor: "white",
            textStyle:{
              color: "blue",
            },
            left: '60%'
        },
        series: [
          {
            name: 'Highest',
            type: 'line',
            data: [10, 11, 13, 11, 12, 12, 9, 8, 10, 5, 6, 8, 9, 12,15,18,19,10,16,44,22,12,45,6],
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' }
              ]
            },
            markLine: {
              data: [{ type: 'average', name: 'Avg' }]
            }
          },
          {
            name: 'Lowest',
            type: 'line',
            data: [-10, -11, -13, -11, -12, -12, -9, -8, -10, -5, -6, -8, -9, -12,-15,-18,-19,-10,-16,-44,-7, -24, -6,-10],
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' }
              ]
            },
            markLine: {
              data: [
                { type: 'average', name: 'Avg' },
                [
                  {
                    symbol: 'none',
                    x: '90%',
                    yAxis: 'max'
                  },
                  {
                    symbol: 'circle',
                    label: {
                      position: 'start',
                      formatter: 'Max'
                    },
                    type: 'max',
                    name: '最高点'
                  }
                ]
              ]
            }
          }
        ]
      }
    })
    function handleQuery(){
      // 生成模拟数据
      const maxArray = [];
      const minArray = [];
      for(let i = 0; i < 24; i++) {
        // 生成20-40度之间的随机温度
        maxArray.push(Math.round((Math.random() * 20 + 20) * 10) / 10);
        // 生成-10到10度之间的随机温度
        minArray.push(Math.round((Math.random() * 20 - 10) * 10) / 10);
      }
      return Promise.resolve({
        data: {
          content: maxArray.map((max, index) => ({
            max: max,
            min: minArray[index]
          }))
        }
      });
    }
    onMounted(async ()=>{
      let maxArray = []
      let minArray = []
      let data
      data = await handleQuery()
      data = data.data.content
      //console.log(data)
      for (let i = 0; i < data.length; i++) {
        maxArray.push(data[i].max)
        minArray.push(data[i].min)
      }
      state.option.series[0].data = maxArray
      state.option.series[1].data = minArray
      const chartDom = document.getElementById('centerBottom');
      let myChart = echarts.init(chartDom);
      state.option && myChart.setOption(state.option)
    })
    return{
      ...toRefs(state)
    }
  },
})
</script>

<style scoped>
.centerBottom{
  display: flex;
}
</style>