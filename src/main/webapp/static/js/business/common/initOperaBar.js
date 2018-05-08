/**
 * 页面公共方法及初始化
 * liuhuiqing
 */
$(document).ready(function () {
    var barListUnique = $(".barListUnique").attr("v");
    handler.initOperaBar({pageId: barListUnique + "ListBarDiv"});
});
