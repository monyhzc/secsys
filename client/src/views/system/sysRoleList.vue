<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="角色名称">
        <el-input v-model="parms.roleName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button icon="el-icon-delete" style="color: #ff7670" @click="resetBtn">重置</el-button>
        <el-button v-if="hasPerm('sys:role:add')" type="primary" icon="el-icon-plus" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" size="small" :data="roleList" border stripe>
      <el-table-column label="角色名称" prop="roleName"></el-table-column>
      
      <el-table-column v-if="isPlatformAdmin" label="所属公司" prop="companyName" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.companyName" type="info">{{ scope.row.companyName }}</el-tag>
          <span v-else>平台公共/未分配</span>
        </template>
      </el-table-column>

      <el-table-column label="备注" prop="remark"></el-table-column>
      <el-table-column align="center" width="290" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:role:edit')" type="primary" icon="el-icon-edit" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:role:assignMenu')" type="primary" icon="el-icon-setting" size="small" @click="assignBtn(scope.row)">分配权限</el-button>
          <el-button v-if="hasPerm('sys:role:delete')" type="danger" icon="el-icon-delete" size="small" @click="deleteBtn(scope.row)">删除</el-button>
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
    ></el-pagination>

    <sys-dialog
      :title="dialog.title"
      :visible="dialog.visible"
      :height="dialog.height"
      :width="dialog.width"
      @onClose="onClose"
      @onConfirm="onConfirm"
    >
      <div slot="content">
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" :inline="true" size="small">
          <el-form-item prop="roleName" label="角色名称">
            <el-input v-model="addModel.roleName"></el-input>
          </el-form-item>
          
          <el-form-item v-if="isPlatformAdmin" prop="companyId" label="所属公司">
             <el-select v-model="addModel.companyId" placeholder="请选择" filterable clearable>
                <el-option
                  v-for="item in companyList"
                  :key="item.companyId"
                  :label="item.companyName"
                  :value="item.companyId">
                </el-option>
             </el-select>
          </el-form-item>

          <el-form-item label="备注">
            <el-input v-model="addModel.remark"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </sys-dialog>

    <sys-dialog
      :title="assignDialog.title"
      :visible="assignDialog.visible"
      :height="assignDialog.height"
      :width="assignDialog.width"
      @onClose="assignClose"
      @onConfirm="assignConfirm"
    >
      <div slot="content">
        <el-tree
          ref="assignTree"
          :data="assignTreeData"
          node-key="menuId"
          :props="defaultProps"
          empty-text="暂无数据"
          :show-checkbox="true"
          :default-expand-all="true"
          :default-checked-keys="assignTreeChecked"
        ></el-tree>
      </div>
    </sys-dialog>
  </el-main>
</template>

