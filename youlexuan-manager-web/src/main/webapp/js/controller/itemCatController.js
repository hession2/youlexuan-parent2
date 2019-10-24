 //商品类目控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			//
			$scope.entity.parentId = $scope.parentId;//赋予上级ID

			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	/*$scope.reloadList();//重新加载*/
                    $scope.findByParentId($scope.parentId);//重新加载 返回到当前页面
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					//$scope.reloadList();//刷新列表
                    $scope.findByParentId($scope.parentId);//重新加载 返回到当前页面
					$scope.selectIds=[];
				}						
			}		
		);				
	}

	//$scope.a =0;
	//自定义删除
    $scope.dele2=function(){
       //去根据id区查询
		var sele = $scope.selectIds;
		for (var i = 0;i <sele.length;i++){
            itemCatService.findByParentId(sele[i]).success(
                function(response){
                	if (response.length>0) {
                        /*$scope.a=response.length;
                        alert($scope.a);*/
                        $scope.selectIds=[];
					}

                }
            );
        }
        /*alert($scope.a);*/
       /* if (a == false) {
            window.location.reload();
            alert("你要删除的选项有儿子,请先删除!!!!");
            return false;
		}else{
            $scope.dele();
		}*/
    }

	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//根据parentId获取商品分类
    $scope.findByParentId=function(parentId){
		//查询时记录上级id  用于增加分类
		$scope.parentId=parentId;

        itemCatService.findByParentId(parentId).success(
            function(response){
                $scope.list= response;
            }
        );
    }

    //这只一个级别变量
	$scope.grade=1;//默认为1

	//每次点击查看下一级时 +1
	$scope.setGrade=function(value){
        $scope.grade=value;
	}

	//根据级别的来显示导航
	$scope.selectList=function (p_entity) {
		if ($scope.grade==1){
			$scope.entity_2=null;
			$scope.entity_3=null;
		}
		if ($scope.grade==2){
            $scope.entity_2=p_entity;
            $scope.entity_3=null;
		}
        if ($scope.grade==3){
            $scope.entity_3=p_entity;
        }

        $scope.findByParentId(p_entity.id);	//查询此级下级列表

    }
    
    //回现全部模板类型
	$scope.findTypeList=function () {
        typeTemplateService.findAll().success(
        	function (response) {
                $scope.typeList = response;
            }
		)
    }

    //记录上级id
	$scope.parentId=0;



});	