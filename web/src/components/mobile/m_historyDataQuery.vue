<template>
  <br/>
    <p style="color: white; font-family: 'Adobe 宋体 Std L';">请选择你要查询的日期范围：</p>
    <n-space vertical>
      <n-date-picker
          v-model:value="range2"
          type="datetimerange"
          :shortcuts="rangeShortcuts"
      />
      <n-data-table
          size="small"
          :columns="columns"
          :data="data"
          :pagination="pagination"
      />
    </n-space>
</template>
<script lang="ts">
import {defineComponent, onMounted, ref, watch} from 'vue'
import axios from "axios";
let data = ref();
let range2 = ref();
let columns = ref([
  {
    title: 'val1',
    dataIndex: 'val1',
    key: 'val1'
  },
  {
    title: 'val2',
    dataIndex: 'val2',
    key: 'val2'
  },
  {
    title: 'val3',
    dataIndex: 'val3',
    key: 'val3'
  },
  {
    title: 'val4',
    dataIndex: 'val4',
    key: 'val4'
  },
  {
    title: 'createTime',
    dataIndex: 'createTime',
    key: 'createTime'
  },
]);
declare let formatDate: any;
const formatDateWrapper = (time:any)=> {
  const data = new Date(time);
  return formatDate(data, 'yyyy-MM-dd hh:mm:ss');
}

const handleQuery = async (params: any) => {

  await axios.get("/fbg/list", {
    params: {
      page: params.page,
      size: params.size,
      startTime: params.startTime,
      endTime: params.endTime
    }
  }).then(function (response) {
    let resultList = []

    let flag = 1;
    let columnList = []
    if (response.data.content.list.length !== 0) {
      for(let i = 0; i < response.data.content.list.length; i++){
        let obj = JSON.parse(response.data.content.list[i])
        obj.createTime =  formatDateWrapper(Number(obj.createTime));
        if(flag === 1){
          for(let i = 0; i < Object.getOwnPropertyNames(obj).length; i++){
            columnList.push({
              title : Object.getOwnPropertyNames(obj)[i],
              dataIndex  : Object.getOwnPropertyNames(obj)[i],
              key: Object.getOwnPropertyNames(obj)[i]
            })
          }
          flag = 0;
        }
        resultList.push(obj);
      }
      columns.value = columnList;
      data.value = resultList
    }
  })
};

export default defineComponent({

  components:{
  },
  setup () {
    /**
     * 数据查询
     * @param params
     */
    onMounted(()=> {
      //console.log("onMounted")
      handleQuery({
        page: 1,
        size: 10,
      })
      watch(range2, (newValue, oldValue) => {
        //console.log(newValue, oldValue);
        //console.log("range2.value[0]" + range2.value[0]);
        //console.log("range2.value[1]" + range2.value[1]);

        handleQuery({
          page: 1,
          size: 10,
          startTime: range2.value[0],
          endTime: range2.value[1]
        })
      });
    })
    return {
      columns,
      data,
      range2,
    }
  }
})
</script>