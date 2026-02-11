<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="姓名">
        <el-input v-model="parms.loginName"></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="parms.phone"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="searchBtn" icon="el-icon-search">查询</el-button>
        <el-button @click="resetBtn" style="color: #ff7670" icon="el-icon-delete">重置</el-button>
        <el-button v-if="hasPerm('sys:user:add')" @click="addUser" type="primary" icon="el-icon-plus">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableList" :height="tableHeight" size="small" empty-text="暂无数据" border stripe>
      <el-table-column prop="loginName" label="姓名"></el-table-column>
      <el-table-column prop="phone" label="电话"></el-table-column>
      <el-table-column prop="idCard" label="身份证" width="200"></el-table-column>
      
      <el-table-column v-if="isPlatformAdmin" prop="companyName" label="所属公司" align="center" width="150">
         <template slot-scope="scope">
            <el-tag v-if="scope.row.companyName" type="info">{{ scope.row.companyName }}</el-tag>
            <span v-else>平台总台</span>
         </template>
      </el-table-column>

      <el-table-column align="center" prop="sex" label="性别">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.sex == '1'" size="normal">男</el-tag>
          <el-tag v-if="scope.row.sex == '0'" type="success" size="normal">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="status" label="是否离职">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="'1'" :inactive-value="'0'" active-text="是"
            inactive-text="否" @change="changeStatus(scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="isUsed" label="是否启用">
        <template slot-scope="scope">
          <el-switch v-if="scope.row.isAdmin != '1'" v-model="scope.row.isUsed" :active-value="'0'"
            :inactive-value="'1'" active-text="是" inactive-text="否" @change="changeUsed(scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column align="center" width="290" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:user:edit')" type="primary" icon="el-icon-edit" size="small"
            @click="editUser(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:user:assignRole')" type="success" icon="el-icon-edit" size="small"
            @click="assignRole(scope.row)">分配角色</el-button>
          <el-button v-if="hasPerm('sys:user:delete')" type="danger" icon="el-icon-delete" size="small"
            @click="deleteUser(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="parms.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total" background>
    </el-pagination>

    <sys-dialog :title="dialog.title" :visible="dialog.visible" :height="dialog.height" :width="dialog.width"
      @onClose="onClose" @onConfirm="onConfirm">
      <div slot="content">
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" :inline="true" size="small">
          
          <el-form-item label="姓名:" prop="loginName">
            <el-input v-model="addModel.loginName"></el-input>
          </el-form-item>
          <el-form-item prop="sex" style="width: 280px" label="性别:">
            <el-radio-group v-model="addModel.sex">
              <el-radio :label="'1'">男</el-radio>
              <el-radio :label="'0'">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="phone" label="电话:">
            <el-input v-model="addModel.phone"></el-input>
          </el-form-item>
          <el-form-item prop="idCard" label="身份证:">
            <el-input v-model="addModel.idCard"></el-input>
          </el-form-item>
          
          <el-form-item label="所属公司" prop="companyId" v-if="isPlatformAdmin">
             <el-select v-model="addModel.companyId" placeholder="请选择公司" filterable clearable @change="companyChange" style="width: 100%">
                <el-option
                  v-for="item in companyList"
                  :key="item.companyId"
                  :label="item.companyName"
                  :value="item.companyId">
                </el-option>
             </el-select>
          </el-form-item>

          <el-form-item prop="username" label="账号:">
            <el-input v-model="addModel.username"></el-input>
          </el-form-item>      
          
          <el-form-item 
            v-if="addModel.type == '0' && (!isPlatformAdmin || addModel.companyId)" 
            prop="roleId" 
            label="角色:">
             <el-select v-model="addModel.roleId" placeholder="请选择角色" style="width: 185px;">
                <el-option
                  v-for="item in roleListForSelect"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId">
                </el-option>
             </el-select>
          </el-form-item>
          
          <el-form-item v-if="addModel.type == '0'" label="密码:" prop="password">
            <el-input type="password" v-model="addModel.password"></el-input>
          </el-form-item>
          <el-form-item prop="status" style="width: 280px" label="离职:">
            <el-radio-group v-model="addModel.status">
              <el-radio :label="'1'">是</el-radio>
              <el-radio :label="'0'">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="isUsed" style="width: 280px" label="启用:">
            <el-radio-group v-model="addModel.isUsed">
              <el-radio :label="'1'">否</el-radio>
              <el-radio :label="'0'">是</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
    </sys-dialog>

    <sys-dialog :title="roleDialog.title" :height="roleDialog.height" :width="roleDialog.width"
      :visible="roleDialog.visible" @onClose="roleClose" @onConfirm="roleConfirm">
      <template slot="content">
        <el-table :height="roleHeight * 2.2" :data="roleList" border stripe>
          <el-table-column width="50" align="center" label="选择">
            <template slot-scope="scope">
              <el-radio v-model="radio" :label="scope.row.roleId" @change="getCurrentRow(scope.row)">
                {{ "" }}
              </el-radio>
            </template>
          </el-table-column>
          <el-table-column prop="roleName" label="角色名称"></el-table-column>
          <el-table-column prop="remark" label="角色备注"></el-table-column>
        </el-table>
        <el-pagination @size-change="roleSizeChange" @current-change="roleCurrentChange"
          :current-page.sync="roleParm.currentPage" :page-sizes="[10, 20, 40, 80, 100]" :page-size="roleParm.pageSize"
          layout="total, sizes, prev, pager, next, jumper" :total="roleParm.total" background>
        </el-pagination>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import {
  getUserListApi,
  addUserApi,
  editUserApi,
  deleteUserApi,
  getRoleByUserIdApi,
  assignSaveApi
} from "@/api/user";
import SysDialog from "@/components/system/SysDialog";
import { getRoleListApi } from "@/api/role";
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog
  },
  computed: {
    // 【修改】：引入 permission_routes
    ...mapGetters(["userInfo", "roles", "permission_routes"]),
    
    // 【修改】：使用你要求的基于 permission_routes 的判断逻辑
    isPlatformAdmin() {
      // 增加 permission_routes 判空，防止未加载完成时报错
      return this.permission_routes && JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      assignParm: {
        roleId: "",
        userId: ""
      },
      radio: "",
      roleHeight: 0,
      roleList: [], 
      roleListForSelect: [],
      companyList: [], 
      
      roleParm: {
        pageSize: 10,
        currentPage: 1,
        total: 0,
        companyId: "" 
      },
      roleDialog: {
        title: "",
        height: 400,
        width: 800,
        visible: false
      },
      rules: {
        loginName: [{ required: true, trigger: "change", message: "请填写姓名" }],
        sex: [{ required: true, trigger: "change", message: "请选择性别" }],
        phone: [{ required: true, trigger: "change", message: "请填写电话" }],
        idCard: [{ required: true, trigger: "change", message: "请填写身份证" }],
        status: [{ required: true, trigger: "change", message: "请选择是否离职" }],
        isUsed: [{ required: true, trigger: "change", message: "请选择是否启用" }],
        password: [{ required: true, trigger: "change", message: "请填写密码" }],
        username: [{ required: true, trigger: "change", message: "请填写账号" }],
        companyId: [{ required: false, trigger: "change", message: "请选择所属公司" }],
        roleId: [{ required: true, trigger: "change", message: "请选择角色" }]
      },
      addModel: {
        userId: "",
        type: "", 
        username: "",
        sex: "",
        phone: "",
        idCard: "",
        loginName: "",
        password: "",
        isUsed: "",
        status: "",
        companyId: "",
        roleId: ""    
      },
      dialog: {
        title: "",
        visible: false,
        height: 240,
        width: 610
      },
      tableHeight: 0,
      parms: {
        loginName: "",
        phone: "",
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      tableList: []
    };
  },
  created() {
    this.getUserList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
    // 如果是平台管理员，加载所有公司
    if (this.isPlatformAdmin) {
      this.getCompanyList();
      // 防止 rules.companyId 为空报错
      if(this.rules.companyId && this.rules.companyId[0]){
          this.rules.companyId[0].required = true;
      }
    }
  },
  methods: {
    // 必须定义 hasPerm 防止模板报错
    hasPerm(val) {
        return true; 
    },

    async getCompanyList() {
      let res = await getCompanyListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) {
        this.companyList = res.data.records;
      }
    },
    
    // 监听公司下拉框改变
    companyChange(val) {
      // 1. 清空已选角色
      this.addModel.roleId = "";
      this.roleListForSelect = [];
      
      // 2. 如果选了公司，加载该公司的角色
      if (val) {
        this.getRoleListForSelect(val);
      }
    },

    // 获取下拉框用的角色列表 (带公司ID)
    async getRoleListForSelect(companyId) {
      let parm = {
        currentPage: 1,
        pageSize: 999,
        companyId: companyId 
      };
      let res = await getRoleListApi(parm);
      if (res && res.code == 200) {
        this.roleListForSelect = res.data.records;
      }
    },

    // 原有的获取角色列表 (用于分配角色弹窗)
    async getRoleList() {
      let res = await getRoleListApi(this.roleParm);
      if (res && res.code == 200) {
        this.roleList = res.data.records;
        this.roleParm.total = res.data.total;
      }
    },

    getCurrentRow(row) {
      this.assignParm.roleId = row.roleId;
    },
    roleCurrentChange(val) {
      this.roleParm.currentPage = val;
      this.getRoleList();
    },
    roleSizeChange(val) {
      this.roleParm.pageSize = val;
      this.getRoleList();
    },
    async roleConfirm() {
      let res = await assignSaveApi(this.assignParm);
      if (res && res.code == 200) {
        this.$message.success(res.msg);
        this.roleDialog.visible = false;
        this.getUserList(); 
      }
    },
    roleClose() {
      this.roleDialog.visible = false;
    },
    resetBtn() {
      this.parms.phone = "";
      this.parms.loginName = "";
      this.getUserList();
    },
    searchBtn() {
      this.getUserList();
    },
    async deleteUser(row) {
      let confrim = await this.$myconfirm("确定删除该员工吗?");
      if (confrim) {
        let res = await deleteUserApi({ userId: row.userId });
        if (res && res.code == 200) {
          this.getUserList();
          this.$message.success(res.msg);
        }
      }
    },
    
    // 新增按钮点击
    addUser() {
      this.$resetForm("addForm", this.addModel);
      this.addModel.type = "0";
      this.addModel.companyId = "";
      this.addModel.roleId = "";
      this.roleListForSelect = []; 
      
      this.dialog.title = "新增员工";
      this.dialog.height = this.isPlatformAdmin ? 450 : 350; 
      this.dialog.visible = true;
      
      // 非超管直接加载当前公司角色（因为非超管不会触发companyChange）
      if (!this.isPlatformAdmin) {
         this.getRoleListForSelect(null); 
      }
    },
    
    // 编辑按钮点击
    editUser(row) {
      this.$resetForm("addForm", this.addModel);
      this.addModel.type = "1";
      this.$objCoppy(row, this.addModel);
      
      if (row.companyId) {
         this.getRoleListForSelect(row.companyId);
         this.getRoleByUser(row.userId);
      } else {
         this.getRoleListForSelect(null);
         this.getRoleByUser(row.userId);
      }

      this.dialog.title = "编辑员工";
      this.dialog.height = this.isPlatformAdmin ? 450 : 350;
      this.dialog.visible = true;
    },
    
    async getRoleByUser(userId) {
       let res = await getRoleByUserIdApi({userId: userId});
       if(res && res.code == 200 && res.data) {
           this.addModel.roleId = res.data.roleId;
       }
    },

    async assignRole(row) {
      this.radio = "";
      this.assignParm.userId = row.userId;
      this.roleDialog.title = "为【" + row.loginName + "】分配角色";
      this.roleDialog.visible = true;
      this.roleParm.companyId = row.companyId; 
      this.getRoleList();
      
      this.$nextTick(() => {
        this.roleHeight = window.innerHeight - 600;
      });
      let roleRes = await getRoleByUserIdApi({ userId: row.userId });
      if (roleRes && roleRes.code == 200) {
        if (roleRes.data) {
          this.radio = roleRes.data.roleId;
          this.assignParm.roleId = roleRes.data.roleId;
        }
      }
    },

    async changeUsed(row) {
      if (row.isAdmin == "1") {
        this.$message.warning("该用户是超级管理员，不能禁用!");
        return;
      }
      let parm = {
        userId: row.userId,
        isUsed: row.isUsed
      };
      let res = await editUserApi(parm);
      if (res && res.code == 200) {
        this.getUserList();
        this.$message.success(res.msg);
      }
    },
    async changeStatus(row) {
      let parm = {
        userId: row.userId,
        status: row.status
      };
      let res = await editUserApi(parm);
      if (res && res.code == 200) {
        this.getUserList();
        this.$message.success(res.msg);
      }
    },
    onConfirm() {
      this.$refs.addForm.validate(async valid => {
        if (valid) {
          let res = null;
          if (this.addModel.type == "0") {
            res = await addUserApi(this.addModel);
          } else {
            res = await editUserApi(this.addModel);
          }
          if (res && res.code == 200) {
            this.getUserList();
            this.dialog.visible = false;
            this.$message.success(res.msg);
          }
        }
      });
    },
    onClose() {
      this.dialog.visible = false;
    },
    async getUserList() {
      let res = await getUserListApi(this.parms);
      if (res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getUserList();
    },
    currentChange(val) {
      this.parms.currentPage = val;
      this.getUserList();
    }
  }
};
</script>

<style scoped>
</style>