import http from '@/utils/http'

// 获取系统状态信息
export async function getSysStatusApi() {
  return await http.get("/api/sysStatus/info")
}
