import http from '@/utils/http'

// 获取工单列表
export async function getWorkOrderListApi(parm) {
  return await http.get("/api/workOrder/list", parm)
}

// 新增工单
export async function addWorkOrderApi(parm) {
  return await http.post("/api/workOrder", parm)
}

// 编辑工单
export async function editWorkOrderApi(parm) {
  return await http.put("/api/workOrder", parm)
}

// 处理工单
export async function handleWorkOrderApi(parm) {
  return await http.post("/api/workOrder/handle", parm)
}

// 复核工单
export async function reviewWorkOrderApi(parm) {
  return await http.post("/api/workOrder/review", parm)
}

// 用户评价
export async function evaluateWorkOrderApi(parm) {
  return await http.post("/api/workOrder/evaluate", parm)
}

// 获取统计数据
export async function getWorkOrderStatsApi() {
  return await http.get("/api/workOrder/getStats")
}

// 删除工单
export async function deleteWorkOrderApi(parm) {
  return await http.delete("/api/workOrder/" + parm)
}
