/**
 * 页面公共方法及初始化
 * liuhuiqing
 */
$(document).ready(function () {
    // 防拷贝
    system.noCopy();

    // 键盘操作特殊处理
    system.keydownEvent();

    // 控制文本框变色样式
//    system.chgInputStyle();

    // 列表样式切换
    // tableListStyle();

    // 增加提交等待效果
    system.preSubmitForm();

    // 为必填写添加星号
//    system.addStar();

    // 校验
});

(function ($ns) {
    /**
     * 为必填写添加星号
     */
    $ns.addStar = function () {
        $(".required").each(function () {
            $th = $(this).parent().prev();
            if (!$th.text().startWith("\\*")) {
                $th.prepend("<span class=\"red\">* </span>");
            }
        });
    };

    /**
     * 防拷贝
     */
    $ns.noCopy = function () {
        $(".nocopy").each(function (i) {
            $(this).css({
                color: "darkgray"
            });
            $(this).bind("selectstart", function () {
                event.returnValue = false;
            });
        });
    };

    /**
     * 回车自动跳转聚焦，backspace失效处理
     */

    $ns.keydownEvent = function () {
        $ns.enterEvent();
        $(document).bind("keydown", function (event) {
            $ns.backspaceForbiden(event)
        });
    };

    /**
     * 回车键特殊处理
     */
    $ns.enterEvent = function () {
        var $input = $("input,select");
        $input.on("keypress", function (e) {
            var keyCode = $ns.getEventKeyCode(e);
            if (keyCode == 13 && this.form) {
                for (var i = 0; i < this.form.elements.length; i++) {
                    if (this == this.form.elements[i]) break;
                }
                i = (i + 1) % this.form.elements.length;
                this.form.elements[i].focus();
                return false;
            } else {
                return true;
            }
        });
    };

    /**
     * backspace键特殊处理
     * @return
     */
    $ns.backspaceForbiden = function (event) {
        var keyCode = $ns.getEventKeyCode(event);
        if (keyCode == 8) {//判断按键为backSpace键
            //获取按键按下时光标做指向的element
            var elem = event.srcElement || event.target;
            //判断是否需要阻止按下键盘的事件默认传递
            var name = elem.nodeName;
            if (name != 'INPUT' && name != 'TEXTAREA') {
                return _stopIt(event);
            }
            var type_e = elem.type.toUpperCase();
            if (name == 'INPUT' && (type_e != 'TEXT' && type_e != 'TEXTAREA' && type_e != 'PASSWORD' && type_e != 'FILE')) {
                return _stopIt(event);
            }
            if (name == 'INPUT' && (elem.readOnly == true || elem.disabled == true)) {
                return _stopIt(event);
            }
        }

        function _stopIt(e) {
            if (e.returnValue) {
                e.returnValue = false;
            }
            if (e.preventDefault) {
                e.preventDefault();
            }

            return false;
        };
    };

    /**
     * 获得事件源的keycode
     * @param event
     * @returns {Number}
     */
    $ns.getEventKeyCode = function (event) {
        return event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    };

    /**
     * 表格滚动条自适应
     */
    $ns.tableAutosize = function () {
        $(".tableAutosize table").each(
            function () {
                $(this).parent().width($(this).parent().prev().width())
                    .css("margin", "0 auto");
                $(this).width($(this).parent().width()).css("margin",
                    "0 auto");
            });
    };

    /**
     * 主按钮鼠标经过样式
     */
    $ns.btnHover = function () {
        $(".b_foot").hover(function () {
            this.className = "b_foot_hover";
        }, function () {
            this.className = "b_foot";
        });
        $(".b_foot_long").hover(function () {
            this.className = "b_foot_long_hover";
        }, function () {
            this.className = "b_foot_long";
        });
    };

    /**
     * 增加提交表单前处理动作
     */
    $ns.preSubmitForm = function () {
        var formNum = document.forms.length;
        for (var i = 0; i < formNum; i++) {
            var form = document.forms[i];
            if (form) {
                form.oldSubmit = form.submit;
                form.submit = function (isDisplayLoading) {
                    if (typeof(isDisplayLoading) == "undefined" || isDisplayLoading) {
                        $ns.loading();
                    }
                    form.oldSubmit();
                };
            }
        }
    };

    /**
     * 填出确认对话框
     * @param message
     * @param callback
     */
    $ns.confirm = function (message, callback) {
        if (typeof message === 'function') {
            callback = message;
            message = "";
        }
        if (!message) {
            message = "确定要进行该操作！";
        }
        $ns.open({
            title: "确 认",
            content: message,
            type: '11',
            width: 0,
            height: 30,
            isClose: true,
            callback: callback
        });
    };

    /**
     * 提示框
     * @param message
     * @param callback
     */
    $ns.alert = function (message, callback) {
        if (typeof message === 'function') {
            callback = message;
            message = "";
        }
        if (!message) {
            message = "提示信息！";
        }
        $ns.open({
            title: "提 示",
            content: message,
            type: '12',
            width: 0,
            height: 0,
            isClose: true,
            callback: callback
        });
    };

    /**
     * 提示框
     * @param message
     * @param callback
     */
    $ns.alertTop = function (message, callback) {
        if (typeof message === 'function') {
            callback = message;
            message = "";
        }
        if (!message) {
            message = "提示信息！";
        }
        $ns.open({
            title: "提 示",
            content: message,
            type: '15',
            width: 0,
            height: 0,
            isClose: true,
            callback: callback
        });
    };

    /**
     * 确认输入框
     * @param message
     * @param callback
     */
    $ns.prompt = function (message, callback) {
        if (typeof message === 'function') {
            callback = message;
            message = "";
        }
        if (!message) {
            message = "输入信息";
        }
        var $message = $("<div align='center'/>");
        var id = new Date().getTime();
        $message.append(message + "：<p><textarea id='" + id + "' rows='5' cols='40'/></p>");
        $ns.open({
            title: "输 入",
            content: $message,
            type: '11',
            width: 0,
            height: 0,
            isClose: true,
            callback: function () {
                callback($message.find("#" + id).val());
            }
        });
    };

    /**
     * 弹出层
     * @param title
     * @param content
     * @param callback
     * @param width
     * @param height
     */
    $ns.openContent = function (title, content, callback, width, height) {
        if (typeof content === 'function') {
            height = width;
            width = callback;
            callback = content;
            content = title;
            title = "新对话框";
        }
        if (!content) {
            $ns.alert("调用方法openContent(title,content,callback,width,height)缺少参数！");
            return;
        }
        $ns.open({
            title: title,
            content: content,
            type: '11',
            width: width > 149 ? width : 800,
            height: height > 99 ? height : 400,
            callback: callback
        });
    };

    /**
     * 弹出层
     * @param paramsObj
     */
    $ns.openContentByObject = function (paramsObj) {
        $ns.open(paramsObj);
    }

    /**
     * 根据url内容弹出层
     * @param title
     * @param url
     * @param callback
     * @param width
     * @param height
     */
    $ns.openUrl = function (title, url, callback, width, height) {
        if (typeof url === 'function') {
            height = width;
            width = callback;
            callback = url;
            url = title;
            title = "新对话框";
        }
        if (!url) {
            $ns.alert("调用方法openUrl(title,url,callback)缺少参数！");
            return;
        }
        $ns.open({
            title: title,
            content: url,
            type: '21',
            width: width > 149 ? width : 800,
            height: height > 99 ? height : 400,
            callback: callback
        });
    };

    /**
     * 根据url内容弹出层
     * @param paramsObj
     */
    $ns.openUrlByObject = function (paramsObj) {
        paramsObj.type = "21";
        $ns.open(paramsObj);
    }

    /**
     * 根据url内容弹出层
     * @param paramsObj
     * 没有按钮 top为10
     */
    $ns.openUrlByCont = function (paramsObj) {
        paramsObj.type = "23";
        $ns.open(paramsObj);
    }

    $ns.openUrlByDialog = function (paramsObj) {
        paramsObj.type = "24";
        $ns.open(paramsObj);
    }
    /**
     *根据url内容弹出层(frame)
     * @param title
     * @param url
     * @param callback
     * @param width
     * @param height
     */
    $ns.openFrame = function (title, url, callback, width, height) {
        if (typeof url === 'function') {
            height = width;
            width = callback;
            callback = url;
            url = title;
            title = "新对话框";
        }
        if (!url) {
            $ns.alert("调用方法openUrl(title,url,callback)缺少参数！");
            return;
        }
        $ns.open({
            title: title,
            content: url,
            type: '31',
            width: width > 149 ? width : 800,
            height: height > 99 ? height : 400,
            callback: callback
        });
    };

    /**
     * 根据url内容弹出层(frame)
     * @param paramsObj
     */
    $ns.openFrameByObject = function (paramsObj) {
        paramsObj.type = "31";
        $ns.open(paramsObj);
    }

    /**
     *页面弹出提示框
     * @param paramsObj 对象属性有：
     *  title
     *  msg
     *  width
     *  height
     *  timeout 显示时间，为0时，始终显示
     *  soundSrc 提示声音文件路径 为true或default时，使用默认声音
     *  position 显示位置:topLeft,topCenter,topRight,centerLeft,center,centerRight,bottomLeft,bottomCenter,bottomRight
     */
    $ns.openTip = function (paramsObj) {
        paramsObj = initTipParam(paramsObj);
        var positionObj = {};
        try {
            if (typeof eval(paramsObj.position) === 'function') {
                positionObj = eval(paramsObj.position + '()');
            }
        } catch (e) {
            positionObj = bottomRight();
        } finally {
            positionObj.height = paramsObj.height;
            positionObj.msg = paramsObj.msg;
            positionObj.timeout = paramsObj.timeout;
            positionObj.title = paramsObj.title;
            positionObj.width = paramsObj.width;
        }
        $.messager.show(positionObj);
        if (paramsObj.soundSrc) {
            $ns.playSound(paramsObj.soundSrc);
        }
    };

    /**
     * 使用bootbox插件弹出对话框(只有关闭按钮)
     * @param paramsObj
     */
    $ns.openViewDialog = function (paramsObj) {
        paramsObj = $ns.initOpenParam(paramsObj);
        var $content = $(paramsObj.content);
        bootbox.dialog({
                title: paramsObj.title,
                message: $content,
                className: "dialog",
                buttons: {
                    close: {
                        label: "关 闭",
                        className: "btn-error",
                        callback: function () {
                            if (typeof paramsObj.callback === 'function') {
                                paramsObj.callback($content);
                            }
                        }
                    }
                }
            }
        );
        $(".dialog").keydown(function (event) {
            // 按 Esc
            if (event.which == 27) {
                bootbox.hideAll();
            }
        });
    };

    /**
     * 使用bootbox插件弹出对话框
     * @param paramsObj
     */
    $ns.openDialog = function (paramsObj) {
        paramsObj = $ns.initOpenParam(paramsObj);
        var $content = $(paramsObj.content);
        bootbox.dialog({
                title: paramsObj.title,
                message: $content,
                className: "dialog",
                buttons: {
                    close: {
                        label: "关 闭",
                        className: "btn-error",
                        callback: function () {
                        }
                    },
                    success: {
                        label: "确 定",
                        className: "btn-success",
                        callback: function () {
                            if (typeof paramsObj.callback === 'function') {
                                return paramsObj.callback($content);
                            }
                        }
                    }
                }
            }
        );
    };

    /**
     * 关闭页面弹出提示框
     */
    $ns.closeTip = function () {
        $(".messager-body").window('close');
    };

    /**
     * 初始化提示框信息
     * @param params
     * @returns {{title: *, msg: *, timeout: (number|*), soundSrc: *, position: *, width: (number|*), height: (number|*)}}
     */
    function initTipParam(params) {
        params = typeof(params) == 'object' ? params : {};
        return {
            title: params.title ? params.title : "提 示",
            msg: params.msg ? params.msg : "你有新消息！",
            timeout: params.timeout > 0 ? params.timeout : 0,
            showSpeed: params.showSpeed > 0 ? params.showSpeed : 500,
            soundSrc: params.soundSrc ? (params.soundSrc == true || params.soundSrc == 'default') ? "/static/themes/ui/media/order_ring.wav" : params.soundSrc : "",
            position: params.position ? params.position : "bottomRight",
            width: params.width > 50 ? params.width : 250,
            height: params.height > 50 ? params.height : 100
        };
    };

    $ns.playSound = function (src) {
        var id = src.replace(/[&\/.:]/g, "_");
        var $body = $(document.body);
        $body.find("#" + id).remove();
        // 浏览器不支持 audio(html5) 标签,则用embed标签(firefox need install plugin)。
        $body.append("<span id='" + id + "'style='display:none;'><audio src='" + src + "' autoplay=true></audio><embed src='" + src + "' hidden=true autostart=true></embed></span>");
    }

    /**
     * 弹出对话框底层方法
     * * @param paramsObj
     *  title 标题
     *  content 弹出层内容：来源取决于type
     *  type 类型，两位数字，第一位标识content取值：1 直接显示content内容 ，2 content内容为url，3 content内容为url并放在frame中，需要获取；
     *         第二位标识取值：1生成确认和取消按钮 ，2生成关闭按钮
     *  width
     *  height
     *  callback 按钮回调函数
     *  isClose 生成的确定按钮默认是否绑定关闭功能
     */
    $ns.open = function (paramsObj) {
        paramsObj = $ns.initOpenParam(paramsObj);
        $ns.createPopLayer();
        var $id = $ns.buildContent(paramsObj);
        var top = 0;
        paramsObj.width ? $id.width(paramsObj.width) : top = 180;
        if (paramsObj.height) {
            $id.height(paramsObj.height + 100);
            $id.find(".ajax_wait").height(paramsObj.height + 80);
        }
        var commonType = paramsObj.type.charAt(1);
        if (commonType == 3) {
            $ns.setLayoutTop($id, 10);
        } else if (commonType == 5) {
            $ns.setLayoutTop($id, 760);
        } else {
            $ns.setLayout($id, top);
        }
    };

    /**
     * 初始化弹出框入参
     * @param params
     * @returns {{id: *, title: *, content: *, type: *, width: *, height: *, callback: (*|callback|g._listeners.callback), isClose: boolean}}
     */
    $ns.initOpenParam = function (params) {
        params = typeof(params) == 'object' ? params : {};
        return {
            id: params.id ? params.id : new Date().getTime(),
            title: params.title ? params.title : "标 题",
            content: params.content ? params.content : "还没有内容！",
            type: params.type ? params.type : "11",
            width: params.width,
            height: params.height,
            callback: params.callback,
            isClose: params.isClose ? true : false
        };
    };

    /**
     * 生成弹出框内容并绑定事件
     * @param paramsObj
     *  title 标题
     *  content 弹出层内容：来源取决于type
     *  type 类型，两位数字，第一位标识content取值：1 直接显示content内容 ，2 content内容为url，3 content内容为url并放在frame中，需要获取；
     *         第二位标识取值：1生成确认和取消按钮 ，2生成关闭按钮
     *  callback 按钮回调函数
     *  isClose 生成的确定按钮默认是否绑定关闭功能
     * @returns {string}
     */
    $ns.buildContent = function (paramsObj) {
        var contentType = paramsObj.type.charAt(0);
        var $id = "";
        if (contentType == 2) {
            $id = buildByUrl(paramsObj);
        } else if (contentType == 3) {
            $id = buildByFrameUrl(paramsObj);
        } else {
            $id = buildByContent(paramsObj);
        }
        return $id;
    };

    /**
     * 根据url生成弹出层内容，弹出层为新页面（iframe）
     * @returns {*|jQuery|HTMLElement}
     */
    function buildByFrameUrl(paramsObj) {
        var $id = buildCommonContent(paramsObj);
        var $wait = $("<iframe align=middle marginwidth=0 marginheight=0 frameborder=no width=100% height=100%></iframe>");
        $wait.attr("src", paramsObj.content);
        appendContentToPage($id, $wait);
        return $id;
    }

    /**
     * 根据内容构建弹出层对象
     * @param paramsObj
     * @returns {*|jQuery|HTMLElement}
     */
    function buildByContent(paramsObj) {
        var $id = buildCommonContent(paramsObj);
        appendContentToPage($id, paramsObj.content);
        return $id;
    }

    /**
     * 当前页面弹出层，内容为指定url异步获取
     * @param paramsObj
     * @returns {*|jQuery|HTMLElement}
     */
    function buildByUrl(paramsObj) {
        var $id = buildCommonContent(paramsObj);
        var $wait = $("<div>正在加载，请稍候......<div class='loading_gif'></div></div>");
        $.ajax({
            url: paramsObj.content,
            cache: false,
            success: function (result) {
                if (result && result.result && result.result.retMsg) {
                    $wait.html(result.result.retMsg);
                } else {
                    $wait.html(result);
                }
            },
            error: function (jqXHR, errorThrown) {
                $wait.html("请求出现错误" + errorThrown);
            }
        });
        appendContentToPage($id, $wait);
        return $id;
    };

    /**
     * ajax加载等待提示层构建
     * @param paramsObj
     *  title
     *  commonType
     *  callback
     *  id
     * @returns {*|jQuery|HTMLElement}
     */
    function buildForLoading(paramsObj) {
        var $id = buildCommonContent(paramsObj);
        var $wait = $("<div>正在加载，请稍候......<div class='loading_gif'></div></div>");
        appendContentToPage($id, $wait);
        return $id;
    };

    function appendContentToPage($id, obj) {
        return $id.find(".ajax_wait").prepend(obj);
    }

    /**
     * * 构建弹出层基础架构
     * @param paramsObj
     *  title 标题
     *  commonType 公共组件（是否带有确定，关闭等按钮），取值参照buildContent方法注释
     *  callback 公共组件回调函数
     *  isClose 公共组件中点击确定按钮是否直接关闭组件
     *  id 生成层id属性
     * @returns {*|jQuery|HTMLElement}
     */
    function buildCommonContent(paramsObj) {
        var id = paramsObj.id ? paramsObj.id : new Date().getTime();
        var confirmId = "confirm" + id;
        var cancelId = "cancelId" + id;
        var commonType = paramsObj.type.charAt(1);
        var commonContent = "";
        var $title = $("<h2>" + paramsObj.title + "<a href='#' class='close" + id + "'>关 闭</a></h2>");
        if (commonType == 1) {
            commonContent = "<div class='dialog-foot'>" +
                "<button id='" + confirmId + "' class='button bg-aqua mr10'>" +
                "确 定</button><button id='" + cancelId + "' class='button mr10'>" +
                "取 消</button></div>";
        } else if (commonType == 2 || commonType == 5) {
            commonContent = "<p><input type='button' id='" +
                cancelId +
                "' class='button' value='关 闭'/></div>";
        }
        var $id = $("<div id='" +
            id +
            "' class='ajax_window'>" +
            "<div class='ajax_wait'>" +
            commonContent +
            "</div>" +
            "</div>");
        $id.prepend($title);
        movePage($title);
        $id.find(".close" + id).click(function () {
            $ns.unLoading($id)
        });
        $id.find("[id$='" + id + "']").click(function () {
            if ($(this).attr("id") == confirmId) {
                if (typeof paramsObj.callback === 'function') {
                    paramsObj.callback($id);
                }
                paramsObj.isClose ? $ns.unLoading($id) : "";
                return true;
            } else {
                if ($("#" + confirmId).length == 0 && typeof paramsObj.callback === 'function') {
                    paramsObj.callback($id);
                }
                $ns.unLoading($id);
                return false;
            }
        });
        return $id;
    };

    /**
     * 点击弹出层title可以移动
     * @param $title
     */
    function movePage($title) {
        var $id = $title.parent();
        $id.css({'position': 'absolute'});
        $title.hover(function () {
            $(this).css("cursor", "move");
        }, function () {
            $(this).css("cursor", "default");
        })
        var maxX = $(document).width() - 40;
        var maxY = $(document).height() - 40;
        $title.mousedown(function (e) {
            var offset = $(this).offset();
            var x = e.pageX - offset.left;
            var y = e.pageY - offset.top;
            $id.css({'opacity': '0.9'});
            //绑定鼠标的移动事件，因为光标在DIV元素外面也要有效果，所以要用document的事件，而不用DIV元素的事件
            $(document).bind("mousemove", function (ev) {
                $title.bind('selectstart', function () {
                    return false;
                });
                //获得X轴方向移动的值
                var _x = ev.pageX - x;
                var _y = ev.pageY - y;
                _x = _x > 0 ? _x > maxX ? maxX : _x : 0;
                _y = _y > 0 ? _y > maxY ? maxY : _y : 0;
                $id.css({'left': _x + "px", 'top': _y + "px"});
                return false;
            });
        });

        $(document).mouseup(function () {
            $(this).unbind("mousemove");
            $id.css({'opacity': ''});
        })
    };

    /**
     * 同步获得指定url资源
     * @param url
     * @returns {string|xhr.responseText|*|responseText}
     */
    $ns.ajaxGetSync = function (url) {
        return $.ajax({
            url: url,
            async: false
        }).responseText;
    };

    /**
     * 有选择的加载页面内容
     * @param id 将请求结果插入到页面的位置，id或jquery对象
     * @param url 目标url和jquery选择器的字符串组合，例如："/myurl #myPageId"
     * @param data 请求入参key/value
     * @param callback 请求成功回调函数，函数入参是整个html响应
     * @returns {boolean}
     */
    $ns.ajaxLoad = function (id, url, data, callback) {
        var $id = paramJquery(id);
        if (!$id) {
            return false;
        }
        $id.load(url, data, callback);
    };

    /**
     * 异步获得指定url资源
     * @param url
     * @param callback
     */
    $ns.ajaxGetAsync = function (url, callback) {
        var id = new Date().getTime();
        $ns.loading(id);
        $.ajax({
            url: url,
            cache: false,
            success: function (result) {
                callback(result);
            },
            error: function (jqXHR, errorThrown) {
                $ns.alert("请求出现错误" + errorThrown);
            },
            complete: function (XMLHttpRequest, textStatus) {
                $ns.unLoading(id);
            }
        });
    };

    /**
     * 异步post请求，用于表单提交
     * @param url
     * @param data
     * @param callback
     */
    $ns.ajaxPostAsync = function (url, data, callback) {
        var id = new Date().getTime();
        $ns.loading(id);
        $.ajax({
            type: "POST",
            url: url,
            data: data,
            success: function (result) {
                callback(result);
            },
            error: function (jqXHR, errorThrown) {
                $ns.alert("请求出现错误" + errorThrown);
            },
            complete: function (XMLHttpRequest, textStatus) {
                $ns.unLoading(id);
            }
        });
    };

    /**
     * 表单提交ajax效果
     */
    $ns.loading = function (id) {
        $ns.createPopLayer();
        id = id ? id : "ajaxLoadingDiv";
        var $id = buildForLoading({
            id: id,
            title: "提 示",
            type: "10"
        });
        $ns.setLayout($id, 180);
    };

    /**
     * 取消ajax等待效果
     * @param id
     * @returns {boolean}
     */
    $ns.unLoading = function (id) {
        var $id = paramJquery(id);
        if (!$id) {
            return false;
        }
        var $popLayer = $(".pop_layer");
        $id.remove();
        var data_param = $popLayer.attr("data_param") - 1;
        data_param = data_param < 0 ? 0 : data_param;
        $popLayer.attr("data_param", data_param);
        if (data_param == 0) {
            $popLayer.css({display: "none"});
        }
    };

    /**
     * 绑定关闭弹出框事件
     * @param srcId 要绑定事件弹出框的id或jquery对象
     * @param desId 要绑定事件的元素id或jquery对象
     * @param type 要绑定的事件类型：click,change,mouseover等
     * @returns {boolean}
     */
    $ns.bindClose = function (srcId, desId, type) {
        var $srcId = paramJquery(srcId);
        var $desId = paramJquery(desId);
        if (!$srcId || !$desId) {
            return false;
        }
        type = type ? type : "click";
        $desId.bind(type, function () {
            var id = $srcId.attr("id");
            $(".close" + id).click();
        });
    };

    /**
     * 关闭指定弹出框
     * @param id 要关闭弹出框id或jquery对象
     */
    $ns.close = function (id) {
        var $id = paramJquery(id);
        if (!$id) {
            return false;
        }
        var myId = $id.attr("id");
        $(".close" + myId).click();
    };

    /**
     * 设置元素页面位置
     * @param id
     * @param top
     * @returns {boolean}
     */
    $ns.setLayout = function (id, top) {
        var $id = paramJquery(id);
        if (!$id) {
            return false;
        }
        $(document.body).append($id);
        var h = $ns.getScrollTop();
        var w1 = $ns.getScrollLeft();
        top = top > 0 ? top : ($ns.getClientHeight() - $id.height()) / 2;
        top = top > 50 ? top : 50;
        var left = ($ns.getClientWidth() - $id.width()) / 2;
        left = left > 0 ? left : 0;
        $id.css("top", h + top);
        $id.css("left", w1 + left);
    };

    /**
     * IM设置元素页面位置
     * @param id
     * @param top 0
     * @returns {boolean}
     */
    $ns.setLayoutTop = function (id, top) {
        var $id = paramJquery(id);
        if (!$id) {
            return false;
        }
        $(document.body).append($id);
        var h = $ns.getScrollTop();
        var w1 = $ns.getScrollLeft();

        var left = ($ns.getClientWidth() - $id.width()) / 2;
        left = left > 0 ? left : 0;
        $id.css("top", top);
        $id.css("left", w1 + left);
    };

    $ns.createPopLayer = function () {
        /**
         * 遮罩层
         * @param id
         */
        var $popLayer = $(".pop_layer");
        if ($popLayer.length == 0) {
            $popLayer = $("<div class='pop_layer' data_param='1'></div>");
            $(document.body).append($popLayer);
        } else {
            var data_param = $popLayer.attr("data_param") - 0 + 1
            $popLayer.attr("data_param", data_param);
        }
        $popLayer.css({display: "block", height: $(document).height()});
    };

    /**
     * 控制文本框变色样式
     */
    $ns.chgInputStyle = function () {
        var $input = $("input:text,input:password,textarea");
        $input.on("focus", function () {
            $(this).addClass("input_high");
        }).on("blur", function () {
            $(this).removeClass("input_high");
        });
    };

    // 取窗口滚动条高度
    $ns.getScrollTop = function () {
        var scrollTop = 0;
        if (document.documentElement && document.documentElement.scrollTop) {
            scrollTop = document.documentElement.scrollTop;
        } else if (document.body) {
            scrollTop = document.body.scrollTop;
        }
        return scrollTop;
    };

    //取窗口滚动条宽度
    $ns.getScrollLeft = function () {
        var scrollLeft = 0;
        if (document.documentElement && document.documentElement.scrollLeft) {
            scrollLeft = document.documentElement.scrollLeft;
        } else if (document.body) {
            scrollLeft = document.body.scrollLeft;
        }
        return scrollLeft;
    };

    //取窗口可视范围的宽度
    $ns.getClientWidth = function () {
        var clientWidth = 0;
        var bodyWidth = document.body.clientWidth;
        var documentWidth = document.documentElement.clientWidth;
        if (bodyWidth && documentWidth) {
            clientWidth = (bodyWidth < documentWidth) ? bodyWidth : documentWidth;
        } else {
            clientWidth = (bodyWidth > documentWidth) ? bodyWidth : documentWidth;
        }
        return clientWidth;
    };
    //取窗口可视范围的高度
    $ns.getClientHeight = function () {
        var clientHeight = $(window).height();
        return clientHeight;
    };

    /**
     * 列表样式切换
     */
    $ns.tableListStyle = function () {
        var count = 0;
        $(".list tr:gt(0)").each(function () {
            var c = count++ % 2 == 0 ? "tr_pink" : "tr_green";
            $(this).addClass(c);
        });
    };

    /**
     * 将指定分页标签下的链接转换为异步ajax访问方式
     * @param pageId
     */
    $ns.visitToAjax = function (pageId, tar) {
        var $pageId = paramJquery(pageId);
        var $tar = tar || $pageId.parent();
        $pageId.find(".pageCssDefault a").each(function () {
            var url = $(this).attr("href");
            $(this).attr("href", "javascript:void(0);");
            $(this).bind("click", function () {
                $ns.refreshPageAjax($tar, url);
            });
        });
    };

    /**
     * 局部ajax请求
     * @param pageId 要刷新的页面id
     * @param url
     */
    $ns.refreshPageAjax = function (pageId, url, param, callback) {
        var $pageId = paramJquery(pageId);
        $ns.ajaxPostAsync(url, param, function (result) {
            $pageId.fadeTo(1,0).html(result).fadeTo(600,1);
            if (callback) callback();
            //$ns.parentFrameResize();
        });
    };

    /**
     * easyUI中tab页面高亮选中
     * @param tabId tab页id或jquery对象
     * @param index tab页编码下标数（从0开始计数）或id
     * @param callback 选中后回调函数，回调函数的参数是选中的tab页的jquery对象
     */
    $ns.easyUITabSelect = function (tabId, index, callback) {
        var $tabId = paramJquery(tabId);
        if (checkEasyUITab($tabId)) {
            var tabs = $tabId.tabs('tabs');
            var num = tabs.length;
            if (typeof index != "number") {
                for (var i = 0; i < num; i++) {
                    var tab = tabs[i];
                    if (tab.panel("options").id == index) {
                        index = $tabId.tabs('getTabIndex', tab);
                    }
                }
            }
            index = index < 0 || index > (num - 1) ? 0 : index;
            $tabId.tabs('select', index);
            if (typeof callback === 'function') {
                callback();
            }
        }
    };

    /**
     * 获得指定tabId下选中的tab页的id值
     * @param tabId
     * @returns {*}
     */
    $ns.easyUITabSelected = function (tabId) {
        var $tabId = paramJquery(tabId);
        if (checkEasyUITab($tabId)) {
            return $tabId.tabs("getSelected").panel("options").id;
        }
    };

    /**
     *
     * @param tabId
     * @param callback
     */
    $ns.easyUITabOnSelect = function (tabId, callback) {
        var $tabId = paramJquery(tabId);
        if (checkEasyUITab($tabId)) {
            $tabId.tabs({
                onSelect: function (title) {
                    if (typeof callback === 'function') {
                        callback(title);
                    } else {
                        var act = $(title).attr("act");
                        var tar = $(title).attr("tar");
                        if (act) {
                            tar = tar ? tar : act;
                            $ns.refreshPageAjax(tar, act);
                        }
                    }
                }
            });
        }
    };

    /**
     * 递归往上查找符合条件的jquery对象
     * @param root 开始查找的第一层元素id或jquery对象
     * @param obj 目标对象jquery选择器字符串
     * @param maxLevel 最多往上查找几层
     */
    $ns.getParentJqueryObj = function (root, obj, maxLevel) {
        var $root = paramJquery(root);
        maxLevel = maxLevel > 0 ? maxLevel : 10;
        var $curr = $root.find(obj);
        if (($curr && $curr.length > 0) || maxLevel < 1) {
            return $curr;
        }
        return $ns.getParentJqueryObj($root.parent(), obj, --maxLevel);
    };


    /**
     * tab选项卡
     * @param obj obj
     * @param select 默认选中
     * @param trigger 切换绑定方法
     */
    $ns.tabs = function (id) {
        $ns.tabsCommon = new $ns.TabModule(id);
    }
    $ns.select = function (tar, act) {
        $ns.tabsCommon.selectOne(tar, act);
    }
    $ns.selectOne = function () {
    };
    $ns.TabModule = function (id) {
        this.oTab = paramJquery(id);
        this.toggle = this.oTab.attr("toggle") || "click";
        this.navs = this.oTab.find(".tab-nav>li");
        this.bindEvent();
    };
    $ns.TabModule.prototype.bindEvent = function () {
        var _this = this;
        this.navs.each(function () {
            $(this).unbind(_this.toggle).bind(_this.toggle, function () {
                var tar = $(this).find("a").attr("tar");
                var act = $(this).find("a").attr("act");
                _this.selectOne(tar, act);
                _this.oTab.attr("select", tar);
                $("#index").val(tar);
            });
        });
    };
    $ns.TabModule.prototype.selectOne = function (tar, act) {
        var menuObj = this.oTab.find("a[tar=" + tar + "]").parent();
        var tarObj = this.oTab.find("[tar-id=" + tar + "]");
        if (menuObj.hasClass("active")) return;
        menuObj.addClass("active").siblings().removeClass("active");
        if (act) {
            system.refreshPageAjax(tarObj, act);
        }
        this.oTab.find("[tar-id=" + tar + "]").show().siblings().hide();
        $ns.selectOne(tar);
    };


    system.datePicker = function () {
        $(".datePicker").off().on("change", function () {
            var index = parseInt($(this).attr("datePickerNum"), 10);
            var end = $(".datePicker").size();
            var next = $(".datePicker[datePickerNum='" + (index + 1) + "']");
            var min = parseInt($(this).val().replace(":", ""), 10);
            for (var i = index; i < end; i++) {
                $(".datePicker[datePickerNum='" + (i + 1) + "']").find("option[value='']").attr("selected", true);
            }
            next.find("option").each(function () {
                var cur = parseInt($(this).val().replace(":", ""), 10);
                if (cur <= min) {
                    $(this).hide();
                } else {
                    $(this).show();
                }
            });
            return false;
        });
    }


    /**
     * 公共方法
     * @param param
     * @returns {*}
     */

    $ns.getCh = function () {
        return $(window).height();
    };
    $ns.getCw = function () {
        return $(window).width();
    };


    /**
     * 全选按钮绑定事件
     * @param param
     * @returns {*}
     */
    $ns.allCheckedBindEvent = function () {
        $(".all-check").off().on("click", function () {
            $(".table input[type='checkbox']").attr("checked", $(this).is(":checked"));
        });
        $(".table input[type='checkbox']").off().on("click", function () {
            var isAllChecked = true;
            $(".table input[type='checkbox']").each(function () {
                if (!$(this).attr("checked")) {
                    isAllChecked = false;
                    return false;
                }
            });

            isAllChecked ? $(".all-check").attr("checked", true) : $(".all-check").attr("checked", false);
        });
    };


    /**
     * 函数参数初始化（对象和id兼容初始化）
     * @param param
     * @returns {*}
     */
    function paramJquery(param) {
        var $param;
        if (typeof(param) == 'object') {
            $param = param;
        } else {
            $param = $("#" + param);
        }
        return $param;
    }

    $ns.paramJquery = function (param) {
        var $param;
        if (typeof(param) == 'object') {
            $param = param;
        } else {
            $param = $("#" + param);
        }
        return $param;
    }

    function topLeft() {
        return {
            showType: 'show',
            style: {
                right: '',
                left: 0,
                top: document.body.scrollTop + document.documentElement.scrollTop,
                bottom: ''
            }
        };
    }

    function topCenter() {
        return {
            showType: 'slide',
            style: {
                right: '',
                top: document.body.scrollTop + document.documentElement.scrollTop,
                bottom: ''
            }
        };
    }

    function topRight() {
        return {
            showType: 'show',
            style: {
                left: '',
                right: 0,
                top: document.body.scrollTop + document.documentElement.scrollTop,
                bottom: ''
            }
        };
    }

    function centerLeft() {
        return {
            showType: 'fade',
            style: {
                left: 0,
                right: '',
                bottom: ''
            }
        };
    }

    function center() {
        return {
            showType: 'fade',
            style: {
                right: '',
                bottom: ''
            }
        };
    }

    function centerRight() {
        return {
            showType: 'fade',
            style: {
                left: '',
                right: 0,
                bottom: ''
            }
        };
    }

    function bottomLeft() {
        return {
            showType: 'show',
            style: {
                left: 0,
                right: '',
                top: '',
                bottom: -document.body.scrollTop - document.documentElement.scrollTop
            }
        };
    }

    function bottomCenter() {
        return {
            showType: 'slide',
            style: {
                right: '',
                top: '',
                bottom: -document.body.scrollTop - document.documentElement.scrollTop
            }
        };
    }

    function bottomRight() {
        return {
            showType: 'slide'
        };
    }

})(using("system"));

