package routing;

class BTrieNode {
    int nextHop;
    BTrieNode leftChild;
    BTrieNode rightChild;
    boolean isBranchNode;
    int bit;
    boolean[] key;
    BTrieNode parent;
    
    BTrieNode() {
        this.nextHop = -1;
        this.leftChild = null;
        this.rightChild = null;
        this.isBranchNode = false;
        this.bit = -1;
        this.key = null;
        this.parent = null;
    }
}

public class BTrie {
    
    BTrieNode root;
    
    public BTrie() {
        root = null;
    }
    
    private void addLevelAndInsertNode(BTrieNode node, BTrieNode newNode, int bitValue) {
        BTrieNode pNode = node.parent;
        if(pNode == null || pNode.bit < bitValue) {
            BTrieNode rootNode = new BTrieNode();
            rootNode.bit = bitValue;
            rootNode.isBranchNode = true;
            if(newNode.key[bitValue] == false) {
                rootNode.leftChild = newNode;
                rootNode.rightChild = node;
            }
            else {
                rootNode.leftChild = node;
                rootNode.rightChild = newNode;
            }
            node.parent = rootNode;
            newNode.parent = rootNode;
            if(pNode == null) {
                root = rootNode;
            }
            else {
                rootNode.parent = pNode;
                if(pNode.leftChild == node)
                    pNode.leftChild = rootNode;
                else
                    pNode.rightChild = rootNode;
            }
        }
        else {
            this.addLevelAndInsertNode(pNode, newNode, bitValue);
        }
    }
    
    public void addNode(boolean[] key, int nextHop) {
        BTrieNode newNode = new BTrieNode();
        newNode.key = key;
        newNode.nextHop = nextHop;
        
        if(root == null) {
            root = newNode;
        }
        else {
            BTrieNode temp = root;
            while(temp != null) {
                if(temp.isBranchNode == false) {
                    if(temp.key == null) {
                        System.out.println("inside addNode ... temp is not branch node, still skey is null ... exiting ...");
                        System.exit(1);
                    }
                    int bitValue = -1;
                    for(int i=0;i<32;i++) {
                        if(temp.key[i] != newNode.key[i]) {
                            bitValue = i;
                            break;
                        }
                    }
                    if(bitValue == -1) {
                        System.out.println("Duplicate key ... Updating nextHop value ...");
                        temp.nextHop = newNode.nextHop;
                    }
                    else {
                        this.addLevelAndInsertNode(temp, newNode, bitValue);
                    }
                    break;
                }
                else {
                    if(newNode.key[temp.bit] == false)
                        temp = temp.leftChild;
                    else
                        temp = temp.rightChild;
                }
            }
        }
    }
    
    public int getNextHop(boolean[] dest) {
        BTrieNode temp = this.root;
        while(temp != null) {
            if(temp.isBranchNode == false) {
                if(temp.parent != null) {
                    System.out.print("PREFIX: " );
                    for(int i=0;i<=temp.parent.bit;i++) {
                        System.out.print(dest[i] == false ? 0 : 1);
                    }
                    System.out.print("\n");
                }
                return temp.nextHop;
                
            }
            else {
                if(dest[temp.bit] == false)
                    temp = temp.leftChild;
                else
                    temp = temp.rightChild;
            }
        }
        return -1;
    }
    
    public void cleanUp() {
        if(root != null)
            cleanUp(root);
    }
    
    private int cleanUp(BTrieNode node) {
        int leftVal = -1; int rightVal = -1;
        if(node.leftChild != null)
            leftVal = cleanUp(node.leftChild);
        if(node.rightChild != null)
            rightVal = cleanUp(node.rightChild);
        
        if(leftVal != -1 && rightVal != -1 && leftVal == rightVal) {
            node.isBranchNode = false;
            node.bit = -1;
            node.leftChild = null;
            node.rightChild = null;
            node.nextHop = leftVal;
        }
        
        if(node.isBranchNode == false)
            return node.nextHop;
        else
            return -1;
    }
    
    public boolean checkStructure() {
        boolean success = true;
        success = checkStructure(root);
        return success;
    }
    
    private boolean checkStructure(BTrieNode node) {
        boolean success = true;
        if(node != null) {
            if(node.isBranchNode == true) {
                if(node.bit == -1)
                    success = false;
                if(node.parent == null && node != root)
                    success = false;
                if(node.leftChild == null || node.rightChild == null)
                    success = false;
            }
            else {
                if(node.bit != -1)
                    success = false;
                
                if(node.nextHop == -1)
                    success = false;
                if(node.parent == null && node != root)
                    success = false;
                if(node.leftChild != null || node.rightChild != null)
                    success = false;
            }
            boolean leftsuccess = true;
            boolean rightsuccess = true;
            leftsuccess = checkStructure(node.leftChild);
            rightsuccess = checkStructure(node.rightChild);
            if(leftsuccess == false || rightsuccess == false)
                success = false;
        }
        return success;
    }
    public void displayTrie() {
        displayTrie(root);
    }
    
    private String getKey(boolean[] key) {
        if(key == null)
            return "NO KEY";
        String keyarray = "";
        for(int i=0;i<32;i++) {
            if(key[i] == false)
                keyarray = keyarray + "0";
            else
                keyarray = keyarray + "1";
        }
        return keyarray;
    }
    
    private void displayTrie(BTrieNode node) {
        if(node != null) {
            System.out.println("NODE: " + node + " BIT = " + node.bit + " KEY = " + getKey(node.key) + " IS BRANCH: " + node.isBranchNode + " LC " + node.leftChild + " RC " + node.rightChild + " P " + node.parent + " NH " + node.nextHop);
            displayTrie(node.leftChild);
            displayTrie(node.rightChild);
        }
    }
}
