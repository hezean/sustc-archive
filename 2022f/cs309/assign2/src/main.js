import { createApp } from "vue";
import App from "./App.vue";

import TDesign from "tdesign-vue-next";
import ArcoVue from "@arco-design/web-vue";
import ArcoVueIcon from '@arco-design/web-vue/es/icon';
import "tdesign-vue-next/es/style/index.css";
import '@arco-design/web-vue/dist/arco.css';

import store from "./store/index";
import i18n from "./i18n";
import router from "@/router";


createApp(App)
    .use(TDesign)
    .use(ArcoVue)
    .use(ArcoVueIcon)
    .use(store)
    .use(router)
    .use(i18n)
    .mount("#app");
