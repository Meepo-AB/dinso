import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { router } from './router'
import './styles/app.css'

console.log(`
✨ 🚀 💪 ✨🚀 💪 ✨🚀 💪 ✨🚀 💪 ✨
+--------------------------------------+
|      Lycka till med uppgiften!       |
|              Du fixar det!           |
+--------------------------------------+
✨ 🚀 💪 ✨🚀 💪 ✨🚀 💪 ✨🚀 💪 ✨
`)

createApp(App).use(createPinia()).use(router).mount('#app')
