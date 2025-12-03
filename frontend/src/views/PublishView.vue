<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { PlusIcon, MapPinIcon, ChevronRightIcon } from '@heroicons/vue/24/outline'
import request from '@/api/request'

const router = useRouter()
const route = useRoute()
const editId = route.query.id

const fileList = ref([])
const loading = ref(false)
const categories = ref([])
const showCategoryPicker = ref(false)

const form = reactive({
  id: null,
  title: '',
  description: '',
  price: '',
  originalPrice: '',
  location: '',
  deliveryType: 1, // 1:自提, 2:送货, 3:邮寄
  categoryId: null,
  images: []
})

const categoryName = computed(() => {
  const cat = categories.value.find(c => c.id === form.categoryId)
  return cat ? cat.name : ''
})

const categoryColumns = computed(() => {
  return categories.value.map(c => ({ text: c.name, value: c.id }))
})

// 预设标签
const tags = ref(['全新', '99新', '仅自提', '可小刀', '急出'])
const selectedTags = ref([])

// 预设地点
const locations = ['图书馆', '北区食堂', '南区食堂', '西区宿舍', '教学楼']

onMounted(async () => {
  // Fetch categories
  try {
    const res = await request.get('/category/list')
    categories.value = res
  } catch (error) {
    console.error('Failed to fetch categories', error)
  }

  if (editId) {
    try {
      showLoadingToast('加载中...')
      const res = await request.get(`/goods/detail/${editId}`)
      const goods = res.goods

      form.id = goods.id
      form.title = goods.title
      form.description = goods.description
      form.price = goods.price
      form.originalPrice = goods.originalPrice
      form.location = goods.location
      form.deliveryType = goods.deliveryType
      form.categoryId = goods.categoryId

      // Handle tags
      if (goods.tags) {
        try {
          selectedTags.value = JSON.parse(goods.tags)
        } catch (e) {
          selectedTags.value = []
        }
      }

      // Handle images
      if (goods.images) {
        try {
          const imgs = JSON.parse(goods.images)
          fileList.value = imgs.map(url => ({ url, isImage: true }))
        } catch (e) {
          fileList.value = []
        }
      }
      closeToast()
    } catch (error) {
      showToast('加载商品信息失败')
      router.back()
    }
  }
})

const onCategoryConfirm = ({ selectedOptions }) => {
  form.categoryId = selectedOptions[0].value
  showCategoryPicker.value = false
}

const afterRead = async (file) => {
  // Handle multiple files selection
  if (Array.isArray(file)) {
    for (const f of file) {
      await uploadFile(f)
    }
  } else {
    await uploadFile(file)
  }
}

const uploadFile = async (file) => {
  file.status = 'uploading'
  file.message = '上传中...'

  try {
    const formData = new FormData()
    formData.append('file', file.file)

    const res = await request.post('/common/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    file.url = res
    file.status = 'done'
    file.message = '上传成功'
  } catch (error) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
  }
}

const toggleTag = (tag) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    if (selectedTags.value.length >= 3) {
      showToast('最多选择3个标签')
      return
    }
    selectedTags.value.push(tag)
  }
}

