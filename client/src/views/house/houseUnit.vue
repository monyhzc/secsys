<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="栋数名称:">
        <el-input v-model="parms.buildNme"></el-input>
      </el-form-item>
      <el-form-item label="单元名称:">
        <el-input v-model="parms.unitName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button @click="resetBtn" style="color: #ff7670" icon="el-icon-close">重置</el-button>
        <el-button v-if="hasPerm('sys:houseUnit:add')" @click="addBtn" icon="el-icon-plus" type="primary">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column v-if="isPlatformAdmin" label="所属公司" prop="companyName" width="180" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.companyName" type="info">{{ scope.row.companyName }}</el-tag>
          <span v-else>平台公共</span>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="栋数名称"></el-table-column>
      <el-table-column prop="unitName" label="单元名称"></el-table-column>
      <el-table-column width="200" align="center" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:houseUnit:edit')" icon="el-icon-edit" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:houseUnit:delete')" icon="el-icon-delete" type="danger" size="small" @click="deleteBtn(scope.row)">删除</el-button>
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
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" :inline="true" size="small">
          
          <el-form-item v-if="isPlatformAdmin" label="所属公司" prop="companyId">
             <el-select v-model="addModel.companyId" placeholder="请选择" filterable clearable @change="companyChange">
                <el-option
                  v-for="item in companyList"
                  :key="item.companyId"
                  :label="item.companyName"
                  :value="item.companyId">
                </el-option>
             </el-select>
          </el-form-item>

          <el-form-item prop="buildId" label="所属栋数">
            <el-select v-model="addModel.buildId" placeholder="请选择栋数" filterable>
              <el-option v-for="item in buildList" :key="item.buildId" :label="item.name" :value="item.buildId"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item prop="unitName" label="单元名称">
            <el-input v-model="addModel.unitName"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, unitListApi, editApi, deleteApi } from "@/api/houseUnit";
import SysDialog from "@/components/system/SysDialog";
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog
  },
  computed: {
    ...mapGetters(["userInfo", "permission_routes"]),
    isPlatformAdmin() {
      // 判断路由中是否有公司管理菜单权限，以此识别平台管理员
      return JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      companyList: [],
      buildList: [],
      rules: {
        buildId: [{ required: true, message: "请选择栋数", trigger: "change" }],
        unitName: [{ required: true, message: "请填写单元名称", trigger: "blur" }],
        companyId: [{ required: false, message: "请选择所属公司", trigger: "change" }]
      },
      addModel: {
        editType: "", // 0:新增 1:编辑
        unitId: "",
        buildId: "",
        unitName: "",
        companyId: ""
      },
      addDialog: {
        title: "",
        height: 150,
        width: 650,
        visible: false
      },
      tableHeight: 0,
      tableList: [],
      parms: {
        buildNme: "",
        unitName: "",
        currentPage: 1,
        pageSize: 10,
        total: 0
      }
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
    this.getList();
    
    // 初始化：如果是平台管理员，开启必填校验并加载公司列表
    if (this.isPlatformAdmin) {
      this.rules.companyId[0].required = true;
      this.getCompanyList();
    } else {
      // 普通管理员：直接加载自己的栋数
      this.getBuildList(); 
    }
  },
  methods: {
    // 获取公司列表
    async getCompanyList() {
      let res = await getCompanyListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) {
        this.companyList = res.data.records;
      }
    },
    
    // 【修复】获取栋数列表，支持传参
    async getBuildList(companyId) {
      // 构建参数对象
      let parm = {};
      if (companyId) {
        parm.companyId = companyId;
      }
      let res = await unitListApi(parm);
      if (res && res.code == 200) {
        this.buildList = res.data;
      }
    },

    // 级联事件：公司改变 -> 刷新栋数
    companyChange(val) {
      this.addModel.buildId = ""; // 清空已选栋数
      this.buildList = [];
      if (val) {
        this.getBuildList(val);
      }
    },

    onConfirm() {
      this.$refs.addForm.validate(async valid => {
        if (valid) {
          let res = null;
          if (this.addModel.editType == "0") {
            res = await addApi(this.addModel);
          } else {
            res = await editApi(this.addModel);
          }
          if (res && res.code == 200) {
            this.getList();
            this.addDialog.visible = false;
            this.$message.success(res.msg);
          }
        }
      });
    },
    onClose() {
      this.addDialog.visible = false;
    },
    async getList() {
      let res = await getListApi(this.parms);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
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
      const confirm = await this.$myconfirm('确定删除该数据吗?');
      if (confirm) {
        let res = await deleteApi({ unitId: row.unitId });
        if (res && res.code == 200) {
          this.getList();
          this.$message.success(res.msg)
        }
      }
    },
    // 编辑按钮
    async editBtn(row) {
      this.$resetForm("addForm", this.addModel);
      this.addModel.editType = "1";
      this.addDialog.title = '编辑单元';
      this.addDialog.height = this.isPlatformAdmin ? 200 : 150;
      
      // 数据回显
      this.$objCoppy(row, this.addModel);
      
      // 级联回显：如果是平台管理员，需要先加载该公司下的栋数
      if (this.isPlatformAdmin) {
        if (row.companyId) {
           await this.getBuildList(row.companyId);
        }
      } else {
        // 普通管理员保险起见检查一下列表
        if (this.buildList.length === 0) {
           await this.getBuildList();
        }
      }
      
      this.addDialog.visible = true;
    },
    // 新增按钮
    addBtn() {
      this.$resetForm("addForm", this.addModel);
      this.addModel.editType = "0";
      
      if (!this.isPlatformAdmin) {
        this.addModel.companyId = "";
        // 普通管理员确保栋数列表已加载
        if (this.buildList.length === 0) {
           this.getBuildList();
        }
      } else {
        // 平台管理员清空栋数列表，等待选择公司
        this.buildList = [];
      }
      
      this.addDialog.title = "新增单元";
      this.addDialog.height = this.isPlatformAdmin ? 200 : 150;
      this.addDialog.visible = true;
    },
    resetBtn() {
      this.parms.buildNme = "";
      this.parms.unitName = "";
      this.getList();
    },
    searchBtn() {
      this.getList();
    }
  }
};
</script>

<style scoped></style>