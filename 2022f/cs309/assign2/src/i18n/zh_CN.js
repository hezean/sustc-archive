export default {
  navbar: {
    home: "首页",
    manage: "管理",
    language: "Change to English"
  },
  footer: {
    about: "关于",
    feedback: "反馈",
    lucky: "试试手气",
  },
  manage: {
    nav: {
      course: {
        name: "课程管理",
        list: "课程列表",
        table: "排课管理",
      },
      student: {
        name: "学生管理",
        quit: "强制退学",
        fail: "安排挂科",
        admin: "行政化",
      },
      canteen: {
        name: "食堂管理",
        debug: "报告 🐛",
        battery: "充电 🔋",
        smoke: "精神食粮 🚬"
      },
    },
    list: {
      open_courses: "开设课程",
      open_courses1: "Opening Courses",
      add: "新建班级",
      edit: "编辑班级",
      cols: {
        code: "课程代码",
        name: "课程名",
        lang: "语言",
        teacher: "讲师",
        date: "日期",
        time: "开始时间",
        duration: "时长",
        loc: "地点",
        operation: "操作",
      },
      filter: {
        lang_zh: "中文",
        lang_en: "英语",
        lang_bi: "双语",
      },
      op: {
        edit: "编辑",
        del: "删除",
      }
    },
    add: {
      code: "课程代码",
      code_empty: "课程代码不得为空",
      code_err: "课程代码不合法：字母+数字",

      name: "课程名",
      name_empty: "课程名不得为空",
      name_err: "课程名不合法：仅接受英文名",

      lang: "语言",
      lang_zh: "中文",
      lang_en: "英文",
      lang_bi: "双语",
      lang_empty: "语言设置不得为空",

      teacher: "讲师",
      teacher_empty: "讲师名不得为空",
      teacher_err: "讲师名不合法：仅接受英文名",

      date: "日期",
      date_prompt: "选择今天或以前的日期",
      date_empty: "日期不得为空",
      date_invalid: "仅允许选择今天或以前的日期",

      time: "时间",
      time_st: "开始时间",
      time_ed: "结束时间",
      time_empty: "开始时间不得为空",
      time_err: "开始时间必须早于结束时间",

      duration: "开始+时长",
      dur_err: "课程时长必须大于 0",

      space_err: "开头或结尾不得为空格",

      location: "地点",
      location_prompt: "选择教室所在区域",
      location_empty: "地点不得为空",

      cancel: "取消",
      submit: "提交",
      success: "提交成功",
    },
    del: "确认删除课程?"
  },
};
