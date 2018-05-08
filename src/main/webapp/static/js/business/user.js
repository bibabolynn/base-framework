/**
 * 页面操作通用处理方法的封装
 * create by liuhuiqing
 */
(function ($ns) {
    $ns.userInputFormValidator = function (formId) {
        $(formId).bootstrapValidator({
            live: 'enabled',
            message: '非法的输入参数',
            fields: {
                loginName: {
                    validators: {
                        notEmpty: {
                            message: '登录名不能为空!'
                        },
                        stringLength: {
                            min: 5,
                            max: 20,
                            message: '登录名长度必须在5到20个字符之间!'
                        },
                        regexp: {
                            regexp: /^[a-zA-Z\.]+$/,
                            message: '登录名必须是英文字符!'
                        }
                    }
                },
                phone: {
                    validators: {
                        regexp: {
                            regexp: /^[1-9]{1}[0-9]{2,10}$/,
                            message: '电话必须是3到11位数字!'
                        }
                    }
                },
                orgCode: {
                    validators: {
                        notEmpty: {
                            message: '商家不能为空!'
                        }
                    }
                }
            }
        });
    };

})(using("user"));