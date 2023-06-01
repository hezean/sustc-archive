# SUSTech-CourseTable
[![GitHub license](https://img.shields.io/github/license/HeZean/sustis)](https://github.com/HeZean/SUSTech-CourseTable) [![Python version](https://img.shields.io/badge/python->=%203.8-blue)](https://github.com/HeZean/SUSTech-CourseTable)

## 快速开始

1. 电脑中装有 `python3` 和 `pip3`

2. 检查 `user.json`

   - `sid` 和 `pwd` 指 **学号** 和 **CAS的密码**
   - `year` 填入学年（如 `2021-2022`）、`semester` 填入学期（*秋季学期填 1、春季学期填 2，夏季学期填 3*）
   - 将 `year` 和 `semester` 两项直接删掉，会获取当前学期的课表

3. 下载并解压 repo，或 clone 到本地，在命令行中进入 `coursetable` 所在目录，运行程序并按输出提示进行操作

   ```shell
   cd /path/to/the/repo
   python3 main.py
   ```
   
   > 如果报错 `ModuleNotFoundError`，需要手动安装依赖
   > ```bash
   > python3 -m pip install -r requirements.txt
   >  ```

4. 将本目录下生成的 `schedule.ics` [导入手机](https://miaotony.xyz/NUAA_ClassSchedule/HowToImport.html)
