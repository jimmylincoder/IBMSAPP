-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保持哪些类不被混淆(继承他的貌似没生效)
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.*
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends com.flinkinfo.flsdk.android.*

#-------------------------引用的包
-keep class or.bouncycastle.**{* ;}
-keep class org.apache.commons.io.** {* ;}
-keep class com.alibaba.fastjson.** {* ;}
-keep class com.alibaba.fastjson.** {* ;}
-keep class com.flinkinfo.flsdk.** {* ;}
-keep class com.flinkinfo.aar.** {* ;}
-keep class com.google.zxing.**{* ;}
-keep class net.sourceforge.pinyin4j.**{* ;}



-keep class de.greenrobot.event.**{* ;}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
-keep class rong.**{* ;}
-keep class io.rong.**{* ;}


#主要不让混淆的地方
-keep class com.flinkinfo.epimapp.page.** {* ;}
-keep class com.flinkinfo.epimapp.component.uri_handle.**{* ;}
-keep class com.flinkinfo.epimapp.widget.activity.** {* ;}
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class **.R$* {
    *;
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends com.flinkinfo.flsdk.android.BaseFragment {
   public void *(android.view.View);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends com.flinkinfo.flsdk.android.BaseAppFragment {
   public void *(android.view.View);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends com.flinkinfo.flsdk.android.BaseActivity {

}
#保持自定义控件类不被混淆
-keepclassmembers class * extends com.flinkinfo.flsdk.android.BaseFragmentActivity {

}
-keepclassmembers class * extends com.flinkinfo.epimapp.widget.activity.EpimAppActivity {

}
-keepclassmembers class * extends com.flinkinfo.epimapp.widget.activity.EpimAppFragmentActivity {

}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}


-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn
-ignorewarnings


#融云代码混淆
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
 public *;
}

-keepattributes Exceptions,InnerClasses

-keep class io.rong.** {*;}

-keep class * implements io.rong.imlib.model.MessageContent{*;}

-keepattributes Signature

-keepattributes *Annotation*

-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
 public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**

-keep class com.ultrapower.** {*;}