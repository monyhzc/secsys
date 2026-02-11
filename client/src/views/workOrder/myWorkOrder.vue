<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="标题">
        <el-input v-model="parms.title"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" @click="resetBtn" icon="el-icon-close">重置</el-button>
        <el-button icon="el-icon-plus" type="primary" @click="addBtn">提交工单</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column label="标题" prop="title"></el-table-column>
      <el-table-column label="优先级" prop="priority" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.priority == '0'" type="info" size="small">低</el-tag>
          <el-tag v-if="scope.row.priority == '1'" type="primary" size="small">中</el-tag>
          <el-tag v-if="scope.row.priority == '2'" type="warning" size="small">高</el-tag>
          <el-tag v-if="scope.row.priority == '3'" type="danger" size="small">紧急</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" type="danger" size="small">待处理</el-tag>
          <el-tag v-if="scope.row.status == '1'" type="warning" size="small">处理中</el-tag>
          <el-tag v-if="scope.row.status == '2'" type="primary" size="small">待复核</el-tag>
          <el-tag v-if="scope.row.status == '3'" type="success" size="small">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评分" prop="satisfaction" width="150" align="center">
        <template slot-scope="scope">
          <el-rate v-if="scope.row.satisfaction" v-model="scope.row.satisfaction" disabled></el-rate>
          <span v-else-if="scope.row.status == '3'">未评价</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" prop="createTime" width="160"></el-table-column>
      <el-table-column align="center" width="220" label="操作">
        <template slot-scope="scope">
          <el-button icon="el-icon-view" type="info" size="mini" @click="viewBtn(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status == '3' && !scope.row.satisfaction" icon="el-icon-star-off" type="warning" size="mini" @click="evaluateBtn(scope.row)">评价</el-button>
          <el-button v-if="scope.row.status == '0'" icon="el-icon-edit" type="primary" size="mini" @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="scope.row.status == '0'" icon="el-icon-delete" type="danger" size="mini" @click="deleteBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="parms.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total" background>
    </el-pagination>

    <!-- 新增/编辑弹窗 -->
    <sys-dialog :title="addDialog.title" :height="addDialog.height" :width="addDialog.width" :visible="addDialog.visible"
      @onClose="onClose" @onConfirm="onConfirm">
      <template slot="content">
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" size="small">
          <el-form-item prop="title" label="标题">
            <el-input v-model="addModel.title"></el-input>
          </el-form-item>
          <el-form-item prop="priority" label="优先级">
            <el-select v-model="addModel.priority" placeholder="请选择优先级" style="width: 100%">
              <el-option label="低" value="0"></el-option>
              <el-option label="中" value="1"></el-option>
              <el-option label="高" value="2"></el-option>
              <el-option label="紧急" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="description" label="描述">
            <el-input type="textarea" :rows="3" v-model="addModel.description"></el-input>
          </el-form-item>
          <el-form-item label="图片">
            <el-upload
              class="avatar-uploader"
              action="#"
              :http-request="uploadImage"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload">
              <img v-if="addModel.image" :src="getImageUrl(addModel.image)" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item prop="handlerId" label="处理人">
            <el-select v-model="addModel.handlerId" placeholder="请选择处理人" filterable style="width: 100%">
              <el-option v-for="item in staffList" :key="item.userId" :label="item.loginName" :value="item.userId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="reviewerId" label="复核人">
            <el-select v-model="addModel.reviewerId" placeholder="请选择复核人" filterable style="width: 100%">
              <el-option v-for="item in staffList" :key="item.userId" :label="item.loginName" :value="item.userId"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>

    <!-- 评价弹窗 -->
    <sys-dialog :title="'评价工单'" :height="200" :width="400" :visible="evaluateDialog.visible"
      @onClose="evaluateDialog.visible = false" @onConfirm="evaluateConfirm">
      <template slot="content">
        <div style="text-align: center; padding-top: 20px;">
          <el-rate v-model="evaluateModel.satisfaction" show-text></el-rate>
        </div>
      </template>
    </sys-dialog>

    <!-- 详情弹窗 (UI 优化版) -->
    <sys-dialog :title="'工单详情'" :height="600" :width="800" :visible="viewDialog.visible"
      @onClose="viewDialog.visible = false">
      <template slot="content">
        <div class="detail-container">
          <!-- 头部状态栏 -->
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

          <!-- 基本信息 -->
          <el-divider content-position="left"><i class="el-icon-info"></i> 基本信息</el-divider>
          <el-table :data="basicInfoData" border style="width: 100%;" class="custom-table">
            <el-table-column prop="label" label="项目" width="120" align="right" class-name="table-label-column"></el-table-column>
            <el-table-column prop="value" label="内容" class-name="table-value-column">
              <template slot-scope="scope">
                <div v-if="scope.row.key === 'description'" class="description-content">{{ scope.row.value || '暂无描述' }}</div>
                <div v-else>{{ scope.row.value }}</div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 图片展示 -->
          <div v-if="viewData.image" class="detail-section">
            <div class="section-title">相关图片</div>
            <div class="image-box">
              <el-image 
                style="width: 200px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);" 
                :src="getImageUrl(viewData.image)" 
                :preview-src-list="[getImageUrl(viewData.image)]">
              </el-image>
            </div>
          </div>

          <!-- 处理结果 -->
          <el-divider content-position="left"><i class="el-icon-s-operation"></i> 执行情况</el-divider>
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
                <div v-else-if="scope.row.key === 'satisfaction'">
                  <el-rate v-if="scope.row.value" v-model="scope.row.value" disabled show-score></el-rate>
                  <span v-else class="text-muted">尚未评价</span>
                </div>
                <div v-else>{{ scope.row.value }}</div>
              </template>
            </el-table-column>
          </el-table>
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
import { getWorkOrderListApi, addWorkOrderApi, editWorkOrderApi, deleteWorkOrderApi, evaluateWorkOrderApi } from "@/api/workOrder";
import { getUserListApi } from "@/api/user";
import { uploadImageApi } from "@/api/imgUpload";
import { getUserId } from "@/utils/auth";

