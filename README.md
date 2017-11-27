# FloatSideBar

a sidebar like sony z1
仿索尼z1手机通讯录的侧边栏

# GIF图预览

![](https://github.com/imcloud/FloatSideBar/blob/master/sidebar.gif)

# 使用方式

### kotlin编写，所以必须支持kotlin

在项目/build.gradle
```
buildscript {
    ext.kotlin_version = '1.1.51'  // 或更新版本
    repositories {
        ...
    }
    dependencies {
        ...
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        ...
    }
}
```
### 引用库
在app/build.gradle

```
 implementation 'com.aceegg.sidebar:floatsidebar:1.0.3'
```
在layout里, 宽高必须match_parent
```
<com.aceegg.sidebar.FloatSideBar
    android:id="@+id/side_bar"
    android:layout_width="match_parent" // must be use match_parent
    android:layout_height="match_parent" // must be use match_parent
    app:index="@array/sidebar_index"
    app:index_margin_right="4dp"
    app:index_text_color="#666666"
    app:index_choose_color="#00a8ff"
    app:index_text_size="14sp"
    />
```

##### 属性说明
属性 | 作用
---- | ----
index | 设置你的索引列表，引用资源文件中的字符串数组
index_margin_right | 距右边屏幕的距离，单位dp
index_text_color | 索引文字颜色
index_text_size | 索引文字大小
index_choose_color | 被选中的索引的文字颜色

默认的array/sidebar_index，你也可以自定义一组在xml里赋值给view
```
<resources>
    <string-array name="sidebar_index">
        <item>!</item>
        <item>A</item>
        <item>B</item>
        <item>C</item>
        <item>D</item>
        <item>E</item>
        <item>F</item>
        <item>G</item>
        <item>H</item>
        <item>I</item>
        <item>J</item>
        <item>K</item>
        <item>L</item>
        <item>M</item>
        <item>N</item>
        <item>O</item>
        <item>P</item>
        <item>Q</item>
        <item>R</item>
        <item>S</item>
        <item>T</item>
        <item>U</item>
        <item>V</item>
        <item>W</item>
        <item>X</item>
        <item>Y</item>
        <item>Z</item>
    </string-array>
</resources>
```
# License
-------
Copyright (C) 2017-2018 imcloud (aceegg.com)

FloatSideBar binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).
