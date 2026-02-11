<template>
  <el-main>
    <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="small">
      <el-form-item label="车位名称">
        <el-input v-model="parms.parkName"></el-input>
      </el-form-item>
      <el-form-item label="车位类型">
        <el-select v-model="parms.parkType">
          <el-option
            v-for="item in parkTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="车辆类别">
        <el-select v-model="parms.vehicleType">
          <el-option
            v-for="item in vehicleTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用状态">
        <el-select v-model="parms.parkStatus">
          <el-option
            v-for="item in parkStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" @click="resetBtn" icon="el-icon-delete">重置</el-button>
        <el-button v-if="hasPerm('sys:parkList:add')" icon="el-icon-plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <el-table :height="tableHeight" :data="tableList" border stripe size="small">
      <el-table-column label="车位名称" prop="parkName"></el-table-column>
      
      <el-table-column v-if="isPlatformAdmin" label="所属公司" prop="companyName" width="180" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.companyName" type="info">{{ scope.row.companyName }}</el-tag>
          <span v-else>平台公共</span>
        </template>
      </el-table-column>

      <el-table-column label="车位类型" prop="parkType">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parkType == '0'" type="danger" size="normal">地上</el-tag>
          <el-tag v-if="scope.row.parkType == '1'" type="success" size="normal">地下</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="车辆类别" prop="vehicleType">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.vehicleType == '0'" type="success" size="normal">小汽车</el-tag>
          <el-tag v-if="scope.row.vehicleType == '1'" type="warning" size="normal">货车</el-tag>
          <el-tag v-if="scope.row.vehicleType == '2'" type="danger" size="normal">巴士</el-tag>
          <el-tag v-if="scope.row.vehicleType == '3'" size="normal">摩托车</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="使用状态" prop="parkStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parkStatus == '0'" size="normal">未使用</el-tag>
          <el-tag v-if="scope.row.parkStatus == '1'" type="danger" size="normal">已使用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="序号" prop="parkNum"></el-table-column>
      <el-table-column align="center" width="200" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:parkList:edit')" icon="el-icon-edit" type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button v-if="hasPerm('sys:parkList:delete')" icon="el-icon-delete" type="danger" size="small" @click="deleteBtn(scope.row)">删除</el-button>
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
      :title="addDialog.title"
      :height="addDialog.height"
      :width="addDialog.width"
      :visible="addDialog.visible"
      @onClose="onClose"
      @onConfirm="onConfirm"
    >
      <template slot="content">
        <el-form :model="addModel" ref="addForm" :rules="rules" label-width="80px" :inline="true" size="small">
          <el-form-item prop="parkName" label="车位名称">
            <el-input v-model="addModel.parkName"></el-input>
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

          <el-form-item prop="parkType" label="车位类型">
            <el-select v-model="addModel.parkType">
              <el-option
                v-for="item in parkTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="vehicleType" label="车辆类别">
            <el-select v-model="addModel.vehicleType">
              <el-option
                v-for="item in vehicleTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="parkStatus" label="使用状态">
            <el-select v-model="addModel.parkStatus">
              <el-option
                v-for="item in parkStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="parkNum" label="车位序号">
            <el-input v-model="addModel.parkNum"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, editApi, deleteApi } from "@/api/parkList";
import SysDialog from "@/components/system/SysDialog";
// 【新增】引入公司API和Vuex
import { getCompanyListApi } from "@/api/company";
import { mapGetters } from "vuex";

export default {
  components: {
    SysDialog
  },
  computed: {
    // 【新增】复用权限路由判断逻辑
    ...mapGetters(["userInfo", "permission_routes"]),
    isPlatformAdmin() {
      // 只有平台管理员才有 sysCompanyList 菜单权限
      return JSON.stringify(this.permission_routes).indexOf('sysCompanyList') !== -1;
    }
  },
  data() {
    return {
      // 【新增】公司列表
      companyList: [],
      //车位类型
      parkTypeOptions: [
        {
          value: "0",
          label: "地上"
        },
        {
          value: "1",
          label: "地下"
        }
      ],
      //车辆类别
      vehicleTypeOptions: [
        {
          value: "0",
          label: "小汽车"
        },
        {
          value: "1",
          label: "货车"
        },
        {
          value: "2",
          label: "巴士"
        },
        {
          value: "3",
          label: "摩托车"
        }
      ],
      //使用状态
      parkStatusOptions: [
        {
          value: "0",
          label: "未使用"
        },
        {
          value: "1",
          label: "已使用"
        }
      ],
      //表单验证规则
      rules: {
        parkName: [{ required: true, message: "请输入车位名称", trigger: "blur" }],
        parkType: [{ required: true, message: "请选择车位类型", trigger: "change" }],
        vehicleType: [{ required: true, message: "请选择车辆类别", trigger: "change" }],
        parkStatus: [{ required: true, message: "请选择使用状态", trigger: "change" }],
        parkNum: [{ required: true, message: "请输入序号", trigger: "blur" }],
        // 【新增】平台管理员必填
        companyId: [{ required: false, message: "请选择所属公司", trigger: "change" }]
      },
      //新增或编辑表单数据
      addModel: {
        editType: "", //0：新增 1：编辑
        parkId: "",
        parkName: "",
        parkType: "",
        vehicleType: "",
        parkStatus: "",
        parkNum: "",
        companyId: "" // 【新增】
      },
      //弹框属性
      addDialog: {
        title: "",
        height: 180,
        width: 620,
        visible: false
      },
      //表格高度
      tableHeight: 0,
      //列表数据
      tableList: [],
      //搜索参数
      parms: {
        pageSize: 10,
        currentPage: 1,
        parkName: "",
        parkType: "",
        vehicleType: "",
        parkStatus: "",
        total: 0
      }
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
    this.getList();
    
    // 【新增】如果是平台管理员，加载公司列表并开启校验
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
    //弹框确认
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
            this.$message.success(res.msg);
            this.addDialog.visible = false;
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
      let res = await getListApi(this.parms);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
    //页数改变
    currentChange(val) {
      this.parms.currentPage = val;
      this.getList();
    },
    //页容量改变
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    async deleteBtn(row) {
      //信息提示
      const confirm = await this.$myconfirm("确定删除该数据吗?");
      if (confirm) {
        let res = await deleteApi({ parkId: row.parkId });
        if (res && res.code == 200) {
          //刷新表格
          this.$message.success(res.msg);
          this.getList();
        }
      }
    },
    //编辑按钮
    editBtn(row) {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置编辑属性
      this.addModel.editType = "1";
      //设置弹框属性
      this.addDialog.title = "编辑车位";
      // 动态高度
      this.addDialog.height = this.isPlatformAdmin ? 230 : 180;
      this.addDialog.visible = true;
      //赋值回显
      this.$objCoppy(row, this.addModel);
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置设置编辑标识
      this.addModel.editType = "0";
      // 重置公司
      if (!this.isPlatformAdmin) {
        this.addModel.companyId = "";
      }
      //设置弹框属性
      this.addDialog.title = "新增车位";
      // 动态高度
      this.addDialog.height = this.isPlatformAdmin ? 230 : 180;
      this.addDialog.visible = true;
    },
    //重置按钮
    resetBtn() {
      this.parms.parkName = '';
      this.parms.parkStatus = '';
      this.parms.parkType = '';
      this.parms.vehicleType = '';
      this.getList();
    },
    //搜索按钮
    searchBtn() {
      this.getList();
    }
  }
};
</script>

<style scoped></style>