export default {
  components: {
    SysDialog,
  },
  data() {
    return {
      tableHeight: 0,
      tableList: [],
      staffList: [],
      addModel: {
        editType: "",
        id: "",
        title: "",
        description: "",
        image: "",
        handlerId: "",
        reviewerId: "",
        priority: "1",
      },
      evaluateModel: {
        id: "",
        satisfaction: 5,
      },
      rules: {
        title: [{ required: true, trigger: "blur", message: "请输入标题" }],
        description: [{ required: true, trigger: "blur", message: "请输入描述" }],
        handlerId: [{ required: true, trigger: "change", message: "请选择处理人" }],
        reviewerId: [{ required: true, trigger: "change", message: "请选择复核人" }],
        priority: [{ required: true, trigger: "change", message: "请选择优先级" }],
      },
      addDialog: { title: "", height: 520, width: 600, visible: false },
      viewDialog: { visible: false },
      evaluateDialog: { visible: false },
      viewData: {},
      basicInfoData: [],
      executionInfoData: [],
      parms: { title: "", submitterId: getUserId(), currentPage: 1, pageSize: 10, total: 0 },
    };
  },
  created() { this.getList(); this.getStaffList(); },
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
    async uploadImage(param) {
        const formData = new FormData();
        formData.append("file", param.file);
        let res = await uploadImageApi(formData);
        if (res && res.code == 200) {
            this.addModel.image = res.data;
            this.$message.success("图片上传成功");
        }
    },
    beforeAvatarUpload(file) {
        const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isJPG) this.$message.error('上传图片只能是 JPG/PNG 格式!');
        if (!isLt2M) this.$message.error('上传图片大小不能超过 2MB!');
        return isJPG && isLt2M;
    },
    async getStaffList() {
      let res = await getUserListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) this.staffList = res.data.records;
    },
    async getList() {
      let res = await getWorkOrderListApi(this.parms);
      if (res && res.code == 200) { this.tableList = res.data.records; this.parms.total = res.data.total; }
    },
    searchBtn() { this.getList(); },
    resetBtn() { this.parms.title = ""; this.getList(); },
    addBtn() {
      this.$resetForm("addForm", this.addModel);
      this.addModel.editType = "0";
      this.addModel.image = "";
      this.addModel.priority = "1";
      this.addDialog.title = "提交工单";
      this.addDialog.visible = true;
    },
    editBtn(row) {
      this.$resetForm("addForm", this.addModel);
      this.$objCoppy(row, this.addModel);
      this.addModel.editType = "1";
      this.addDialog.title = "编辑工单";
      this.addDialog.visible = true;
    },
    async deleteBtn(row) {
      let confirm = await this.$myconfirm("确定删除该工单吗?");
      if (confirm) {
        let res = await deleteWorkOrderApi(row.id);
        if (res && res.code == 200) { this.getList(); this.$message.success(res.msg); }
      }
    },
    evaluateBtn(row) { this.evaluateModel.id = row.id; this.evaluateModel.satisfaction = 5; this.evaluateDialog.visible = true; },
    async evaluateConfirm() {
      let res = await evaluateWorkOrderApi(this.evaluateModel);
      if (res && res.code == 200) { this.getList(); this.$message.success("评价成功"); this.evaluateDialog.visible = false; }
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
        { label: '满意度评价', value: row.satisfaction || '尚未评价', key: 'satisfaction' }
      ];
      this.viewDialog.visible = true;
    },
    onClose() { this.addDialog.visible = false; },
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = this.addModel.editType == "0" ? await addWorkOrderApi(this.addModel) : await editWorkOrderApi(this.addModel);
          if (res && res.code == 200) { this.getList(); this.$message.success(res.msg); this.addDialog.visible = false; }
        }
      });
    },
    currentChange(val) { this.parms.currentPage = val; this.getList(); },
    sizeChange(val) { this.parms.pageSize = val; this.getList(); },
  },
};
</script>

<style scoped>
.avatar-uploader .el-upload { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; position: relative; overflow: hidden; }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 120px; height: 120px; line-height: 120px; text-align: center; border: 1px dashed #d9d9d9; }
.avatar { width: 120px; height: 120px; display: block; }

.detail-container { padding: 10px; }
.detail-header { display: flex; justify-content: flex-start; margin-bottom: 20px; background: #f8f9fa; padding: 15px; border-radius: 4px; }
.header-item { margin-right: 40px; }
.header-item .label { font-size: 14px; color: #666; margin-right: 10px; }
.detail-section { margin-top: 20px; }
.section-title { font-size: 14px; font-weight: bold; margin-bottom: 10px; color: #333; }
.image-box { padding: 5px; }
.text-muted { color: #999; font-style: italic; }

/* 自定义表格样式 */
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

/* 描述内容样式 */
.description-content {
  line-height: 1.8;
  color: #495057;
  padding: 12px;
  font-size: 14px;
  background: #f8f9fa;
  border-radius: 4px;
  min-height: 60px;
}

/* 结果内容样式 */
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