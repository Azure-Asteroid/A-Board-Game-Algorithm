public interface MinPQ<Key> extends Iterable<Key> {
    public boolean isEmpty(); 

    public int size(); 

    public void insert(Key v); 

    public Key min(); 

    public Key delMin(); 
}