/**
 * 函数命名空间定义
 * @param nameSpace
 * @param noCheckName
 * @return
 */
function using(nameSpace, noCheckName) {
    nameSpace = (nameSpace || '').split(/\s*\.\s*/g);
    var m = window, d, ns;
    for (var i = 0, l = nameSpace.length; i < l; i++) {
        ns = nameSpace[i];
        if (d) d += '.' + ns;
        else d = ns;
        if (!m[ns]) m[ns] = {};
        if (noCheckName !== true && !m[ns].$name) m[ns].$name = d;
        m = m[ns];
    }
    return m;
}


/**
 * 扩展字符串对象的startWith方法
 * @param str
 * @return
 */
String.prototype.startWith = function (str) {
    var reg = new RegExp("^" + str);
    return reg.test(this);
};

/**
 * 扩展字符串对象的endWith方法
 * @param str
 * @return
 */
String.prototype.endWith = function (str) {
    var reg = new RegExp(str + "$");
    return reg.test(this);
};

/**
 * 扩展jquery，实现div拖拽
 */
(function ($) {
    //拖拽插件,参数:id或object
    $.move = function (_this) {
        if (typeof(_this) == 'object') {
            _this = _this;
        } else {
            _this = $("#" + _this);
        }
        if (!_this) {
            return false;
        }

        _this.css({'position': 'absolute'}).hover(function () {
            $(this).css("cursor", "move");
        }, function () {
            $(this).css("cursor", "default");
        })
        _this.mousedown(function (e) {
            var offset = $(this).offset();
            var x = e.pageX - offset.left;
            var y = e.pageY - offset.top;
            _this.css({'opacity': '0.7'});
            //绑定鼠标的移动事件，因为光标在DIV元素外面也要有效果，所以要用doucment的事件，而不用DIV元素的事件
            $(document).bind("mousemove", function (ev) {
                _this.bind('selectstart', function () {
                    return false;
                });
                if ($.browser.msie && ( !document.documentMode || document.documentMode < 9 ) && !ev.button) {
                    //解决ie点击滚动条不触发mouseup事件问题
                    return _this.mouseup(ev);
                }
                //获得X轴方向移动的值
                var _x = ev.pageX - x;
                var _y = ev.pageY - y;
                _this.css({'left': _x + "px", 'top': _y + "px"});
                return false;
            });
        });

        $(document).mouseup(function () {
            $(this).unbind("mousemove");
            _this.css({'opacity': ''});
        })
    };

    //绑定选择事件
    system.allCheckedBindEvent();

})(jQuery);