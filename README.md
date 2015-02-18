# AndroidChineseString
这是个用来做中文繁简转换的 Android Studio/ IntelliJ IDEA 插件。<br>
This is a Android Studio/ IntelliJ IDEA plugin to convert the strings.xml between Chinese.

# Usage
对着中文 values 文件夹下的 strings.xml 单击右键，再选择 Convert between Chinese。<br>
中文 values 文件夹指的是 values-zh-rCN, values-zh-rHK 和 values-zh-rTW。单纯的 values-zh 是不能判断是繁还是简。<br>
Right click the strings.xml in Chinese values folder, choose 'Convert to other languages'.<br>
Chinese values folder is values-zh-rCN, values-zh-rHK and values-zh-rTW. It is unknown that the language is Simplified Chinese or Traditional Chinese in values-zh folder.

# Build
导入 IntelliJ IDEA 即可编译。<br>
Just import into IntelliJ IDEA and build.

# Thanks
- [OpenCC](https://github.com/BYVoid/OpenCC)
    - 中文繁简转换工具
    - conversion between Traditional and Simplified Chinese
- [juniversalchardet](https://code.google.com/p/juniversalchardet/)
    - 检测编码
    - encoding detector
- [AndroidLocalizationer](https://github.com/westlinkin/AndroidLocalizationer)
    - 学习如何制作插件的例子
    - I learned how to develop plugin from it

# License
本项目内容采用 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) 授权。<br>
The content of this project itself is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
