//服务层
app.service('loginService',function($http){

    //获取登录人的名字
    this.login=function(){
        return $http.get('../login/name.do');
    }

});