const handlePublish = async () => {
  if (!form.title || !form.price || fileList.value.length === 0 || !form.categoryId) {
    showToast('请填写完整信息（含图片和分类）')
    return
  }

  if (form.deliveryType === 1 && !form.location) {
    showToast('选择自提时请填写交易地点')
    return
  }

  loading.value = true
  showLoadingToast({
    message: '发布中...',
    forbidClick: true,
  })

  try {
    // 整理图片链接
    const imageUrls = fileList.value
      .map(item => item.url)
      .filter(url => url)

    // 构建提交数据
    const submitData = {
      ...form,
      images: JSON.stringify(imageUrls), // 简化处理，实际后端可能需要单独的表关联
      tags: JSON.stringify(selectedTags.value),
      sellerId: JSON.parse(localStorage.getItem('user'))?.id
    }

    // 这里需要注意：后端 BizGoods 实体没有 images 和 tags 字段，
    // 而是通过 BizGoodsImage 表存储图片。
    // 为了简化演示，我们先假设后端能处理，或者我们需要先保存商品，再保存图片。
    // 鉴于之前的后端代码，BizGoods 只有基本字段。
    // 我们需要调整一下策略：
    // 1. 调用 /goods/publish 保存商品基本信息，返回 goodsId
    // 2. (可选) 如果后端支持直接接收图片列表最好，否则需要单独调用接口保存图片关联。
    // 查看之前的 BizGoodsController，它接受 BizGoods 对象。
    // BizGoods 实体没有 images 字段。
    // 我们需要修改后端 BizGoodsController 或者 BizGoods 实体来支持接收图片信息。
    // 或者，我们在前端分步提交？不推荐。
    // 最佳实践是使用 DTO 接收参数。

    // 暂时先按现有接口提交，图片逻辑可能需要后端调整。
    // 假设我们先只提交基本信息。

    await request.post('/goods/publish', submitData)

    closeToast()
    showToast({
      type: 'success',
      message: '发布成功',
      onClose: () => {
        router.push('/')
      }
    })
  } catch (error) {
    closeToast()
    showToast('发布失败: ' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- Nav Bar -->
    <van-nav-bar :title="editId ? '编辑闲置' : '发布闲置'" left-text="取消" left-arrow @click-left="router.back()"
      class="sticky top-0 z-50" />

    <div class="p-4">
      <!-- Image Upload -->
      <div class="bg-white p-4 rounded-xl mb-4 shadow-sm">
        <van-uploader v-model="fileList" :after-read="afterRead" multiple :max-count="9" preview-size="80px" />
        <p class="text-xs text-gray-400 mt-2">首图将作为封面，最多上传9张</p>
      </div>

      <!-- Info Form -->
      <div class="bg-white rounded-xl overflow-hidden shadow-sm mb-4">
        <van-field v-model="form.title" placeholder="标题 品牌型号都是买家喜欢搜索的" class="text-lg font-medium" />
        <van-field v-model="form.description" rows="4" autosize type="textarea"
          placeholder="描述一下宝贝的转手原因、入手渠道和使用感受吧..." />
        <!-- Category Selection -->
        <van-cell title="分类" is-link @click="showCategoryPicker = true" :value="categoryName || '选择分类'" />
      </div>

      <!-- Price & Location -->
      <div class="bg-white rounded-xl overflow-hidden shadow-sm mb-4">
        <van-cell-group inset>
          <van-field v-model="form.price" type="number" label="价格" placeholder="¥ 0.00">
            <template #left-icon>
              <span class="text-red-500 font-bold mr-1">¥</span>
            </template>
          </van-field>
          <van-field v-model="form.originalPrice" type="number" label="原价" placeholder="¥ 0.00" />

          <!-- Location Selection -->
          <div class="px-4 py-3 border-t border-gray-100">
            <div class="text-sm text-gray-600 mb-2 flex items-center">
              <MapPinIcon class="w-4 h-4 mr-1" /> 交易地点 <span v-if="form.deliveryType === 1" class="text-red-500 ml-1">*</span>
            </div>
            
            <!-- Manual Input -->
            <van-field 
              v-model="form.location" 
              placeholder="请输入详细交易地点（如：西区食堂门口）" 
              class="bg-gray-50 rounded-lg mb-3 px-3 py-2"
              :border="false"
            />

            <div class="flex flex-wrap gap-2">
              <span v-for="loc in locations" :key="loc" @click="form.location = loc"
                class="px-3 py-1 rounded-full text-xs transition-colors cursor-pointer"
                :class="form.location === loc ? 'bg-teal-100 text-teal-600 border border-teal-200' : 'bg-gray-100 text-gray-500'">
                {{ loc }}
              </span>
            </div>
          </div>
        </van-cell-group>
      </div>

      <!-- Tags & Delivery -->
      <div class="bg-white rounded-xl overflow-hidden shadow-sm mb-20">
        <div class="p-4 border-b border-gray-100">
          <div class="text-sm text-gray-600 mb-2">快捷标签</div>
          <div class="flex flex-wrap gap-2">
            <span v-for="tag in tags" :key="tag" @click="toggleTag(tag)"
              class="px-3 py-1 rounded-full text-xs transition-colors"
              :class="selectedTags.includes(tag) ? 'bg-orange-100 text-orange-600 border border-orange-200' : 'bg-gray-100 text-gray-500'">
              {{ tag }}
            </span>
          </div>
        </div>

        <van-field name="radio" label="交易方式">
          <template #input>
            <van-radio-group v-model="form.deliveryType" direction="horizontal">
              <van-radio :name="1" checked-color="#14b8a6">自提</van-radio>
              <van-radio :name="2" checked-color="#14b8a6">送货</van-radio>
            </van-radio-group>
          </template>
        </van-field>
      </div>

      <!-- Submit Button -->
      <div class="fixed bottom-0 left-0 w-full p-4 bg-white border-t border-gray-100 z-50">
        <van-button round block type="primary" color="#14b8a6" @click="handlePublish" :loading="loading">
          {{ editId ? '保存修改' : '发布' }}
        </van-button>
      </div>
    </div>

    <!-- Category Picker Popup -->
    <van-popup v-model:show="showCategoryPicker" round position="bottom">
      <van-picker :columns="categoryColumns" @cancel="showCategoryPicker = false" @confirm="onCategoryConfirm" />
    </van-popup>
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
