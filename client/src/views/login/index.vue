<template>

  <div class="container">

    <div style="width: 25%; display: flex;background-color: white; ">

      <div style="flex: 1;width: 50%;padding: 50px; display: flex;flex-direction: column;justify-content: center;" >
        <div style="text-align: center; font-size: 28px; margin-bottom: 50px;color: #000000">物 业 管 理 系 统</div>
        <el-form  :model="loginForm" ref="loginForm" :rules="rules"  size="normal">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item prop="userType">
            <el-radio-group v-model="loginForm.userType" class="custom-radio-group">
              <el-radio :label="0" class="custom-radio">业主</el-radio>
              <el-radio :label="1" class="custom-radio">管理员</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="my-btn" @click="onSubmit">登录</el-button>
          </el-form-item>
        </el-form>
      </div>

    </div>


  </div>


</template>

<style lang="scss" scoped>


.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("../../assets/images/bg1.jpg");
  background-size: 100% 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}




.login-wrapper {
  background-image: url("../../assets/images/login_bg.png");
  background-size: cover;
  // background: linear-gradient(to bottom, #C9FEFF, #fff);
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.login-form {
  width: 100%;
}

.login-container {
  width: 400px;
  margin-left: auto;
  /* 将左边距离设为auto，使其靠右对齐 */
  margin-right: 80px;
  /* 设置右边距离 */
  padding-top: 40px;
  padding-bottom: 40px;
  padding-right: 70px;
  border-radius: 10px;
  box-shadow: 1px 2px 4px rgb(0, 0, 0);
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.my-btn {
  width: 100%;
}
</style>

<script>
export default {
  data() {
    return {
      //登录表单绑定数据源
      loginForm: {
        username: "",
        password: "",
        userType: "", //0：业主  1： 管理员
      },
      //表单验证规则
      rules: {
        username: [
          {
            trigger: "change",
            required: true,
            message: "请输入用户名",
          },
        ],
        password: [
          {
            trigger: "change",
            required: true,
            message: "请输入密码",
          },
        ],
        userType: [
          {
            trigger: "change",
            required: true,
            message: "请选择用户类型",
          },
        ],
      },
    };
  },
  methods: {
    //登录提交事件
    onSubmit() {
      //表单验证
      this.$refs.loginForm.validate((valid) => {
        //验证通过才提交表单
        if (valid) {
          this.loading = true;
          //调用store里面的login方法
          this.$store
            .dispatch("user/login", this.loginForm)
            .then(() => {
              //跳转路由
              this.$router.push({ path: this.redirect || "/" });
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            });
        }
      });
    },
  },
};
</script>
