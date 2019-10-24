 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,$location,goodsService,uploadService,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    /*$scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }*/
    $scope.findOne=function(){
        //从goods.html将id传到goods.edit.html中
        var id= $location.search()['id'];//获取参数值
        if(id==null){
            return ;
        }
        goodsService.findOne(id).success(
            function(response){
                $scope.entity= response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表
                $scope.entity.goodsDesc.itemImages=
                    JSON.parse($scope.entity.goodsDesc.itemImages);
                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);


                //SKU列表规格列转换
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec =
                        JSON.parse( $scope.entity.itemList[i].spec);
                }

            }
        );
    }

    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(items,'attributeName',specName);
        if(object==null){
            return false;
        }else{
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }
    }



    //保存
    $scope.save = function () {

        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction=editor.html();


        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert('保存成功');
                    $scope.entity={};
                    editor.html("");

                    location.href="goods.html";//跳转到商品列表页

                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //在增加成功后弹出提示，并清空实体（因为编辑页面无列表）
    $scope.add = function () {
        //提取kindeditor编辑器的内容
        $scope.entity.goodsDesc.introduction=editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert('保存成功');
                    $scope.entity = {};
                    editor.html('');//清空富文本编辑器
                } else {
                    alert(response.message);
                }
            }
        );


    }

    /**
     * 上传图片
     */
    $scope.uploadFile=function(){
        uploadService.uploadFile().success(function(response) {
            if(response.success){//如果上传成功，取出url
                $scope.image_entity.url=response.message;//设置文件地址
            }else{
                alert(response.message);
            }
        }).error(function() {
            alert("上传发生错误");
        });
    };



    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity=function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //列表中移除图片
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }


    //获取商品的一级分类
    $scope.selectItemCat1List=function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List=response;
            }
        )
    }

    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function(newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findByParentId(newValue).success(
            function(response){
                $scope.itemCat2List=response;
            }
        );
    });

    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function(newValue, oldValue) {
        //根据选择的值，查询三级分类
        itemCatService.findByParentId(newValue).success(
            function(response){
                $scope.itemCat3List=response;
            }
        );
    });

    //获取对应的模板id
    $scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {
        //根据选择的值，模板id
        itemCatService.findOne(newValue).success(
            function(response){
                $scope.entity.goods.typeTemplateId=response.typeId; //更新模板ID
            }
        );
    });
    //通过模板id找对应的品牌  下啦
    $scope.$watch('entity.goods.typeTemplateId', function(newValue, oldValue) {
        //根据选择的值，模板id
        typeTemplateService.findOne(newValue).success(
            function(response){

                $scope.typeTemplate=response;
                $scope.typeTemplate.brandIds= JSON.parse( $scope.typeTemplate.brandIds);//品牌列表

                //如果没有ID，则加载模板中的扩展数据
                if($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
                }

            }
        );

        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList=response;
            }
        )

    });


    /**
     * 我们需要将用户选中的选项保存在tb_goods_desc表的specification_items字段中，定义json格式如下：
     [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
     entity.goodsDesc. specificationItems=[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}]

     1、	先判断specName是否在$scope.entity.goodsDesc.specificationItems中
     Obj = [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}]
     2、	如果不在specificationItems中，那么push一个新的
     specificationItems.push{"attributeName":name,"attributeValue":[value]});
     3、	如果在specificationItems中，那么根据checked的状态 决定往obj.attributeValue中增或删optionName

     */
    //保存在tb_goods_desc表的specification_items字段中
    $scope.updateSpecAttribute=function($event,name,value){
        //1、先判断specName是否在$scope.entity.goodsDesc.specificationItems中
        var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName', name);
        if(object!=null){
            //如果在specificationItems中，那么根据checked的状态 决定往obj.attributeValue中增或删optionName
            if($event.target.checked ){
                object.attributeValue.push(value);
            }else{//取消勾选
                object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);//移除选项
                if(object.attributeValue.length==0){
                    //如果选项都取消了，将此条记录移除
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
                }
            }
        }else{
            //如果不在specificationItems中，那么push一个新的
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]});
        }
    }

    //生成SKU列表(深克隆)
    /**
     * $scope.entity.goodsDesc.specificationItems = [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}]
     * $scope.entity.itemList = [{spec: {"机身内存":"16G","网络":"联通3G"},price:0,num:99999,status:'0',isDefault:'0' }]
     */
    //创建SKU列表
    $scope.createItemList=function(){
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
        var items=  $scope.entity.goodsDesc.specificationItems;//[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]}]
        for(var i=0;i< items.length;i++){
            $scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
        }
    }
    //添加列值
    addColumn=function(list,columnName,conlumnValues){
        var newList=[];//新的集合
        for(var i=0;i<list.length;i++){
            var oldRow= list[i];
            for(var j=0;j<conlumnValues.length;j++){
                var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
                newRow.spec[columnName]=conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;

    }

    //显示商品的状态
    $scope.status=['审核中','已审核','审核未通过','关闭'];


    $scope.itemCatList=[];//商品分类列表
    //加载商品分类列表
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    //代码解释：因为我们需要根据分类ID得到分类名称，所以我们将返回的分页结果以数组形式再次封装。
                    //把商品的类别id 对应名字 数组形式
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );
    }



});