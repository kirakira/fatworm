package fatworm.storage.bplustree;

import fatworm.storage.*;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;

public class BPlusTree {
    public enum KeySize {
        FIXED_4_BYTES,
        VARIANT
    }

    private KeySize keySize;
    private IOHelper io;
    private int block;
    private int fanout;
    private int root;
    private Comparator compare;
    private Bucket bucket;

    private static char[] magic = {'f', 'a', 't', 'w', 'o', 'r', 'm', 'b', 'p', 't'};

    private BPlusTree(IOHelper ioHelper, int block, Comparator compare, KeySize size) {
        io = ioHelper;
        this.block = block;
        this.compare = compare;
        bucket = new Bucket(io);
        keySize = size;
    }

    public static BPlusTree load(IOHelper ioHelper, int block, Comparator compare) {
        BPlusTree ret = new BPlusTree(ioHelper, block, compare, null);
        try {
            byte[] data  = new byte[ret.io.getBlockSize()];
            ret.io.readBlock(block, data, 0);

            for (int i = 0; i < magic.length; ++i) {
                if (data[i] != (byte) magic[i])
                    return null;
            }

            int s = magic.length;

            if (data[s] == 0)
                ret.keySize = KeySize.FIXED_4_BYTES;
            else
                ret.keySize = KeySize.VARIANT;
            ++s;

            ret.fanout = ByteLib.bytesToInt(data, s);
            s += 4;

            ret.root = ByteLib.bytesToInt(data, s);

            return ret;
        } catch (java.io.IOException e) {
            return null;
        }
    }

    public static BPlusTree create(IOHelper ioHelper, Comparator compare, KeySize size) throws java.io.IOException {
        BPlusTree ret = new BPlusTree(ioHelper, ioHelper.occupy(), compare, size);
        byte[] data = new byte[ret.io.getBlockSize()];

        for (int i = 0; i < magic.length; ++i)
            data[i] = (byte) magic[i];

        int s = magic.length;

        if (ret.keySize == KeySize.FIXED_4_BYTES) {
            ret.fanout = 511;
            data[s] = 0;
        } else {
            ret.fanout = 256;
            data[s] = 1;
        }
        ++s;

        ByteLib.intToBytes(ret.fanout, data, s);
        s += 4;

        ret.root = 0;
        ByteLib.intToBytes(ret.root, data, s);

        ret.io.writeBlock(ret.block, data, 0);

        return ret;
    }

