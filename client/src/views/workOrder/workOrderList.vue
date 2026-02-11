<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="标题">
        <el-input v-model="parms.title"></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="parms.status" placeholder="请选择状态" clearable>
          <el-option label="待处理" value="0"></el-option>
          <el-option label="处理中" value="1"></el-option>
          <el-option label="待复核" value="2"></el-option>
          <el-option label="已完成" value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" @click="resetBtn" icon="el-icon-close">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column label="标题" prop="title"></el-table-column>
      <el-table-column label="描述" prop="description" show-overflow-tooltip></el-table-column>
      <el-table-column label="提交人" prop="submitterName"></el-table-column>
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" type="danger" size="small">待处理</el-tag>
          <el-tag v-if="scope.row.status == '1'" type="warning" size="small">处理中</el-tag>
          <el-tag v-if="scope.row.status == '2'" type="primary" size="small">待复核</el-tag>
          <el-tag v-if="scope.row.status == '3'" type="success" size="small">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" prop="createTime" width="160"></el-table-column>
      <el-table-column align="center" width="150" label="操作">
        <template slot-scope="scope">
          <el-button icon="el-icon-view" type="info" size="small" @click="viewBtn(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status == '0' || scope.row.status == '1'" icon="el-icon-edit" type="success" size="small" @click="handleBtn(scope.row)">处理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="parms.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total" background>
    </el-pagination>

    <sys-dialog :title="handleDialog.title" :height="handleDialog.height" :width="handleDialog.width" :visible="handleDialog.visible"
      @onClose="handleDialog.visible = false" @onConfirm="onConfirm">
      <template slot="content">
        <el-form :model="handleModel" ref="handleForm" :rules="rules" label-width="80px" size="small">
          <el-form-item label="标题">
            <el-input v-model="handleModel.title" disabled></el-input>
          </el-form-item>
          <el-form-item prop="orderResult" label="处理结果">
            <el-input type="textarea" :rows="5" v-model="handleModel.orderResult" placeholder="请输入处理结果"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>

    <sys-dialog :title="'工单详情'" :height="750" :width="950" :visible="viewDialog.visible"
      @onClose="viewDialog.visible = false">
      <template slot="content">
        <div class="detail-container">
          <div class="detail-header">
            <div class="header-item">
              <span class="label">当前状态:</span>
              <el-tag :type="getStatusType(viewData.status)" effect="dark" size="medium">{{ getStatusLabel(viewData.status) }}</el-tag>
            </div>
            <div class="header-item">
              <span class="label">优先级:</span>
              <el-tag :type="getPriorityType(viewData.priority)" effect="plain" size="medium">{{ getPriorityLabel(viewData.priority) }}</el-tag>
            </div>
          </div>

          <div class="detail-section">
            <div class="section-title">
              <i class="el-icon-info"></i> 基本信息
            </div>
            <el-table :data="basicInfoData" border style="width: 100%;" class="custom-table">
              <el-table-column prop="label" label="项目" width="120" align="right" class-name="table-label-column"></el-table-column>
              <el-table-column prop="value" label="内容" class-name="table-value-column">
                <template slot-scope="scope">
                  <div v-if="scope.row.key === 'description'" class="description-content">{{ scope.row.value || '暂无描述' }}</div>
                  <div v-else>{{ scope.row.value }}</div>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div v-if="viewData.image" class="detail-section">
            <div class="section-title">
              <i class="el-icon-picture"></i> 现场图片
            </div>
            <div class="image-box">
              <el-image 
                class="detail-image"
                :src="getImageUrl(viewData.image)" 
                :preview-src-list="[getImageUrl(viewData.image)]"
                fit="cover">
              </el-image>
            </div>
          </div>

          <div v-else class="detail-section">
            <div class="section-title">
              <i class="el-icon-picture"></i> 现场图片
            </div>
            <div class="no-image">
              <el-empty description="暂无图片"></el-empty>
            </div>
          </div>

          <div class="detail-section">
            <div class="section-title">
              <i class="el-icon-s-operation"></i> 执行情况
            </div>
            <el-table :data="executionInfoData" border style="width: 100%;" class="custom-table">
              <el-table-column prop="label" label="项目" width="120" align="right" class-name="table-label-column"></el-table-column>
              <el-table-column prop="value" label="内容" class-name="table-value-column">
                <template slot-scope="scope">
                  <div v-if="scope.row.key === 'orderResult'" :class="{'result-empty': !scope.row.value}" class="result-content">
                    {{ scope.row.value || '暂无处理结果' }}
                  </div>
                  <div v-else-if="scope.row.key === 'reviewResult'" :class="{'result-empty': !scope.row.value}" class="result-content">
                    {{ scope.row.value || '暂无复核意见' }}
                  </div>
                  <div v-else>{{ scope.row.value }}</div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </template>
      <template slot="footer">
        <el-button size="small" @click="viewDialog.visible = false">关闭</el-button>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import SysDialog from "@/components/system/SysDialog.vue";
import { getWorkOrderListApi, handleWorkOrderApi } from "@/api/workOrder";
import { getUserId } from "@/utils/auth";

