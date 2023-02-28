export default {
  navbar: {
    home: "Home",
    manage: "Management",
    language: "切换至中文"
  },
  footer: {
    about: "About",
    feedback: "Feedback",
    lucky: "Feeling Lucky",
  },
  manage: {
    nav: {
      course: {
        name: "Manage Courses",
        list: "Course List",
        table: "Course Table",
      },
      student: {
        name: "Manage Students",
        quit: "Force Quit",
        fail: "Arrange Failing Course",
        admin: "Administrative",
      },
      canteen: {
        name: "Canteen Reports",
        debug: "De-🐛",
        battery: "Charge 🔋",
        smoke: "Smoke 🚬",
      },
    },
    list: {
      open_courses: "Opening Courses",
      open_courses1: "Listing courses...",
      add: "Add class",
      edit: "Edit class",
      cols: {
        code: "Course Code",
        name: "Course Name",
        lang: "Language",
        teacher: "Teacher",
        date: "Date",
        time: "Start Time",
        duration: "Duration",
        loc: "Location",
        operation: "Operations",
      },
      filter: {
        lang_zh: "Chinese",
        lang_en: "English",
        lang_bi: "Bilingual",
      },
      op: {
        edit: "Edit",
        del: "Delete",
      }
    },
    add: {
      code: "Course Code",
      code_empty: "course code must not be empty",
      code_err: "invalid course code: should be alphabets + numbers",

      name: "Course Name",
      name_empty: "course name must not be empty",
      name_err: "invalid course name: only english course names are accepted",

      lang: "Language",
      lang_zh: "Chinese",
      lang_en: "English",
      lang_bi: "Bilingual",
      lang_empty: "language must not be empty",

      teacher: "Teacher",
      teacher_empty: "teacher name must not be empty",
      teacher_err: "invalid teacher name: only english names are accepted",

      date: "Class Date",
      date_prompt: "Pick a date not after today",
      date_empty: "date must not be empty",
      date_invalid: "date bust be not after than today",

      time: "Course Time",
      time_st: "Start Time",
      time_ed: "End Time",
      time_empty: "start time must not be empty",
      time_err: "start time must be prior than end time",

      duration: "Start+Duration",
      dur_err: "duration must be greater than 0",

      space_err: "cannot start or end with spaces",

      location: "Location",
      location_prompt: "Pick a classroom location",
      location_empty: "location must not be empty",

      cancel: "Cancel",
      submit: "Submit",
      success: "Success!",

    },
    del: "Delete the course?"
  },
};
