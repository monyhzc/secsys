import http from '@/utils/http'

// 获取列表
export async function getListApi(parm){
    return await http.get("/api/houseList/list",parm)
}

// 新增
export async function addApi(parm){
    return await http.post("/api/houseList",parm)
}

// 根据栋数id查询单元列表
export async function getUnitListByIdApi(parm){
    return await http.get("/api/houseUnit/getUnitListByBuildId",parm)
}

// 查询栋数列表 (供下拉框用，支持传 companyId)
export async function getBuildApi(parm){
    // 注意：这里调用的是楼栋管理的接口，之前已经改造过支持传参
    return await http.get("/api/HouseBuilding/unitList",parm)
}

// 编辑
export async function editApi(parm){
    return await http.put("/api/houseList",parm)
}

// 删除
export async function deleteApi(parm){
    return await http.delete("/api/houseList",parm)
}