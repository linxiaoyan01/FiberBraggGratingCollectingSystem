<template>
  <br/>
  <n-space vertical>
    <n-card
        title="📖 断点位置维修情况"
        embedded
        :bordered="false"
        style="--n-text-color: rgb(77 112 148);--n-title-text-color: rgb(190 202 214);"
    >
      如果已经维修完成，请及时在本页面点击”完成维修“
      <br/>阵列序号为0表示该通道没有断点，1表示第一个光纤光栅断裂，以此类推
      <br/>维修状态为0表示为处理，1表示处理完成
    </n-card>

    <a-select
        ref="select"
        v-model:value="value1"
        style="width: 120px"
        @focus="focus"
        @change="handleChange"
    >
      <a-select-option value="已处理">已处理</a-select-option>
      <a-select-option value="未处理">未处理</a-select-option>
    </a-select>

    <n-data-table
        size="large"
        :columns="columns"
        :data="data"
        :pagination="pagination"
        align="center"
        bordered="true"
    />
  </n-space>
</template>
<script lang="ts">
import {h, defineComponent, onMounted, ref, watch, provide} from 'vue'

import MThePopconfirm from './m_thePopconfirm.vue'
import { useMessage } from 'naive-ui';
import axios from "axios";
type breakpoint = {
  id: number
  createTime: number
  tag: number
  state: number
}
declare let formatDate: any;

const createColumns = ({printRow}:any) => {
  return [
    {
      title: 'ID',
      key: 'id'
    },
    {
      title: '创建时间',
      key: 'createTime'
    },
    {
      title:'通道号',
      key:'channel',
    },
    {
      title: '阵列序号',
      key: 'arrayNum',
    },
    {
      title: '维修状态',
      key: 'state'
    },
    {
      title: '处理',
      key: '处理',
      render(row:any) {
        return h(
            MThePopconfirm,
            {
              size: 'small',
              onClick:()=>printRow(row)
            },
        )
      }
    }
  ]
}
/**
 * 数据查询
 * @param params
 */
const data = ref();
const value1 = ref("已处理");
const rowDataId = ref();
const handleQuery = (params:any)=> {
  axios.get("/bpr/list", {
    params: {
      page: params.page,
      size: params.size,
      state: params.state
    }
  }).then(function (response) {
    response.data.content.list.forEach((item:any)=>{
      item.createTime =  formatDateWrapper(Number(item.createTime))
    })
    data.value = response.data.content.list;
  });
}
const handleQueryFinish = (params:any)=> {
  axios.get("/bpr/finishList", {
    params: {
      page: params.page,
      size: params.size,
      state: params.state
    }
  }).then(function (response) {
    response.data.content.list.forEach((item:any)=>{
      item.createTime = formatDateWrapper(Number(item.createTime))
    })
    data.value = response.data.content.list;
  });
}
const formatDateWrapper = (time:any)=>{
  const data = new Date(time);
  return formatDate(data,'yyyy-MM-dd hh:mm:ss');
}
export default defineComponent({
  name: 'm_breakpoint',
  components:{
    MThePopconfirm
  },
  setup () {
    provide('selectData', value1);
    provide('rowDataId',rowDataId)
    onMounted(()=>{
      //console.log("onMounted")
      handleQuery({
        page: 1,
        size: 10,
        state: 0
      })
      watch(value1,(newValue, oldValue)=>{
        //console.log(newValue,oldValue);
        if(value1.value == '未处理'){
          handleQuery({
            page:1,
            size:10,
            state: 0
          })
        }else{
          handleQueryFinish({
            page: 1,
            size:10,
            state: 1
          })
        }
      });
    });
    return {
      value1,
      data,
      columns: createColumns({
        printRow(row: breakpoint) {
          rowDataId.value = row.id;
        }
      }),
      pagination: {
        pageSize:10
      }
    }
  }
})
</script>
<style>
</style>
