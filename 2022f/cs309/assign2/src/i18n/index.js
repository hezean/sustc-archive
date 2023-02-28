import { createI18n } from "vue-i18n";

import zh_CN from "./zh_CN";
import en_US from "./en_US";


const i18n = createI18n({
  locale: localStorage.getItem("locale"),
  fallbackLocale: "en",

  legacy: false,
  globalInjection: true,
  fallbackWarn: false,
  missingWarn: false,

  messages: {
    "zh": zh_CN,
    "en": en_US,
  }
});

export default i18n;
