window.tabs = {
    add: function (obj) {
        var id = "tab_" + obj.id;
        $(".active").removeClass("active");
        var isExist = $("#" + id)[0];
        //如果TAB不存在，创建一个新的TAB
        if (!isExist || obj.isNew) {
            if(isExist){
                this.close(id);
            }
            //固定TAB中IFRAME高度
            var mainHeight = $(document.body).height() - 95;
            //创建新TAB的title
            var title = '<li role="presentation" id="tab_' + id + '"><a href="#' + id + '"aria-controls="' + id + '"role="tab" data-toggle="tab">' + obj.title;
            //是否允许关闭
            if (obj.close) {
                title += ' <i class="close-tab glyphicon glyphicon-remove" tabclose="' + id + '"></i>';
            }
            title += '</a></li>';
            //是否指定TAB内容
            var content = "";
            if (obj.content) {
                content = '<div role="tabpanel" class="tab-pane" id="' + id + '">' + obj.content + '</div>';
            } else {//没有内容，使用IFRAME打开链接
                content = '<div role="tabpanel" class="tab-pane" id="' + id + '"><iframe name="frame_' + id + '" src="' + obj.url + '"width="100%" height="' + mainHeight +
                    '"frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="yes" allowtransparency="yes"></iframe></div>';
            }
            //加入TABS
            $(".nav-tabs").append(title).on("click", "[tabclose]", function (e) {
                var id = $(this).attr("tabclose");
                tabs.close(id);
            });
            $(".tab-content").append(content);
        }
        //激活TAB
        $("#tab_" + id).addClass('active').attr("data-id",obj.id);
        $("#" + id).addClass("active").attr("data-id",obj.id);
    },
    close: function (id) {
        //如果关闭的是当前激活的TAB，激活他的前一个TAB
        if ($("li.active").attr('id') == "tab_" + id) {
            $("#tab_" + id).prev().addClass('active');
            $("#" + id).prev().addClass('active');
        }
        //关闭TAB
        $("#tab_" + id).remove();
        $("#" + id).remove();
    }
}