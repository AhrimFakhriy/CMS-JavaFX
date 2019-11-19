package main.model.datastructure.binarytree;

class TreeNode<KeyType extends Comparable<KeyType>, DataType> {
    private KeyType key;
    DataType data;
    TreeNode<KeyType, DataType> left, right;

    TreeNode(KeyType key, DataType data) {
        this.key = key;
        this.data = data;
    }

    void put(KeyType key, DataType data) {
        if(key.compareTo(this.key) < 0) {
            if (left == null)
                left = new TreeNode<>(key, data);
            else
                left.put(key, data);

        } else if(key.compareTo(this.key) > 0){
            if(right == null)
                right = new TreeNode<>(key, data);
            else
                right.put(key, data);
        } else {
            this.data = data;
        }
    }

    DataType get(KeyType key) {
        if(this.key.equals(key))
            return data;

        if(key.compareTo(this.key) < 0)
            return left == null ? null : left.get(key);

        return right == null ? null : right.get(key);
    }
}
