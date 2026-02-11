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
      <el-table-column label="提交人" prop="submitterName"></el-table-column>
      <el-table-column label="处理人" prop="handlerName"></el-table-column>
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" type="danger" size="small">待处理</el-tag>
          <el-tag v-if="scope.row.status == '1'" type="warning" size="small">处理中</el-tag>
          <el-tag v-if="scope.row.status == '2'" type="primary" size="small">待复核</el-tag>
          <el-tag v-if="scope.row.status == '3'" type="success" size="small">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" prop="createTime" width="160"></el-table-column>
      <el-table-column align="center" width="180" label="操作">
        <template slot-scope="scope">
          <el-button icon="el-icon-view" type="info" size="mini" @click="viewBtn(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status == '2'" icon="el-icon-check" type="success" size="mini" @click="reviewBtn(scope.row)">复核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="parms.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total" background>
    </el-pagination>

    <!-- 复核弹窗 -->
    <sys-dialog :title="reviewDialog.title" :height="reviewDialog.height" :width="reviewDialog.width" :visible="reviewDialog.visible"
      @onClose="reviewDialog.visible = false" @onConfirm="onConfirm">
      <template slot="content">
        <el-form :model="reviewModel" ref="reviewForm" :rules="rules" label-width="80px" size="small">
          <el-form-item label="标题">
            <el-input v-model="reviewModel.title" disabled></el-input>
          </el-form-item>
          <el-form-item label="处理结果">
            <el-input type="textarea" v-model="reviewModel.orderResult" disabled></el-input>
          </el-form-item>
          <el-form-item prop="reviewResult" label="复核意见">
            <el-input type="textarea" :rows="5" v-model="reviewModel.reviewResult" placeholder="请输入复核意见"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>

    <!-- 详情弹窗 -->
    <sys-dialog :title="'工单详情'" :height="600" :width="800" :visible="viewDialog.visible"
      @onClose="viewDialog.visible = false">
      <template slot="content">
        <div class="detail-container">
          <div class="detail-header">
            <div class="header-item">
              <span class="label">当前状态:</span>
              <el-tag :type="getStatusType(viewData.status)" effect="dark">{{ getStatusLabel(viewData.status) }}</el-tag>
            </div>
            <div class="header-item">
              <span class="label">优先级:</span>
              <el-tag :type="getPriorityType(viewData.priority)" effect="plain">{{ getPriorityLabel(viewData.priority) }}</el-tag>
            </div>
          </div>

          <el-divider content-position="left"><i class="el-icon-info"></i> 基本信息</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="工单标题" :span="2">{{ viewData.title }}</el-descriptions-item>
            <el-descriptions-item label="提交人员">{{ viewData.submitterName }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ viewData.createTime }}</el-descriptions-item>
            <el-descriptions-item label="处理人员">{{ viewData.handlerName }}</el-descriptions-item>
            <el-descriptions-item label="复核人员">{{ viewData.reviewerName }}</el-descriptions-item>
            <el-descriptions-item label="问题描述" :span="2">{{ viewData.description }}</el-descriptions-item>
          </el-descriptions>

          <div v-if="viewData.image" class="detail-section">
            <div class="section-title">现场图片</div>
            <div class="image-box">
              <el-image 
                style="width: 200px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);" 
                :src="getImageUrl(viewData.image)" 
                :preview-src-list="[getImageUrl(viewData.image)]">
              </el-image>
            </div>
          </div>

          <el-divider content-position="left"><i class="el-icon-s-operation"></i> 执行情况</el-divider>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="处理结果">{{ viewData.orderResult || '尚未填写' }}</el-descriptions-item>
            <el-descriptions-item label="复核意见">{{ viewData.reviewResult || '尚未填写' }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ viewData.finishTime || '-' }}</el-descriptions-item>
          </el-descriptions>
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
import { getWorkOrderListApi, reviewWorkOrderApi } from "@/api/workOrder";
import { getUserId } from "@/utils/auth";

export default {
  components: { SysDialog },
  data() {
    return {
      tableHeight: 0, tableList: [],
      reviewModel: { id: "", title: "", orderResult: "", reviewResult: "" },
      rules: { reviewResult: [{ required: true, trigger: "blur", message: "请输入复核结果" }] },
      reviewDialog: { title: "复核工单", height: 350, width: 600, visible: false },
      viewDialog: { visible: false },
      viewData: {},
      parms: { title: "", status: "", reviewerId: getUserId(), currentPage: 1, pageSize: 10, total: 0 },
    };
  },
  created() { this.getList(); },
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
    reviewBtn(row) {
      this.reviewModel.id = row.id; this.reviewModel.title = row.title;
      this.reviewModel.orderResult = row.orderResult || "";
      this.reviewModel.reviewResult = row.reviewResult || "";
      this.reviewDialog.visible = true;
    },
    viewBtn(row) { this.viewData = row; this.viewDialog.visible = true; },
    onConfirm() {
      this.$refs.reviewForm.validate(async (valid) => {
        if (valid) {
          let res = await reviewWorkOrderApi(this.reviewModel);
          if (res && res.code == 200) { this.getList(); this.$message.success(res.msg); this.reviewDialog.visible = false; }
        }
      });
    },
    currentChange(val) { this.parms.currentPage = val; this.getList(); },
    sizeChange(val) { this.parms.pageSize = val; this.getList(); },
  },
};
</script>

<style scoped>
.detail-container { padding: 10px; }
.detail-header { display: flex; justify-content: flex-start; margin-bottom: 20px; background: #f8f9fa; padding: 15px; border-radius: 4px; }
.header-item { margin-right: 40px; }
.header-item .label { font-size: 14px; color: #666; margin-right: 10px; }
.detail-section { margin-top: 20px; }
.section-title { font-size: 14px; font-weight: bold; margin-bottom: 10px; color: #333; }
.image-box { padding: 5px; }
.text-muted { color: #999; font-style: italic; }
</style>