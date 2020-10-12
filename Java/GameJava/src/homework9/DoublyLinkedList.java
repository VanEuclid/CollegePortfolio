package homework9;

public class DoublyLinkedList<AnyType> {
    private class Node {
        Node(AnyType data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
        AnyType data;
        Node next, prev;
    }

    public class Iterator {
        Node current;
        Iterator(Node n) {
            current = n;
        }
        // This invalidates current
        public void remove() {
            current.prev.next = current.next;
            current.next.prev = current.prev;
        }
        public void insert(AnyType data) {
            Node newNode = new Node(data, current.next, current);
            current.next.prev = newNode;
            current.next = newNode;
        }
        public boolean valid() {
            return (current != null && current != header && current != footer);
        }
        public void next() {
            current = current.next;
        }
        public void prev() {
            current = current.prev;
        }
        public AnyType getData() {
            return current.data;
        }
    }

    Node header, footer;

    DoublyLinkedList() {
        header = new Node(null, null, null);
		footer = new Node(null, null, header);
		header.next = footer;
	}
    public void insert(AnyType data) {
        footer.prev = new Node(data, footer, footer.prev);
        footer.prev.prev.next = footer.prev;
    }
    public void remove(AnyType data) {
        Iterator i = first();
        while (i.valid() && !i.getData().equals(data)) i.next();
        if (i.valid()) i.remove();
    }
    public Iterator first() {
        return new Iterator(header.next);
    }
    public Iterator last() {
        return new Iterator(footer.prev);
    }
    public Iterator find(AnyType data) {
        Iterator current = first();
        while (current.valid()) {
            if (current.getData().equals(data)) return current;
            current.next();
        }
        return null;
    }
    public int size(){
        int s = 0;
        Iterator current = first();
        while (current.valid()) {
            s++;
            current.next();
        }
        return s;
    }
}
