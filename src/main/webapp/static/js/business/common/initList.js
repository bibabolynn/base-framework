/**
 * 页面公共方法及初始化
 * liuhuiqing
 */
$(document).ready(function () {
    var listUnique = $(".listUnique").attr("v");
    if(listUnique){
        handler.initList({pageId:listUnique+"ListDiv",dbclick:true});
    }else{
        console.log("没有找到样式pageUnique的v属性!");
    }

});
