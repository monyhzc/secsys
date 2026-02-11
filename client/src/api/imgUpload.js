import http from '@/utils/http'

// 上传图片
export async function uploadImageApi(parm) {
  return await http.upload("/api/upload", parm)
}