<script>
import {
  getRoleListApi,
  addRoleApi,
  editRoleApi,
  deleteRoleApi,
  getAssignTreeApi,
  assignSaveApi,
} from "@/api/role";
import SysDialog from "@/components/system/SysDialog";
// 【新增】引入公司列表API和Vuex
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog,
  },
  computed: {
    // 【核心逻辑】复用 sysUserList 中的逻辑，通过 permission_routes 判断是否为平台管理员
    ...mapGetters(["userInfo", "permission_routes"]),
    isPlatformAdmin() {
      // 只有平台管理员才有 sysCompanyList 菜单权限
      return JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      //权限树数据
      defaultProps: {
        children: "children",
        label: "menuLabel",
      },
      assignTreeData: [],
      assignTreeChecked: [],
      //角色id
      roleId: "",
      //分配权限弹框属性
      assignDialog: {
        title: "",
        visible: false,
        height: 450,
        width: 300,
      },
      //表格高度
      tableHeight: 0,
      //列表数据
      roleList: [],
      // 【新增】公司列表
      companyList: [],
      //列表分页查询参数
      parms: {
        pageSize: 10,
        currentPage: 1,
        roleName: "",
        total: 0,
      },
      //弹框属性
      dialog: {
        title: "",
        visible: false,
        height: 150, // 默认高度
        width: 630,
      },
      //新增表单绑定数据
      addModel: {
        roleId: "",
        roleName: "",
        remark: "",
        type: "", //0:新增 1：编辑
        companyId: "" // 【新增】绑定公司ID
      },
      //表单验证规则
      rules: {
        roleName: [{ required: true, trigger: "blur", message: "请输入角色名称" }],
        // 【新增】平台管理员必选公司，mounted中动态控制
        companyId: [{ required: false, trigger: "change", message: "请选择所属公司" }]
      },
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
    this.getRoleList();
    
    // 【新增】如果是平台管理员，加载公司列表并开启必填校验
    if (this.isPlatformAdmin) {
      this.getCompanyList();
      this.rules.companyId[0].required = true;
    }
  },
  methods: {
    // 【新增】获取公司列表
    async getCompanyList() {
      let res = await getCompanyListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) {
        this.companyList = res.data.records;
      }
    },
    //分配权限确认
    async assignConfirm() {
      let ids = this.$refs.assignTree
        .getCheckedKeys()
        .concat(this.$refs.assignTree.getHalfCheckedKeys());
      let parm = {
        roleId: this.roleId,
        list: ids,
      };
      let res = await assignSaveApi(parm);
      if (res && res.code == 200) {
        this.$message.success(res.msg);
        this.assignDialog.visible = false;
      }
    },
    //分配权限弹框关闭
    assignClose() {
      this.assignDialog.visible = false;
    },
    //分配权限按钮
    async assignBtn(row) {
      this.roleId = row.roleId;
      this.assignDialog.title = "为【" + row.roleName + "】分配权限";
      this.assignDialog.visible = true;
      let parm = {
        userId: this.$store.getters.userId,
        roleId: row.roleId,
      };
      let res = await getAssignTreeApi(parm);
      if (res && res.code == 200) {
        this.assignTreeData = res.data.listmenu;
        this.assignTreeChecked = res.data.checkList;
      }
    },
    //获取列表
    async getRoleList() {
      let res = await getRoleListApi(this.parms);
      if (res && res.code == 200) {
        this.roleList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
    //搜索按钮
    searchBtn() {
      this.getRoleList();
    },
    //删除按钮
    async deleteBtn(row) {
      let confirm = await this.$myconfirm("确定删除该数据吗?");
      if (confirm) {
        let res = await deleteRoleApi({ roleId: row.roleId });
        if (res && res.code == 200) {
          this.$message.success(res.msg);
          this.getRoleList();
        }
      }
    },
    //编辑按钮
    editBtn(row) {
      this.$resetForm("addForm", this.addModel);
      this.addModel.type = "1";
      this.dialog.title = "编辑角色";
      this.$objCoppy(row, this.addModel);
      // 动态高度
      this.dialog.height = this.isPlatformAdmin ? 200 : 150;
      this.dialog.visible = true;
    },
    //确认事件
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModel.type == "0") {
            //新增
            res = await addRoleApi(this.addModel);
          } else {
            res = await editRoleApi(this.addModel);
          }
          //返回成功
          if (res && res.code == 200) {
            //刷新表格
            this.getRoleList();
            this.$message.success(res.msg);
            this.dialog.visible = false;
          }
        }
      });
    },
    //弹框关闭事件
    onClose() {
      this.dialog.visible = false;
    },
    //页数改变时触发
    currentChange(val) {
      this.parms.currentPage = val;
      this.getRoleList();
    },
    //页容量改变时触发
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getRoleList();
    },
    //新增按钮
    addBtn() {
      //清空表单数据
      this.$resetForm("addForm", this.addModel);
      // 如果不是平台管理员，重置公司ID
      if (!this.isPlatformAdmin) {
        this.addModel.companyId = "";
      }
      this.addModel.type = "0";
      this.dialog.title = "新增角色";
      // 动态高度
      this.dialog.height = this.isPlatformAdmin ? 200 : 150;
      this.dialog.visible = true;
    },
    //重置按钮
    resetBtn() {
      this.parms.roleName = "";
      this.getRoleList();
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-tree {
  .el-tree-node {
    position: relative;
    padding-left: 10px;
  }
  .el-tree-node__children {
    padding-left: 20px;
  }
  .el-tree-node :last-child:before {
    height: 40px;
  }
  .el-tree > .el-tree-node:before {
    border-left: none;
  }
  .el-tree > .el-tree-node:after {
    border-top: none;
  }
  .el-tree-node:before,
  .el-tree-node:after {
    content: "";
    left: -4px;
    position: absolute;
    right: auto;
    border-width: 1px;
  }
  .tree :first-child .el-tree-node:before {
    border-left: none;
  }
  .el-tree-node:before {
    border-left: 1px dashed #d9d9d9;
    bottom: 0px;
    height: 100%;
    top: -25px;
    width: 1px;
  }
  .el-tree-node:after {
    border-top: 1px dashed #d9d9d9;
    height: 20px;
    top: 14px;
    width: 24px;
  }
}
</style>
