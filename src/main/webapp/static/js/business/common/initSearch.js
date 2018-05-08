/**
 * 页面公共方法及初始化
 * liuhuiqing
 */
$(document).ready(function () {
    var searchUnique = $(".searchUnique").attr("v");
    if (searchUnique) {
        handler.initSearch({pageId: searchUnique + "SearchDiv", dbclick: true});
    } else {
        console.log("没有找到样式searchUnique的v属性!");
    }

});
