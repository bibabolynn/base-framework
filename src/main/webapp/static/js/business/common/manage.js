/**
 * 页面操作通用处理方法的封装
 * create by liuhuiqing
 */
(function ($ns) {
    $ns.initList = function (params, extEvent) {
        system.visitToAjax(params.pageId);
        $ns.bindListEvent(params);
        if (typeof(eval(extEvent)) == "function") {
            extEvent(params);
        }
    };
    $ns.initSearch = function (params, extEvent) {
        $ns.bindSearchEvent(params);
        if (typeof(eval(extEvent)) == "function") {
            extEvent(params);
        }
    };

    $ns.initOperaBar = function (params, extEvent) {
        $ns.refreshOperaBar(params);
        $ns.bindOperaBarEvent(params);
        if (typeof(eval(extEvent)) == "function") {
            extEvent(params);
        }
    };

    $ns.bindSearchEvent = function (params) {
        var pageId = params.pageId;
        var $searchPage = $("#" + pageId);
        var key = $searchPage.attr("v");
        //绑定新增处理事件
        $searchPage.find(".find").unbind("click").bind("click", function () {
            var $searchList = $searchPage.find("#searchList");
            if ($searchList.length < 1) {
                return bootbox.alert("页面" + pageId + "中没有找到列表id=searchList的元素");
            }
            var searchFormData = $searchPage.find("#" + key + "SearchForm").serialize();
            // 分页入参
            var pageNo = $(".current").data("pageNo");
            if(pageNo){
                searchFormData = searchFormData + "&pageNo=" + pageNo;
            }
            var pageSize = $searchPage.find("#pageSize").val();
            if (pageSize && pageSize > 0) {
                searchFormData = searchFormData + "&pageSize=" + pageSize;
            }
            // 排序入参
            var sort = "DESC";
            var $sortClass = $searchPage.find(".glyphicon-arrow-down");
            if ($sortClass.length < 1) {
                $sortClass = $searchPage.find(".glyphicon-arrow-up");
                sort = "ASC";
            }
            if ($sortClass.length > 0) {
                var sortName = $sortClass.attr("name");
                searchFormData = searchFormData + "&sort=" + sortName + "-" + sort;
            }
            system.ajaxPostAsync("/" + key + "/list", searchFormData, function (data) {
                $searchList.fadeTo(1,0).html(data).fadeTo(600,1);
            });
        });
    };

    $ns.bindListEvent = function (params) {
        var pageId = params.pageId;
        var $listPage = $("#" + pageId);
        var key = $listPage.attr("v");// 页面操作标记
        var $allCheck = $listPage.find("input[name='" + key + "AllCheckbox']");// 全选按钮元素
        var checkBoxKey = "input[name='" + key + "Checkbox']";// 单选按钮元素定位条件
        // 绑定列表排序功能
        $ns.refreshSortBar(key);

        // 绑定全选按钮事件
        $allCheck.unbind("click").bind("click", function () {
            var value = $(this).is(':checked');
            $listPage.find(checkBoxKey).each(function () {
                $(this).prop("checked", value);
            });
        });
        // 绑定修改处理事件
        $listPage.find(".edit").unbind("click").bind("click", function () {
            var editId = $(this).attr("v");
            if (!editId) {
                return system.alert("请选择要操作的记录！");
            }
            system.ajaxGetAsync("/" + key + "/edit/" + editId, function (result) {
                system.openDialog({
                    title: "修 改",
                    content: result,
                    callback: function (data) {
                        $ns.saveOrUpdate(key, data.parent(), "/" + key + "/update.json");
                        // 返回false 禁止关闭弹出框
                        return false;
                    }
                });
            });
        });

        // 绑定查看处理事件
        $listPage.find(".view").unbind("click").bind("click", function () {
            var editId = $(this).attr("v");
            if (!editId) {
                return system.alert("请选择要操作的记录！");
            }
            system.ajaxGetAsync("/" + key + "/edit/" + editId, function (result) {
                var $content = $(result);
                $content.find(":input").each(function () {
                    $(this).attr("disabled", "disabled");
                });
                system.openViewDialog({
                    title: "查 看",
                    content: $content
                });
            });
        });

        //绑定启用禁用处理事件
        $listPage.find(".start_stop").unbind("click").bind("click", function () {
            var startStop = $(this);
            var startStopId = startStop.attr("v");
            if (!startStopId) {
                return system.alert("请选择要操作的记录！");
            }
            var yn = startStop.hasClass("stop") ? 0 : 1;
            bootbox.confirm("确定要进行该操作?", function (yes) {
                if (yes) {
                    system.ajaxPostAsync("/" + key + "/update.json?id=" + startStopId + "&yn=" + yn, null, function (result) {
                        $ns.parseResult({
                            pageId: pageId,
                            result: result,
                            callback: function () {
                                if (yn == 0) {
                                    startStop.removeClass("stop").addClass("start").text("已启用");
                                } else {
                                    startStop.removeClass("start").addClass("stop").text("已禁用");
                                }
                            }
                        });
                    });
                }
            });
        });

        //绑定记录双击事件触发修改操作
        $listPage.find(".table tr:gt(0)").hover(
            function () {
                var tr = $(this);
                tr.data("class", tr.attr("class"));
                tr.removeClass().addClass("warning")
            }, function () {
                var tr = $(this);
                tr.removeClass().addClass(tr.data("class"))
            }).find("td:gt(0)").unbind("click").bind("click", function () {
            var $checkBox = $(this).parent().find(checkBoxKey);
            if ($checkBox.length > 0) {
                $checkBox.prop("checked", !$checkBox.is(':checked'));
            }
        }).unbind("dblclick").bind("dblclick", function () {
            if (!params.dbclick) {
                return false;
            }
            var $tr=$(this).parent();
            var editId = $tr.find(checkBoxKey).val();
            if (!editId) {
                return false;
            }
            var $view = $tr.find(".view");
            var $edit = $tr.find(".edit");
            if($view.length > 0){
                $view.click();
            } else if($edit.length > 0){
                $edit.click();

            }
        });
    };

    $ns.bindOperaBarEvent = function (params) {
        var pageId = params.pageId;
        var $listPage = $("#" + pageId);
        var key = $listPage.attr("v");//页面操作标记
        var checkBoxKey = "input[name='" + key + "Checkbox']";//单选按钮元素定位条件
        var editTemplateKey = key + "EditTemplate";// 新增/编辑表单模板id

        // 绑定新增处理事件
        $listPage.find(".add").unbind("click").bind("click", function () {
            system.openDialog({
                title: "新 增",
                content: $.tmpl($("#" + editTemplateKey).html()),
                callback: function (data) {
                    $ns.saveOrUpdate(key, data.parent(), "/" + key + "/save.json");
                    return false;
                }
            });
        });

        // 绑定调整分页数量事件
        $listPage.find("#pageSize").unbind("change").bind("change", function () {
            $ns.clickSearch(key);
        });

        //绑定刷新按钮事件
        $listPage.find(".refresh").unbind("click").bind("click", function () {
            var $current = $listPage.find(".current");
            if($current.length > 0){
                $current.data("pageNo",$current.text());
            }
            $ns.clickSearch(key);
        });

        // 绑定页面元素过滤处理事件
        $listPage.find(".filler").unbind("input propertychange").bind("input propertychange", function () {
            var value = $(this).val();
            $listPage.find(".table tr:gt(0)").each(function () {
                var isHidden = $(this).is(':hidden');
                if (value && $(this).text().indexOf(value) < 0) {
                    if(!isHidden){
                        $(this).fadeTo(100,0).hide(100);
                    }
                } else {
                    if(isHidden){
                        $(this).show(100).fadeTo(100,1);
                    }
                }
            });
        });

        // 绑定删除处理事件
        $listPage.find(".remove").unbind("click").bind("click", function () {
            var deleteId = $(this).attr("v");
            if (!deleteId) {
                return bootbox.alert("请选择要操作的记录！");
            }
            system.confirm("确定要进行删除操作?", function () {
                system.ajaxPostAsync("/" + key + "/delete/" + deleteId + "?format=json", null, function (data) {
                    $ns.parseResult({
                        pageId: pageId,
                        result: data,
                        callback: function () {
                            $ns.clickSearch(key);
                        }
                    });
                });
            });
        });

        //绑定批量删除事件
        $listPage.find(".removeAll").unbind("click").bind("click", function () {
            var ids = "";
            var count = 0;
            var query = $(this).attr("q");
            query = query ? "?" + query : "";
            $listPage.find(checkBoxKey + ":checked").each(function () {
                if (count == 0) {
                    ids = ids + $(this).val();
                } else {
                    ids = ids + "," + $(this).val();
                }
                count = count + 1;
            });
            if (!ids) {
                return bootbox.alert("请选择要操作的记录！");
            }
            bootbox.confirm("确定要进行批量删除操作?", function (yes) {
                if (yes) {
                    system.ajaxPostAsync("/" + key + "/delete/" + ids + "?format=json", null, function (result) {
                        $ns.parseResult({
                            pageId: pageId,
                            result: result,
                            callback: function () {
                                $ns.clickSearch(key);
                            }
                        });
                    });
                }
            });
        });

        //绑定下载处理事件
        $listPage.find(".download").unbind("click").bind("click", function () {
            var $searchForm = $("#" + key + "SearchForm");
            if ($searchForm.length < 1) {
                console.log("没有找到id=" + key + "SearchForm的表单,无法进行下载操作!");
                return;
            }
            var searchFormData = $searchForm.serialize();
            bootbox.confirm("确定要进行下载操作?", function (yes) {
                if (yes) {
                    window.location.href = "/" + key + "/download?" + searchFormData;
                }
            });
        });
    };

    // 添加或删除操作工具栏项目
    $ns.refreshOperaBar = function (params) {
        var $listPage = $("#" + params.pageId);
        var $manageBarTemplate = $.tmpl($listPage.find("#manageBarTemplate").html());
        // 初始化管理工具栏
        if ($manageBarTemplate.length > 0) {
            if (!params.refreshOperaBar || params.refreshOperaBar.length < 1) {
                $listPage.prepend($manageBarTemplate.html());
                return;
            }
            var optionArrays = params.refreshOperaBar;
            var length = optionArrays.length;
            var $selfBar = $manageBarTemplate.find(".selfBar");
            for (var i = 0; i < length; i++) {
                var options = optionArrays[i];
                if (options.type == "remove" || options.type == "delete") {
                    var $selector = $manageBarTemplate.find("." + options.className);
                    if ($selector.length < 1) {
                        bootbox.alert("操作工具栏初始化错误,没有找到className=" + options.className + "对应元素样式!");
                        continue;
                    }
                    $selector.remove();
                } else {
                    var $addContent = $("<button href='javascript:;' class='" + options.className + " btn btn-sm '> " + options.text + "</button>");
                    if (!options.callback) {
                        bootbox.alert("操作工具栏初始化错误,没有找到className=" + options.className + "对应元素的回调函数!");
                        continue;
                    }
                    $addContent.click(options.callback);
                    $selfBar.append($addContent);
                }
            }
            $listPage.prepend($manageBarTemplate);
        }
    };

    // 添加排序功能
    $ns.refreshSortBar = function (key) {
        var $listBar = $("#" + key + "ListBarDiv");
        var $sort = $listBar.find(".sort");
        $listBar.find(".active th").each(function () {
            var pCol = $(this).attr("pCol");
            var $pColButton = $sort.find("button[name='" + pCol + "']");
            if (pCol && $pColButton.length < 1) {
                var text = $(this).text();
                var sort = "glyphicon-sort";
                var up = "glyphicon-arrow-up";
                var down = "glyphicon-arrow-down";
                $pColButton = $("<button href='javascript:;' class='" + sort + " btn btn-xs btn-default glyphicon ' name='" + pCol + "'>" + text + "</button>");
                $pColButton.unbind("click").bind("click", function () {
                    if ($(this).hasClass(sort)) {
                        $sort.find("." + up).removeClass(up).addClass(sort);
                        $sort.find("." + down).removeClass(down).addClass(sort);
                        $(this).removeClass(sort).addClass(down);
                    } else if ($(this).hasClass(down)) {
                        $(this).removeClass(down).addClass(up);
                    } else if ($(this).hasClass(up)) {
                        $(this).removeClass(up).addClass(down);
                    }
                    $ns.clickSearch(key);
                });
                $sort.append($pColButton);
            }
        });
    };

    /**
     * 重新触发查询
     */
    $ns.clickSearch = function (key) {
        var $btn = $("#" + key + "QueryButton");
        if ($btn.length < 1) {
            $btn = $(".find");
        }
        if ($btn.length > 0) {
            $btn.click();
        }
    };

    /**
     * 保存或更新表单信息
     * @param key 当前页面操作表关键字
     * @param popDoc 弹出页面元素对象
     * @param actionUrl 相对路径（对应requestMapping取值）
     */
    $ns.saveOrUpdate = function (key, popDoc, actionUrl) {
        var $form = $(popDoc).find("form");
        var formId = $form.attr("id");
        $form.attr("action", actionUrl);
        if (!formId) {
            formId = key + "InputForm";
        }
        var formValidateMethodNameBase = formId + "Validator";
        var validateFormFuncName = key + "." + formValidateMethodNameBase;
        try {
            if (typeof(eval(validateFormFuncName)) == "function") {
                eval(validateFormFuncName + "('#" + formId + "');");
            }
        } catch (err) {
            console.log("没有找到表单提交校验方法:" + validateFormFuncName);
            try {
                if (typeof(eval(formValidateMethodNameBase)) == "function") {
                    eval(formValidateMethodNameBase + "('#" + formId + "');");
                }
            } catch (e) {
                console.log("没有找到表单提交校验方法:" + formValidateMethodNameBase);
            }
        }
        $ns.validateSubmit({
            "form": $form,
            "callback": function (result) {
                $ns.parseResult({
                    pageId: popDoc,
                    result: result,
                    callback: function () {
                        $ns.clickSearch(key);
                        bootbox.hideAll();
                    }
                });
            }
        });
    };

    /**
     * options
     * @param form
     * @param button
     * @param callback
     * @returns {boolean}
     */
    $ns.validateSubmit = function (options) {
        var $form = $(options.form);
        var bootstrapValidator = $form.data('bootstrapValidator');
        if ($(options.button).attr("type") == 'submit') {
            $form.on('success.form.bv', function (e) {
                // Prevent form submission
                e.preventDefault();
                // Get the form instance
                var $form = $(e.target);
                // Get the BootstrapValidator instance
                var bv = $form.data('bootstrapValidator');
            });
        } else {
            // bootstrapValidator.resetForm(true);// 重置表单所有验证规则
            if (bootstrapValidator) {
                bootstrapValidator.validate();
                if (!bootstrapValidator.isValid()) {
                    return false;
                }
            }
        }
        // Use Ajax to submit form data
        system.ajaxPostAsync($form.attr('action'), $form.serialize(), options.callback);
    };

    /**
     * 服务返回结果解析方法 当operation_table的上相邻元素的id=search时，为异步刷新页面
     * @param callback成功回调服务
     * @param pageId 弹出页面元素对象
     * @param result 返回的JsonResponse/errors结果对象
     */
    $ns.parseResult = function (options) {
        //检查错误信息
        var result = options.result;
        var $pageId = system.paramJquery(options.pageId);
        if ($pageId.length < 1) {
            $pageId = $(document);
        }
        var errMsg = $ns.errorMsg(result, $pageId);
        if (errMsg) {
            return false;
        }
        var response = result.resultResponse;
        if (response.code == "0") {
            var msg = response.msg;
            bootbox.alert(msg, function () {
                if (typeof(options.callback) == "function") {
                    options.callback(response);
                }
            });
            return true;
        } else {
            bootbox.alert(response.msg);
            return false;
        }
    };

    /**
     * 错误信息处理
     * @param serviceResponse
     * @param pageId
     * @returns {string}
     */
    $ns.errorMsg = function (result, $pageId) {
        //检查错误信息
        var errors = (result ? result.errors : "");
        var errMsg = "";
        if (!errors) {
            return errMsg;
        }
        // 清空之前异常提示
        $pageId.find("small[data-bv-result='INVALID']").remove();
        for (var key in errors) {
            var name = key;
            var $item = $pageId.find(":input[name='" + name + "']");
            if (!$item.length && name.lastIndexOf(".") > -1) {
                name = name.substring(name.lastIndexOf(".") + 1);
                $item = $pageId.find(":input[name='" + name + "']");
            }
            if ($item.length) {
                $item.closest(".form-group", $pageId).removeClass("has-success").addClass("has-error");
                var $parent = $item.parent();
                $parent.find(".glyphicon-ok").removeClass("glyphicon-ok").addClass("glyphicon-remove");
                var bvdata = $parent.find("small[data-bv-for='" + name + "']");
                if (bvdata.length) {
                    bvdata.show().text(errors[key]);
                } else {
                    $parent.append("<small class=\"help-block\" data-bv-for=\"" + name
                        + "\" data-bv-result=\"INVALID\">" + errors[key] + "</small>");
                }
                errMsg = errMsg + errors[key] + "\n\r";
            }
        }
        return errMsg;
    };


    $ns.formatJson = function (txt, compress/*是否为压缩模式*/) {/* 格式化JSON源码(对象转换为JSON文本) */
        var indentChar = '    ';
        if (/^\s*$/.test(txt)) {
            alert('数据为空,无法格式化! ');
            return;
        }
        try {
            var data = eval('(' + txt + ')');
        }
        catch (e) {
            alert('数据源语法错误,格式化失败! 错误信息: ' + e.description, 'err');
            return;
        }
        ;
        var draw = [], last = false, This = this, line = compress ? '' : '\n', nodeCount = 0, maxDepth = 0;

        var notify = function (name, value, isLast, indent/*缩进*/, formObj) {
            nodeCount++;
            /*节点计数*/
            for (var i = 0, tab = ''; i < indent; i++) tab += indentChar;
            /* 缩进HTML */
            tab = compress ? '' : tab;
            /*压缩模式忽略缩进*/
            maxDepth = ++indent;
            /*缩进递增并记录*/
            if (value && value.constructor == Array) {/*处理数组*/
                draw.push(tab + (formObj ? ('"' + name + '":') : '') + '[' + line);
                /*缩进'[' 然后换行*/
                for (var i = 0; i < value.length; i++)
                    notify(i, value[i], i == value.length - 1, indent, false);
                draw.push(tab + ']' + (isLast ? line : (',' + line)));
                /*缩进']'换行,若非尾元素则添加逗号*/
            } else if (value && typeof value == 'object') {/*处理对象*/
                draw.push(tab + (formObj ? ('"' + name + '":') : '') + '{' + line);
                /*缩进'{' 然后换行*/
                var len = 0, i = 0;
                for (var key in value) len++;
                for (var key in value) notify(key, value[key], ++i == len, indent, true);
                draw.push(tab + '}' + (isLast ? line : (',' + line)));
                /*缩进'}'换行,若非尾元素则添加逗号*/
            } else {
                if (typeof value == 'string') value = '"' + value + '"';
                draw.push(tab + (formObj ? ('"' + name + '":') : '') + value + (isLast ? '' : ',') + line);
            }
            ;
        };
        var isLast = true, indent = 0;
        notify('', data, isLast, indent, false);
        return draw.join('');
    }

})(using("handler"));