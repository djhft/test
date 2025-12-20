// api/upload.js
import request from './request';

export const uploadImage = (formData) => {
    return request.post('api/files/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};
export function deleteImage(filename) {
    return request({
        url: `/api/files/image/${filename}`,
        method: 'delete'
    }).catch(error => {
        // 统一处理错误
        if (error.response) {
            // 服务器返回的错误
            const { status, data } = error.response;

            switch (status) {
                case 404:
                    throw new Error('文件不存在');
                case 403:
                    throw new Error('没有删除权限');
                case 500:
                    throw new Error(data.message || '服务器错误');
                default:
                    throw new Error(data.message || '删除失败');
            }
        } else if (error.request) {
            // 请求已发出但没有收到响应
            throw new Error('服务器无响应');
        } else {
            // 请求配置出错
            throw new Error('请求配置错误');
        }
    });
}