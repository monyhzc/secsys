<template>
  <div class="dashboard-container">

    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="header-title">最新公告</span>
      </div>
      <div class="content">
        <div class="section-title">
          <p>{{ new_notices.title }}</p>
        </div>
        <div class="notice-content">
          <p>{{ new_notices.noticeContent }}</p>
        </div>
        <div class="time">
          <p class="time-zone">
            {{ new_notices.createTime }}
          </p>
        </div>
      </div>
    </el-card>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="header-title">欢迎来到物业管理系统</span>
      </div>
      <div class="content">
        <div class="section-title">物业管理系统简介</div>
        <div class="section-content">
          <p>欢迎使用我们的物业管理系统，我们致力于为您提供便捷、高效的物业管理服务。无论您是业主、租户还是物业管理员，我们都将竭诚为您服务，为您的生活带来更多便利。</p>
        </div>
        <div class="section-title">服务提供</div>
        <div class="section-content">
          <p>我们提供以下服务：</p>
          <ul>
            <li>物业信息管理：管理小区信息、楼栋信息、房屋信息等。</li>
            <li>费用管理：管理停车费，提供费用明细和缴费记录。</li>
          </ul>
        </div>
        <div class="section-title">我们的使命</div>
        <div class="section-content">
          <p>我们的使命是确保您安心居住，我们的激情是为您创造舒适生活。在您的每一个需求后面，都有我们专业的团队为您守护。</p>
        </div>
        </div>
    </el-card>
  </div>
</template>


<script>
import { mapGetters } from "vuex";
import { getOneApi } from "@/api/notice";
// import { title } from "@/settings"; // 暂时注释未使用的引用，保持代码整洁
export default {
  data() {
    return {
      new_notices: {}, // 初始化为对象而非数组，因为后端返回的是单个对象
      parms: {
        title: "",
        content: "",
        time: "",
      },
    }
  },
  created() {
    this.getOne();
  },
  methods: { // 方法应该放在 methods 对象中，原代码放在了 data 中，这里顺便修复
      //获取最新公告
      async getOne() {
        let res = await getOneApi(this.parms);
        if (res && res.code == 200) {
          //给表格赋值
          console.log(res);
          // 注意：如果后端返回的是列表，这里可能需要取 res.data[0]
          // 根据原代码逻辑，假设 res.data 直接是对象
          this.new_notices = res.data; 
        }
      },
  },
  name: "Dashboard",
  computed: {
    ...mapGetters(["name"]),
  },
};
</script>



<style lang="scss" scoped>
.notice-content {
  line-height: 30px;
  text-indent: 2em;
  font-size: 18px;
  width: 100%;
  height: 400px;
  padding: 3px 0;
  color: #666;
}

.time-zone {
  font-size: 18px;
  width: 200px;
  float: right;
}

.dashboard-container {
  display: flex;
  justify-content: space-around;
  margin: 20px;
}

.box-card {
  flex: 1;
  margin: 0 10px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
}

.box-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.welcome-card {
  background-color: #f0f8ff;
}

.notice-card {
  background-color: #fff0f5;
}

.header-title {
  color: #333;
  font-size: 25px;
  font-weight: 600;
}

.content {
  padding: 15px;
}

.section-title {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin-top: 5px;
}

.section-content {
  padding: 10px 0;
  color: #666;
  line-height: 1.6;
}

.section-title::after {
  content: '';
  display: block;
  width: 50px;
  height: 3px;
  background-color: #409eff;
  margin-top: 5px;
}

.highlight {
  color: #FF7670;
  font-weight: 600;
}
</style>