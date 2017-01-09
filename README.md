前言
===
现在大家都认可Python在接口测试方面效率比较高，究其原因，可能是Python的请求库功能强大，但Java的HttpClient封装得好的话，也可以一句代码发送请求，还有一点，Java的TestNg我个人认为是一个非常强大的测试框架，Python中的那些测试框架应该没有与之比肩的，但即便始此，Java在接口测试上还是举步维艰，这是因为在请求后对结果的处理，Python天然支持json解析，而Java呢？得依靠第三方库，且解析取值得一大片代码，更见鬼的是这一大片代码是毫无复用性可言，更有甚者，在解析时会搞一个pojo文件，更让Python者觉得用Java简直是灾难。

为了解决测试人员在Java对json解析的困惑，zson就应运而生了。因为我本人做过UI自动化测试，对XPATH有一定的了解，所以zson对json的操作中加入了一个类似于xpath的路径的概念，即利用一个路径来操作json串。如果一个json串有非常复杂的层级关系，如果想获取最里面的某个key的值，正常情况下那就得一层一层的解析进去，非常的繁琐，如果用zson，只需要一句代码，给定一个路径（值得注意的是，也可以是相对路径哦），就可以获取到对应的值，这样可以大大的提高生产力。

使用场景
===
设定一个json串：
```
{
    "retCode": "200",
    "retMsg": "success",
    "data": [
        {
            "id": 1,
            "name": "test",
            "date": "2017-01-09 13:30:00"
        },
        {
            "id": 2,
            "name": "test1",
            "date": "2017-01-09 13:40:00"
        }
    ]
}
```
如果想要获取以上json串的所有"name"的值，对于正常解析，你得遍历，但对于zson，你只需要这样：
```
ZsonResult zr = ZSON.parseJson(json);
List<Object> names = zr.getValues("//name");
```
我们在进行结果断言时，有时候请求返回的一整个json串作为一个期望值来进行断言，但json串中往往会存在有不固定的值，比如上面json串的"date"，每次都是变化的，这样就不好断言了，于是，在zson中，我们可以把这个date的值进行更改，改成一个固定的值:
```
ZsonResult zr = ZSON.parseJson(json);
zr.updateValue("//date","0000-00-00 00:00:00");
```
或者干脆删除这个结点:
```
ZsonResult zr = ZSON.parseJson(json);
zr.deleteValue("//date");
```
以上zson对json串的操作包含了查找，更新，删除。zson还有对json串中增加一个子字符串的操作:
```
ZsonResult zr = ZSON.parseJson(json);
zr.addValue("/data",2,"{\"id\":3,\"name\":\"test2\",\"date\":\"2017-01-09 14:30:00\"}");
```

zson
===

####专为测试人员打造的JSON解析器

当然，有很多很好的JSON解析的JAR包，比如JSONOBJECT,GSON，甚至也有为我们测试人员而打造的JSONPATH，但我还是自已实现了一下（之前也实现过，现在属于重构）。其主要特点是用一个类似于xpath的选择器来获取相应的值。

***

####特点

+ 无需层层解析
+ 根据给定的路径(类XPATH路径)来获取相应的值
+ 支持相对路径

***

####实现思路

思想是这样的，以这个JSON串为例(我自已随手写的)：

```
{"a":"b","cb":{"a":1},"d":["a",{"a":3},{"a":2},""],"e":"b"}
```

```
{
    "a": "b",
    "cb": {
        "a": 1
    },
    "d": [
        "a",
        {
            "a": 3
        },
        {
            "a": 2
        },
        ""
    ],
    "e": "b"
}
```

我们在保证只扫描一次字符串的情况下，就把JSON串解析成功。于是，我先定义了一个List: 

`private List<Object> collections = new ArrayList<Object>();`

