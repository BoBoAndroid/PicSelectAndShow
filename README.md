# PicSelectAndShow
## PicSelectAndShow一个为安卓开发者简化选择图片代码量的框架
#### 演示图：<br/>
![](https://github.com/BoBoAndroid/PicSelectAndShow/raw/master/screenshot/演示用.gif)
#### 使用方法：<br/>
在你项目的build.gradle中加入

         allprojects{

                repositories{

                     maven{url 'https://jitpack.io'}

                 }

          }

然后就是加依赖了：compile 'com.github.BoBoAndroid:PicSelectAndShow:1.0'
#### 注意事项：<br/>
最好程序里面把操作相机，和读写存储卡的权限给允许了，虽然框架有兼容处理，他会弹出选择然后再点击才能进入，体验不是很好。后续再研究怎么解决一下。<br/>
具体每个功能的使用方法，可以参考：
[我的博客](http://blog.csdn.net/bobo1127881870) 
