import HomePage from "@/page/HomePage";
import Management from "@/page/ManagementPage";
import CourseList from "@/components/CourseList";
import CourseTable from "@/components/CourseTable";

export default [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    component: HomePage,
  },
  {
    path: "/manage",
    component: Management,
    children: [
      {
        path: "",
        redirect: "/manage/course",
      },
      {
        path: "course",
        children: [
          {
            path: "",
            redirect: "/manage/course/list",
          },
          {
            path: "list",
            component: CourseList,
          },
          {
            path: "table",
            component: CourseTable,
          },
        ],
      },
    ],
  },
];
