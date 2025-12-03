<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showSuccessToast } from 'vant'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

const address = ref('')

onMounted(() => {
  address.value = userStore.userInfo.address || ''
})

const handleSave = async () => {
  try {
    const updatedUser = {
      ...userStore.userInfo,
      address: address.value
    }
    
    // Call API to update user
    await request.post('/user/save', updatedUser)
    
    // Update store
    userStore.setLoginState(userStore.token, updatedUser)
    
    showSuccessToast('保存成功')
    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (error) {
    showToast('保存失败: ' + error.message)
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <van-nav-bar
      title="我的地址"
      left-text="返回"
      left-arrow
      @click-left="router.back()"
      class="sticky top-0 z-50"
    />

    <div class="p-4">
      <div class="bg-white rounded-xl overflow-hidden shadow-sm mb-6">
        <van-field
          v-model="address"
          rows="3"
          autosize
          label="详细地址"
          type="textarea"
          placeholder="请输入详细地址，如：xx校区xx号楼xx室"
          show-word-limit
          maxlength="100"
        />
      </div>

      <van-button round block type="primary" color="#14b8a6" @click="handleSave">
        保存地址
      </van-button>
    </div>
  </div>
</template>

<style scoped>
:deep(.van-nav-bar__text) {
  color: #14b8a6;
}
:deep(.van-nav-bar .van-icon) {
  color: #14b8a6;
}
</style>
