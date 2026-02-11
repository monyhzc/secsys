<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="公司名称">
        <el-input v-model="parms.companyName" placeholder="请输入公司名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #FF7670" icon="el-icon-close" @click="resetBtn">重置</el-button>
        <el-button v-if="hasPerm('sys:company:add')" icon="el-icon-plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" :data="tableList" border stripe size="small">
      <el-table-column prop="companyName" label="公司名称"></el-table-column>
      <el-table-column prop="contactPerson" label="联系人"></el-table-column>
      <el-table-column prop="contactPhone" label="联系电话"></el-table-column>
      <el-table-column prop="expireTime" label="到期时间" width="160"></el-table-column>
      <el-table-column align="center" width="280" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:company:edit')" icon="el-icon-edit" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:company:term')" icon="el-icon-time" type="warning" size="small" @click="termBtn(scope.row)">续期</el-button>
          <el-button v-if="hasPerm('sys:company:delete')" icon="el-icon-delete" type="danger" size="small" @click="deleteBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]"
      :page-size="parms.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total"
      background
    >
    </el-pagination>

    <sys-dialog
      :title="dialog.title"
      :visible="dialog.visible"
      :height="dialog.height"
      :width="dialog.width"
      @onClose="onClose"
      @onConfirm="onConfirm"
    >
      <div slot="content">
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" size="small">
          <el-form-item prop="companyName" label="公司名称">
            <el-input v-model="addModel.companyName"></el-input>
          </el-form-item>
          <el-form-item prop="contactPerson" label="联系人">
            <el-input v-model="addModel.contactPerson"></el-input>
          </el-form-item>
          <el-form-item prop="contactPhone" label="联系电话">
            <el-input v-model="addModel.contactPhone"></el-input>
          </el-form-item>
          <el-form-item prop="expireTime" label="使用期限">
            <el-date-picker
              v-model="addModel.expireTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择到期时间"
              style="width: 100%">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </sys-dialog>

    <sys-dialog
      :title="termDialog.title"
      :visible="termDialog.visible"
      :height="termDialog.height"
      :width="termDialog.width"
      @onClose="termClose"
      @onConfirm="termConfirm"
    >
      <div slot="content">
        <el-form :model="termModel" ref="termForm" label-width="80px" size="small">
          <el-form-item label="公司名称">
            <el-input v-model="termModel.companyName" disabled></el-input>
          </el-form-item>
          <el-form-item prop="expireTime" label="到期时间" :rules="[{ required: true, message: '请选择时间', trigger: 'change' }]">
            <el-date-picker
              v-model="termModel.expireTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择续期时间"
              style="width: 100%">
            </el-date-picker>
          </el-form-item>
        </el-form>
      </div>
    </sys-dialog>
  </el-main>
</template>

<script>
// 【修改】引入 updateTermApi
import { getCompanyListApi, addCompanyApi, editCompanyApi, deleteCompanyApi, updateTermApi } from '@/api/company'
import SysDialog from "@/components/system/SysDialog";

export default {
  name: 'sysCompanyList',
  components: {
    SysDialog
  },
  data() {
    return {
      tableHeight: 0,
      tableList: [],
      // 查询参数
      parms: {
        currentPage: 1,
        pageSize: 10,
        companyName: '',
        total: 0
      },
      // 表单数据
      addModel: {
        editType: '', // 0:新增 1:编辑
        companyId: '',
        companyName: '',
        contactPerson: '',
        contactPhone: '',
        expireTime: '' // 【新增】字段
      },
      // 表单验证规则
      rules: {
        companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
        contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        expireTime: [{ required: true, message: '请选择使用期限', trigger: 'change' }] // 【新增】规则
      },
      // 新增/编辑弹框属性
      dialog: {
        title: '',
        visible: false,
        height: 200, // 稍微调高一点以容纳新字段
        width: 630
      },
      // 【新增】续期弹框属性
      termDialog: {
        title: '公司续期',
        visible: false,
        height: 150,
        width: 600
      },
      termModel: {
        companyId: '',
        companyName: '',
        expireTime: ''
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    })
    this.getList();
  },
  methods: {
    // 获取列表
    async getList() {
      let res = await getCompanyListApi(this.parms);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
    // 搜索按钮
    searchBtn() {
      this.parms.currentPage = 1; 
      this.getList();
    },
    // 重置按钮
    resetBtn() {
      this.parms.companyName = '';
      this.parms.currentPage = 1;
      this.getList();
    },
    // 新增按钮
    addBtn() {
      // 清空表单
      this.$resetForm('addForm', this.addModel);
      this.addModel.editType = '0';
      this.dialog.title = '新增物业公司';
      this.dialog.visible = true;
    },
    // 编辑按钮
    editBtn(row) {
      this.$resetForm('addForm', this.addModel);
      this.addModel.editType = '1';
      this.dialog.title = '编辑物业公司';
      // 数据回显
      this.$objCoppy(row, this.addModel);
      this.dialog.visible = true;
    },
    // 删除按钮
    async deleteBtn(row) {
      let confirm = await this.$myconfirm("确定删除该数据吗?");
      if (confirm) {
        let res = await deleteCompanyApi({ companyId: row.companyId });
        if (res && res.code == 200) {
          this.$message.success(res.msg);
          this.getList();
        }
      }
    },
    // 弹框确认
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModel.editType == '0') {
            res = await addCompanyApi(this.addModel);
          } else {
            res = await editCompanyApi(this.addModel);
          }
          if (res && res.code == 200) {
            this.$message.success(res.msg);
            this.getList();
            this.dialog.visible = false;
          }
        }
      });
    },
    // 弹框关闭
    onClose() {
      this.dialog.visible = false;
    },
    // 页容量改变
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    // 页码改变
    currentChange(val) {
      this.parms.currentPage = val;
      this.getList();
    },
    // 【新增】点击“续期”按钮
    termBtn(row) {
      this.termModel.companyId = row.companyId;
      this.termModel.companyName = row.companyName;
      this.termModel.expireTime = row.expireTime;
      this.termDialog.visible = true;
    },
    // 【新增】续期弹窗关闭
    termClose() {
      this.termDialog.visible = false;
    },
    // 【新增】续期提交
    async termConfirm() {
      if (!this.termModel.expireTime) {
        this.$message.warning("请选择到期时间");
        return;
      }
      let res = await updateTermApi({
          companyId: this.termModel.companyId,
          expireTime: this.termModel.expireTime
      });
      if (res && res.code == 200) {
        this.$message.success(res.msg);
        this.getList(); // 刷新列表
        this.termDialog.visible = false;
      }
    }
  }
}
</script>

<style scoped>
</style>