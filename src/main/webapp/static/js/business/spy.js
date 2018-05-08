/**
 * 页面操作通用处理方法的封装
 * create by liuhuiqing
 */
(function ($ns) {
    $ns.spyInputFormValidator = function (formId) {
        $(formId).bootstrapValidator({
            live: 'enabled',
            message: '非法的输入参数',
            fields: {
                p: {
                    validators: {
                        notEmpty: {
                            message: 'password不能为空!'
                        },
                        stringLength: {
                            min: 6,
                            max: 20,
                            message: 'password长度必须在6-20之间!'
                        }
                    }
                },
                s: {
                    validators: {
                        notEmpty: {
                            message: 'sql不能为空!'
                        }
                    }
                }
            }
        });
    };

})(using("spy"));