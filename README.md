Android瀑布流实例
========

此项目由于最初设计问题，导致现在问题比较多，暂时停止维护。

我现在在其他类似的瀑布流上进行完善开发,

####请关注：[PinterestLikeAdapterView](https://github.com/dodola/PinterestLikeAdapterView)

有必要解释一下程序为什么采用addview方式而不是做成类似于ListView的那种Adapter数据方式。

首先考虑的是这样实现比较简单，代码量不多，简单易懂，不用涉及AdapterView里的一些复杂View显示方法(onMeasure,onLayout等)，回收算法也是采用相对简单实用的方式，虽然那个现在还有Bug，就是突然刷新到第一页的时候会有图片无法显示，解释一下，算法为了保证index的不越界而进行了一些范围上的判断条件宽松设计....

重要的一点是这种设计的优点是可以不用服务器去返回高度和长度，视图会在图片下载完成后计算出整个ItemView的高度，然后添加到主视图中，比较灵活。

缺点不少，采用这种方式无法实现类似下拉刷新的功能，就是View无法在现有视图上面添加，只能向下扩展，可能是能力有限，没有实现出来。

实现了类似于迷尚android和蘑菇街android的瀑布流布局

![Screenshot](https://github.com/dodola/android_waterfall/raw/master/screen1.png)
![Screenshot](https://github.com/dodola/android_waterfall/raw/master/screen2.png)
