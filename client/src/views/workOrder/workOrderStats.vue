<template>
  <el-main>
    <!-- 顶部数据看板 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-icon"><i class="el-icon-document"></i></div>
          <div class="stat-info">
            <div class="label">工单总量</div>
            <div class="number">{{ totalCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-icon"><i class="el-icon-circle-check"></i></div>
          <div class="stat-info">
            <div class="label">解决率</div>
            <div class="number">{{ solveRate }}%</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="stat-icon"><i class="el-icon-star-on"></i></div>
          <div class="stat-info">
            <div class="label">满意度</div>
            <div class="number">{{ avgSatisfaction.toFixed(1) }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-icon"><i class="el-icon-time"></i></div>
          <div class="stat-info">
            <div class="label">平均耗时</div>
            <div class="number">{{ avgDuration.toFixed(1) }} <small>h</small></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表展示区 -->
    <el-row :gutter="20" style="margin-top: 30px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header"><span>工单状态分布</span></div>
          <div id="statusChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header"><span>优先级占比</span></div>
          <div id="priorityChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </el-main>
</template>

<script>
import { getWorkOrderStatsApi } from "@/api/workOrder";

export default {
  data() {
    return {
      totalCount: 0,
      solveRate: 0,
      avgSatisfaction: 0,
      avgDuration: 0,
      statusMap: {},
      priorityMap: {},
      charts: { status: null, priority: null }
    };
  },
  mounted() {
    this.getStats();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      if(this.charts.status) this.charts.status.resize();
      if(this.charts.priority) this.charts.priority.resize();
    },
    async getStats() {
      let res = await getWorkOrderStatsApi();
      if (res && res.code == 200) {
        const data = res.data;
        this.statusMap = data.statusCount || {};
        this.priorityMap = data.priorityCount || {};
        this.avgSatisfaction = data.avgSatisfaction || 0;
        this.avgDuration = data.avgDuration || 0;
        this.totalCount = Object.values(this.statusMap).reduce((a, b) => a + Number(b), 0);
        const solved = this.statusMap["3"] || 0;
        this.solveRate = this.totalCount > 0 ? Math.round((solved / this.totalCount) * 100) : 0;
        
        this.$nextTick(() => {
          this.initStatusChart();
          this.initPriorityChart();
        });
      }
    },
    initStatusChart() {
      const chartDom = document.getElementById('statusChart');
      this.charts.status = window.echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%', left: 'center' },
        series: [{
          name: '工单状态',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
          data: [
            { value: this.statusMap["0"] || 0, name: '待处理', itemStyle: { color: '#F56C6C' } },
            { value: this.statusMap["1"] || 0, name: '处理中', itemStyle: { color: '#E6A23C' } },
            { value: this.statusMap["2"] || 0, name: '待复核', itemStyle: { color: '#409EFF' } },
            { value: this.statusMap["3"] || 0, name: '已完成', itemStyle: { color: '#67C23A' } }
          ]
        }]
      };
      this.charts.status.setOption(option);
    },
    initPriorityChart() {
      const chartDom = document.getElementById('priorityChart');
      this.charts.priority = window.echarts.init(chartDom);
      const option = {
        tooltip: { trigger: 'item' },
        series: [{
          name: '优先级',
          type: 'pie',
          radius: [20, 140],
          center: ['50%', '50%'],
          roseType: 'area',
          itemStyle: { borderRadius: 8 },
          data: [
            { value: this.priorityMap["0"] || 0, name: '低', itemStyle: { color: '#909399' } },
            { value: this.priorityMap["1"] || 0, name: '中', itemStyle: { color: '#409EFF' } },
            { value: this.priorityMap["2"] || 0, name: '高', itemStyle: { color: '#E6A23C' } },
            { value: this.priorityMap["3"] || 0, name: '紧急', itemStyle: { color: '#F56C6C' } }
          ]
        }]
      };
      this.charts.priority.setOption(option);
    }
  }
};
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  color: white;
  height: 100px;
}
.stat-card.blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-card.green { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-card.orange { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); }
.stat-card.purple { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }

.stat-icon { font-size: 40px; opacity: 0.3; margin-right: 15px; }
.stat-info .label { font-size: 14px; opacity: 0.8; }
.stat-info .number { font-size: 28px; font-weight: bold; margin-top: 5px; }
</style>