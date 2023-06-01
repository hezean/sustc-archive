# SUSTech TIS 退补选抢课

[![GitHub license](https://img.shields.io/github/license/HeZean/SUSTech-tisQiangke)](https://github.com/HeZean/SUSTech-tisQiangke) ![Functionbility](https://img.shields.io/badge/passing%20check-2022%2F9%2F5-brightgreen)

### Prerequisites

```bash
pip install requests  # 请确保电脑装有 Python 3 环境，并安装 pip 或 conda
```

### Usage

> 建议在开始抢课前五分钟左右运行脚本，同时自己手动抢课  

1. 将 `user.json` 里的 `sid` 和 `pwd` 改为自己的学号和 CAS 密码

2. 将课程名完整复制到 json 中，运行程序检查课程信息是否自动匹配正确

3. 对于未能自动匹配的课程，进入选课界面，找到你要抢的课，点`选课`键


    #### 选课按钮是被禁用的？
    
      1. 在按钮上右键，点击`inspect`  
         <img src="fig/1.png" height=180 />
         
      2. 屏幕右侧的开发者工具会弹出并标亮一行代码，双击`disabled="disabled"`并全选删除  
         <img src="fig/2.png" height=100 />
         
      3. 这时按钮已经变成绿色可用状态，点击按钮系统上方应该会出现错误信息（「不在选课时段」，因为正常人大概会选择在开始抢课前配置脚本）


3. 将开发者工具上方的标签页切换至 `Network(Fetch/XHR)`，多按几下按钮，最后几条请求应该都是 `addGouwuche`  
   <img src="https://i.imgur.com/2wnUmGY.png" height=140 />

4. 点击请求名字，展开请求信息，`Headers` 栏下划到底出现 `Form Data`，把 `p_id` 填到 json 中 `courses1`  
   <img src="https://i.imgur.com/q3ZO1jJ.png" height=360 />

5. 运行脚本，have fun！
```
python3 /path/to/this/file/tis.py
```

**本代码仅供技术交流使用，个人使用不当造成的一切后果自负。**
