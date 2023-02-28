import { createWebHistory, createRouter } from "vue-router";
import NProgress from "nprogress";
import "nprogress/nprogress.css";

import routes from "./routes";


NProgress.configure({
  showSpinner: false,
});

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { x: 0, y: 0 };
  },
  routes: routes,
});

router.beforeEach((to, from, next) => {
  NProgress.start();
  next();
});

router.afterEach(() => {
  NProgress.done();
});

export default router;
