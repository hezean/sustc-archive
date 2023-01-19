import {createWebHistory, createRouter} from "vue-router";

import Github from "@/components/Github";
import StackOverflow from "@/components/StackOverflow"

const routes = [
    {
        path: "/",
        redirect: "/github"
    },
    {
        path: "/github",
        name: "Github",
        component: Github,
    },
    {
        path: "/stackoverflow",
        name: "StackOverflow",
        component: StackOverflow,
    },
]

export const router = createRouter({
    history: createWebHistory(),
    routes: routes,
})

export default router
