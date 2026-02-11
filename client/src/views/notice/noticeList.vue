<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="标题">
        <el-input v-model="parms.title"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" @click="resetBtn" icon="el-icon-close">重置</el-button>
        <el-button v-if="hasPerm('sys:noticeList:add')" icon="el-icon-plus" type="primary"
          @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column label="标题" prop="title"></el-table-column>
      <el-table-column label="内容" prop="noticeContent"></el-table-column>
      
      <el-table-column v-if="isPlatformAdmin" label="所属公司" prop="companyName" align="center" width="180">
        <template slot-scope="scope">
           <el-tag v-if="scope.row.companyName" type="info" size="small">{{ scope.row.companyName }}</el-tag>
           <span v-else style="color:#909399;font-size:12px;">平台公告</span>
        </template>
      </el-table-column>

      <el-table-column label="时间" prop="createTime"></el-table-column>
      <el-table-column align="center" width="180" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:noticeList:edit')" icon="el-icon-edit" type="primary" size="small"
            @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:noticeList:delete')" icon="el-icon-delete" type="danger" size="small"
            @click="deleteBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="parms.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total" background>
    </el-pagination>

    <sys-dialog :title="addDialog.title" :height="addDialog.height" :width="addDialog.width" :visible="addDialog.visible"
      @onClose="onClose" @onConfirm="onConfirm">
      <template slot="content">
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" size="small">
          
          <el-form-item v-if="isPlatformAdmin" prop="companyId" label="所属公司">
             <el-select v-model="addModel.companyId" placeholder="请选择" filterable clearable style="width: 100%">
                <el-option
                  v-for="item in companyList"
                  :key="item.companyId"
                  :label="item.companyName"
                  :value="item.companyId">
                </el-option>
             </el-select>
          </el-form-item>

          <el-form-item prop="title" label="标题">
            <el-input v-model="addModel.title"></el-input>
          </el-form-item>
          
          <el-form-item prop="noticeContent" label="内容">
            <el-input type="textarea" :rows="10" v-model="addModel.noticeContent"></el-input>
          </el-form-item>
          
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import SysDialog from "@/components/system/SysDialog.vue";
import { getListApi, addApi, editApi, deleteApi } from "@/api/notice";
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog,
  },
  computed: {
    ...mapGetters(["permission_routes"]),
    isPlatformAdmin() {
      return JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      companyList: [],
      tableHeight: 0,
      tableList: [],
      addModel: {
        editType: "",
        noticeId: "",
        title: "",
        noticeContent: "",
        companyId: ""
      },
      rules: {
        title: [
          { required: true, trigger: "change", message: "请输入标题" },
        ],
        noticeContent: [
          { required: true, trigger: "change", message: "请输入内容" },
        ],
        companyId: [
          { required: false, trigger: "change", message: "请选择所属公司" }
        ]
      },
      addDialog: {
        title: "",
        // 因为表单变成了垂直排列，高度需要适当增加
        height: 480, 
        // 宽度适当增加，让大文本框看起来更舒服
        width: 700,
        visible: false,
      },
      parms: {
        title: "",
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
    };
  },
  created() {
    this.getList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
    if(this.isPlatformAdmin) {
        this.getCompanyList();
        this.rules.companyId[0].required = true;
        // 如果是管理员，多了一个下拉框，高度再加一点
        this.addDialog.height = 530; 
    }
  },
  methods: {
    async getCompanyList() {
      let res = await getCompanyListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) {
        this.companyList = res.data.records;
      }
    },
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModel.editType == "0") {
            res = await addApi(this.addModel);
          } else {
            res = await editApi(this.addModel);
          }
          if (res && res.code == 200) {
            this.getList();
            this.$message.success(res.msg);
            this.addDialog.visible = false;
          }
        }
      });
    },
    onClose() {
      this.addDialog.visible = false;
    },
    currentChange(val) {
      this.parms.currentPage = val;
      this.getList();
    },
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    async deleteBtn(row) {
      let confirm = await this.$myconfirm("确定删除该数据吗?");
      if (confirm) {
        let res = await deleteApi({ noticeId: row.noticeId });
        if (res && res.code == 200) {
          this.getList();
          this.$message.success(res.msg);
        }
      }
    },
    editBtn(row) {
      this.$resetForm("addForm", this.addModel);
      this.$objCoppy(row, this.addModel);
      this.addModel.editType = "1";
      this.addDialog.title = "编辑公告";
      this.addDialog.visible = true;
    },
    addBtn() {
      this.$resetForm("addForm", this.addModel);
      
      if(!this.isPlatformAdmin) {
          this.addModel.companyId = ""; 
      } else {
          this.addModel.companyId = ""; 
      }

      this.addModel.editType = "0";
      this.addDialog.title = "新增公告";
      this.addDialog.visible = true;
    },
    resetBtn() {
      this.parms.title = "";
      this.getList();
    },
    searchBtn() {
      this.getList();
    },
    async getList() {
      let res = await getListApi(this.parms);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
  },
};
</script>

<style scoped>
</style>