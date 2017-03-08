前言
===
现在大家都认可Python在接口测试方面效率比较高，究其原因，可能是Python的请求库功能强大，但Java的HttpClient封装得好的话，也可以一句代码发送请求，还有一点，Java的TestNg我个人认为是一个非常强大的测试框架，Python中的那些测试框架应该没有与之比肩的，但即便始此，Java在接口测试上还是举步维艰，这是因为在请求后对结果的处理，Python天然支持json解析，而Java呢？得依靠第三方库，且解析取值得一大片代码，更见鬼的是这一大片代码是毫无复用性可言，更有甚者，在解析时会搞一个pojo文件，更让Python者觉得用Java简直是灾难。

为了解决测试人员在Java对json解析的困惑，zson就应运而生了。因为我本人做过UI自动化测试，对XPATH有一定的了解，所以zson对json的操作中加入了一个类似于xpath的路径的概念，即利用一个路径来操作json串。如果一个json串有非常复杂的层级关系，如果想获取最里面的某个key的值，正常情况下那就得一层一层的解析进去，非常的繁琐，如果用zson，只需要一句代码，给定一个路径（值得注意的是，也可以是相对路径哦），就可以获取到对应的值，这样可以大大的提高生产力。

作者联系方式
===

QQ：408129370

QQ群：536192476

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

选择器path说明
===

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
***
>     找出所有的firstName: //firstName 
>     输出:["Eric","Sergei"]


示例二:

`{"a":["a"],"cb":{"a":1},"d":["a",{"a":[1,2]},{"a":2},""],"e":"b"}`

>     路径: /d/*[1]/a 
>     输出:[1,2]
***
>     路径: /d/*[1]/a/*[0]
>     输出:1

***

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

思想是这样的，以这个JSON串为例(我
