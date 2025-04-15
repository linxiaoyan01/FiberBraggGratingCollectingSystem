<template>
  <div id="leftTopChart" class="leftTopChart"></div>
</template>

<script lang="js">
import * as echarts from 'echarts';
import {defineComponent, onMounted} from "vue";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'leftTopChart',
  setup: function () {
    Date.prototype.format = function (fmt) {
      var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
      };
      if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
      }
      for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
          fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
      }
      return fmt;
    }
    const strainColors = {
      strain1: '#3d2939',
      strain2: '#000',
      strain3: '#403897',
      strain4: '#f93',
      strain5: '#bc002d',
      strain6: '#924fa2',
      strain7: '#044500',
      strain8: '#09947d',
      strain9: '#ef2b2d',
      strain10: '#82043c',
      strain11: '#442b1e',
      strain12: '#330a17',
      strain13: '#70247d',
    };
    onMounted(() => {
      var chartDom = document.getElementById('leftTopChart');
      var myChart = echarts.init(chartDom);
      var option;
      let strain = [];
      option = {
        title: {
          text: '海底电缆实时应变值(με)',
          textStyle: {
            color: '#ffffff',
            fontFamily: '宋体',
            fontSize: 14,
          },
          left: 10,
          top: 5,
        },
        xAxis: {
          max: 'dataMax'
        },
        yAxis: {
          type: 'category',
          data: ['strain1', 'strain2', 'strain3', 'strain4', 'strain5', 'strain6', 'strain7', 'strain8', 'strain9', 'strain10', 'strain11', 'strain12', 'strain13'],
          inverse: true,
          animationDuration: 300,
          animationDurationUpdate: 300,
        },
        grid: {
          top: 60,
          bottom: 40,
          left: 80,
          right: 40
        },
        toolbox: {
          show: true,
          feature: {
            dataView: {readOnly: false},
            magicType: {type: ['line', 'bar']},
            restore: {},
            saveAsImage: {}
          }
        },
        series: [
          {
            name: 'X',
            type: 'bar',
            data: [7.80,-5.75,-9.80,-9.74,-9.94,-8.68,-8.41,4.34,-0.97,5.44,-1.40,12.13,6.08],
            // strain,
            label: {
              show: true,
              color: '#fff',
              position: 'right',
              // valueAnimation: true
            }
          }
        ],
        animationDuration: 0,
        animationDurationUpdate: 3000,
        animationEasing: 'linear',
        animationEasingUpdate: 'linear',
        visualMap: {
          orient: 'horizontal',
          left: -5,
          bottom: -5,
          min: -50,
          max: 50,
          text: ['High', 'Low'],
          textStyle: {
            color: 'white'
          },
          // Map the score column to color
          dimension: 0,
          inRange: {
            color: ['#403897', '#288888', '#988888']
          },
          padding: 5,
          textGap: 20
        },
        graphic: {
          elements: [
            {
              type: 'text',
              right: 40,
              bottom: 0,
              style: {
                text: "",
                font: 'bolder 17px monospace',
                fill: 'rgba(225, 225, 225, 255)'
              },
              z: 100
            }
          ]
        }
      };

      // 模拟数据生成函数
      function generateSimulatedData() {
        const simulatedData = [];
        for (let i = 0; i < 13; i++) {
          // 生成-30到30之间的随机应变值
          let value = Math.round((Math.random() * 60 - 30) * 100) / 100;
          simulatedData.push(value);
        }
        return simulatedData;
      }

      // 定时更新数据
      function updateChart() {
        strain = generateSimulatedData();
        option.series[0].data = strain;
        option.graphic.elements[0].style.text = new Date().format("yyyy-MM-dd hh:mm:ss");
        myChart.setOption(option);
      }

      // 初始更新
      updateChart();
      
      // 每3秒更新一次数据
      setInterval(updateChart, 3000);

      // 窗口大小改变时重置图表大小
      window.addEventListener('resize', () => {
        myChart.resize();
      });
    })
  }
})
</script>

<style scoped>
.leftTopChart {
  display: flex;
}
</style>