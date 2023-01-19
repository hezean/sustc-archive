# CLion 中使用 OpenCV (macOS)
Tested on: ![](https://img.shields.io/badge/macOS-Monterey-ff69b4) ![](https://img.shields.io/badge/CLion-2022.1-brightgreen) ![](https://img.shields.io/badge/CXX__STANDARD-17-blue)

#### 1. 安装 Homebrew （已有可跳过）

   在 终端/Terminal 中输入以下命令会安装中科大源的 brew

   ```bash
   /usr/bin/ruby -e "$(curl -fsSL https://cdn.jsdelivr.net/gh/ineo6/homebrew-install/install)"
   ```

   安装完成后可以输入下面命令检查是否正确安装

   ```bash
   brew --version
   # 输出应该类似如下
   # Homebrew 3.3.16
   # Homebrew/homebrew-core (git revision 6a390a6b2e0; last commit 2022-02-23)
   # Homebrew/homebrew-cask (git revision 04f038c5b6; last commit 2022-02-23)
   ```



#### 2. 使用 brew 安装 OpenCV

   ```bash
   brew install opencv@3
   ```

   在 macOS Monterey 中可能出现依赖安装出错，如下图所示，手动安装依赖后再 `brew install opencv@3`
   > <img src="https://i.imgur.com/zRWnb5b.png" alt="Screen Shot 2022-05-09 at 14.08.03" style="zoom:33%;" />
   >
   > 在安装依赖 glog （绿色字）时出错，手动 `brew install glog` 即可，其他报错类似处理

安装可能需要 10+min，完成后显示类似下图

<img src="https://i.imgur.com/CzuYTul.png" alt="Screen Shot 2022-05-09 at 15.00.23" style="zoom:33%;" />

最后！让 brew 创建 symlinks

```bash
brew ln opencv@3
```



#### 3. CLion Project 中添加 OpenCV dependency

新建一个 project （建议将 C++ 标准设为 17 或更高），打开 `CMakeLists.txt`，在正确位置添加下面几行

```cmake
cmake_minimum_required(VERSION 3.22)
project(cv)

set(CMAKE_CXX_STANDARD 17)

find_package(OpenCV REQUIRED)                          # added
include_directories(${OpenCV_INCLUDE_DIRS})            # added

add_executable(cv main.cpp)

target_link_libraries(${PROJECT_NAME} ${OpenCV_LIBS})  # added
```

修改 CMakeLists.txt 后，编辑器上方应该会出现一行 `CMake project needs to be reloaded`，点击 `Reload changes` （建议开启 auto-reload）



#### 4. Say Hello World to OpenCV!

将下面代码复制进 `main.cpp` 并运行

```cpp
#include "opencv2/opencv.hpp"
#include "opencv2/highgui/highgui.hpp"

using namespace cv;

int main() {
    namedWindow("Output", 1);

    Mat output = Mat::zeros(120, 350, CV_8UC3);
    putText(output,
            "Hello World :)",
            cvPoint(15, 70),
            FONT_HERSHEY_PLAIN,
            3,
            cvScalar(0, 255, 0),
            4);

    imshow("Output", output);
    waitKey(0);
    return 0;
}
```

<img src="https://i.imgur.com/5vnLAs0.png" alt="Screen Shot 2022-05-09 at 15.16.34" style="zoom: 25%;" />
