<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的物业费</span>
        </div>
      </template>
      <el-table :data="propertyFeeList" style="width: 100%" border>
        <el-table-column prop="propertyFeeId" label="物业费ID" width="100"></el-table-column>
        <el-table-column prop="houseId" label="房屋ID" width="80"></el-table-column>
        <el-table-column prop="payPropertyMonth" label="缴费年月" width="120"></el-table-column>
        <el-table-column prop="houseArea" label="房屋面积" width="100"></el-table-column>
        <el-table-column prop="baseMoney" label="基础物业费" width="120"></el-table-column>
        <el-table-column prop="lateFee" label="滞纳金" width="100"></el-table-column>
        <el-table-column prop="totalMoney" label="总金额" width="120"></el-table-column>
        <el-table-column prop="payPropertyStatus" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.payPropertyStatus === '1' ? 'success' : 'danger'">
              {{ scope.row.payPropertyStatus === '1' ? '已缴费' : '未缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payPropertyTime" label="缴费时间" width="180"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'MyPropertyFee',
  data() {
    return {
      propertyFeeList: []
    }
  },
  created() {
    this.getPropertyFeeList()
  },
  methods: {
    // 获取我的物业费列表
    getPropertyFeeList() {
      // 这里需要调用后端接口获取当前用户的物业费记录
      // 暂时使用模拟数据
      this.propertyFeeList = [
        {
          propertyFeeId: 1,
          houseId: 1,
          payPropertyMonth: '2024-03',
          houseArea: 100,
          baseMoney: 250,
          lateFee: 0.25,
          totalMoney: 250.25,
          payPropertyStatus: '0',
          payPropertyTime: null
        },
        {
          propertyFeeId: 2,
          houseId: 1,
          payPropertyMonth: '2024-02',
          houseArea: 100,
          baseMoney: 250,
          lateFee: 0,
          totalMoney: 250,
          payPropertyStatus: '1',
          payPropertyTime: '2024-02-28 10:00:00'
        }
      ]
    }
  }
}
</script>
