# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#**********************************通用格式***************************************
#-keep class com.xx.xxx.**{*;}                  #保持某个包内的文件不被混淆
#-dontwarn com.xxx**                            #忽略某个包的警告
#-keep public class com.x.xx.xxx.leiming        #保持某个类不被混淆
#-keep public class * extends com.xxx.**        #保持继承某个类的类不被混淆
#-libraryjars libs/xxx.jar                      #混淆第三方jar包，其中xxx为jar包名
#**********************************通用格式***************************************

-dontskipnonpubliclibraryclasses # 不忽略非公共的库类
-optimizationpasses 5            # 指定代码的压缩级别
-dontusemixedcaseclassnames      # 是否使用大小写混合
-dontpreverify                   # 混淆时是否做预校验
-ignorewarning                   # 忽略警告
-dontoptimize                    # 优化不优化输入的类文件

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法




 #保持哪些类不被混淆
#-keep public class * extends android.app.Fragment
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class com.android.vending.licensing.ILicensingService
#-keep public class * extends android.support.v4.app.Fragment#如果有引用v4包可以添加这行
#-keep public class * extends android.support.** #如果有引用v4或者v7包，需添加
-keep class android.support.**{*;}
-keep class com.github.ybq.android.spinkit.**{*;}
-keep class org.hamcrest.**{*;}
-keep class org.junit.**{*;}
-keep class junit.**{*;}
-keep class okhttp3.**{*;}
-keep class okio.**{*;}

-keep class com.loopj.android.http.**{*;}
-keep class com.hik.mcrsdk.**{*;}
-keep class org.MediaPlayer.PlayM4.**{*;}
-keep class org.simpleframework.xml.**{*;}
-keep class com.hikvision.sdk.**{*;}



#如果引用了v4或者v7包 ,忽略支持包的警告
-dontwarn android.support.**


#**********************************保持Gson不混淆***************************************
-keepattributes Signature        #不混淆泛型
-keepattributes *Annotation*     # 不混淆注解，保持注解
#如果使用有Gson，则添加以下配置,上面两个在其他方面也有用
-keep class com.google.gson.**{*;}
#下面需要填写自己bean对象对应的包名，所以建议将使用Gson解析的都放在同一包下
-keep class com.minlu.fosterpig.bean.** {*;}
#**********************************保持Gson不混淆***************************************

#**********************************混淆后日志相关***************************************
-verbose                         # 混淆时记录日志  -dontverbose(混淆时不记录日志)
#生成混淆后的日志数据，gradle build时在本项目根目录输出
-dump class_files.txt            #apk包内所有class的内部结构
-printseeds seeds.txt            #未混淆的类和成员
-printusage unused.txt           #打印未被使用的代码  列出从 apk 中删除的代码
-printmapping mapping.txt        #混淆前后的映射
#**********************************混淆后日志相关***************************************


#**********************************混淆第三方jar包***************************************
#混淆第三方法jar包一般没有这么简单，最好看第三方jar具体怎么混淆
#-libraryjars libs/xxx.jar        #混淆第三方jar包，其中xxx为jar包名
#三星应用市场需要添加:sdk-v1.0.0.jar,look-v1.0.1.jar
#-libraryjars libs/sdk-v1.0.0.jar
#-libraryjars libs/look-v1.0.1.jar
#**********************************混淆第三方jar包***************************************


#不混淆H5交互
-keepattributes *JavascriptInterface*
#ClassName是类名，H5_Object是与javascript相交互的object，建议以内部类形式书写
-keepclassmembers class **.ClassName$H5_Object{*;}


-keepnames class * implements java.io.Serializable #不混淆Serializable

-keepclassmembers class **.R$* { #不混淆资源类
    public static <fields>;
}
-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {      # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {      # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {             # 保持枚举 enum 类不被混淆  如果混淆报错，建议直接使用 -keepclassmembers class * implements java.io.Serializable即可
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {         # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}


-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}