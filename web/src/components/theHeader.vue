<template>
  <a-layout-header class="header">
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <div class="header-wrap">
        <div class="header-left">
          <a-menu-item key="/p_index">
            <router-link to="/p_index">海底管道应力/应变监测系统</router-link>
          </a-menu-item>
          <a-menu-item key="screen-switch">
            <router-link to="/screen2">
              海底管道位移变形监测系统
            </router-link>
          </a-menu-item>
          <a-menu-item key="/p_index/detail">
            <router-link to="/p_index/detail">历史数据查询</router-link>
          </a-menu-item>
          <a-menu-item key="/p_index/sensor">
            <router-link to="/p_index/sensor">传感器查询</router-link>
          </a-menu-item>
          <a-menu-item key="/p_index/admin/user">
            <router-link to="/p_index/admin/user">用户</router-link>
          </a-menu-item>
        </div>
        <div class="header-right">
          <a-menu-item key="/p_index/about">
            <router-link to="/p_index/about">关于</router-link>
          </a-menu-item>
          <a-menu-item v-if="user.id" @click="logout">
            退出登录
          </a-menu-item>
        </div>
      </div>
    </a-menu>
  </a-layout-header>
</template>
<script lang="ts">
import { defineComponent, ref, computed } from 'vue';
import store from "@/store";
import axios from "axios";
import {message} from "ant-design-vue";
import { useRouter, useRoute } from 'vue-router'
export default defineComponent({
  name: 'the-header',
  setup(){
    const router=useRouter()
    const route = useRoute();
    const user = computed(()=>{
      return store.state.user
    });
    let transitionName = ref()
    const logout = () =>{
      //console.log("退出登录开始");
      //console.log(typeof user.value.token)
      axios.get('/user/logout/'+user.value.token).then((response)=>{
        const data = response.data;
        //console.log(data);
        if(data.success){
          router.push({ path: '/p_index/login' })
          message.success("退出登录成功");
          store.commit("setUser", {});
        }else {
          message.error(data.message);
          store.commit("setUser", {});
        }
      })
    }
    // 计算当前所在的屏幕
    const currentScreen = computed(() => {
      return route.path;
    });
    // watch: {
    //   //使用watch 监听$router的变化
    //   $route(to, from) {
    //     //如果to索引大于from索引,判断为前进状态,反之则为后退状态
    //     console.log(to, "to");
    //     console.log(from, "from");
    //     if (to.meta.index > from.meta.index) {
    //       //设置动画名称
    //       transitionName.value = "slide-left";
    //     } else {
    //       transitionName.value = "slide-right";
    //     }
    //   }
    // }
    return {
      transitionName,
      logout,
      user,
      currentScreen,
    }
  },
});
</script>
<style scoped>
.ant-menu{
  font-size: 16px;
}
.header-wrap{
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.header-left{
  display: flex;
  justify-content: left;
}
.header-right{
  display: flex;
  justify-content: left;
}
.slide-right-enter-active, .slide-right-leave-active, .slide-left-enter-active, .slide-left-leave-active {
  will-change: transform;
  transition: all 500ms;
  position: absolute;
}
.slide-right-enter {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}

.slide-right-leave-active {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.slide-left-enter {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.slide-left-leave-active {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}
img{
  position: relative;
  width: 45px;
  height: 45px;
  margin-top: 10px;
  margin-right: 10px;
}
.header{
  top: 0px;
  left: 0px;
  right: 0px;
}

/* 添加菜单项文字颜色 */
:deep(.ant-menu-item) {
  color: #809ed4 !important;
}

:deep(.ant-menu-item a) {
  color: #809ed4 !important;
}

/* 鼠标悬停时的颜色 */
:deep(.ant-menu-item:hover),
:deep(.ant-menu-item a:hover) {
  color: #a8c1e9 !important;
}
</style>