collections用来存放这个JSON串中所有的LIST与MAP，在扫描时，一旦碰到{或[，就new一个Map或List,然后add到collections中去了：

![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index.png)

存放进去后，我们需要一个map来记录collections里的list或map的状态，比如是否已经闭合了，是一个list还是一个map,在collections中的index：

`private Map<String, Map<String, Integer>> index = new HashMap<String, Map<String, Integer>>();`

![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index1.png)

可以看到，这个MAP的key是由1 1.1 1.2 1.1.1这样来组成的，所以，这个key就可以用来表示json的层级结构了，当然我还用了一个list来保存这些key的顺序：

`private List<String> level = new ArrayList<String>();`

![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index2.png)

这样一来，数据结构就很清晰了。接下来要做的事，就是在扫描中的一些判断了，保持以下几个点：

1. 碰到[或{就new一个对象，并将对象存放到collections中去

2. 碰到'\\'需要转义的，得直接跳过去，并存放到扫描出来的临时变量中去。比如\\{就不需要new一个对象

3. 碰到"符号，就要打个标记，在下一个"出现之前，把扫描出来的都当成一个字符串放到临时变量中去。

4. 碰到:符号，就要开始标记是个map的开始了，并把之后出现的字符串都存放到另一个临时变量中去。

5. 碰到,符号，就要开始处理临时变量了，如果是map就把之前存的两个昨时变量，一个作为KEY，一个作为VALUE，都放到collections中对应的map中去，如果是list,则把之前存的第一个临时变量，放到collections对应的list中去。

6. 碰到]或}符号，则表示一个list或map被解析完全了，则这时候要去更新index中的对应的list或map的状态了。

解析完了后，所有的数据都在collections index level这三个变量中了，于是，我们只需要定一个取数据的规则就行了，我用的是一种类似于xpath的语法格式来取值的，这时候只需要解析下这个xpath路径就可以得出这个key，然后在collections中拿值就可以了！

***

####使用方法

![image](https://github.com/zhangfei19841004/zson/blob/master/imgs/index3.png)

>备注：上面的例子中，我们可以看到，类XPATH的路径支持绝对路径和相对路径，用\*\[]来表示一个list,[]里面放要获取的值在list中的index，比如/*\[0]指获取list中的第1个值。用map的key来获取其对应的值!

***

####使用说明

```
Zson z = new Zson(); //new一个Zson对象

ZsonResult zr = z.parseJson(j); //解析JSON字符串后，得到一个ZsonResult对象
```

>     zr对象可用的方法:

```
Object getValue(String path) //返回一个除了List或Map的Object对象，如果是List或Map，会转换成为JSON字符串返回

Map<String, Object> getMap(String path) //返回一个Map对象

List<Object> getList(String path) //返回一个List对象

String toJsonString(Object obj) //将Map或List转换成为JSON字符串
```

***

####选择器path说明

示例一:

```
[
    {
        "firstName": "Eric",
        "lastName": "Clapton",
        "instrument": "guitar"
    },
    {
        "firstName": "Sergei",
        "lastName": "Rachmaninoff",
        "instrument": "piano"
    }
]
```

>     找出第二个firstName: /*[1]/firstName 
>     输出:Sergei
***
>     找出第一个Map: /*[0]  
>     输出:{"firstName": "Eric","lastName": "Clapton","instrument": "guitar"}


示例二:

`{"a":["a"],"cb":{"a":1},"d":["a",{"a":[1,2]},{"a":2},""],"e":"b"}`

>     路径: /d/*[1]/a 
>     输出:[1,2]
***
>     路径: /d/*[1]/a/*[0]
>     输出:1

***

####2016年4月16日更新日志

```
支持相对路径！
示例二中：
getValues()
zr.getValues("/a//*[0]"));//输出[a]
zr.getValues("//*[1]"));//输出[2]
说明:相对路径所得出来的值只显示非MAP或LIST的值，如果是MAP或LIST，则会被忽略！
```

####2016年6月16日更新日志

```
更加丰富的API：
String s1 = "[{ \"firstName\": \"Eric\", \"lastName\": \"Clapton\", \"instrument\": \"guitar\" },{ \"firstName\": \"Sergei\", \"lastName\": \"Rachmaninoff\", \"instrument\": \"piano\" }] ";
String s2 = "[0,1,2,3.14,4.00,\"3\",true,\"\"]";
String s3 = "{\"a\":[\"a1\"],\"cb\":{\"a\":1},\"d\":[\"a\",{\"a\":[1,20]},{\"a\":2},\"\"],\"e\":\"b\"}";
Zson z = new Zson();
ZsonResult zr1 = z.parseJson(s1);
System.out.println(zr1.getValue("/*[1]/firstName"));
System.out.println(zr1.getMap("/*[1]"));

ZsonResult zr2 = z.parseJson(s2);
System.out.println(zr2.getInteger("/*[1]"));
System.out.println(zr2.getLong("/*[2]"));
System.out.println(zr2.getDouble("/*[3]"));
System.out.println(zr2.getFloat("/*[4]"));
System.out.println(zr2.getString("/*[5]"));
System.out.println(zr2.getBoolean("/*[6]"));

ZsonResult zr3 = z.parseJson(s3);
System.out.println(zr3.getValues("//*[0]"));
System.out.println(zr3.getValues("//*[1]"));
System.out.println(zr3.getList("/a"));
System.out.println(zr3.getMap("/cb"));
```

####2016年6月28日更新日志
```
增加API：
removeValue(String path);
功能：移除JSON串中的某个map,list或值，path为绝对路径

getResult();
功能：返回解析完的整个数据对象
```

####2017年1月3日更新日志
```
这次更新主要是对结果的处理进行了重构，因为考虑到结果的处理主要是用到了路径，所以在解析json串时，将每个结点的路径给保存下来了，相当于给结果做了一个索引，这样处理结果时，就非常的快，且非常的方便了。重构后的API解释如下：
    public boolean isValid();//判断json串是否合法
    
	public Object getResult();//获取整个结果对象
    
	public Object getValue(String path);//根据路径获取结果集的第一个值
    
	public Object getValue();//获取整个结果json字符串
    
	public List<Object> getValues(String path);//获取符合路径的所有结果集
    
	public String getString(String path);//根据路径获取结果集的第一个值，并转换为字符串
    
	public int getInteger(String path);//根据路径获取结果集的第一个值，并转换为Integer
    
	public long getLong(String path);//根据路径获取结果集的第一个值，并转换为Long
	
	public double getDouble(String path);//根据路径获取结果集的第一个值，并转换为Double
	
	public float getFloat(String path);//根据路径获取结果集的第一个值，并转换为Float
	
	public boolean getBoolean(String path);//根据路径获取结果集的第一个值，并转换为Boolean
	
	public void addValue(String path, int index, Object json);//在路径path下的index位置添加一个新的json串
    
	public void addValue(String path, String key, Object json);//在路径path下添加一个key为'key',value为'json'
	
	public void addValue(int index, Object json);//在根路径下的index位置添加一个新的json串
	
	public void addValue(String key, Object json);//在根路径下添加一个key为'key',value为'json'
	
	public void deleteValue(String path);//删除path的所有对象
    
	public void updateValue(String path, Object json);//更新path的值为'json'
```
