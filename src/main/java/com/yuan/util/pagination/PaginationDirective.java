package com.yuan.util.pagination;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by liuhuiqing on 2014/8/8.
 */
public class PaginationDirective extends org.apache.velocity.runtime.directive.Directive{

    @Override
    public String getName() {
        return "pageTag";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter internalContextAdapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        SimpleNode controllerSimpleNode = (SimpleNode) node.jjtGetChild(0);
        String controller = (String) controllerSimpleNode.value(internalContextAdapter);
        SimpleNode actionSimpleNode = (SimpleNode) node.jjtGetChild(1);
        String action = (String) actionSimpleNode.value(internalContextAdapter);
        SimpleNode pageSimpleNode = (SimpleNode) node.jjtGetChild(2);
        PageBean page = (PageBean) pageSimpleNode.value(internalContextAdapter);
        SimpleNode paramsSimpleNode = (SimpleNode) node.jjtGetChild(3);
        Map<String,String> params = (Map<String, String>) paramsSimpleNode.value(internalContextAdapter);
        writer.write(PaginationUtil.pageCompoent(controller, action, page, params));
        return true;
    }
}
