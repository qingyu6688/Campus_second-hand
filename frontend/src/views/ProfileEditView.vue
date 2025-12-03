<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showSuccessToast } from 'vant'
import {
  UserIcon,
  IdentificationIcon,
  PhoneIcon,
  AcademicCapIcon,
  BuildingOfficeIcon,
  CameraIcon,
  ChevronLeftIcon
} from '@heroicons/vue/24/outline'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  nickname: '',
  name: '',
  schoolName: '',
  dormitory: '',
  address: '',
  mobile: '',
  avatar: ''
})

const fileList = ref([])

onMounted(() => {
  // Initialize form with user data
  const user = userStore.userInfo
  form.value = {
    nickname: user.nickname || '',
    name: user.name || '',
    schoolName: user.schoolName || '',
    dormitory: user.dormitory || '',
    address: user.address || '',
    mobile: user.mobile || '',
    avatar: user.avatar || ''
  }

  if (user.avatar) {
    fileList.value = [{ url: user.avatar, isImage: true }]
  }
})

const afterRead = async (file) => {
  file.status = 'uploading'
  file.message = '上传中...'

  try {
    const formData = new FormData()
    formData.append('file', file.file)

    const res = await request.post('/common/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    form.value.avatar = res
    file.url = res
    file.status = 'done'
    file.message = '上传成功'
  } catch (error) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
  }
}

const handleSave = async () => {
  try {
    const updatedUser = {
      ...userStore.userInfo,
      ...form.value
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
  <div class="min-h-screen bg-gray-50 pb-24">
    <!-- Header Wrapper -->
    <div class="relative pt-12 pb-32 px-4">
      <!-- Background with Overflow Hidden -->
      <div
        class="absolute inset-0 bg-gradient-to-br from-teal-500 to-emerald-600 rounded-b-[3rem] shadow-xl overflow-hidden z-0">
        <!-- Decorative circles -->
        <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>
        <div class="absolute bottom-0 left-0 w-48 h-48 bg-black/5 rounded-full -ml-10 -mb-10 blur-2xl"></div>
        <div class="absolute top-20 left-10 w-20 h-20 bg-teal-400/20 rounded-full blur-xl"></div>
      </div>

      <!-- Header Content -->
      <div class="relative z-10 flex items-center justify-between text-white mb-6">
        <div
          class="w-10 h-10 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md cursor-pointer active:scale-95 transition-transform hover:bg-white/30"
          @click="router.back()">
          <ChevronLeftIcon class="w-6 h-6" />
        </div>
        <h1 class="text-xl font-bold tracking-wide">编辑资料</h1>
        <div class="w-10"></div> <!-- Spacer -->
      </div>

      <!-- Avatar Upload (Floating) -->
      <div class="absolute -bottom-16 left-1/2 transform -translate-x-1/2 z-20">
        <van-uploader v-model="fileList" :after-read="afterRead" :max-count="1" reupload :deletable="false">
          <div class="relative group cursor-pointer">
            <div class="w-32 h-32 rounded-full bg-white p-1.5 shadow-2xl ring-4 ring-white/50">
              <div class="w-full h-full rounded-full bg-gray-100 overflow-hidden relative">
                <img v-if="form.avatar" :src="form.avatar" class="w-full h-full object-cover" />
                <div v-else
                  class="w-full h-full flex items-center justify-center bg-gray-50 text-gray-300 text-4xl font-bold">
                  {{ form.nickname?.charAt(0).toUpperCase() || 'U' }}
                </div>
                <!-- Overlay on hover -->
                <div
                  class="absolute inset-0 bg-black/30 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity backdrop-blur-[2px]">
                  <CameraIcon class="w-8 h-8 text-white" />
                </div>
              </div>
            </div>
            <div
              class="absolute bottom-1 right-1 bg-teal-500 text-white p-2.5 rounded-full shadow-lg border-4 border-white transform group-hover:scale-110 transition-transform">
              <CameraIcon class="w-4 h-4" />
            </div>
          </div>
        </van-uploader>
      </div>
    </div>

    <!-- Form -->
    <div class="mt-24 px-4 space-y-6">
      <div class="bg-white rounded-3xl shadow-sm overflow-hidden border border-gray-100">
        <div class="p-5 border-b border-gray-50 flex items-center hover:bg-gray-50/50 transition-colors">
          <div class="w-10 h-10 rounded-full bg-teal-50 flex items-center justify-center mr-4">
            <UserIcon class="w-5 h-5 text-teal-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">昵称</div>
            <input v-model="form.nickname" type="text" placeholder="请输入昵称"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 border-b border-gray-50 flex items-center hover:bg-gray-50/50 transition-colors">
          <div class="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center mr-4">
            <IdentificationIcon class="w-5 h-5 text-blue-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">真实姓名</div>
            <input v-model="form.name" type="text" placeholder="请输入真实姓名"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 border-b border-gray-50 flex items-center hover:bg-gray-50/50 transition-colors">
          <div class="w-10 h-10 rounded-full bg-green-50 flex items-center justify-center mr-4">
            <PhoneIcon class="w-5 h-5 text-green-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">手机号</div>
            <input v-model="form.mobile" type="tel" placeholder="请输入手机号"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 border-b border-gray-50 flex items-center hover:bg-gray-50/50 transition-colors">
          <div class="w-10 h-10 rounded-full bg-purple-50 flex items-center justify-center mr-4">
            <AcademicCapIcon class="w-5 h-5 text-purple-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">学校</div>
            <input v-model="form.schoolName" type="text" placeholder="请输入学校名称"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 flex items-center hover:bg-gray-50/50 transition-colors">
          <div class="w-10 h-10 rounded-full bg-orange-50 flex items-center justify-center mr-4">
            <BuildingOfficeIcon class="w-5 h-5 text-orange-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">宿舍楼</div>
            <input v-model="form.dormitory" type="text" placeholder="例如：西区5号楼"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>
      </div>

      <!-- Save Button -->
      <div class="fixed bottom-8 left-4 right-4 z-50">
        <button @click="handleSave"
          class="w-full bg-gradient-to-r from-teal-500 to-emerald-600 text-white font-bold py-4 rounded-2xl shadow-lg shadow-teal-200/50 active:scale-[0.98] transition-all hover:shadow-xl flex items-center justify-center space-x-2">
          <span>保存修改</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom, 20px);
}
</style>
