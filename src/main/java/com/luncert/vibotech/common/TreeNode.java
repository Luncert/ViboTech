package com.luncert.vibotech.common;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {

  private T data;

  private List<TreeNode<T>> children = new ArrayList<>();

  public TreeNode(T data) {
    this.data = data;
  }

  public T getData() {
    return data;
  }

  public void addChild(TreeNode<T> child) {
    children.add(child);
  }

  public List<TreeNode<T>> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return "TreeNode{" +
        "data=" + data +
        ", children=" + children +
        '}';
  }
}
