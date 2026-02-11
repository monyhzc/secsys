<template>
  <el-main>
    <el-form :model="parm" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="楼栋名称">
        <el-input v-model="parm.name"></el-input>
      </el-form-item>
      <el-form-item label="楼栋类型">
        <el-select v-model="parm.type" placeholder="" clearable filterable>
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button @click="cancelBtn" style="color: #ff7670" icon="el-icon-close">重置</el-button>
        <el-button v-if="hasPerm('sys:houseBuilding:add')" icon="el-icon-plus" type="primary"
          @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :height="tableHeight" :data="tableList" border stripe size="small">
      <el-table-column label="楼栋名称" prop="name"></el-table-column>
      
      <el-table-column v-if="isPlatformAdmin" label="所属公司" prop="companyName" width="180" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.companyName" type="info">{{ scope.row.companyName }}</el-tag>
          <span v-else>平台公共</span>
        </template>
      </el-table-column>

      <el-table-column label="楼栋类型" prop="type">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type == '0'" size="normal">普通房</el-tag>
          <el-tag type="success" v-if="scope.row.type == '1'" size="normal">电梯房</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="楼栋序号" prop="orderNum"></el-table-column>
      <el-table-column align="center" width="200" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:houseBuilding:edit')" icon="el-icon-edit" type="primary" size="small"
            @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:houseBuilding:delete')" icon="el-icon-delete" type="danger" size="small"
            @click="deleteBtn(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination @size-change="sizeChange" @current-change="currentChange" :current-page.sync="parm.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]" :page-size="parm.pageSize" layout="total, sizes, prev, pager, next, jumper"
      :total="parm.total" background>
    </el-pagination>
    
    <sys-dialog :title="addDialog.title" :height="addDialog.height" :width="addDialog.width"
      :visible="addDialog.visible" @onClose="onClose" @onConfirm="onConfirm">
      <template slot="content">
        <el-form :model="addModle" ref="addRef" :rules="rules" label-width="80px" :inline="true" size="small">
          <el-form-item prop="name" label="楼栋名称">
            <el-input v-model="addModle.name"></el-input>
          </el-form-item>
          
          <el-form-item v-if="isPlatformAdmin" prop="companyId" label="所属公司">
             <el-select v-model="addModle.companyId" placeholder="请选择" filterable clearable>
                <el-option
                  v-for="item in companyList"
                  :key="item.companyId"
                  :label="item.companyName"
                  :value="item.companyId">
                </el-option>
             </el-select>
          </el-form-item>

          <el-form-item prop="type" label="楼栋类型">
            <el-select v-model="addModle.type" placeholder="" clearable filterable>
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="orderNum" label="楼栋序号">
            <el-input type="number" v-model="addModle.orderNum"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, editApi, deleteApi } from "@/api/building";
import SysDialog from "@/components/system/SysDialog";
// 【新增】
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog,
  },
  computed: {
    // 【新增】
    ...mapGetters(["userInfo", "permission_routes"]),
    isPlatformAdmin() {
      // 通过判断是否有“公司管理”菜单权限来确定是否为平台管理员
      return JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      // 【新增】
      companyList: [],
      options: [
        {
          value: "0",
          label: "普通房",
        },
        {
          value: "1",
          label: "电梯房",
        },
      ],
      rules: {
        name: [
          {
            required: true,
            trigger: "change",
            message: "请填写楼栋名称",
          },
        ],
        type: [
          {
            required: true,
            trigger: "change",
            message: "请选择楼栋类型",
          },
        ],
        orderNum: [
          {
            required: true,
            trigger: "change",
            message: "请填写楼栋序号",
          },
        ],
        // 【新增】
        companyId: [{ required: false, message: "请选择所属公司", trigger: "change" }]
      },
      addModle: {
        editType: "", //0 :新增 1：编辑
        buildId: "",
        type: "",
        name: "",
        orderNum: "",
        companyId: "" // 【新增】
      },
      addDialog: {
        title: "",
        height: 150,
        width: 650,
        visible: false,
      },
      //表格高度
      tableHeight: 0,
      //列表数据
      tableList: [],
      parm: {
        name: "",
        type: "",
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
    this.getList();
    
    // 【新增】
    if (this.isPlatformAdmin) {
      this.getCompanyList();
      this.rules.companyId[0].required = true;
    }
  },
  methods: {
    // 【新增】
    async getCompanyList() {
      let res = await getCompanyListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) {
        this.companyList = res.data.records;
      }
    },
    //弹框确认
    onConfirm() {
      this.$refs.addRef.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModle.editType == "0") {
            res = await addApi(this.addModle);
          } else {
            res = await editApi(this.addModle);
          }
          if (res && res.code == 200) {
            this.getList();
            this.addDialog.visible = false;
            this.$message.success(res.msg);
          }
        }
      });
    },
    //弹框关闭
    onClose() {
      this.addDialog.visible = false;
    },
    //获取列表
    async getList() {
      let res = await getListApi(this.parm);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parm.total = res.data.total;
      }
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addRef", this.addModle);
      //设置弹框属性
      this.addModle.editType = "0"; //标识新增
      // 重置公司
      if (!this.isPlatformAdmin) {
        this.addModle.companyId = "";
      }
      this.addDialog.title = "新增楼栋";
      // 动态高度
      this.addDialog.height = this.isPlatformAdmin ? 200 : 150;
      this.addDialog.visible = true;
    },
    //重置按钮
    cancelBtn() {
      this.parm.name = "";
      this.parm.type = "";
      this.getList();
    },
    //页容量改变时触发
    sizeChange(val) {
      this.parm.pageSize = val;
      this.getList();
    },
    //页数改变时触发
    currentChange(val) {
      this.parm.currentPage = val;
      this.getList();
    },
    //删除按钮
    async deleteBtn(row) {
      //先提示，不能直接删除
      const confirm = await this.$myconfirm("确定删除该数据吗？");
      if (confirm) {
        let res = await deleteApi({ buildId: row.buildId });
        if (res && res.code == 200) {
          //提示信息
          this.$message.success(res.msg);
          //刷新表格
          this.getList();
        }
      }
    },
    //编辑按钮
    editBtn(row) {
      //清空表单
      this.$resetForm("addRef", this.addModle);
      //设置编辑状态
      this.addModle.editType = "1";
      //设置弹框属性
      this.addDialog.title = "编辑楼栋";
      // 动态高度
      this.addDialog.height = this.isPlatformAdmin ? 200 : 150;
      this.addDialog.visible = true;
      //数据回显
      this.$objCoppy(row, this.addModle);
    },
  },
};
</script>

<style scoped></style>