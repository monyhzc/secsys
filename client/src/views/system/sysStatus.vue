<template>
  <el-main>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card header="内存使用监控" shadow="hover">
          <div id="memoryGauge" style="height: 300px;"></div>
          <div style="text-align: center; color: #666; font-size: 14px; margin-top: -40px;">
            已使用: {{ server.jvmMemoryUsed }} MB / 总量: {{ server.jvmMemoryMax }} MB
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="用户结构分布" shadow="hover">
          <div id="userPie" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 25px;">
      <el-col :span="8">
        <el-card header="服务器环境" shadow="hover">
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="OS">{{ server.osName }}</el-descriptions-item>
            <el-descriptions-item label="架构">{{ server.osArch }}</el-descriptions-item>
            <el-descriptions-item label="CPU核心">{{ server.processors }} Core</el-descriptions-item>
            <el-descriptions-item label="系统负载">{{ server.loadAverage > 0 ? server.loadAverage : 'N/A' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card header="业务活跃度概览" shadow="hover">
          <el-row :gutter="20" style="text-align: center; padding: 10px 0;">
            <el-col :span="8">
              <div class="data-item">
                <div class="data-label">注册业主</div>
                <div class="data-val">{{ users.liveCount }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-label">物业人员</div>
                <div class="data-val">{{ users.adminCount }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-label">工单总数</div>
                <div class="data-val highlight">{{ business.orderCount }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </el-main>
</template>

<script>
import { getSysStatusApi } from "@/api/sysStatus";

export default {
  data() {
    return {
      server: {},
      users: { totalCount: 0 },
      business: {},
      charts: { gauge: null, pie: null }
    };
  },
  mounted() {
    this.fetchData();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      if(this.charts.gauge) this.charts.gauge.resize();
      if(this.charts.pie) this.charts.pie.resize();
    },
    async fetchData() {
      let res = await getSysStatusApi();
      if (res && res.code == 200) {
        this.server = res.data.server;
        this.users = res.data.users;
        this.business = res.data.business;
        this.$nextTick(() => {
          this.initGauge();
          this.initPie();
        });
      }
    },
    initGauge() {
      const chartDom = document.getElementById('memoryGauge');
      this.charts.gauge = window.echarts.init(chartDom);
      const option = {
        series: [{
          type: 'gauge',
          progress: { show: true, width: 18 },
          axisLine: { lineStyle: { width: 18 } },
          axisTick: { show: false },
          splitLine: { length: 15, lineStyle: { width: 2, color: '#999' } },
          anchor: { show: true, showAbove: true, size: 25, itemStyle: { borderWidth: 10 } },
          title: { show: false },
          detail: { valueAnimation: true, formatter: '{value}%', fontSize: 30, offsetCenter: [0, '70%'] },
          data: [{ value: this.server.jvmMemoryRate }]
        }]
      };
      this.charts.gauge.setOption(option);
    },
    initPie() {
      const chartDom = document.getElementById('userPie');
      this.charts.pie = window.echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'item' },
        legend: { top: '5%', left: 'center' },
        series: [{
          name: '用户组成',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          data: [
            { value: this.users.liveCount, name: '业主', itemStyle: { color: '#67C23A' } },
            { value: this.users.adminCount, name: '物业/管理', itemStyle: { color: '#409EFF' } }
          ]
        }]
      };
      this.charts.pie.setOption(option);
    }
  }
};
</script>

<style scoped>
.data-item { padding: 15px; }
.data-label { color: #909399; font-size: 14px; margin-bottom: 10px; }
.data-val { font-size: 30px; font-weight: bold; color: #303133; }
.data-val.highlight { color: #409EFF; }
</style>