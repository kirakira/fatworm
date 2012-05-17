package fatworm.storage.bplustree;

import fatworm.storage.*;
import fatworm.util.ByteBuffer;
import fatworm.storage.bucket.Bucket;

import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class BPlusTree {
    public enum KeySize {
        FIXED_1_BYTE,
        FIXED_4_BYTES,
        FIXED_8_BYTES,
        VARIANT
    }

    private IOHelper io;
    private Comparator<byte[]> compare;

    private Bucket head;

    private KeySize keySize;
    private int fanout;
    private int root;

    private static char[] magic = {'f', 'a', 't', 'w', 'o', 'r', 'm', 'b', 'p', 't'};

    private BPlusTree(IOHelper ioHelper, Comparator<byte[]> compare) {
        io = ioHelper;
        this.compare = compare;
    }

    public static BPlusTree load(IOHelper ioHelper, Comparator<byte[]> compare, int block) {
        BPlusTree ret = new BPlusTree(ioHelper, compare);
        ret.head = Bucket.load(ret.io, block);
        byte[] data  = ret.head.getData();
        ByteBuffer buffer = new ByteBuffer(data);

        for (int i = 0; i < magic.length; ++i) {
            if (buffer.getChar() != magic[i])
                return null;
        }

        byte ks = buffer.getByte();
        if (ks == 0)
            ret.keySize = KeySize.FIXED_1_BYTE;
        else if (ks == 1)
            ret.keySize = KeySize.FIXED_4_BYTES;
        else if (ks == 2)
            ret.keySize = KeySize.FIXED_8_BYTES;
        else if (ks == 3)
            ret.keySize = KeySize.VARIANT;
        else
            return null;

        ret.fanout = buffer.getInt();
        ret.root = buffer.getInt();

        return ret;
    }

    public static BPlusTree create(IOHelper ioHelper, Comparator<byte[]> compare, KeySize size) throws java.io.IOException {
        BPlusTree ret = new BPlusTree(ioHelper, compare);

        ret.keySize = size;
        ret.root = 0;

        if (ret.keySize == KeySize.FIXED_1_BYTE)
            ret.fanout = 816;
        else if (ret.keySize == KeySize.FIXED_4_BYTES)
            ret.fanout = 510;
        else if (ret.keySize == KeySize.FIXED_8_BYTES)
            ret.fanout = 340;
        else if (ret.keySize == KeySize.VARIANT)
            ret.fanout = 170;
        else
            return null;

        ret.head = Bucket.create(ret.io);
        ret.saveHead();

        return ret;
    }

    private int saveHead() throws java.io.IOException {
        ByteBuffer buffer = new ByteBuffer();
        for (int i = 0; i < magic.length; ++i)
            buffer.putChar(magic[i]);

        if (keySize == KeySize.FIXED_1_BYTE)
            buffer.putByte((byte) 0);
        else if (keySize == KeySize.FIXED_4_BYTES)
            buffer.putByte((byte) 1);
        else if (keySize == KeySize.FIXED_8_BYTES)
            buffer.putByte((byte) 2);
        else if (keySize == KeySize.VARIANT)
            buffer.putByte((byte) 3);

        buffer.putInt(fanout);
        buffer.putInt(root);

        head.setData(buffer.array());
        return head.save();
    }

    public int getBlock() {
        return head.block();
    }

    public boolean check() {
        try {
            if (root == 0)
                return true;
            else if (!check(root, 0))
                return false;
            else {
                Node n = new Node(root);
                while (!n.isLeaf()) {
                    n = new Node(n.pointers[0]);
                }
                byte[] last = null;
                int count = 0;
                while (true) {
                    for (int i = 0; i < n.keys.length; ++i) {
                        ++count;
                        if (last == null)
                            last = n.keys[i];
                        else {
                            if (compare.compare(last, n.keys[i]) >= 0)
                                return false;
                            last = n.keys[i];
                        }
                    }
                    if (n.pointers[n.pointers.length - 1] == 0)
                        break;
                    else
                        n = new Node(n.pointers[n.pointers.length - 1]);
                }
                System.out.println(count + " values in total");
                return true;
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean check(int root, int level) throws java.io.IOException {
        Node n = new Node(root);
        System.out.println("Level " + level + ": " + n.pointers() + " children" + (n.isLeaf() ? " (leaf)" : ""));
        for (int i = 1; i < n.keys(); ++i)
            if (compare.compare(n.keys[i - 1], n.keys[i]) >= 0)
                return false;
        if (n.pointers() > fanout) {
            System.err.println("Node has too many pointers");
            return false;
        }
        if ((root == this.root && !n.isLeaf() && n.pointers() < 2) || (root != this.root && n.pointers() < n.minPointers())) {
            System.err.println("Node has too few pointers");
            return false;
        }
        if (n.isLeaf())
            return true;
        else {
            for (int i = 0; i < n.pointers(); ++i)
                if (n.pointers[i] != 0 && !check(n.pointers[i], level + 1))
                    return false;
            return true;
        }
    }

    private void changeRoot(int value) throws java.io.IOException {
        root = value;
        saveHead();
    }

    private static class SearchResult {
        boolean success;
        int block;
        boolean leaf;

        public SearchResult() {
            this(false, 0, false);
        }

        public SearchResult(boolean success, int block, boolean leaf) {
            this.success = success;
            this.block = block;
            this.leaf = leaf;
        }
    }

    private static class InsertResult {
        boolean split;
        byte[] key;
        int value;
    }

    private SearchResult searchNode(int root, byte[] key) throws java.io.IOException {
        if (root == 0)
            return new SearchResult(false, 0, false);
        else {
            Node node = new Node(root);

            SearchResult ret = new SearchResult();
            ret.block = node.findValue(key);
            if (ret.block == 0)
                ret.success = false;
            else
                ret.success = true;
            ret.leaf = node.isLeaf();

            return ret;
        }
    }

    private List<Integer> getList(byte[] data) {
        ByteBuffer buffer = new ByteBuffer(data);
        int len = buffer.getInt();
        LinkedList<Integer> ret = new LinkedList<Integer>();
        for (int i = 0; i < len; ++i)
            ret.add(buffer.getInt());
        return ret;
    }

    private byte[] putList(List<Integer> list) {
        ByteBuffer buffer = new ByteBuffer();
        buffer.putInt(list.size());
        for (Integer i: list)
            buffer.putInt(i.intValue());
        return buffer.array();
    }

    public List<Integer> find(byte[] key) throws java.io.IOException {
        int current = root;

        do {
            SearchResult sr = searchNode(current, key);
            if (!sr.success)
                return new LinkedList<Integer>();
            else if (sr.leaf) {
                Bucket bucket = Bucket.load(io, sr.block);
                return getList(bucket.getData());
            } else {
                current = sr.block;
            }
        } while (true);
    }

    public void insert(byte[] key, int value) throws java.io.IOException {
        if (root == 0) {
            List<Integer> b = new LinkedList<Integer>();
            b.add(value);
            Bucket bucket = Bucket.create(io);
            bucket.setData(putList(b));
            int bucketBlock = bucket.save();
            Node n = new Node(true, key, bucketBlock, 0);
            changeRoot(n.save());
        } else {
            InsertResult ret = insertRaw(root, key, value);
            if (ret.split) {
                Node n = new Node(false, ret.key, root, ret.value);
                changeRoot(n.save());
            }
        }
    }

    private InsertResult insertRaw(int current, byte[] key, int value) throws java.io.IOException {
        Node n = new Node(current);
        int block = n.findValue(key);
        if (n.isLeaf()) {
            List<Integer> b;
            if (block == 0)
                b = new LinkedList<Integer>();
            else {
                Bucket bucket = Bucket.load(io, block);
                b = getList(bucket.getData());
                bucket.remove();
            }
            b.add(value);
            
            Bucket bucket = Bucket.create(io);
            bucket.setData(putList(b));
            int bucketBlock = bucket.save();
            return n.insertSplitSave(key, bucketBlock);
        } else {
            InsertResult ret = insertRaw(block, key, value);
            if (ret.split)
                return n.insertSplitSave(ret.key, ret.value);
            else
                return ret;
        }
    }

    public void remove(byte[] key, int value) throws java.io.IOException {
        if (root == 0)
            System.err.println("Removing a key that doesn't exist");
        else {
            Node n = new Node(root);
            if (removeRaw(n, key, value)) {
                if (n.isLeaf()) {
                    if (n.pointers.length == 1) {
                        changeRoot(0);
                        n.remove();
                    } else
                        n.save();
                } else {
                    if (n.pointers.length == 1) {
                        changeRoot(n.pointers[0]);
                        n.remove();
                    } else
                        n.save();
                }
            }
        }
    }

    private boolean removeRaw(Node node, byte[] key, int value) throws java.io.IOException {
        if (node.isLeaf()) {
            int pos = node.findIndex(key);
            if (pos == -1) {
                System.err.println("Removing a key that doesn't exist");
                return false;
            } else {
                Bucket bucket = Bucket.load(io, node.pointers[pos]);
                List<Integer> list = getList(bucket.getData());
                boolean removed = false;
                for (Iterator<Integer> iter = list.iterator(); iter.hasNext();) {
                    Integer i = iter.next();
                    if (i.intValue() == value) {
                        iter.remove();
                        removed = true;
                        break;
                    }
                }
                if (!removed) {
                    System.err.println("Removing a value that doesn't exist");
                    return false;
                } else {
                    if (list.size() == 0) {
                        bucket.remove();
                        if (node.remove(pos))
                            return true;
                        else {
                            node.save();
                            return false;
                        }
                    } else {
                        bucket.setData(putList(list));
                        bucket.save();
                        return false;
                    }
                }
            }
        } else {
            int pos = node.findIndex(key);
            Node nk = new Node(node.pointers[pos + 1]);
            if (removeRaw(nk, key, value)) {
                Node nl = null, nr = null;
                if (pos != -1) {
                    nl = new Node(node.pointers[pos]);
                    if (node.redistributeSave(pos, nl, nk, true)) {
                        node.save();
                        return false;
                    }
                }
                if (pos != node.keys.length - 1) {
                    nr = new Node(node.pointers[pos + 2]);
                    if (node.redistributeSave(pos + 1, nk, nr, false)) {
                        node.save();
                        return false;
                    }
                }
                if (nl == null) {
                    if (!node.mergeSave(pos + 1, nk, nr)) {
                        node.save();
                        return false;
                    } else
                        return true;
                } else {
                    if (!node.mergeSave(pos, nl, nk)) {
                        node.save();
                        return false;
                    } else
                        return true;
                }
            } else
                return false;
        }
    }

    class Node {
        boolean leaf;
        int[] pointers;
        byte[][] keys;

        Bucket bucket;

        private Node(boolean leaf) {
            this.leaf = leaf;
            bucket = Bucket.create(io);
        }

        public Node(int block) throws java.io.IOException {
            bucket = Bucket.load(io, block);
            ByteBuffer buffer = new ByteBuffer(bucket.getData());

            leaf = buffer.getBoolean();
            int count = buffer.getInt();
            pointers = new int[count];
            keys = new byte[count - 1][];
            for (int i = 0; i < pointers.length; ++i)
                pointers[i] = buffer.getInt();
            for (int i = 0; i < keys.length; ++i) {
                int l;
                if (keySize == KeySize.FIXED_1_BYTE)
                    l = 1;
                else if (keySize == KeySize.FIXED_4_BYTES)
                    l = 4;
                else if (keySize == KeySize.FIXED_8_BYTES)
                    l = 8;
                else
                    l = buffer.getInt();
                keys[i] = new byte[l];
                buffer.getBytes(keys[i], 0, l);
            }
        }

        public Node(boolean leaf, byte[] key, int pointerl, int pointerr) {
            this.leaf = leaf;
            bucket = Bucket.create(io);
            keys = new byte[1][];
            keys[0] = key;
            pointers = new int[2];
            pointers[0] = pointerl;
            pointers[1] = pointerr;
        }

        public int save() throws java.io.IOException {
            ByteBuffer buffer = new ByteBuffer();
            buffer.putBoolean(leaf);
            buffer.putInt(pointers.length);
            for (int i = 0; i < pointers.length; ++i)
                buffer.putInt(pointers[i]);
            for (int i = 0; i < keys.length; ++i) {
                int l;
                if (keySize == KeySize.FIXED_1_BYTE)
                    l = 1;
                else if (keySize == KeySize.FIXED_4_BYTES)
                    l = 4;
                else if (keySize == KeySize.FIXED_8_BYTES)
                    l = 8;
                else {
                    l = keys[i].length;
                    buffer.putInt(l);
                }
                buffer.putBytes(keys[i], 0, l);
            }

            bucket.setData(buffer.array());
            return bucket.save();
        }

        public void remove() {
            bucket.remove();
        }

        private int binarySearch(byte[] key) {
            int l = 0, r = keys.length - 1, m;
            while (l < r) {
                m = (l + r + 1) / 2;
                int c = compare.compare(keys[m], key);
                if (c == 0)
                    return m;
                else if (c < 0)
                    l = m;
                else
                    r = m - 1;
            }
            if (compare.compare(keys[l], key) <= 0)
                return l;
            else
                return -1;
        }

        public int findValue(byte[] key) {
            int pos = binarySearch(key);
            if (leaf) {
                if (pos != -1 && compare.compare(keys[pos], key) == 0)
                    return pointers[pos];
                else
                    return 0;
            } else {
                if (pos == -1)
                    return pointers[0];
                else
                    return pointers[pos + 1];
            }
        }

        public int findIndex(byte[] key) {
            int pos = binarySearch(key);
            if (leaf) {
                if (pos != -1 && compare.compare(keys[pos], key) == 0)
                    return pos;
                else
                    return -1;
            } else
                return pos;
        }

        public boolean isLeaf() {
            return leaf;
        }

        public int pointers() {
            return pointers.length;
        }

        public int keys() {
            return keys.length;
        }

        private int minPointers() {
            if (leaf)
                return fanout / 2 + 1;
            else
                return (fanout + 1) / 2;
        }

        public boolean remove(int pos) throws java.io.IOException {
            int[] newPointers = new int[pointers.length - 1];
            byte[][] newKeys = new byte[keys.length - 1][];

            if (leaf)
                newPointers[newPointers.length - 1] = pointers[newPointers.length];
            else
                newPointers[0] = pointers[0];

            for (int i = 0; i < newKeys.length; ++i) {
                if (leaf) {
                    if (i < pos) {
                        newKeys[i] = keys[i];
                        newPointers[i] = pointers[i];
                    } else {
                        newKeys[i] = keys[i + 1];
                        newPointers[i] = pointers[i + 1];
                    }
                } else {
                    if (i < pos) {
                        newKeys[i] = keys[i];
                        newPointers[i + 1] = pointers[i + 1];
                    } else {
                        newKeys[i] = keys[i + 1];
                        newPointers[i + 1] = pointers[i + 2];
                    }
                }
            }

            pointers = newPointers;
            keys = newKeys;

            return pointers.length < minPointers();
        }

        public boolean redistributeSave(int keyIndex, Node left, Node right, boolean leftToRight) throws java.io.IOException {
            System.out.println("redistribute save");
            System.out.println("before: " + left.keys.length + " + " + right.keys.length);
            byte[] mid = keys[keyIndex];

            if (leftToRight) {
                if (left.pointers.length <= left.minPointers())
                    return false;

                if (left.leaf) {
                    int[] newPointers = new int[left.pointers.length - 1];
                    byte[][] newKeys = new byte[left.keys.length - 1][];

                    byte[] ck = left.keys[left.keys.length - 1];
                    int cp = left.pointers[left.pointers.length - 2];

                    newPointers[newPointers.length - 1] = left.pointers[left.pointers.length - 1];
                    for (int i = 0; i < newPointers.length - 1; ++i)
                        newPointers[i] = left.pointers[i];
                    for (int i = 0; i < newKeys.length; ++i)
                        newKeys[i] = left.keys[i];
                    left.pointers = newPointers;
                    left.keys = newKeys;

                    newPointers = new int[right.pointers.length + 1];
                    newKeys = new byte[right.keys.length + 1][];
                    newPointers[0] = cp;
                    newKeys[0] = ck;
                    keys[keyIndex] = ck;
                    for (int i = 0; i < right.pointers.length; ++i)
                        newPointers[i + 1] = right.pointers[i];
                    for (int i = 0; i < right.keys.length; ++i)
                        newKeys[i + 1] = right.keys[i];
                    right.pointers = newPointers;
                    right.keys = newKeys;
                } else {
                    int[] newPointers = new int[left.pointers.length - 1];
                    byte[][] newKeys = new byte[left.keys.length - 1][];

                    byte[] ck = left.keys[left.keys.length - 1];
                    int cp = left.pointers[left.pointers.length - 1];

                    for (int i = 0; i < newPointers.length; ++i)
                        newPointers[i] = left.pointers[i];
                    for (int i = 0; i < newKeys.length; ++i)
                        newKeys[i] = left.keys[i];
                    left.pointers = newPointers;
                    left.keys = newKeys;

                    newPointers = new int[right.pointers.length + 1];
                    newKeys = new byte[right.keys.length + 1][];
                    newPointers[0] = cp;
                    newKeys[0] = mid;
                    keys[keyIndex] = ck;
                    for (int i = 0; i < right.pointers.length; ++i)
                        newPointers[i + 1] = right.pointers[i];
                    for (int i = 0; i < right.keys.length; ++i)
                        newKeys[i + 1] = right.keys[i];
                    right.pointers = newPointers;
                    right.keys = newKeys;
                }

                left.save();
                right.save();

                System.out.println("after: " + left.keys.length + " + " + right.keys.length);
                return true;
            } else {
                if (right.pointers.length <= right.minPointers())
                    return false;

                if (left.leaf) {
                    int[] newPointers = new int[right.pointers.length - 1];
                    byte[][] newKeys = new byte[right.keys.length - 1][];

                    byte[] ck = right.keys[0];
                    int cp = right.pointers[0];

                    for (int i = 0; i < newPointers.length; ++i)
                        newPointers[i] = right.pointers[i + 1];
                    for (int i = 0; i < newKeys.length; ++i)
                        newKeys[i] = right.keys[i + 1];
                    right.pointers = newPointers;
                    right.keys = newKeys;

                    newPointers = new int[left.pointers.length + 1];
                    newKeys = new byte[left.keys.length + 1][];
                    newPointers[newPointers.length - 1] = left.pointers[left.pointers.length - 1];
                    newPointers[newPointers.length - 2] = cp;
                    newKeys[newKeys.length - 1] = ck;
                    keys[keyIndex] = ck;
                    for (int i = 0; i < left.pointers.length - 1; ++i)
                        newPointers[i] = left.pointers[i];
                    for (int i = 0; i < left.keys.length; ++i)
                        newKeys[i] = left.keys[i];
                    left.pointers = newPointers;
                    left.keys = newKeys;
                } else {
                    int[] newPointers = new int[right.pointers.length - 1];
                    byte[][] newKeys = new byte[right.keys.length - 1][];

                    byte[] ck = right.keys[0];
                    int cp = right.pointers[0];

                    for (int i = 0; i < newPointers.length; ++i)
                        newPointers[i] = right.pointers[i + 1];
                    for (int i = 0; i < newKeys.length; ++i)
                        newKeys[i] = right.keys[i + 1];
                    right.pointers = newPointers;
                    right.keys = newKeys;

                    newPointers = new int[left.pointers.length + 1];
                    newKeys = new byte[left.keys.length + 1][];
                    newPointers[newPointers.length - 1] = cp;
                    newKeys[newKeys.length - 1] = mid;
                    keys[keyIndex] = ck;
                    for (int i = 0; i < left.pointers.length; ++i)
                        newPointers[i] = left.pointers[i];
                    for (int i = 0; i < left.keys.length; ++i)
                        newKeys[i] = left.keys[i];
                    left.pointers = newPointers;
                    left.keys = newKeys;
                }

                left.save();
                right.save();

                System.out.println("after: " + left.keys.length + " + " + right.keys.length);
                return true;
            }
        }

        public boolean mergeSave(int keyIndex, Node left, Node right) throws java.io.IOException {
            System.out.println("merge save");
            int[] newPointers;
            byte[][] newKeys;
            if (left.leaf) {
                newPointers = new int[left.pointers.length + right.pointers.length - 1];
                newKeys = new byte[left.keys.length + right.keys.length][];

                System.arraycopy(left.keys, 0, newKeys, 0, left.keys.length);
                System.arraycopy(right.keys, 0, newKeys, left.keys.length, right.keys.length);
                System.arraycopy(left.pointers, 0, newPointers, 0, left.pointers.length - 1);
                System.arraycopy(right.pointers, 0, newPointers, left.pointers.length - 1, right.pointers.length);
            } else {
                newPointers = new int[left.pointers.length + right.pointers.length];
                newKeys = new byte[left.keys.length + right.keys.length + 1][];

                byte[] mid = keys[keyIndex];

                System.arraycopy(left.keys, 0, newKeys, 0, left.keys.length);
                newKeys[left.keys.length] = mid;
                System.arraycopy(right.keys, 0, newKeys, left.keys.length + 1, right.keys.length);
                System.arraycopy(left.pointers, 0, newPointers, 0, left.pointers.length);
                System.arraycopy(right.pointers, 0, newPointers, left.pointers.length, right.pointers.length);
            }
            left.pointers = newPointers;
            left.keys = newKeys;
            left.save();
            right.remove();

            newPointers = new int[pointers.length - 1];
            newKeys = new byte[keys.length - 1][];
            System.arraycopy(keys, 0, newKeys, 0, keyIndex);
            System.arraycopy(pointers, 0, newPointers, 0, keyIndex + 1);
            System.arraycopy(keys, keyIndex + 1, newKeys, keyIndex, keys.length - keyIndex - 1);
            System.arraycopy(pointers, keyIndex + 2, newPointers, keyIndex + 1, pointers.length - keyIndex - 2);
            keys = newKeys;
            pointers = newPointers;

            return pointers.length < minPointers();
        }

        public InsertResult insertSplitSave(byte[] key, int value) throws java.io.IOException {
            int pos = binarySearch(key);
            if (pos != -1 && compare.compare(keys[pos], key) == 0) {
                if (leaf) {
                    pointers[pos] = value;
                    save();

                    InsertResult ret = new InsertResult();
                    ret.split = false;
                    return ret;
                } else {
                    System.err.println("Inserting a key that already existed");
                    return null;
                }
            }
            ++pos;

            byte[][] newKeys = new byte[keys.length + 1][];
            int[] newPointers = new int[pointers.length + 1];

            if (leaf)
                newPointers[pointers.length] = pointers[pointers.length - 1];
            else
                newPointers[0] = pointers[0];
            for (int i = 0; i < newKeys.length; ++i) {
                if (leaf) {
                    if (i < pos) {
                        newPointers[i] = pointers[i];
                        newKeys[i] = keys[i];
                    } else if (i == pos) {
                        newPointers[i] = value;
                        newKeys[i] = key;
                    } else {
                        newPointers[i] = pointers[i - 1];
                        newKeys[i] = keys[i - 1];
                    }
                } else {
                    if (i < pos) {
                        newKeys[i] = keys[i];
                        newPointers[i + 1] = pointers[i + 1];
                    } else if (i == pos) {
                        newKeys[i] = key;
                        newPointers[i + 1] = value;
                    } else {
                        newKeys[i] = keys[i - 1];
                        newPointers[i + 1] = pointers[i];
                    }
                }
            }

            keys = newKeys;
            pointers = newPointers;

            InsertResult ret = new InsertResult();
            if (pointers.length > fanout)
                ret = splitSave();
            else {
                save();

                ret = new InsertResult();
                ret.split = false;
            }

            return ret;
        }

        private InsertResult splitSave() throws java.io.IOException {
            if (pointers.length <= fanout) {
                System.err.println("Splitting a node that is not overfull");
                return null;
            }

            InsertResult ret = new InsertResult();
            ret.split = true;
            Node node = new Node(leaf);
            if (leaf) {
                int right = (pointers.length + 1) / 2, left = pointers.length + 1 - right, mid = pointers.length / 2;

                node.pointers = new int[right];
                node.keys = new byte[right - 1][];

                System.arraycopy(pointers, left - 1, node.pointers, 0, right);
                System.arraycopy(keys, left - 1, node.keys, 0, right - 1);
                
                int next = node.save();
                int[] newPointers = new int[left];
                byte[][] newKeys = new byte[left - 1][];
                System.arraycopy(pointers, 0, newPointers, 0, left - 1);
                System.arraycopy(keys, 0, newKeys, 0, left - 1);
                newPointers[left - 1] = next;

                ret.key = keys[mid];
                ret.value = next;

                pointers = newPointers;
                keys = newKeys;

                save();
            } else {
                int right = pointers.length / 2, left = pointers.length - right, mid = (pointers.length - 1) / 2;

                node.pointers = new int[right];
                node.keys = new byte[right - 1][];

                System.arraycopy(pointers, left, node.pointers, 0, right);
                System.arraycopy(keys, left, node.keys, 0, right - 1);

                int next = node.save();
                int[] newPointers = new int[left];
                byte[][] newKeys = new byte[left - 1][];
                System.arraycopy(pointers, 0, newPointers, 0, left);
                System.arraycopy(keys, 0, newKeys, 0, left - 1);

                ret.key = keys[mid];
                ret.value = next;

                pointers = newPointers;
                keys = newKeys;

                save();
            }

            return ret;
        }
    }
}
