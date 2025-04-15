<template>
  <the-header></the-header>
  <a-layout-content
      :style="{margin: 0, height: height_top.height}"
      class="login-container">
      <div class="login-content">
        <div class="login-box">
          <div class="login-title">
            <h1 class="title-text">海底电缆智能健康监测系统</h1>
            <div class="decoration-line"></div>
          </div>
          <n-card class="login-card">
            <a-form
                :model="LoginUser"
                name="basic"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
                autocomplete="off"
                @finish="onFinish"
                @finishFailed="onFinishFailed"
            >
              <a-form-item
                  label="用户名"
                  name="loginName"
                  :rules="[{ required: true, message: '请输入用户名' }]"
              >
                <a-input v-model:value="LoginUser.loginName" placeholder="请输入用户名" size="large" />
              </a-form-item>

              <a-form-item
                  label="密码"
                  name="password"
                  :rules="[{ required: true, message: '请输入密码' }]"
              >
                <a-input-password v-model:value="LoginUser.password" placeholder="请输入密码" size="large" />
              </a-form-item>

              <a-form-item name="remember" :wrapper-col="{ offset: 6, span: 18 }">
                <a-checkbox v-model:checked="LoginUser.remember">记住密码</a-checkbox>
              </a-form-item>

              <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
                <a-button type="primary" html-type="submit" :loading="loading" block size="large">登录</a-button>
              </a-form-item>
            </a-form>
          </n-card>
        </div>
      </div>
  </a-layout-content>
</template>

<script lang="ts" >
import {defineComponent, ref, onMounted} from 'vue';
import { useRouter } from 'vue-router'
import axios from 'axios';
import { message } from 'ant-design-vue';
import store from "@/store";
import TheHeader from '@/components/theHeader.vue';
import TheFooter from '@/components/theFooter.vue';
declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  components:{
    TheHeader,
    TheFooter
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const LoginUser = ref({
      password: '',
      loginName: '',
      remember: false
    });

    const height_top = ref({
      height: ''
    });

    onMounted(()=>{
      if(window.innerHeight != null){
        height_top.value.height = window.innerHeight+'px';
      }
      window.onresize = () => {
        return (() => {
          height_top.value.height = window.innerHeight+'px';
        })();
      };
      
      // 从localStorage获取保存的用户名和密码
      const savedUser = localStorage.getItem('loginUser');
      if (savedUser) {
        const { loginName, password, remember } = JSON.parse(savedUser);
        LoginUser.value = { loginName, password, remember };
      }
    })

    const onFinish = async () => {
      loading.value = true;
      try {
        const password = hexMd5(LoginUser.value.password + KEY);
        const response = await axios.post("/user/login", {
          ...LoginUser.value,
          password
        });
        
        const data = response.data;
        if (data.success) {
          // 如果选择记住密码，保存到localStorage
          if (LoginUser.value.remember) {
            localStorage.setItem('loginUser', JSON.stringify(LoginUser.value));
          } else {
            localStorage.removeItem('loginUser');
          }
          
          store.commit("setUser", data.content);
          message.success("登录成功");
          router.push({ path: '/' });
        } else {
          message.error(data.message || "登录失败");
        }
      } catch (error) {
        message.error("登录失败，请稍后重试");
      } finally {
        loading.value = false;
      }
    };

    const onFinishFailed = (errorInfo: any) => {
      console.log('Failed:', errorInfo);
    };

    return {
      onFinish,
      onFinishFailed,
      LoginUser,
      height_top,
      loading
    };
  },
});
</script>

<style scoped>
.login-container {
  background-image: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url(https://img.freepik.com/free-photo/blue-sky-with-clouds_1203-1603.jpg);
  background-size: cover;
  background-position: center;
}

.login-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.login-box {
  width: 600px;
  padding: 30px;
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
}

.title-text {
  font-size: 36px;
  color: white;
  margin-bottom: 20px;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.decoration-line {
  width: 300px;
  height: 4px;
  background: linear-gradient(90deg, transparent, #fff, transparent);
  margin: 0 auto;
  position: relative;
}

.decoration-line::before,
.decoration-line::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  background: #fff;
  border-radius: 50%;
  top: -3px;
}

.decoration-line::before {
  left: 0;
}

.decoration-line::after {
  right: 0;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  padding: 20px;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  font-size: 16px;
  color: #333;
}

:deep(.ant-input),
:deep(.ant-input-password),
:deep(.ant-input-affix-wrapper) {
  border-radius: 6px;
  height: 45px;
  background-color: #f5f5f5 !important;
  border: 1px solid #d9d9d9 !important;
  transition: all 0.3s;
  padding: 0 11px;
}

:deep(.ant-input:hover),
:deep(.ant-input-password:hover),
:deep(.ant-input-affix-wrapper:hover) {
  border-color: #40a9ff !important;
  background-color: #fff !important;
}

:deep(.ant-input:focus),
:deep(.ant-input-password:focus),
:deep(.ant-input-affix-wrapper-focused) {
  border-color: #40a9ff !important;
  background-color: #fff !important;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

:deep(.ant-input-password .ant-input) {
  background-color: transparent !important;
  padding-right: 30px;
}

:deep(.ant-input-password .ant-input-suffix) {
  right: 11px;
  color: rgba(0, 0, 0, 0.45);
}

:deep(.ant-input-password .anticon) {
  font-size: 16px;
  vertical-align: middle;
}

:deep(.ant-input-password .ant-input-password-icon) {
  cursor: pointer;
  transition: color 0.3s;
}

:deep(.ant-input-password .ant-input-password-icon:hover) {
  color: #40a9ff;
}

:deep(.ant-btn) {
  height: 45px;
  font-size: 18px;
  border-radius: 6px;
  background: #1890ff;
  border: none;
  transition: all 0.3s;
}

:deep(.ant-btn:hover) {
  background: #40a9ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

:deep(.ant-checkbox-wrapper) {
  font-size: 16px;
  color: #333;
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}
</style>