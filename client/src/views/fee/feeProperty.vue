<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物业费管理</span>
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="物业费规则" name="rule"></el-tab-pane>
            <el-tab-pane label="物业费管理" name="property"></el-tab-pane>
          </el-tabs>
        </div>
      </template>

      <!-- 物业费规则管理 -->
      <div v-if="activeTab === 'rule'">
        <el-button type="primary" size="small" @click="openAddRuleDialog">新增规则</el-button>
        <el-table :data="ruleList" style="width: 100%" border>
          <el-table-column prop="ruleId" label="规则ID" width="80"></el-table-column>
          <el-table-column prop="ruleName" label="规则名称"></el-table-column>
          <el-table-column prop="pricePerSquare" label="每平米价格" width="120"></el-table-column>
          <el-table-column prop="lateFeeRate" label="滞纳金费率" width="150"></el-table-column>
          <el-table-column prop="lateFeeDays" label="滞纳天数" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
                {{ scope.row.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" @click="openEditRuleDialog(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="deleteRule(scope.row.ruleId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 物业费管理 -->
      <div v-if="activeTab === 'property'">
        <el-button type="primary" size="small" @click="openAddPropertyDialog">新增物业费</el-button>
        <el-table :data="propertyList" style="width: 100%" border>
          <el-table-column prop="propertyFeeId" label="物业费ID" width="100"></el-table-column>
          <el-table-column prop="userId" label="业主ID" width="80"></el-table-column>
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
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" @click="openEditPropertyDialog(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="deleteProperty(scope.row.propertyFeeId)">删除</el-button>
              <el-button v-if="scope.row.payPropertyStatus === '0'" size="mini" type="success" @click="payProperty(scope.row.propertyFeeId)">标记缴费</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 新增/编辑规则对话框 -->
    <el-dialog title="规则管理" :visible.sync="ruleDialogVisible" width="500px">
      <el-form :model="ruleForm" :rules="ruleRules" ref="ruleForm">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称"></el-input>
        </el-form-item>
        <el-form-item label="每平米价格" prop="pricePerSquare">
          <el-input v-model.number="ruleForm.pricePerSquare" placeholder="请输入每平米价格" type="number"></el-input>
        </el-form-item>
        <el-form-item label="滞纳金费率" prop="lateFeeRate">
          <el-input v-model.number="ruleForm.lateFeeRate" placeholder="请输入滞纳金费率" type="number"></el-input>
        </el-form-item>
        <el-form-item label="滞纳天数" prop="lateFeeDays">
          <el-input v-model.number="ruleForm.lateFeeDays" placeholder="请输入滞纳天数" type="number"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="ruleForm.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="ruleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule">确定</el-button>
      </div>
    </el-dialog>

    <!-- 新增/编辑物业费对话框 -->
    <el-dialog title="物业费管理" :visible.sync="propertyDialogVisible" width="600px">
      <el-form :model="propertyForm" :rules="propertyRules" ref="propertyForm">
        <el-form-item label="业主ID" prop="userId">
          <el-input v-model.number="propertyForm.userId" placeholder="请输入业主ID" type="number"></el-input>
        </el-form-item>
        <el-form-item label="房屋ID" prop="houseId">
          <el-input v-model.number="propertyForm.houseId" placeholder="请输入房屋ID" type="number"></el-input>
        </el-form-item>
        <el-form-item label="规则ID" prop="ruleId">
          <el-select v-model="propertyForm.ruleId" placeholder="请选择规则">
            <el-option v-for="rule in ruleList" :key="rule.ruleId" :label="rule.ruleName" :value="rule.ruleId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="缴费年月" prop="payPropertyMonth">
          <el-input v-model="propertyForm.payPropertyMonth" placeholder="请输入缴费年月，格式：2024-03"></el-input>
        </el-form-item>
        <el-form-item label="房屋面积" prop="houseArea">
          <el-input v-model.number="propertyForm.houseArea" placeholder="请输入房屋面积" type="number"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="payPropertyStatus">
          <el-radio-group v-model="propertyForm.payPropertyStatus">
            <el-radio label="1">已缴费</el-radio>
            <el-radio label="0">未缴费</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="propertyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProperty">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'FeeProperty',
  data() {
    return {
      activeTab: 'rule',
      ruleList: [],
      propertyList: [],
      ruleDialogVisible: false,
      propertyDialogVisible: false,
      ruleForm: {
        ruleId: '',
        ruleName: '',
        pricePerSquare: '',
        lateFeeRate: '',
        lateFeeDays: '',
        status: '1'
      },
      propertyForm: {
        propertyFeeId: '',
        userId: '',
        houseId: '',
        ruleId: '',
        payPropertyMonth: '',
        houseArea: '',
        payPropertyStatus: '0'
      },
      ruleRules: {
        ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
        pricePerSquare: [{ required: true, message: '请输入每平米价格', trigger: 'blur' }],
        lateFeeRate: [{ required: true, message: '请输入滞纳金费率', trigger: 'blur' }],
        lateFeeDays: [{ required: true, message: '请输入滞纳天数', trigger: 'blur' }]
      },
      propertyRules: {
        userId: [{ required: true, message: '请输入业主ID', trigger: 'blur' }],
        houseId: [{ required: true, message: '请输入房屋ID', trigger: 'blur' }],
        ruleId: [{ required: true, message: '请选择规则', trigger: 'change' }],
        payPropertyMonth: [{ required: true, message: '请输入缴费年月', trigger: 'blur' }],
        houseArea: [{ required: true, message: '请输入房屋面积', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getRuleList()
  },
  methods: {
    handleTabClick(tab) {
      if (tab.name === 'property') {
        this.getPropertyList()
      }
    },
    // 获取规则列表
    getRuleList() {
      this.$http.get('/api/feePropertyRule/list').then(res => {
        if (res.code === 200) {
          this.ruleList = res.data
        }
      })
    },
    // 获取物业费列表
    getPropertyList() {
      this.$http.get('/api/feeProperty/list').then(res => {
        if (res.code === 200) {
          this.propertyList = res.data
        }
      })
    },
    // 打开新增规则对话框
    openAddRuleDialog() {
      this.ruleForm = {
        ruleId: '',
        ruleName: '',
        pricePerSquare: '',
        lateFeeRate: '',
        lateFeeDays: '',
        status: '1'
      }
      this.ruleDialogVisible = true
    },
    // 打开编辑规则对话框
    openEditRuleDialog(row) {
      this.ruleForm = { ...row }
      this.ruleDialogVisible = true
    },
    // 保存规则
    saveRule() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (this.ruleForm.ruleId) {
            // 编辑
            this.$http.put('/api/feePropertyRule/edit', this.ruleForm).then(res => {
              if (res.code === 200) {
                this.$message.success('编辑成功')
                this.ruleDialogVisible = false
                this.getRuleList()
              } else {
                this.$message.error('编辑失败')
              }
            })
          } else {
            // 新增
            this.$http.post('/api/feePropertyRule/add', this.ruleForm).then(res => {
              if (res.code === 200) {
                this.$message.success('新增成功')
                this.ruleDialogVisible = false
                this.getRuleList()
              } else {
                this.$message.error('新增失败')
              }
            })
          }
        }
      })
    },
    // 删除规则
    deleteRule(ruleId) {
      this.$confirm('确定要删除此规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/feePropertyRule/delete/${ruleId}`).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.getRuleList()
          } else {
            this.$message.error('删除失败')
          }
        })
      })
    },
    // 打开新增物业费对话框
    openAddPropertyDialog() {
      this.propertyForm = {
        propertyFeeId: '',
        userId: '',
        houseId: '',
        ruleId: '',
        payPropertyMonth: '',
        houseArea: '',
        payPropertyStatus: '0'
      }
      this.propertyDialogVisible = true
    },
    // 打开编辑物业费对话框
    openEditPropertyDialog(row) {
      this.propertyForm = { ...row }
      this.propertyDialogVisible = true
    },
    // 保存物业费
    saveProperty() {
      this.$refs.propertyForm.validate((valid) => {
        if (valid) {
          if (this.propertyForm.propertyFeeId) {
            // 编辑
            this.$http.put('/api/feeProperty/edit', this.propertyForm).then(res => {
              if (res.code === 200) {
                this.$message.success('编辑成功')
                this.propertyDialogVisible = false
                this.getPropertyList()
              } else {
                this.$message.error('编辑失败')
              }
            })
          } else {
            // 新增
            this.$http.post('/api/feeProperty/add', this.propertyForm).then(res => {
              if (res.code === 200) {
                this.$message.success('新增成功')
                this.propertyDialogVisible = false
                this.getPropertyList()
              } else {
                this.$message.error('新增失败')
              }
            })
          }
        }
      })
    },
    // 删除物业费
    deleteProperty(propertyFeeId) {
      this.$confirm('确定要删除此物业费记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/feeProperty/delete/${propertyFeeId}`).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.getPropertyList()
          } else {
            this.$message.error('删除失败')
          }
        })
      })
    },
    // 标记缴费
    payProperty(propertyFeeId) {
      this.$confirm('确定要标记为已缴费吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$http.put(`/api/feeProperty/pay/${propertyFeeId}`).then(res => {
          if (res.code === 200) {
            this.$message.success('缴费成功')
            this.getPropertyList()
          } else {
            this.$message.error('缴费失败')
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
</style>
