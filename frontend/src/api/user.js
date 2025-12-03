import request from './request'

export function login(data) {
    return request({
        url: '/user/login', // Need to implement this in backend
        method: 'post',
        data
    })
}

export function register(data) {
    return request({
        url: '/user/save',
        method: 'post',
        data
    })
}

export function getUserInfo(id) {
    return request({
        url: `/user/${id}`,
        method: 'get'
    })
}
