<template>
  <div id="vibrationWave" class="vibrationWave"></div>
</template>
<script lang="js">
import * as echarts from 'echarts';
import {defineComponent, onMounted, onUnmounted} from "vue";

export default defineComponent({
  name: 'vibrationWave',
  setup: function () {
    onMounted(() => {
      let vibration = [];
      const chartDom = document.getElementById('vibrationWave');
      const myChart = echarts.init(chartDom);
      let time = new Date();
      let data = {
        categoryData:[],
        valueData:[]
      };

      // 添加模拟数据生成函数
      function generateMockData() {
        const mockData = [];
        for (let i = 0; i < 500; i++) {
          // 生成-50到50之间的随机振动值
          const value = Math.sin(i * 0.1) * 25 + Math.random() * 10 - 5;
          mockData.push(Number(value.toFixed(2)));
        }
        return mockData;
      }

      function generateData(list) {
        const categoryData = [];
        const valueData = [];
        time = new Date(); // 更新当前时间
        const baseTime = time.getTime();
        
        for (let i = 0; i < list.length; i++) {
          const currentTime = new Date(baseTime + i * 100); // 每个点间隔100ms
          categoryData.push(
            echarts.format.formatTime('yyyy-MM-dd\nhh:mm:ss', currentTime)
          );
          valueData.push(list[i]);
        }
        return {
          categoryData: categoryData,
          valueData: valueData
        };
      }

      let option = {
        title: {
          text: '海底电缆振动波形',
          textStyle: {
            color: '#ffffff',
            fontFamily: '宋体',
            fontSize: 14
          },
          left: 'center',
          top: 5
        },
        toolbox: {
          right: 10,
          top: 10,
          feature: {
            // saveAsImage: {
            //   pixelRatio: 2
            // }
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'line'
          }
        },
        grid: {
          left: 90,
          right: 40,
          bottom: 90,
          top: 50
        },
        dataZoom: [
          {
            type: 'inside'
          },
          {
            type: 'slider'
          }
        ],
        xAxis: {
          data: data.categoryData,
          silent: false,
          splitLine: {
            show: false
          },
          splitArea: {
            show: false
          },
          axisLabel: {
            color: '#ffffff'
          }
        },
        yAxis: {
          name: '振幅',
          nameLocation: 'middle',
          nameGap: 65,
          nameTextStyle: {
            color: '#ffffff',
            padding: [0, 0, 10, 0]
          },
          splitArea: {
            show: false
          },
          scale: true,
          axisLabel: {
            color: '#ffffff'
          }
        },
        series: [
          {
            type: 'line',
            data: data.valueData,
            large: true,
            lineStyle: {
              color: '#3366CC'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(51, 102, 204, 0.3)'
                },
                {
                  offset: 1,
                  color: 'rgba(51, 102, 204, 0)'
                }
              ])
            }
          }
        ]
      };

      function updateData() {
        vibration = generateMockData();
        data = generateData(vibration);
        option.xAxis.data = data.categoryData;
        option.series[0].data = data.valueData;
        myChart.setOption(option);
      }

      // 初始更新
      updateData();
      
      // 每秒更新一次数据
      const timer = setInterval(updateData, 1000);

      // 组件卸载时清除定时器
      onUnmounted(() => {
        if (timer) {
          clearInterval(timer);
        }
        if (myChart) {
          myChart.dispose();
        }
      });

      // 窗口大小改变时重置图表大小
      window.addEventListener('resize', () => {
        myChart.resize();
      });
    })
  },
})
</script>

<style scoped>
.vibrationWave{
  width: 100%;
  height: 400px;
}
</style>