export default {
  components: { SysDialog },
  data() {
    return {
      tableHeight: 0, tableList: [],
      handleModel: { id: "", title: "", orderResult: "" },
      rules: { orderResult: [{ required: true, trigger: "blur", message: "请输入处理结果" }] },
      handleDialog: { title: "处理工单", height: 300, width: 600, visible: false },
      viewDialog: { visible: false },
      viewData: {},
      basicInfoData: [],
      executionInfoData: [],
      parms: { title: "", status: "", currentPage: 1, pageSize: 10, total: 0 },
    };
  },
  created() { 
    if (this.parms.handlerId) {
      delete this.parms.handlerId;
    }
    this.getList(); 
  },
  mounted() { this.$nextTick(() => { this.tableHeight = window.innerHeight - 210; }); },
  methods: {
    getStatusLabel(s) { return { "0": "待处理", "1": "处理中", "2": "待复核", "3": "已完成" }[s] || s; },
    getStatusType(s) { return { "0": "danger", "1": "warning", "2": "primary", "3": "success" }[s] || "info"; },
    getPriorityLabel(p) { return { "0": "低", "1": "中", "2": "高", "3": "紧急" }[p] || p; },
    getPriorityType(p) { return { "3": "danger", "2": "warning", "1": "primary", "0": "info" }[p] || "info"; },
    getImageUrl(url) {
        if(!url) return "";
        if(url.startsWith("http")) return url;
        return process.env.VUE_APP_BASE_API_PRO + url;
    },
    async getList() {
      let res = await getWorkOrderListApi(this.parms);
      if (res && res.code == 200) { this.tableList = res.data.records; this.parms.total = res.data.total; }
    },
    searchBtn() { this.getList(); },
    resetBtn() { this.parms.title = ""; this.parms.status = ""; this.getList(); },
    handleBtn(row) {
      this.handleModel.id = row.id; this.handleModel.title = row.title;
      this.handleModel.orderResult = row.orderResult || "";
      this.handleDialog.visible = true;
    },
    viewBtn(row) {
      this.viewData = row;
      // 基本信息表格数据
      this.basicInfoData = [
        { label: '工单标题', value: row.title, key: 'title' },
        { label: '问题描述', value: row.description || '暂无描述', key: 'description' },
        { label: '提交人员', value: row.submitterName || '暂无', key: 'submitterName' },
        { label: '提交时间', value: row.createTime || '暂无', key: 'createTime' },
        { label: '处理人员', value: row.handlerName || '暂无', key: 'handlerName' },
        { label: '复核人员', value: row.reviewerName || '暂无', key: 'reviewerName' }
      ];
      // 执行情况表格数据
      this.executionInfoData = [
        { label: '处理结果', value: row.orderResult || '暂无处理结果', key: 'orderResult' },
        { label: '复核意见', value: row.reviewResult || '暂无复核意见', key: 'reviewResult' },
        { label: '完成时间', value: row.finishTime || '尚未完成', key: 'finishTime' },
        { label: '满意度评价', value: row.satisfaction ? row.satisfaction + '分' : '尚未评价', key: 'satisfaction' }
      ];
      this.viewDialog.visible = true;
    },
    onConfirm() {
      this.$refs.handleForm.validate(async (valid) => {
        if (valid) {
          let res = await handleWorkOrderApi(this.handleModel);
          if (res && res.code == 200) { this.getList(); this.$message.success(res.msg); this.handleDialog.visible = false; }
        }
      });
    },
    currentChange(val) { this.parms.currentPage = val; this.getList(); },
    sizeChange(val) { this.parms.pageSize = val; this.getList(); },
  },
};
</script>

<style scoped>
.detail-container {
  padding: 25px;
  background: #fff;
  min-height: 600px;
}

.detail-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.header-item {
  margin-right: 40px;
  display: flex;
  align-items: center;
}

.header-item .label {
  font-size: 15px;
  color: #fff;
  margin-right: 12px;
  font-weight: 500;
}

.detail-section {
  margin-top: 30px;
  background: #fafbfc;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #2c3e50;
  display: flex;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8eaed;
}

.section-title i {
  margin-right: 8px;
  font-size: 18px;
  color: #667eea;
}

.custom-table {
  background: #fff;
  border-radius: 6px;
  overflow: hidden;
  margin-top: 10px;
}

.custom-table :deep(.table-label-column .cell) {
  background: #f8f9fa;
  font-weight: 600;
  color: #495057;
  width: 120px;
  padding: 12px 16px;
  text-align: right;
}

.custom-table :deep(.table-value-column .cell) {
  background: #fff;
  color: #212529;
  padding: 12px 16px;
  font-size: 14px;
  text-align: left;
}

.custom-table :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.custom-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

.label-bold {
  font-weight: 600;
  color: #343a40;
}

.description-content {
  line-height: 1.8;
  color: #495057;
  padding: 12px;
  font-size: 14px;
  background: #f8f9fa;
  border-radius: 4px;
  min-height: 60px;
}

.image-box {
  padding: 15px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: center;
}

.detail-image {
  width: 100%;
  max-width: 500px;
  height: 350px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transition: transform 0.3s ease;
}

.detail-image:hover {
  transform: scale(1.02);
}

.no-image {
  padding: 40px;
  text-align: center;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.result-content {
  line-height: 1.8;
  color: #495057;
  padding: 16px;
  font-size: 14px;
  background: #fff;
  border-radius: 4px;
  border-left: 3px solid #667eea;
  min-height: 80px;
}

.result-empty {
  color: #adb5bd;
  font-style: italic;
  border-left-color: #ced4da;
}
</style>