    private void changeRoot(int value) throws java.io.IOException {
        root = value;
        byte[] data = new byte[io.getBlockSize()];
        io.readBlock(block, data, 0);
        ByteLib.intToBytes(root, data, magic.length + 1 + 4);
        io.writeBlock(block, data, 0);
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
            ret.block = node.find(key);
            if (ret.block == 0)
                ret.success = false;
            else
                ret.success = true;
            ret.leaf = node.isLeaf();

            return ret;
        }
    }

    public List<Integer> search(byte[] key) throws java.io.IOException {
        int current = root;

        do {
            SearchResult sr = searchNode(current, key);
            if (!sr.success)
                return new LinkedList<Integer>();
            else if (sr.leaf) {
                return bucket.load(sr.block);
            } else {
                current = sr.block;
            }
        } while (true);
    }

    class Node {
        boolean leaf;
        int[] pointers;
        byte[][] keys;

        private LinkedList<Integer> blocks = new LinkedList<Integer>();

        private Node(boolean leaf) {
            this.leaf = leaf;
        }

        public Node(int block) throws java.io.IOException {
            byte[] data = new byte[io.getBlockSize()], tmp = new byte[io.getBlockSize()];
            io.readBlock(block, data, 0);
            blocks.add(new Integer(block));

            int next = ByteLib.bytesToInt(data, 0);
            int count = ByteLib.bytesToInt(data, 4);
            pointers = new int[count];
            keys = new byte[count - 1][];
            int t = ByteLib.bytesToInt(data, 8);
            if (t == 0)
                leaf = false;
            else
                leaf = true;

            while (next != 0) {
                io.readBlock(next, tmp, 0);
                blocks.add(new Integer(next));
                next = ByteLib.bytesToInt(tmp, 0);
                data = concat(data, tmp, 4);
            }

            int s = 12;

            pointers[0] = ByteLib.bytesToInt(data, s);
            s += 4;
            for (int i = 0; i < count - 1; ++i) {
                if (keySize == KeySize.FIXED_4_BYTES) {
                    keys[i] = new byte[4];
                    System.arraycopy(data, s, keys[i], 0, 4);

                    s += 4;
                } else {
                    int l = ByteLib.bytesToInt(data, s);
                    s += 4;
                    keys[i] = new byte[l];
                    System.arraycopy(data, s, keys[i], 0, l);

                    s += l;
                }

                pointers[i + 1] = ByteLib.bytesToInt(data, s);
                s += 4;
            }
        }

        public Node(boolean leaf, byte[] key, int pointerl, int pointerr) {
            this.leaf = leaf;
            keys = new byte[1][];
            keys[0] = key;
            pointers = new int[2];
            pointers[0] = pointerl;
            pointers[1] = pointerr;
        }

        public int save() throws java.io.IOException {
            byte[] data = new byte[12];
            ByteLib.intToBytes(pointers.length, data, 0);
            if (leaf)
                ByteLib.intToBytes(1, data, 4);
            else
                ByteLib.intToBytes(0, data, 4);
            ByteLib.intToBytes(pointers[0], data, 8);

            for (int i = 0; i < pointers.length - 1; ++i) {
                data = concat(data, keys[i], 0);
                byte[] tmp = new byte[4];
                ByteLib.intToBytes(pointers[i + 1], tmp, 0);
                data = concat(data, tmp, 0);
            }

            LinkedList<Integer> newBlocks = new LinkedList<Integer>();
            int current;
            if (blocks.isEmpty())
                current = io.occupy();
            else
                current = blocks.removeFirst();
            int ret = current;
            newBlocks.add(new Integer(current));
            int s = 0;
            byte[] tmp = new byte[io.getBlockSize()];
            while (data.length - s > 0) {
                if (s + io.getBlockSize() - 4 >= data.length) {
                    System.arraycopy(data, s, tmp, 4, data.length - s);
                    s += data.length - s;
                    ByteLib.intToBytes(0, tmp, 0);
                    io.writeBlock(current, tmp, 0);
                    break;
                } else {
                    int next;
                    if (blocks.isEmpty())
                        next = io.occupy();
                    else
                        next = blocks.removeFirst();
                    newBlocks.add(new Integer(next));

                    System.arraycopy(data, s, tmp, 4, io.getBlockSize() - 4);
                    s += io.getBlockSize() - 4;
                    ByteLib.intToBytes(next, tmp, 0);
                    io.writeBlock(current, tmp, 0);
                    current = next;
                }
            }

            for (Integer i: blocks)
                io.free(i.intValue());

            blocks = newBlocks;

            return ret;
        }

        public void remove() {
            for (Integer i: blocks) {
                io.free(i.intValue());
            }
            blocks = new LinkedList<Integer>();
        }

        private byte[] concat(byte[] a, byte[] b, int boffset) {
            byte[] c = new byte[a.length + b.length - boffset];
            System.arraycopy(a, 0, c, 0, a.length);
            System.arraycopy(b, boffset, c, a.length, b.length - boffset);
            return c;
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

        public int find(byte[] key) {
            int pos = binarySearch(key);
            if (leaf) {
                if (compare.compare(keys[pos], key) == 0)
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

        public boolean isLeaf() {
            return leaf;
        }

        public InsertResult insertSplitSave(byte[] key, int value) throws java.io.IOException {
            int pos = binarySearch(key);
            if (pos != -1 && compare.compare(keys[pos], key) == 0) {
                System.err.println("Inserting a key that is already existed");
                return null;
            }
            ++pos;

            if (pos == 0 && !leaf) {
                System.err.println("Inserting a key that is lower than the minimum of an internal node");
                return null;
            }

            byte[][] newKeys = new byte[keys.length + 1][];
            int[] newPointers = new int[pointers.length + 1];

            if (leaf)
                newPointers[pointers.length] = pointers[pointers.length - 1];
            else
                newPointers[0] = pointers[0];
            for (int i = 0; i < pointers.length; ++i) {
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
            }

            return ret;
        }
    }
}
