<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="栋数名称">
        <el-input class="input-form" placeholder="请输入栋数名称" v-model="parms.buildNme"></el-input>
      </el-form-item>
      <el-form-item label="单元名称">
        <el-input class="input-form" placeholder="请输入单元名称" v-model="parms.unitName"></el-input>
      </el-form-item>
      <el-form-item label="房屋编号">
        <el-input class="input-form" placeholder="请输入房屋编号" v-model="parms.houseNum"></el-input>
      </el-form-item>
      <el-form-item label="使用状态">
        <el-select class="input-form" v-model="parms.status">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">搜索</el-button>
        <el-button style="color: #ff7670" @click="resetBtn" icon="el-icon-close">重置</el-button>
        <el-button v-if="hasPerm('sys:house:add')" icon="el-icon-plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableList" :height="tableHeight" size="small" border stripe>
      <el-table-column label="房屋编号" prop="houseNum"></el-table-column>
      
      <el-table-column v-if="isPlatformAdmin" label="所属公司" prop="companyName" width="180" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.companyName" type="info">{{ scope.row.companyName }}</el-tag>
          <span v-else>平台公共</span>
        </template>
      </el-table-column>

      <el-table-column label="栋数名称" prop="name"></el-table-column>
      <el-table-column label="单元名称" prop="unitName"></el-table-column>
      <el-table-column label="房屋面积" prop="houseArea"></el-table-column>
      <el-table-column align="center" label="使用状态" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '0'" type="danger" size="normal">未使用</el-tag>
          <el-tag v-if="scope.row.status == '1'" type="success" size="normal">已使用</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="200" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:house:edit')" icon="el-icon-edit" type="primary" size="small"
            @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:house:delete')" icon="el-icon-delete" type="danger" size="small"
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
            <el-select v-model="addModel.buildId" @change="selectBuild">
              <el-option v-for="item in buildList" :key="item.buildId" :label="item.name" :value="item.buildId">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item prop="unitId" label="所属单元">
            <el-select v-model="addModel.unitId">
              <el-option v-for="item in unitList" :key="item.unitId" :label="item.unitName" :value="item.unitId">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="houseNum" label="房屋编号">
            <el-input v-model="addModel.houseNum"></el-input>
          </el-form-item>
          <el-form-item prop="houseArea" label="使用面积">
            <el-input v-model="addModel.houseArea"></el-input>
          </el-form-item>
          <el-form-item prop="status" label="使用状态">
            <el-radio-group v-model="addModel.status">
              <el-radio :label="'0'">未使用</el-radio>
              <el-radio :label="'1'">已使用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, getBuildApi, getUnitListByIdApi, editApi, deleteApi } from "@/api/houseList";
import SysDialog from "@/components/system/SysDialog";
// 引入依赖
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog
  },
  computed: {
    ...mapGetters(["userInfo", "permission_routes"]),
    isPlatformAdmin() {
      return JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      // 下拉数据
      companyList: [],
      buildList: [],
      unitList: [],
      
      options: [
        { value: "0", label: "未使用" },
        { value: "1", label: "已使用" }
      ],
      rules: {
        buildId: [{ required: true, message: "请选择栋数", trigger: "change" }],
        unitId: [{ required: true, message: "请选择单元", trigger: "change" }],
        houseNum: [{ required: true, message: "请填写房屋编号", trigger: "blur" }],
        houseArea: [{ required: true, message: "请填写房屋面积", trigger: "blur" }],
        status: [{ required: true, message: "请选择使用状态", trigger: "change" }],
        companyId: [{ required: false, message: "请选择所属公司", trigger: "change" }]
      },
      addModel: {
        editType: "", // 0:新增 1：编辑
        houseId: "",
        unitId: "",
        buildId: "",
        houseNum: "",
        houseArea: "",
        status: "",
        companyId: ""
      },
      addDialog: {
        title: "",
        height: 200,
        width: 610,
        visible: false
      },
      tableHeight: 0,
      tableList: [],
      parms: {
        buildNme: "",
        unitName: "",
        houseNum: "",
        status: "",
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
    
    // 初始化
    if (this.isPlatformAdmin) {
      this.getCompanyList();
      this.rules.companyId[0].required = true;
    } else {
      this.getBuildList(); // 普通管理员加载自己的栋数
    }
  },
  methods: {
    // 获取公司
    async getCompanyList() {
      let res = await getCompanyListApi({ currentPage: 1, pageSize: 999 });
      if (res && res.code == 200) {
        this.companyList = res.data.records;
      }
    },
    
    // 获取栋数 (支持传 companyId)
    async getBuildList(companyId) {
      // 这里的 API 实际上调用的是 /api/HouseBuilding/unitList
      // 我们已经修改它支持接收参数
      let parm = {};
      if (companyId) {
        parm.companyId = companyId;
      }
      let res = await getBuildApi(parm);
      if (res && res.code == 200) {
        this.buildList = res.data;
      }
    },
    
    // 获取单元
    async getUnitListById(buildId) {
      let res = await getUnitListByIdApi({ buildId: buildId });
      if (res && res.code == 200) {
        this.unitList = res.data;
      }
    },

    // 公司改变 -> 刷新栋数
    companyChange(val) {
      this.addModel.buildId = "";
      this.addModel.unitId = "";
      this.buildList = [];
      this.unitList = [];
      if (val) {
        this.getBuildList(val);
      }
    },

    // 栋数改变 -> 刷新单元
    selectBuild(val) {
      this.addModel.unitId = "";
      this.unitList = [];
      if (val) {
        this.getUnitListById(val);
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
    async deleteBtn(row) {
      const confirm = await this.$myconfirm("确定删除该数据吗?");
      if (confirm) {
        let res = await deleteApi({ houseId: row.houseId });
        if (res && res.code == 200) {
          this.$message.success(res.msg);
          this.getList();
        }
      }
    },
    // 编辑按钮 (级联回显)
    async editBtn(row) {
      this.$resetForm("addForm", this.addModel);
      this.addModel.editType = "1";
      this.addDialog.title = "编辑房屋";
      this.addDialog.height = this.isPlatformAdmin ? 250 : 200;
      
      // 基础回显
      this.$objCoppy(row, this.addModel);
      
      // 级联回显
      if (this.isPlatformAdmin) {
        if (row.companyId) {
           await this.getBuildList(row.companyId);
        }
      } else {
        if (this.buildList.length === 0) {
           await this.getBuildList();
        }
      }
      
      if (row.buildId) {
        await this.getUnitListById(row.buildId);
      }
      
      this.addDialog.visible = true;
    },
    // 新增按钮
    addBtn() {
      this.$resetForm("addForm", this.addModel);
      this.addModel.editType = "0";
      
      if (!this.isPlatformAdmin) {
        this.addModel.companyId = "";
        if (this.buildList.length === 0) {
           this.getBuildList();
        }
      } else {
        this.buildList = [];
        this.unitList = [];
      }
      
      this.addDialog.title = "新增房屋";
      this.addDialog.height = this.isPlatformAdmin ? 250 : 200;
      this.addDialog.visible = true;
    },
    resetBtn() {
      this.parms.buildNme = "";
      this.parms.unitName = "";
      this.parms.status = "";
      this.parms.houseNum = "";
      this.parms.currentPage = 1;
      this.getList();
    },
    searchBtn() {
      this.getList();
    },
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    currentChange(val) {
      this.parms.currentPage = val;
      this.getList();
    }
  }
};
</script>

<style scoped></style>