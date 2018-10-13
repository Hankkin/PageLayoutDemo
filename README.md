# PageLayoutDemo
一款简单的page切换 空布局、错误布局、加载布局，支持一键配置、定义，不需要xml编写

> **该功能是支持单独为某个布局设置状态改变的，比如很多同学提到的我一个listview的数据没有获取到，fun initPage(targetView: Any),这个targetView你只需要设置成你的listview或者包裹你listview的parent布局就OK了，具体原理可以看下面的代码解析啊，遍历获取索引，然后记录索引值....**

**如果您想看JAVA版本，点击[https://github.com/Hankkin/PageLayoutDemojava](https://github.com/Hankkin/PageLayoutDemojava)**

> 项目中我们经常会用到的加载数据，加载完数据后显示内容，如果没有数据显示一个空白页，这是如果网络错误了显示一个网络错误页，自定义一个PageLayout。

## DownLoad

[https://fir.im/pagelayout](https://fir.im/pagelayout)

<img width="300" height=“300” src="http://lc-47sd2ifv.cn-n1.lcfile.com/6c9753f0d3a452ef9bb2.png"></img>

## 绪论
Android中经常使用一个空白页和网络错误页用来提高用户体验，给用户一个较好的感官，如果获取到的数据为空，那么会显示一个空白数据页，如果在获取数据的过程中网络错误了，会显示一个网络异常页，像最近比较火的某东这样,见下图。网上也有一些开源的组件，大部分都是自定义继承某个布局在xml中让其作为跟布局，然后将自己的内容布局添加进去，效果也都不错，但是个人总觉得稍微有些麻烦，不是那么灵活，n多个xml布局都去定义，写的心烦，所以有了今天的主角。

<img width="173" height=“274” src="http://lc-47sd2ifv.cn-n1.lcfile.com/da31e9a1ddf099d15ec6.png"></img>
<img width="173" height=“274” src="http://lc-47sd2ifv.cn-n1.lcfile.com/fad14cf4faa9d673b358.png"></img>

## 思考
实现的思路实际上是和上面说的一样，只不过换了一种方式，我们手动获取到contentView，将它从DecorView中移除，然后交给PageLayout取管理。当时考虑的时候就是不想在每个xml中去写页面切换的布局，那么我们可不可以用Java代码去控制？带着下面几个问题一起来看一下。

- 1.自定义一个布局让其作为跟布局
- 2.提供切换**加载loading**、**空白页empty**、**错误页errror**、**内容页content**功能
- 3.怎么让其取管理上边的四个页面？
- 4.contentView怎么添加？
- 5.如果我想切换的跟布局不是个Activity或者Fragment怎么办？
- 6.因为切换页面状态的功能一般都是一个APP统一的，那么可不可以一键配置呢？

## 实现

### 1.代码设计

首先我们定义PageLayout继承FrameLayout或者LinearLayou或者其他的布局都可以，然后我们需要提供切换四个布局的功能，当然如果支持自定义就更好了，还有状态布局里面的一些属性，还方便一键配置，所以最后采用了Builder模式来创建，使用方式就和Android里面的**AlertDialog**一样，通过Builder去构建一个PageLayout。最后的样子是长这样的：

| 方法                       | 注释                          |
| :------------------------- | ----------------------------- |
| showLoading()              | 显示loading                   |
| showError()                | 显示错误布局                  |
| showEmpty()                | 显示空布局                    |
| hide()                     | 显示内容布局                  |
| **Builder**                |                               |
| setLoading()               | setLoadingText()              |
| setError()                 | setDefaultLoadingBlinkText()  |
| setEmpty()                 | setLoadingTextColor()         |
| setDefaultEmptyText()      | setDefaultLoadingBlinkColor() |
| setDefaultEmptyTextColor() | setDefaultErrorText()         |
| setDefaultErrorTextColor() | setEmptyDrawable()            |
| setErrorDrawable()         |                               |



**默认样式**
```
PageLayout.Builder(this)
                .initPage(ll_default)
                .setOnRetryListener(object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }

                })
                .create()
```

**自定义样式**

```
PageLayout.Builder(this)
                .initPage(ll_demo)
                .setLoading(R.layout.layout_loading_demo)
                .setEmpty(R.layout.layout_empty_demo,R.id.tv_page_empty_demo)
                .setError(R.layout.layout_error_demo,R.id.tv_page_error_demo,object : PageLayout.OnRetryClickListener{
                    override fun onRetry() {
                        loadData()
                    }
                })
                .setEmptyDrawable(R.drawable.pic_empty)
                .setErrorDrawable(R.drawable.pic_error)
                .create()
```

### 2.设置PageLayout

> 考虑好了代码设计方式之后，我们来具体实现功能，首先需要考虑上面的5，6点：

**contentView怎么添加？**

**如果我想切换的跟布局不是个Activity或者Fragment怎么办？**

#### 1.Activity
如果我们要切换的跟布局是个Activity时，首先我们需要了解一下Android中的setContentView()方法，很熟悉，是我们新建完Activity后默认会在生命周期方法onCreate()中默认存在的，那么setContentView()做了些什么呢？我们先看一张图：

![image](http://lc-47sd2ifv.cn-n1.lcfile.com/f07096d512318918580c.png)

> 一个Activity是通过ActivityThread创建出来的，创建完毕后，会将DecorView添加到Window中，同时会创建ViewRootImpl对象，并将ViewRootImpl对象和DecorView建立关联，setContentView()是通过getWindow()调用的，这里的window实际初始化的时候初始化为PhoneWindow，也就是说Activity会调用PhoneWindow的setContentView()将layout布局添加到DecorView上，而此时的DecorView就是那个最底层的View。然后通过LayoutInflater.infalte()方法加载布局生成View对象并通过addView()方法添加到Window上，（一层一层的叠加到Window上）所以，Activity其实不是显示视图，Window才是真正的显示视图。

再来看上面的那张图，可以说DecorView是一个界面的真正跟布局，TitleView我们可以通过设置theme样式显示隐藏的，状态布局切换时我们不考虑TitleView，我们只需要考虑ContentView，而ContentView也就是**android.R.id.content**,知道了这些我们来看看怎么获取将contenView交给PageLayout管理。

#### 2.Fragment、View

如果我们要切换的跟布局是个Fragment、View时，我们只需要获取到它的parent

### 3.PageLayout设置跟布局
获取到了contentView跟布局后，我们要移除自己的显示内容的布局，并把这个布局交给PageLayout，下面看一下代码，注释的很详细了

```
 /**
         * set target view for root
         */
        fun initPage(targetView: Any): Builder {
            var content: ViewGroup? = null
            when (targetView) {
                is Activity -> {    //如果是Activity，获取到android.R.content
                    mContext = targetView
                    content = (mContext as Activity).findViewById(android.R.id.content)
                }
                is Fragment -> {    //如果是Fragment获取到parent
                    mContext = targetView.activity!!
                    content = (targetView.view)?.parent as ViewGroup
                }
                is View -> {        //如果是View，也取到parent
                    mContext = targetView.context
                    try {
                        content = (targetView.parent) as ViewGroup
                    } catch (e: TypeCastException) {
                    }
                }
            }
            val childCount = content?.childCount
            var index = 0
            val oldContent: View
            if (targetView is View) {   //如果是某个线性布局或者相对布局时，遍历它的孩子，找到对应的索引，记录下来
                oldContent = targetView
                childCount?.let {
                    for (i in 0 until childCount) {
                        if (content!!.getChildAt(i) === oldContent) {
                            index = i
                            break
                        }
                    }
                }

            } else {    //如果是Activity或者Fragment时，取到索引为第一个的View
                oldContent = content!!.getChildAt(0)
            }
            mPageLayout.mContent = oldContent   //给PageLayout设置contentView
            mPageLayout.removeAllViews()    
            content?.removeView(oldContent)     //将本身content移除，并且把PageLayout添加到DecorView中去
            val lp = oldContent.layoutParams
            content?.addView(mPageLayout, index, lp)
            mPageLayout.addView(oldContent)
            initDefault()   //设置默认状态布局
            return this
        }
```

这样我们就解决了上面的5，6的问题。

### 4.其他

- **因为错误布局中一般都包括一个点击重试的功能，如果你需要自定义布局，你可以在配置PageLayout之前，设置好错误布局和点击事件，然后setError进去，同时也提供了一个默认方式的方法**

```
fun setError(errorView: Int, errorClickId: Int, onRetryClickListener: OnRetryClickListener)
```
- **考虑到此功能的APP统一性，所以并没有提供过多的自定义功能，如果你需要的话，你都可以提前设置好View，然后进行set**
- **之前和同事讨论，xml形式和代码形式哪个更方便更灵活，这些都属于个人喜好吧，如果你更喜欢在xml里写的话，你可以进行改造，也挺简单，目前没提供xml方式，PageLayout的初衷就是模仿AlertDialog方式，随时随地使用状态布局切换**
- **你也可以在BaseActivity和BaseFragment中进行PageLayout的初始化，Demo中未使用，自行解决**

## 效果图

![image](http://lc-47sd2ifv.cn-n1.lcfile.com/a2a787511ddb0461bfb1.gif)


代码已经上传到Github[https://github.com/Hankkin/PageLayoutDemo](https://github.com/Hankkin/PageLayoutDemo)


**Reading：一款不错的Material Desgin风格的Kotlin版本的开源APP**
[https://github.com/Hankkin/Reading](https://github.com/Hankkin/Reading)

欢迎大家Follow、star、fork，谢谢
如果有不合适的地方，请提issues讨论指正










