# zson
专为测试人员打造的JSON解析器

 JAVA自已设计JSON解析器

当然，有很多很好的JSON解析的JAR包，比如JSONOBJECT,GSON，甚至也有为我们测试人员而打造的JSONPATH，但我还是自已实现了一下（之前也实现过，现在属于重构）。

思想是这样的，以这个JSON串为例：String j = "{\"a\":\"b\",\"c\\\"b\":{\"a\":1},\"d\":[\"a\",{\"a\":3},{\"a\":2},\"\"],\"e\":\"b\"}";

我们在保证只扫描一次字符串的情况下，就把JSON串解析成功。于是，我先定义了一个List: private List<Object> collections = new ArrayList<Object>();

collections用来存放这个JSON串中所有的LIST与MAP，在扫描时，一旦碰到{或[，就new一个Map或List,然后add到collections中去了：

![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index.png)

存放进去后，我们需要一个map来记录collections里的list或map的状态，比如是否已经闭合了，是一个list还是一个map,在collections中的index：private Map<String, Map<String, Integer>> index = new HashMap<String, Map<String, Integer>>();
![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index1.png)
可以看到，这个MAP的key是由1 1.1 1.2 1.1.1这样来组成的，所以，这个key就可以用来表示json的层级结构了，当然我还用了一个list来保存这些key的顺序：private List<String> level = new ArrayList<String>();
![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index2.png)

这样一来，数据结构就很清晰了。接下来要做的事，就是在扫描中的一些判断了，保持以下几个点：

1.碰到[或{就new一个对象，并将对象存放到collections中去

2.碰到'\\'需要转义的，得直接跳过去，并存放到扫描出来的临时变量中去。比如\\{就不需要new一个对象

3.碰到"符号，就要打个标记，在下一个"出现之前，把扫描出来的都当成一个字符串放到临时变量中去。

4.碰到:符号，就要开始标记是个map的开始了，并把之后出现的字符串都存放到另一个临时变量中去。

5.碰到,符号，就要开始处理临时变量了，如果是map就把之前存的两个昨时变量，一个作为KEY，一个作为VALUE，都放到collections中对应的map中去，如果是list,则把之前存的第一个临时变量，放到collections对应的list中去。

6.碰到]或}符号，则表示一个list或map被解析完全了，则这时候要去更新index中的对应的list或map的状态了。

解析完了后，所有的数据都在collections index level这三个变量中了，于是，我们只需要定一个取数据的规则就行了，我用的是一种类似于xpath的语法格式来取值的，这时候只需要解析下这个xpath路径就可以得出这个key，然后在collections中拿值就可以了！

以下是代码下载地址：

http://files.cnblogs.com/files/zhangfei/zson.rar

贴一下使用方法：
![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index3.png)
 备注：上面的例子中，我们可以看到，XPATH只支持绝对路径（代码都有，大家可以扩展成相对路径），用*[]来表示一个list,用map的key来找其value!
