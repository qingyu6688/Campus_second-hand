# 校园二手交易平台 (Campus Second Hand)

## 📖 项目简介

**校园二手交易平台** 是一个专为高校学生打造的安全、便捷、清新的闲置物品交易系统。项目包含 **移动端用户前台** 和 **Web端管理后台**，前后端分离设计。

核心设计理念为“轻盈、安全、便捷”，UI 风格采用 **薄荷绿 (Mint Teal)** 与 **暖阳橙 (Warm Sun Yellow)** 的清新配色，拒绝沉重的 AI 紫色调，贴合校园青春气息。

---

## ✨ 核心功能

### 📱 用户前台 (H5/Mobile)
- **用户认证**：支持手机号/学号注册登录，集成**学生身份认证**（上传校园卡/学生证）。
- **商品交易**：
  - **发布商品**：支持多图上传、原价/售价录入、选择校园交易地点（如图书馆、食堂）。
  - **商品展示**：瀑布流/双列布局，支持按分类、价格筛选，展示“仅自提”、“学生认证”等标签。
  - **商品详情**：轮播图展示、卖家信息、收藏、留言互动。
- **个人中心**：管理我发布的商品（支持下架/编辑）、我的收藏、我的买入/卖出记录。
- **消息互动**：站内信/私信功能，方便买卖双方沟通。

### 🖥️ 管理后台 (Web Admin)
- **数据看板**：可视化展示平台每日新增用户、商品发布量、交易额等关键指标 (ECharts)。
- **商品管理**：审核发布商品，违规商品下架处理，分类管理。
- **用户管理**：用户列表查询，学生认证审核。
- **内容管理**：评论管理、聊天记录监管（敏感词过滤/删除）。

---

## 🛠️ 技术栈

### Backend (后端)
- **核心框架**: Spring Boot 3.2.0
- **ORM 框架**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **工具库**: Hutool 5.8.25, Lombok
- **API 文档**: Knife4j 4.3.0 (Swagger 3)
- **安全/认证**: JJWT 0.11.5

### Frontend (用户前台)
- **框架**: Vue 3.5 (Composition API)
- **构建工具**: Vite 7
- **UI 组件库**: Vant 4 (Mobile UI)
- **样式**: Tailwind CSS 3
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **图标**: Heroicons

### Admin (管理后台)
- **框架**: Vue 3.5
- **UI 组件库**: Element Plus
- **图表**: ECharts 6
- **样式**: Tailwind CSS 4, Sass
- **状态管理**: Pinia

---

## 📂 目录结构

```
Campus_second-hand/
├── backend/            # 后端工程 (Spring Boot)
├── frontend/           # 用户前台工程 (Vue 3 + Vant)
├── admin/              # 管理后台工程 (Vue 3 + Element Plus)
├── init.sql            # 数据库初始化脚本
├── 数据库设计.md       # 详细数据库设计文档
├── 需求分析.md         # 项目需求分析文档
└── README.md           # 项目说明文档
```

---

## 🚀 快速开始

### 1. 环境准备
- **JDK**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 2. 数据库设置
1. 创建数据库 `campus_second_hand` (或其他名称)。
2. 执行根目录下的 `init.sql` 脚本，初始化表结构和数据。

### 3. 后端启动
1. 进入 `backend` 目录。
2. 修改 `src/main/resources/application.yml`，配置数据库连接信息（URL, Username, Password）。
3. 运行项目：
   ```bash
   mvn spring-boot:run
   ```
   后端服务默认启动在 `http://localhost:8080`。
   API 文档地址: `http://localhost:8080/doc.html`

### 4. 前端 (用户端) 启动
1. 进入 `frontend` 目录。
2. 安装依赖并启动：
   ```bash
   npm install
   npm run dev
   ```
   访问地址: `http://localhost:5173` (具体端口视控制台输出而定)。

### 5. 后台 (管理端) 启动
1. 进入 `admin` 目录。
2. 安装依赖并启动：
   ```bash
   npm install
   npm run dev
   ```
   访问地址: `http://localhost:5174` (具体端口视控制台输出而定)。

---

## 🎨 设计规范 (Design System)

- **主色 (Primary)**: Mint Teal (`#14b8a6`) - 用于主按钮、激活状态。
- **辅色 (Secondary)**: Warm Sun Yellow (`#f97316`) - 用于价格、提示。
- **背景 (Background)**: Slate-50 (`#f8fafc`) - 全局柔和背景。
- **圆角**: 大圆角风格 (`rounded-xl`, `rounded-2xl`)，亲和力强。

---

## 🤝 贡献
欢迎提交 Issue 或 Pull Request 来完善本项目！

## 📄 许可证
MIT License
