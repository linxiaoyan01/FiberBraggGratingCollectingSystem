<template>
  <div id="threeTemperature" class="threeTemperature"></div>
</template>
<script lang="js">
import * as echarts from 'echarts';
import {defineComponent, onMounted, ref, onUnmounted} from "vue";
import axios from "axios";
import {Tool} from "@/util/tool";
function dateToGMT(strDate){
  const dateStr = strDate.split(" ");
  const strGMT = dateStr[0] + " " + dateStr[1] + " " + dateStr[2] + " " + dateStr[5] + " " + dateStr[3] + " GMT+0800";
  const date = new Date(Date.parse(strGMT));
  return date;
}
export default defineComponent({
  name: 'threeTemperature',
  setup: function () {
    function handleQueryList(startTime, endTime) {
      return axios.get("/txt/listLastHourFortemperature", {
        params: {
          startTime: startTime,
          endTime: endTime,
          category: 3
        }
      })
    }
    function getData (value){
      let now = new Date();  // 直接创建Date对象，不需要+号
      const valueName = now.getFullYear() + '/' + (now.getMonth() + 1) + '/' + now.getDate() +
          ' ' + (now.getHours() >= 10 ? now.getHours() : '0' + now.getHours()) + ':' +
          (now.getMinutes() >= 10 ? now.getMinutes() : '0' + now.getMinutes()) + ':' +
          (now.getSeconds() >= 10 ? now.getSeconds() : '0' + now.getSeconds());
      return {
        name: valueName,
        value: [
          valueName,
          Math.round(value * 100) / 100
        ]
      }
    }

    // 生成模拟温度数据
    function generateSimulatedTemperature(baseTemp, variance) {
      return baseTemp + (Math.random() * variance * 2 - variance);
    }

    onMounted(() => {
      const chartDom = document.getElementById('threeTemperature');
      console.log('chartDom:', chartDom); // 调试信息

      if (!chartDom) {
        console.error('找不到图表容器元素');
        return;
      }

      let myChart = echarts.init(chartDom);
      console.log('myChart:', myChart); // 调试信息

      let temperature1 = [];
      let temperature2 = [];
      let temperature3 = [];

      // 生成初始数据
      for(let i = 0; i < 5; i++) {
        const temp1 = generateSimulatedTemperature(25, 2);
        const temp2 = generateSimulatedTemperature(23, 1.5);
        const temp3 = generateSimulatedTemperature(22, 1);
        
        const data1 = getData(temp1);
        const data2 = getData(temp2);
        const data3 = getData(temp3);
        
        temperature1.push(data1);
        temperature2.push(data2);
        temperature3.push(data3);
      }

      console.log('初始数据:', {temperature1, temperature2, temperature3}); // 调试信息

      const option = {
        color: ['#3366CC', '#FFCC99','#99CC33'],
        legend: {
          show: true,
          orient: 'horizontal',
          right: 'right',
          top: 5,
          textStyle: {
            color: '#ffffff',
            fontFamily: '宋体',
          },
        },
        title: {
          text: '海底电缆实时温度值',
          textStyle: {
            color: '#ffffff',
            fontFamily: '宋体',
            fontSize: 14,
          },
          left: 10,
          top: 5,
        },
        toolbox: {
          show: true,
          feature: {
            // dataView: {readOnly: false},
            // magicType: {type: ['line', 'bar']},
            // restore: {},
            // saveAsImage: {}
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'line',
            animation: true,
            label: {
              backgroundColor: '#6a7985'
            },
            lineStyle: {
              color: '#00FF34'
            }
          },
          formatter: function (params) {
            return '时间：'+ params[0].name + '<br/>温度曲线1温度值 : ' + params[0].value[1]
                + '<br/>温度曲线2温度值 : ' + params[1].value[1]
                + '<br/>温度曲线3温度值 : ' + params[2].value[1];
          },
        },
        grid: {
          left: '50',
          right: '10',
          top: '50',
          bottom:'30'
        },
        xAxis: {
          type: 'category',
          splitLine: {
            show: false
          },
          axisLabel: {
            color: '#ffffff'
          },
          triggerEvent: true
        },
        yAxis: {
          name: '温度值(℃)',
          nameLocation: 'center',
          nameGap: 30,
          type: 'value',
          boundaryGap: [0, '100%'],
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#192148'],
              width: 1,
              type: 'solid'
            }
          },
          axisLine: {
            lineStyle: {
              color: '#fff',
              width: '1'
            }
          },
          axisTick: {
            inside: true
          },
          axisLabel: {
            inside: false,
            color: '#ffffff',
            formatter: '{value}'
          }
        },
        series: [{
          name: '温度传感器1',
          type: 'line',
          smooth: true,
          showSymbol: true, // 显示数据点
          hoverAnimation: true,
          symbolSize: 6,
          itemStyle: {
            color: '#3366CC',
          },
          areaStyle: {
            opacity: 0.1
          },
          data: temperature1
        }, {
          name: '温度传感器2',
          type: 'line',
          smooth: true,
          showSymbol: true, // 显示数据点
          hoverAnimation: true,
          symbolSize: 6,
          itemStyle: {
            color: '#FFCC99',
          },
          areaStyle: {
            opacity: 0.1
          },
          data: temperature2
        }, {
          name: '温度传感器3',
          type: 'line',
          smooth: true,
          showSymbol: true, // 显示数据点
          hoverAnimation: true,
          symbolSize: 6,
          itemStyle: {
            color: '#99CC33',
          },
          areaStyle: {
            opacity: 0.1
          },
          data: temperature3
        }]
      };

      console.log('图表配置:', option); // 调试信息

      try {
        myChart.setOption(option);
        console.log('图表已设置配置'); // 调试信息
      } catch (error) {
        console.error('设置图表配置时出错:', error);
      }

      // 立即更新一次数据
      updateChart();

      // 定时更新数据
      function updateChart() {
        const temp1 = generateSimulatedTemperature(25, 2);
        const temp2 = generateSimulatedTemperature(23, 1.5);
        const temp3 = generateSimulatedTemperature(22, 1);

        if(temperature1.length > 20) {
          temperature1.shift();
          temperature2.shift();
          temperature3.shift();
        }

        temperature1.push(getData(temp1));
        temperature2.push(getData(temp2));
        temperature3.push(getData(temp3));

        try {
          myChart.setOption({
            series: [{
              data: temperature1
            }, {
              data: temperature2
            }, {
              data: temperature3
            }]
          });
        } catch (error) {
          console.error('更新图表数据时出错:', error);
        }
      }

      // 每3秒更新一次数据
      const timer = setInterval(updateChart, 3000);

      // 组件卸载时清除定时器
      onUnmounted(() => {
        if (timer) {
          clearInterval(timer);
        }
        // 销毁图表实例
        if (myChart) {
          myChart.dispose();
        }
      });

      // 窗口大小改变时重置图表大小
      window.addEventListener('resize', () => {
        myChart.resize();
      });
    });
    return {}
  },
})
</script>

<style scoped>
.threeTemperature{
  width: 100%;
  height: 400px;
}
</style>