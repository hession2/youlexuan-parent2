app.controller('indexController' ,function($scope,$controller   ,loginService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.login=function(){
        loginService.login().success(
            function(response){
                $scope.loginName=response.loginName;
                $scope.lastTime=response.lastTime;
            }
        );
    }

});