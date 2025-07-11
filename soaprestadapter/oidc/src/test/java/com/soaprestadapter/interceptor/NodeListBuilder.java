package com.soaprestadapter.interceptor;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NodeListBuilder {
    private final List<Node> nodes = new ArrayList<>();

    public NodeListBuilder add(Node node) {
        nodes.add(node);
        return this;
    }

    public NodeList build() {
        return new NodeList() {
            @Override
            public Node item(int index) {
                return nodes.get(index);
            }

            @Override
            public int getLength() {
                return nodes.size();
            }
        };
    }
}
