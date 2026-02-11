import http from '@/utils/http'

// 获取物业公司列表
export async function getCompanyListApi(parm){
  return await http.get("/api/company/list", parm)
}

// 新增物业公司
export async function addCompanyApi(parm){
  return await http.post("/api/company", parm)
}

// 编辑物业公司
export async function editCompanyApi(parm){
  return await http.put("/api/company", parm)
}

// 删除物业公司
export async function deleteCompanyApi(parm){
  return await http.delete("/api/company/" + parm.companyId)
}

// 【新增】物业公司续期
export async function updateTermApi(parm){
  return await http.put("/api/company/term", parm)
}